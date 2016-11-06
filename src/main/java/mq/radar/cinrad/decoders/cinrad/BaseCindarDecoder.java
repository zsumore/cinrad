package mq.radar.cinrad.decoders.cinrad;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mq.radar.cinrad.MQProjections;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;
import mq.radar.cinrad.decoders.MaxGeographicExtent;
import mq.radar.cinrad.decoders.StreamingProcess;
import mq.radar.cinrad.decoders.StreamingProcessException;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

public class BaseCindarDecoder implements CindarDecoder {
	private final Logger logger = LoggerFactory.getLogger(BaseCindarDecoder.class);
	protected String[] metaLabelString = null;
	protected DefaultFeatureCollection features = null;
	protected DefaultFeatureCollection lineFeatures = null;
	protected SimpleFeatureType schema = null;
	protected SimpleFeatureType lineSchema = null;
	protected GeometryFactory geoFactory = null;
	protected Envelope wsrBounds = null;

	protected Map<String, Object> decodeHints = null;
	protected CinradHeader header = null;
	protected String[] supplementalData = null;
	protected String datetime = null;

	protected CoordinateReferenceSystem crs = null;

	protected String decoderName = "Unknow";

	/**
	 * Constructor
	 * 
	 * @param header
	 *            Description of the Parameter
	 * @throws IOException
	 * @throws FactoryException
	 */
	public BaseCindarDecoder(CinradHeader header) throws DecodeException, IOException, FactoryException {
		this.header = header;
		Envelope envelope = null;
		try {
			envelope = MaxGeographicExtent.getCinradExtent(this.header);
		} catch (TransformException e) {
			logger.error("TransformException:", e);
			e.printStackTrace();
		}
		features = new DefaultFeatureCollection();
		lineFeatures = new DefaultFeatureCollection();
		wsrBounds = envelope;
		metaLabelString = new String[3];
		supplementalData = new String[2];
		datetime = CinradUtils.yyyyMMddHHmmss.format(header.getScanCalendar().getTime());
		// crs =
		// MQProjections.getInstance().getRadarCoordinateSystem(this.header);
		crs = MQProjections.getInstance().getWGS84CoordinateSystem();
		geoFactory = new GeometryFactory(new PrecisionModel(10000), 4326);
		decodeHints = new HashMap<String, Object>();
		// decodeData();
	}

	public String[] getSupplementalDataArray() throws IOException {
		return supplementalData;
	}

	public void decodeData() throws DecodeException, IOException {
		if (features == null) {
			features = new DefaultFeatureCollection();
		}
		features.clear();

		StreamingProcess process = new StreamingProcess() {
			public void addFeature(SimpleFeature feature) throws StreamingProcessException {
				features.add(feature);
			}

			public void close() throws StreamingProcessException {
				logger.info("STREAMING PROCESS close() ::: features.size() = " + features.size());
			}
		};

		decodeData(new StreamingProcess[] { process });

	}

	public SimpleFeatureCollection getFeatures() {
		return features;
	}

	public void decodeData(StreamingProcess[] processArray, boolean autoClose) throws DecodeException, IOException {

	}

	public void setDecodeHint(String hintKey, Object hintValue) throws DecodeHintNotSupportedException {
		throw new DecodeHintNotSupportedException(decoderName, hintKey, decodeHints);

	}

	public SimpleFeatureType[] getFeatureTypes() {
		return new SimpleFeatureType[] { schema };
	}

	public void decodeData(StreamingProcess[] streamingProcessArray) throws DecodeException, IOException {
		decodeData(streamingProcessArray, true);
	}

	public Envelope getCinradExtent() {
		return wsrBounds;
	}

	public SimpleFeatureCollection getLineFeatures() {
		return lineFeatures;
	}

	public SimpleFeatureType getLineFeatureType() {
		return lineSchema;
	}

	public String getMetaLabel(int index) {
		if (metaLabelString[index] == null) {
			return "";
		} else {
			return metaLabelString[index];
		}
	}

	public String getDefaultSymbol() {
		return null;
	}

	public Set<String> getDecodeHintsKey() {
		return decodeHints.keySet();
	}

	public CoordinateReferenceSystem getCRS() {

		return crs;
	}

	@Override
	public void close() {

		if (null != features) {
			features.clear();
		}
		if (null != lineFeatures) {
			lineFeatures.clear();
		}
	}

}
