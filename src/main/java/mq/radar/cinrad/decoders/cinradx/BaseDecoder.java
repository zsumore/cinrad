package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import mq.radar.cinrad.MQProjections;
import mq.radar.cinrad.MQXFilter;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;

public class BaseDecoder implements IBaseDecoder {

	protected IDecodeCinradXHeader decodeCinradXHeader;

	protected Configuration configuration;

	protected MathTransform cinradTransform = null;

	protected CoordinateReferenceSystem crs = null;

	protected GeometryFactory geoFactory = null;

	protected SimpleFeatureType schema = null;

	protected MQXFilter filter = new MQXFilter();

	protected double geometryBuffer = 0.0;

	protected double geometrySimplify = 0.000001;

	public BaseDecoder(IDecodeCinradXHeader decodeHeader) throws ConfigurationException {

		this.decodeCinradXHeader = decodeHeader;

		this.configuration = new PropertiesConfiguration(
				getClass().getClassLoader().getResource(CinradXUtils.DEFAULT_DECODE_CONFIG_FILE));

	}

	@Override
	public SimpleFeatureType[] getFeatureTypes() {
		return new SimpleFeatureType[] { schema };
	}

	@Override
	public void decodeData(boolean autoClosed) throws DecodeException, IOException, TransformException {
		// TODO Auto-generated method stub

	}

	@Override
	public Configuration getDecodeHintsConfig() {
		return configuration;
	}

	@Override
	public void setDecodeHintsConfig(Configuration conf) {
		if (null != conf) {
			ConfigurationUtils.copy(conf, configuration);
		}

	}

	@Override
	public void setDecodeHint(String hintKey, Object hintValue) throws DecodeHintNotSupportedException {
		configuration.addProperty(hintKey, hintValue);

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	protected void initDecodeHints() throws FactoryException {
		if (configuration.containsKey(GEOMETRY_BUFFER)) {
			geometryBuffer = configuration.getDouble(GEOMETRY_BUFFER);
		}
		if (configuration.containsKey(GEOMETRY_SIMPLIFY)) {
			geometryBuffer = configuration.getDouble(GEOMETRY_SIMPLIFY);
		}
		if (configuration.containsKey(MIN_VALUE)) {
			filter.setMinValue(configuration.getDouble(MIN_VALUE));
		}
		if (configuration.containsKey(MAX_VALUE)) {
			filter.setMaxValue(configuration.getDouble(MAX_VALUE));
		}
		if (configuration.containsKey(MIN_AZIMUTH) && configuration.containsKey(MAX_AZIMUTH)) {
			filter.setAzimuthRange(configuration.getDouble(MIN_AZIMUTH), configuration.getDouble(MAX_AZIMUTH));
		}

		crs = MQProjections.getInstance().getCoordinateSystemByProjectionType(
				CinradXUtils.getProjectionByName(configuration.getString(CRS_TARGET, "WGS84").trim()),
				this.decodeCinradXHeader.getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLongitude(),
				this.decodeCinradXHeader.getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLatitude());

		geoFactory = new GeometryFactory(new PrecisionModel(configuration.getInt(GEOMETRY_FACTORY_PRECISION, 1000000)),
				configuration.getInt(GEOMETRY_FACTORY_SRID, 0));

		cinradTransform = CRS.findMathTransform(MQProjections.getInstance().getCoordinateSystemByProjectionType(
				CinradXUtils.getProjectionByType(
						this.decodeCinradXHeader.getICinradXHeader().getProductHeader().getProjectionType()),
				this.decodeCinradXHeader.getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLongitude(),
				this.decodeCinradXHeader.getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLatitude()),
				crs);

	}

	protected boolean testValueRange(float value) {
		if (value >= filter.getMinValue() && value <= filter.getMaxValue())
			return true;
		return false;
	}

}
