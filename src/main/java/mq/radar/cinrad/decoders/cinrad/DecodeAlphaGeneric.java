package mq.radar.cinrad.decoders.cinrad;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import mq.radar.cinrad.decoders.DecodeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.unidata.io.RandomAccessFile;
import uk.ac.starlink.util.Compression;

public class DecodeAlphaGeneric {
	private final Logger logger = LoggerFactory
			.getLogger(DecodeAlphaGeneric.class);

	private CinradHeader header;
	private DataInputStream dis;

	private boolean readBlock1 = true;
	private boolean readBlock2 = true;
	private boolean readBlock3 = true;

	private String block1Text;
	private String block2Text;
	private String block3Text;

	private long decodingTimeInMillis;

	public DecodeAlphaGeneric() throws DecodeException {
	}

	public void decode(CinradHeader h) throws DecodeException {

		this.header = h;

		long startTime = System.currentTimeMillis();

		try {

			// Initiate binary buffered read
			RandomAccessFile f = header.getRandomAccessFile();

			// rewind
			f.seek(0);
			// ADVANCE PAST WMO HEADER
			while (f.readShort() != -1) {
				;
			}

			// Skip over product description block (pg. 3.26 in 2620001J.pdf)
			// This has already been decoded by DecodeL3Header
			try {

				// f.skipBytes(92);
				f.skipBytes(100);
			} catch (Exception e) {
				block1Text = "";
				block2Text = "";
				block3Text = "";
				return;
			}

			byte[] magic = new byte[3];
			f.read(magic);
			Compression compression = Compression.getCompression(magic);
			logger.info(compression.toString());
			f.skipBytes(-3);

			// read remainder of small file into memory
			long compressedFileSize = f.length() - f.getFilePointer();
			byte[] buf = new byte[(int) compressedFileSize];
			f.read(buf);

			// close input file
			// header.close();
			f.flush();
			f.close();

			InputStream decompStream = compression
					.decompress(new ByteArrayInputStream(buf));
			dis = new DataInputStream(decompStream);
			short blockDivider = dis.readShort();
			logger.info("blockDivider=" + blockDivider);
			while (dis.available() > 0) {
				logger.info("BYTES AVAILABLE: " + dis.available());

				// Certain products are 'Stand-Alone Tabular Alphanumeric
				// Products
				// These products don't have 'blocks' but are just a standalone
				// Tabular Alphanumeric Block (BlockID=3)
				// This is defined in 3.3.2 (page 3-5) of 2620001J.pdf
				if (header.getProductCode() == 62 || // Storm Structure
						header.getProductCode() == 73 || // User Alert Message
						header.getProductCode() == 75 || // Free Text Message
						header.getProductCode() == 77 || // PUP Text Message
						header.getProductCode() == 82 // Supplemental Precip
														// Message
				) {

					logger.info("DECODING 'STAND-ALONE TABULAR ALPHANUMERIC PRODUCT'");

					if (!readBlock3) {
						dis.skip(dis.available());
					} else {
						processBlock3(true);
					}
					// There's more stuff, but I'm not sure what and not
					// currently interested.
					break;

				} else {

					short blockID = dis.readShort();
					int blockLen = dis.readInt();

					logger.info("blockID=" + blockID);
					logger.info("blockLen=" + blockLen);
					if (blockID == 1) {
						if (!readBlock1) {
							dis.skip(blockLen - 6);
						} else {
							processBlock1();
						}
					} else if (blockID == 2) {
						if (!readBlock2) {
							dis.skip(blockLen - 6);
						} else {
							processBlock2();
						}
					} else if (blockID == 3) {
						if (!readBlock3) {
							dis.skip(blockLen - 6);
						} else {
							processBlock3();
						}
					}
				}
			}

			buf = null;

			decodingTimeInMillis = System.currentTimeMillis() - startTime;

		} catch (EOFException e) {
			decodingTimeInMillis = System.currentTimeMillis() - startTime;
			logger.info("EOF FOUND - NO DATA FOUND: " + header.getCinradURL());
			block1Text = "";
			block2Text = "";
			block3Text = "";
		} catch (Exception e) {
			decodingTimeInMillis = System.currentTimeMillis() - startTime;
			e.printStackTrace();
			throw new DecodeException("ERROR DECODING FILE: ",
					header.getCinradURL());
		} finally {
			try {
				dis.close();
			} catch (IOException e) {

				e.printStackTrace();
				logger.error("IOException:{}", e);
			}
		}

	}

	@SuppressWarnings("unused")
	private void processBlock1() throws IOException, DecodeException {
		// BLOCK 1 - PRODUCT SYMBOLOGY BLOCK

		StringBuffer sb = new StringBuffer();

		// dis.skipBytes(blockLen-4);
		short numLayers = dis.readShort();

		// advance past next divider
		while (dis.readShort() != -1) {
			;
		}
		int layerLen = dis.readInt();
		int layerBytesRead = 0;

		short packetCode = dis.readShort();
		while (layerBytesRead < layerLen && packetCode != -1) {

			// read packet
			int packetBlockLen = readPacket(packetCode, sb);
			layerBytesRead += packetBlockLen + 2;

			logger.info("layerBytesRead=" + layerBytesRead);

			// read next packet code - if it is -1 then we are
			// done!
			packetCode = dis.readShort();

			logger.info("FOUND PACKET CODE: " + packetCode);
		}

		block1Text = sb.toString();

	}

	private void processBlock2() throws IOException, DecodeException {

		// BLOCK 2 - GRAPHIC ALPHANUMERIC BLOCK
		StringBuffer sb = new StringBuffer();

		// dis.skipBytes(blockLen-4);
		short numPages = dis.readShort();

		logger.info("numPages=" + numPages);

		for (int pageNum = 0; pageNum < numPages; pageNum++) {

			short curPage = dis.readShort();
			short pageLen = dis.readShort();

			logger.info("reading page " + pageNum);
			logger.info("curPage " + curPage);
			logger.info("pageLen " + pageLen);

			int pageBytesRead = 0;

			while (pageBytesRead < pageLen) {

				short packetCode = dis.readShort();
				logger.info("FOUND PACKET CODE: " + packetCode);

				// read packet
				int packetBlockLen = readPacket(packetCode, sb);
				pageBytesRead += packetBlockLen + 4;

				logger.info("pageBytesRead=" + pageBytesRead);

			}

			sb.append("\n\n");
		}
		block2Text = sb.toString();

		short separator = dis.readShort();

		if (separator != -1) {
			logger.info("NOTE: SEPARATOR NOT FOUND IN CORRECT LOCATION");
		}

	}

	private void processBlock3() throws IOException, DecodeException {
		processBlock3(false);
	}

	private void processBlock3(boolean standAlone) throws IOException,
			DecodeException {
		// BLOCK 3 - TABULAR ALPHANUMERIC BLOCK

		StringBuffer sb = new StringBuffer();

		if (!standAlone) {
			// advance past message header block
			while (dis.readShort() != -1) {
				;
			}
			// advance past product description block
			while (dis.readShort() != -1) {
				;
			}

		}

		// dis.skipBytes(blockLen-4);
		short numPages = dis.readShort();

		logger.info("numPages=" + numPages);

		for (int pageNum = 0; pageNum < numPages; pageNum++) {

			logger.info("reading page " + pageNum);

			short numChars = dis.readShort();
			logger.info("numChars=" + numChars);
			// repeat for each
			while (numChars != -1) {

				byte[] charData = new byte[numChars];
				dis.read(charData);
				sb.append(new String(charData) + "\n");
				logger.info(new String(charData));
				// read next line or -1 if we are done
				numChars = dis.readShort();
			}
			sb.append("\n\n");
		}
		block3Text = sb.toString();

	}

	private int readPacket(int packetCode, StringBuffer textBuffer)
			throws IOException, DecodeException {

		short packetBlockLen = 0;

		if (packetCode == 8) {
			packetBlockLen = dis.readShort();
			short valueOfTextString = dis.readShort();
			short iStart = dis.readShort();
			short jStart = dis.readShort();
			byte[] text = new byte[packetBlockLen - 6];
			dis.read(text);
			textBuffer.append(new String(text) + "\n");
			logger.info("packetCode=" + packetCode + " packetLen="
					+ packetBlockLen + " i,j=" + iStart + "," + jStart
					+ " textValue=" + valueOfTextString + " text="
					+ new String(text));
		}
		// special graphic symbol
		else if (packetCode == 20) {
			packetBlockLen = dis.readShort();
			short iPos = dis.readShort();
			short jPos = dis.readShort();
			short pointFeatureType = dis.readShort();
			short pointFeatureAttribute = dis.readShort();
			logger.info("packetCode=" + packetCode + " packetLen="
					+ packetBlockLen + " i,j=" + iPos + "," + jPos
					+ " pointFeatureType=" + pointFeatureType
					+ " pointFeatureAtt=" + pointFeatureAttribute);
		} else if (packetCode == 2) {
			packetBlockLen = dis.readShort();
			short iStart = dis.readShort();
			short jStart = dis.readShort();
			byte[] text = new byte[packetBlockLen - 4];
			dis.read(text);
			logger.info("packetCode=" + packetCode + " packetBlockLen="
					+ packetBlockLen + " i,j=" + iStart + "," + jStart
					+ " text=" + new String(text));
		}

		else {
			// throw new
			// NexradDecodeException("NMD Special Graphic Symbol code="+packetCode+" NOT FOUND.  Found packet code of: "+packetCode,
			// header.getNexradURL());
			logger.info("NMD Special Graphic Symbol code=" + packetCode
					+ " NOT FOUND.  Found packet code of: " + packetCode
					+ " ---- " + header.getCinradURL());
			packetBlockLen = dis.readShort();
			dis.skip(packetBlockLen);
		}

		return packetBlockLen;

	}

	public String getBlock1Text() {
		return block1Text;
	}

	public String getBlock2Text() {
		return block2Text;
	}

	public String getBlock3Text() {
		return block3Text;
	}

	public long getDecodingTimeInMillis() {
		return decodingTimeInMillis;
	}

	public boolean isReadBlock1() {
		return readBlock1;
	}

	public void setReadBlock1(boolean readBlock1) {
		this.readBlock1 = readBlock1;
	}

	public boolean isReadBlock2() {
		return readBlock2;
	}

	public void setReadBlock2(boolean readBlock2) {
		this.readBlock2 = readBlock2;
	}

	public boolean isReadBlock3() {
		return readBlock3;
	}

	public void setReadBlock3(boolean readBlock3) {
		this.readBlock3 = readBlock3;
	}

}
