package mq.radar.cinrad;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;
import mq.radar.cinrad.decoders.cinrad.CindarDecoder;
import mq.radar.cinrad.decoders.cinrad.CinradHeader;
import mq.radar.cinrad.decoders.cinrad.DecodeCinradHeader;
import mq.radar.cinrad.decoders.cinrad.DecodeHail;
import mq.radar.cinrad.decoders.cinrad.DecodeMeso;
import mq.radar.cinrad.decoders.cinrad.DecodeStormStructure;
import mq.radar.cinrad.decoders.cinrad.DecodeStormTracking;
import mq.radar.cinrad.decoders.cinrad.DecodeStreamingRadial;
import mq.radar.cinrad.decoders.cinrad.DecodeTVS;
import mq.radar.cinrad.decoders.cinrad.DecodeVWP;
import mq.radar.cinrad.decoders.cinrad.ProductType;
import mq.radar.cinrad.decoders.cinrad.UnSupportProductException;

import org.opengis.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cinrad {
	private final Logger logger = LoggerFactory.getLogger(Cinrad.class);
	private CinradHeader header;
	private CindarDecoder decoder;

	public Cinrad(URL url) throws DecodeException, UnSupportProductException, IOException, FactoryException {
		this(url, null);

	}

	public Cinrad(URL url, Integer sid)
			throws DecodeException, UnSupportProductException, IOException, FactoryException {
		this(url, sid, null);
	}

	public Cinrad(URL url, Integer sid, Map<String, Object> decodeHints)
			throws DecodeException, UnSupportProductException, IOException, FactoryException {
		if (url != null) {
			if (sid != null) {
				header = new DecodeCinradHeader(sid);
			} else {
				header = new DecodeCinradHeader();
			}
			header.decodeHeader(url);

			int pcode = header.getProductCode();

			ProductType productType = getProductType(pcode);

			switch (productType) {
			case L3RADIAL:
			case L3RASTER:
				decoder = new DecodeStreamingRadial(header);
				break;
			case L3ALPHA:
				if (pcode == 58) {
					decoder = new DecodeStormTracking(header);
				} else if (pcode == 59) {
					decoder = new DecodeHail(header);
				} else if (pcode == 60) {
					decoder = new DecodeMeso(header);
				} else if (pcode == 61) {
					decoder = new DecodeTVS(header);
				} else {
					decoder = new DecodeStormStructure(header);
				}
				break;
			case L3VAD:
				if (pcode == 48) {
					decoder = new DecodeVWP(header);
				}
				break;
			default:
				header.close();
				throw new UnSupportProductException("This product is not support:" + pcode);
			}
			if (decodeHints != null && decodeHints.size() > 0) {
				for (Map.Entry<String, Object> entry : decodeHints.entrySet()) {
					try {
						decoder.setDecodeHint(entry.getKey(), entry.getValue());
					} catch (DecodeHintNotSupportedException e) {
						logger.error("DecodeHintNotSupportedException:", e);

					}
				}
			}
			// decoder.decodeData();

		} else {
			logger.error("URL is null!");
		}
	}

	public void setDecodeHint(Map<String, Object> decodeHints) {

		if (decoder != null && decodeHints != null && decodeHints.size() > 0) {
			for (Map.Entry<String, Object> entry : decodeHints.entrySet()) {
				try {
					decoder.setDecodeHint(entry.getKey(), entry.getValue());
				} catch (DecodeHintNotSupportedException e) {
					logger.error("DecodeHintNotSupportedException:", e);

				}
			}
		}

	}

	public void decodeData() throws DecodeException, IOException {

		decoder.decodeData();

	}

	public void close() {
		if (null != header) {
			header.close();
			header = null;
		}
		if (null != decoder) {
			decoder.close();
			decoder = null;
		}

	}

	public CinradHeader getHeader() {
		return header;
	}

	public CindarDecoder getDecoder() {
		return decoder;
	}

	public static boolean isSupport(int pcode) {
		if (pcode == 19 || pcode == 20 || pcode == 25 || pcode == 26 || pcode == 27 || pcode == 28 || pcode == 30
				|| pcode == 36 || pcode == 37 || pcode == 38 || pcode == 41 || pcode == 43 || pcode == 44 || pcode == 46
				|| pcode == 48 || pcode == 53 || pcode == 56 || pcode == 57 || pcode == 58 || pcode == 59 || pcode == 60
				|| pcode == 61 || pcode == 62 || pcode == 65 || pcode == 66 || pcode == 78 || pcode == 79 || pcode == 80
				|| pcode == 110)
			return true;

		return false;

	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	public static ProductType getProductType(int code) {

		if (code == 19 || code == 20 || code == 25 || code == 26 || code == 27 || code == 28 || code == 30 || code == 43
				|| code == 44 || code == 46 || code == 56 || code == 78 || code == 79 || code == 80 || code == 110) {
			return ProductType.L3RADIAL;
		} else if (code == 36 || code == 37 || code == 38 || code == 41 || code == 53 || code == 57 || code == 65
				|| code == 66) {
			return ProductType.L3RASTER;
		} else if (code == 58 || code == 59 || code == 60 || code == 61 || code == 62) {
			return ProductType.L3ALPHA;
		} else if (code == 48) {
			return ProductType.L3VAD;
		} else {
			return ProductType.UNKNOWN;
		}
	}

}
