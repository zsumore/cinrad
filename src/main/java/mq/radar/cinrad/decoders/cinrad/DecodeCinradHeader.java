package mq.radar.cinrad.decoders.cinrad;

import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.MaxGeographicExtent;
import mq.radar.cinrad.util.CinradEquations;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.nc2.iosp.nids.JNXNidsheader;
import ucar.unidata.io.InMemoryRandomAccessFile;
import ucar.unidata.io.RandomAccessFile;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class DecodeCinradHeader implements CinradHeader {
	private final Logger logger = LoggerFactory.getLogger(DecodeCinradHeader.class);

	private boolean validFile = false;
	private String fileName = "";
	private short productCode;
	private int fileSize;
	private short radarStationCode;
	private short receiveStationCode;
	private short dataBlockSize;

	private double lat;
	private double lon;
	private double alt;

	private short operatingMode;

	private short vcp;
	private short serialNumber;
	private short scanNumber;

	private short[] productSpecific = new short[10];

	private short elevNumber;

	/**
	 * Description of the Field
	 */
	private int[] dataThresholdInfo = new int[16];

	private int[] dataThresholdValue = new int[16];

	private String[] dataThresholdString = new String[16];

	private byte[] dataThresholdBytes = new byte[32];

	private int version;

	private int symbologyBlockOffset;
	private int graphicBlockOffset;
	private int tabularBlockOffset;

	private Calendar scanCalendar = Calendar.getInstance();

	private ucar.unidata.io.RandomAccessFile f;

	private URL url = null;

	private int sid = -999;

	public short getProductCode() {
		return productCode;
	}

	public int getFileSize() {
		return fileSize;
	}

	public short getRadarStationCode() {
		return radarStationCode;
	}

	public short getReceiveStationCode() {
		return receiveStationCode;
	}

	public short getDataBlockSize() {
		return dataBlockSize;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getAlt() {
		return alt;
	}

	public short getOperatingMode() {
		return operatingMode;
	}

	public short getVcp() {
		return vcp;
	}

	public short[] getProductSpecific() {
		return productSpecific;
	}

	public short getElevNumber() {
		return elevNumber;
	}

	public String[] getDataThresholdString() {
		return dataThresholdString;
	}

	public int getVersion() {
		return version;
	}

	public int getSymbologyBlockOffset() {
		return symbologyBlockOffset;
	}

	public int getGraphicBlockOffset() {
		return graphicBlockOffset;
	}

	public int getTabularBlockOffset() {
		return tabularBlockOffset;
	}

	public Calendar getScanCalendar() {

		return scanCalendar;
	}

	// public Calendar getGenerateCalendar() {
	// return genCalendar;
	// }

	public DecodeCinradHeader() {

	}

	public DecodeCinradHeader(int sid) {
		this.sid = sid;

	}

	public void decodeHeader(URL url) throws DecodeException {
		this.url = url;

		// Initiate binary buffered read
		ucar.unidata.io.RandomAccessFile raf = null;
		try {
			if (url.getProtocol().equals("file")) {
				raf = new ucar.unidata.io.RandomAccessFile(url.getFile().replaceAll("%20", " "), "r");

			} else {
				raf = new ucar.unidata.io.http.HTTPRandomAccessFile(url.toString());

			}
			raf.order(ucar.unidata.io.RandomAccessFile.BIG_ENDIAN);
			fileName = raf.getLocation();

		} catch (Exception e) {
			logger.error("While decode Cinrad file Header Occurred", e);

			throw new DecodeException("CONNECTION ERROR: " + url, url);
		}
		decodeHeader(raf);

	}

	void decodeHeader(ucar.unidata.io.RandomAccessFile raf) throws DecodeException {

		String header = null;
		int wmoStart = -1;

		try {

			JNXNidsheader nids = new JNXNidsheader();
			byte[] data = nids.readZLibNIDS(raf);

			// byte[] data = new byte[(int) raf.length()];
			// raf.read(data);

			// close file - we will now use in-memory data
			raf.close();

			f = new ucar.unidata.io.InMemoryRandomAccessFile("Cinrad DATA", data);
			f.order(ucar.unidata.io.RandomAccessFile.BIG_ENDIAN);
			f.seek(0); // rewind

			logger.debug("DECOMPRESSED FILE LENGTH: {}", f.length());
			if (nids.isValidFile(f)) {
				// Valid File
				validFile = true;
				// Get product code )
				productCode = f.readShort();
				// Get volume gen time
				f.readShort();
				// Get volume gen time
				f.readInt();

				// Get file size
				fileSize = f.readInt();

				// Get station code
				radarStationCode = f.readShort();

				// Get receive Station Code
				receiveStationCode = f.readShort();

				// Get datablock size
				dataBlockSize = f.readShort();

				f.seek(0);
				while (f.readShort() != -1) {
					;
				}

				logger.info("--FIRST BREAKPOINT-- FILE POINTER LOCATION ={}", f.getFilePointer());

				// Decode Lat and Lon
				lat = f.readInt() / 1000.0;
				lon = f.readInt() / 1000.0;

				// Decode Radar Site Altitude
				alt = f.readShort();
				// Get product code )
				productCode = f.readShort();

				// ----
				// Get operational mode
				operatingMode = f.readShort();
				// Get volume coverage pattern
				vcp = f.readShort();
				// Get sequence number
				serialNumber = f.readShort();
				// Get volume scan number
				scanNumber = f.readShort();
				// Get volume scan date
				short scandate = f.readShort();
				// Get volume scan time
				int scantime = f.readInt();
				// Get product generation date
				f.readShort();
				// Get product generation time
				f.readInt();
				// Get first 2 product specific codes (halfwords 27 and 28)
				for (int i = 0; i < 2; i++) {
					productSpecific[i] = f.readShort();
				}
				// Get elevation number
				elevNumber = f.readShort();
				// Get the 3rd product specific code (halfword 30)
				productSpecific[2] = f.readShort();
				// Get the data threshold values
				// read halfword 31-47 again as byte array, which can be used
				// differently
				// depending on the product type (8-bit, 16-level, etc...)
				f.read(dataThresholdBytes);
				// The following will process the data threshold values, which
				// are
				// applicable for all products but the 8-bit ones
				InMemoryRandomAccessFile bf = new InMemoryRandomAccessFile("Data Threshold", dataThresholdBytes);
				for (int i = 0; i < 16; i++) {
					// TODO
					if (this.productCode == 19) {
						dataThresholdInfo[i] = bf.readUnsignedByte();
						dataThresholdValue[i] = bf.readUnsignedByte();
					} else {
						dataThresholdInfo[i] = bf.readUnsignedByte();
						dataThresholdValue[i] = bf.readUnsignedByte();
					}
					;
					//System.out.println(this.productCode);
					//System.out.println("-----------------------------");
					//System.out.println(dataThresholdInfo[i]);
					//System.out.println(dataThresholdValue[i]);
					//System.out.println("-----------------------------");

				}
				bf.close();

				// Get the remaining 7 product specific codes (halfword
				for (int i = 0; i < 7; i++) {
					productSpecific[i + 3] = f.readShort();
				}
				version = f.readUnsignedByte();
				f.readUnsignedByte();

				// Get the offset to the Symbology Block
				symbologyBlockOffset = f.readInt();
				// Get the offset to the Graphic Block
				graphicBlockOffset = f.readInt();
				// Get the offset to the Tabular Block
				tabularBlockOffset = f.readInt();

				logger.debug("VERSION:{}", version);

				logger.debug("END OF PROD. DESC. BLOCK: FILE POS:{}", f.getFilePointer());

				logger.debug("symbologyBlockOffset:{}", symbologyBlockOffset);

				logger.debug("graphicBlockOffset:{}", graphicBlockOffset);

				logger.debug("tabularBlockOffset:{}", tabularBlockOffset);

				logger.debug("HEADER::::: {}", f.readShort());
				logger.debug("HEADER::::::{}", f.readShort());

				processThresholds();

				int yyyymmdd = CinradEquations.convertJulianDate(scandate);
				String str = new Integer(yyyymmdd).toString();
				int year = Integer.parseInt(str.substring(0, 4));
				int month = Integer.parseInt(str.substring(4, 6));
				int day = Integer.parseInt(str.substring(6, 8));
				int hour = scantime / 3600;
				int minute = (scantime / 60) % 60;
				int seconds = scantime - hour * 3600 - minute * 60;
				scanCalendar.set(year, month - 1, day, hour, minute, seconds);
				scanCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));

				return;
			} else {
				logger.info("Cinrad File:{} is not a valid file!", fileName);
				return;
			}
		} catch (Exception e) {

			long fploc = 0;
			long fsize = 0;
			try {
				fploc = f.getFilePointer();
				fsize = f.length();
			} catch (Exception ioe) {
			}

			logger.error("ERROR DUMP: wmoStart={}\n{}", wmoStart, header);

			logger.error("ERROR DUMP: f-loct={}file-size={}", fploc, fsize);

			if (fploc == fsize) {
				throw new DecodeException("Header Decode Error = No Section Separators Found: ", url);
			}

			logger.error("CAUGHT EXCEPTION: {}", e);

			try {
				f.close();
			} catch (Exception ee) {

				logger.error("Exception:{}", ee);
			}

			throw new DecodeException("Header Decode Error = " + e.getMessage(), url);

		}

	}

	/**
	 * Description of the Method
	 */
	private void processThresholds() {

		if (productCode == 34) {

			// version 0 (Legacy)
			if (version == 0) {
				// dataThresholdString[0] = "ND";
				dataThresholdString[0] = "FILTER OFF";
				dataThresholdString[1] = "NO CLUTTER";
				dataThresholdString[2] = "L";
				dataThresholdString[3] = "M";
				dataThresholdString[4] = "H (Bypass Map)";
				dataThresholdString[5] = "L";
				dataThresholdString[6] = "M";
				dataThresholdString[7] = "H (Force Filter)";
			}
			// version 1 (ORPG)
			else {
				// dataThresholdString[0] = "ND";
				dataThresholdString[0] = "FILTER OFF";
				dataThresholdString[1] = "NO CLUTTER";
				dataThresholdString[2] = "";
				dataThresholdString[3] = "";
				dataThresholdString[4] = "CLUTTER";
				dataThresholdString[5] = "";
				dataThresholdString[6] = "";
				dataThresholdString[7] = "FORCE FILTER";
			}

			return;
		}

		// int binaryLength;
		for (int i = 0; i < 16; i++) {
			String binaryString = getPaddedBitString(dataThresholdInfo[i], 8);

			// System.out.println("binaryString = "+binaryString);

			if (binaryString.charAt(0) == '1') {
				if (dataThresholdValue[i] == 0) {
					dataThresholdString[i] = "";
				} else if (dataThresholdValue[i] == 1) {
					dataThresholdString[i] = "TH";
				} else if (dataThresholdValue[i] == 2) {
					dataThresholdString[i] = "ND";
				} else if (dataThresholdValue[i] == 3) {
					dataThresholdString[i] = "RF";
				}
			} else {
				dataThresholdString[i] = "" + dataThresholdValue[i];

				if (binaryString.charAt(2) == '1') {
					dataThresholdString[i] = CinradUtils.fmt2.format((double) dataThresholdValue[i] / 20.0);
				} else if (binaryString.charAt(3) == '1') {
					dataThresholdString[i] = CinradUtils.fmt1.format((double) dataThresholdValue[i] / 10.0);
				} else if (binaryString.charAt(4) == '1') {
					dataThresholdString[i] = "> " + dataThresholdValue[i];
				} else if (binaryString.charAt(5) == '1') {
					dataThresholdString[i] = "< " + dataThresholdValue[i];
				} else if (binaryString.charAt(6) == '1') {
					dataThresholdString[i] = "+ " + dataThresholdValue[i];
				} else if (binaryString.charAt(7) == '1') {
					dataThresholdString[i] = "- " + dataThresholdValue[i];
				}

			}
		}
	}

	private String getPaddedBitString(int value, int numBits) {
		String binaryString = Integer.toBinaryString(value);
		// Add enough zeros so binaryString represents all 8 bits
		int binaryLength = binaryString.length();
		for (int n = 0; n < numBits - binaryLength; n++) {
			binaryString = "0" + binaryString;
		}
		return binaryString;
	}

	public RandomAccessFile getRandomAccessFile() {
		return f;
	}

	public byte[] getDataThresholdBytes() {

		return dataThresholdBytes;
	}

	public Envelope getCinradBounds() {
		Envelope envelope = null;
		try {
			envelope = MaxGeographicExtent.getCinradExtent(this);
		} catch (FactoryException e) {
			logger.error("FactoryException:", e);
			e.printStackTrace();
		} catch (TransformException e) {
			logger.error("TransformException:", e);
			e.printStackTrace();
		}
		return envelope;
	}

	public Coordinate getRadarCoordinate() {

		return new Coordinate(lon, lat);
	}

	public boolean isValidFile() {

		return validFile;
	}

	public short getScanNumber() {

		return scanNumber;
	}

	public short getSerialNumber() {

		return serialNumber;
	}

	public void close() {
		try {
			f.close();
		} catch (Exception ee) {

			logger.error("Exception:{}", ee);
		}
	}

	@Override
	public String toString() {
		if (validFile) {
			String breakLine = "\n";
			StringBuffer buffer = new StringBuffer();
			buffer.append("Cindar File: ").append(fileName).append(breakLine);
			buffer.append("Product Code: ").append(productCode).append(breakLine);
			buffer.append("Scan Datetime: ").append(CinradUtils.datetime.format(scanCalendar.getTime()))
					.append(breakLine);
			buffer.append("Radar Station Code: ").append(radarStationCode).append(breakLine);
			buffer.append("Receive Radar Station Code: ").append(receiveStationCode).append(breakLine);
			buffer.append("File Size(Byte): ").append(fileSize).append(breakLine);
			buffer.append("Latitude: ").append(lat).append(";Longitude: ").append(lon).append(";Altitude: ").append(alt)
					.append(breakLine);
			buffer.append("Operating Mode: ").append(operatingMode).append(breakLine);
			buffer.append("RPG Serial Number: ").append(serialNumber).append(breakLine);
			buffer.append("Volume Coverage Pattern: ").append(vcp).append(breakLine);
			buffer.append("Product Specific: ");
			int size = productSpecific.length;
			for (int i = 0; i < size; i++) {
				buffer.append(i).append(":").append(productSpecific[i]).append(",");
			}
			buffer.append(breakLine);
			buffer.append("Elev Number: ").append(elevNumber).append(breakLine);

			buffer.append("Data Level Threshold: ");
			int size2 = dataThresholdString.length;
			for (int i = 0; i < size2; i++) {
				buffer.append(i).append(":").append(dataThresholdString[i]).append(",");
			}
			buffer.append(breakLine);

			buffer.append("Version: ").append(version).append(breakLine);

			buffer.append("Symbology Block Offset: ").append(symbologyBlockOffset).append(breakLine);
			buffer.append("Graphic Block Offset: ").append(graphicBlockOffset).append(breakLine);
			buffer.append("Tabular Block Offset: ").append(tabularBlockOffset).append(breakLine);

			return buffer.toString();
		}
		return super.toString();
	}

	public URL getCinradURL() {
		return url;
	}

	public CindarProducts getProduct() {
		return CindarProducts.getProduct(productCode);
	}

	public String getDataThresholdString(int index) {

		return dataThresholdString[index];
	}

	public Integer getRadarStationID() {
		return sid;
	}

	@Override
	public void setElevNumber(short elev) {

		elevNumber = elev;
	}

	@Override
	public void setRadarStationID(int sid) {
		this.sid = sid;

	}

}
