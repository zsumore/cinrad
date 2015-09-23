package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.operation.MathTransform;

import com.google.common.collect.Multimap;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Polygon;

import mq.radar.cinrad.MQXFilter;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;

public class DecodeRadial implements IRadialDecoder {

	private ICinradXHeader cinradXHeader;
	private Configuration configuration;

	private double geometryBuffer = 0.000001;
	private double geometrySimplify = 0.000001;

	private Multimap<Integer, Polygon> polyMultimap;

	private MQXFilter filter = new MQXFilter();

	private RadialDataBlock radialDataBlock;

	protected SimpleFeatureType schema = null;

	public DecodeRadial(ICinradXHeader cinradXHeader) throws ConfigurationException {
		super();
		this.cinradXHeader = cinradXHeader;

		configuration = new PropertiesConfiguration(
				getClass().getClassLoader().getResource(CinradXUtils.DEFAULT_DECODE_CONFIG_FILE));
	}

	@Override
	public SimpleFeatureType[] getFeatureTypes() {
		return new SimpleFeatureType[] { schema };
	}

	@Override
	public void decodeData(boolean autoClosed) throws DecodeException, IOException {
		initDecodeHints();

		radialDataBlock = new RadialDataBlock();
		radialDataBlock.builder(this.cinradXHeader.getRandomAccessFile(), -1);

		if (autoClosed) {
			this.cinradXHeader.getRandomAccessFile().close();
		}

	}

	private void initDecodeHints() {
		if (configuration.containsKey(GEOMETRY_BUFFER)) {
			geometryBuffer = configuration.getDouble(GEOMETRY_BUFFER);
		}
		if (configuration.containsKey(GEOMETRY_SIMPLIFY)) {
			geometryBuffer = configuration.getDouble(GEOMETRY_SIMPLIFY);
		}
		if (configuration.containsKey(RADIAL_MIN_VALUE)) {
			filter.setMinValue(configuration.getDouble(RADIAL_MIN_VALUE));
		}
		if (configuration.containsKey(RADIAL_MAX_VALUE)) {
			filter.setMaxValue(configuration.getDouble(RADIAL_MAX_VALUE));
		}
		if (configuration.containsKey(MIN_AZIMUTH) && configuration.containsKey(MAX_AZIMUTH)) {
			filter.setAzimuthRange(configuration.getDouble(MIN_AZIMUTH), configuration.getDouble(MAX_AZIMUTH));
		}
	}

	@Override
	public void setDecodeHint(String hintKey, Object hintValue) throws DecodeHintNotSupportedException {
		configuration.addProperty(hintKey, hintValue);

	}

	@Override
	public SimpleFeatureCollection getFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Envelope getCinradXExtent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MathTransform getMathTransform() {
		// TODO Auto-generated method stub
		return null;
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
	public RadialDataBlock getRadialDataBlock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICinradXHeader getICinradXHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
