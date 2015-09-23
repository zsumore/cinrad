package mq.radar.cinrad.decoders.cinradx;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.InMemoryRandomAccessFile;
import ucar.unidata.io.RandomAccessFile;

public class DecodeCinradXHeader implements ICinradXHeader {

	private final Logger logger = LoggerFactory.getLogger(DecodeCinradXHeader.class);

	private RandomAccessFile f;

	private CommonBlocks commonBlocks;

	private ProductHeader productHeader;

	private ProductDependentParameter productDependentParameter;

	private URL url = null;

	private Long length = null;

	@Override
	public CommonBlocks getCommonBlocks() {

		return commonBlocks;
	}

	@Override
	public ProductHeader getProductHeader() {

		return productHeader;
	}

	@Override
	public ProductDependentParameter getProductDependentParameter() {

		return productDependentParameter;
	}

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

			logger.debug("Start build CommonBlocks");
			commonBlocks = new CommonBlocks();
			commonBlocks.builder(f, -1);
			logger.debug("After build CommonBlocks File Pointer:{}", f.getFilePointer());

			logger.debug("Start build ProductHeader");
			productHeader = new ProductHeader();
			productHeader.builder(f, -1);
			logger.debug("After build ProductHeader File Pointer:{}", f.getFilePointer());

			logger.debug("Start build ProductDependentParameter");
			productDependentParameter = new ProductDependentParameter(
					CinradXUtils.getProductType(productHeader.getProductNumber()), f.readBytes(64));

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

}
