package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.operation.TransformException;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;

public interface IBaseDecoder {
	
	public static final String GEOMETRY_BUFFER = "geometry.buffer";
	public static final String GEOMETRY_SIMPLIFY = "geometry.simplify";

	public static final String MIN_VALUE = "minValue";
	public static final String MAX_VALUE = "maxValue";

	public static final String MIN_AZIMUTH = "minAzimuth";
	public static final String MAX_AZIMUTH = "maxAzimuth";

	public static final String REDUCE_POLYGONS = "reducePolygons";

	public static final String GEOMETRY_FACTORY_SRID = "factory.srid";

	public static final String GEOMETRY_FACTORY_PRECISION = "factory.precision";

	public static final String CRS_TARGET = "crs.target";

	public static final String COLOR_MODE = "color.mode";

	public static final String MULTIPOLYGON_MODE = "multiPolygon.mode";
	
	

	/**
	 * The FeatureTypes or 'schemas' used for this decoder. This represents the
	 * geometry of, and the attributes present in the features. Most of the time
	 * this will return one FeatureType, but the support is there for multiple.
	 * It is up to the user to determine in their StreamingProcess classes how
	 * to handle different FeatureTypes.
	 */
	public SimpleFeatureType[] getFeatureTypes();

	/**
	 * Decode the data
	 */
	public void decodeData(boolean autoClosed) throws DecodeException, IOException, TransformException;

	public Configuration getDecodeHintsConfig();

	public void setDecodeHintsConfig(Configuration conf);

	public void setDecodeHint(String hintKey, Object hintValue) throws DecodeHintNotSupportedException;

	public void close();

}
