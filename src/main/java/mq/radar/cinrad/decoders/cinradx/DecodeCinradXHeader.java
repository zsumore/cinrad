package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.InMemoryRandomAccessFile;
import ucar.unidata.io.RandomAccessFile;

public class DecodeCinradXHeader implements IDecodeCinradXHeader {

	private final Logger logger = LoggerFactory.getLogger(DecodeCinradXHeader.class);

	private RandomAccessFile f;

	private ICinradXHeader cinradXheader;

	private URL url = null;

	private Long length = null;

	@Override
	public RandomAccessFile getRandomAccessFile() {

		return f;
	}

	@Override
	public void decodeHeader(URL url) throws DecodeException {
		// Initiate binary buffered read
		System.out.println("Start:" + System.currentTimeMillis());
		this.url = url;
		RandomAccessFile raf = null;
		try {
			if (url.getProtocol().equals("file")) {
				raf = new RandomAccessFile(url.getFile().replaceAll("%20", " "), "r");

			} else {
				raf = new ucar.unidata.io.http.HTTPRandomAccessFile(url.toString());

			}
			raf.order(RandomAccessFile.BIG_ENDIAN);

		} catch (Exception e) {
			logger.error("While decode Cinrad file Header Occurred", e);

			throw new DecodeException("CONNECTION ERROR: " + url, url);
		}
		decodeHeader(raf);
		System.out.println("Stop:" + System.currentTimeMillis());

	}

	void decodeHeader(ucar.unidata.io.RandomAccessFile raf) throws DecodeException {

		try {

			byte[] data = new byte[(int) raf.length()];

			raf.seek(0);
			raf.readFully(data);
			raf.close();

			f = new InMemoryRandomAccessFile("Cinrad-X DATA", data);
			// rewind

			logger.debug("PROCESS FILE LENGTH: {}", f.length());

			f.seek(0);

			cinradXheader = new CinradXHeader();

			logger.debug("Start build CommonBlocks");
			CommonBlocks commonBlocks = new CommonBlocks();
			commonBlocks.builder(f, -1);
			cinradXheader.setCommonBlocks(commonBlocks);
			logger.debug("After build CommonBlocks File Pointer:{}", f.getFilePointer());

			logger.debug("Start build ProductHeader");
			ProductHeader productHeader = new ProductHeader();
			productHeader.builder(f, -1);
			cinradXheader.setProductHeader(productHeader);
			logger.debug("After build ProductHeader File Pointer:{}", f.getFilePointer());

			logger.debug("Start build ProductDependentParameter");
			ProductDependentParameter productDependentParameter = new ProductDependentParameter(
					CinradXUtils.getProductType(productHeader.getProductNumber()), f.readBytes(64));
			cinradXheader.setProductDependentParameter(productDependentParameter);
			logger.debug("After build ProductDependentParameter File Pointer:{}", f.getFilePointer());

			this.length = f.getFilePointer();

		} catch (Exception e) {

			long fploc = 0;
			long fsize = 0;
			try {
				fploc = f.getFilePointer();
				fsize = f.length();
			} catch (Exception ioe) {
			}

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

	@Override
	public Long getCinradXHeaderLength() {

		return length;
	}

	@Override
	public ICinradXHeader getICinradXHeader() {

		return this.cinradXheader;
	}

	@Override
	public void close() {
		if (f != null) {

			try {
				f.flush();
				f.close();
				f = null;
			} catch (IOException e) {
				logger.error("Exception:{}", e);
			}

		}
	}

}
