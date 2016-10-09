package mq.radar.cinrad.decoders.cinradx;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.referencing.operation.MathTransform;

public interface IRadialDecoder extends IBaseDecoder {

	public static final String GEOMETRY_BUFFER = "geometry.buffer";
	public static final String GEOMETRY_SIMPLIFY = "geometry.simplify";

	public static final String RADIAL_MIN_VALUE = "radial.minValue";
	public static final String RADIAL_MAX_VALUE = "radial.maxValue";

	public static final String MIN_AZIMUTH = "minAzimuth";
	public static final String MAX_AZIMUTH = "maxAzimuth";

	public static final String REDUCE_POLYGONS = "reducePolygons";

	public static final String GEOMETRY_FACTORY_SRID = "factory.srid";

	public static final String GEOMETRY_FACTORY_PRECISION = "factory.precision";

	public static final String CRS_TARGET = "crs.target";

	/**
	 * Gets the Features stored after the use of 'decodeData()'
	 * 
	 * @return
	 */
	public SimpleFeatureCollection getFeatures();

	// public CoordinateReferenceSystem getCRS();

	public MathTransform getMathTransform();

	RadialDataBlock getRadialDataBlock();

	ICinradXHeader getICinradXHeader();

}
