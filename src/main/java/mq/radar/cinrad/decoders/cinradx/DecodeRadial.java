package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import mq.radar.cinrad.MQProjections;
import mq.radar.cinrad.MQXFilter;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;

public class DecodeRadial implements IRadialDecoder {

	private final Logger logger = LoggerFactory.getLogger(DecodeRadial.class);

	private IDecodeCinradXHeader decodeCinradXHeader;
	private Configuration configuration;

	private double geometryBuffer = 0.000001;
	private double geometrySimplify = 0.000001;

	private Multimap<Integer, Polygon> polyMultimap = ArrayListMultimap.create();

	protected MQXFilter filter = new MQXFilter();

	private RadialDataBlock radialDataBlock;

	protected SimpleFeatureType schema = null;

	protected DefaultFeatureCollection features = null;

	protected MathTransform cinradTransform = null;

	protected CoordinateReferenceSystem crs = null;

	protected GeometryFactory geoFactory = null;

	private boolean reducePolys = true;

	private int geoIndex;

	public DecodeRadial(IDecodeCinradXHeader decodeHeader) throws ConfigurationException {
		super();
		this.decodeCinradXHeader = decodeHeader;

		configuration = new PropertiesConfiguration(
				getClass().getClassLoader().getResource(CinradXUtils.DEFAULT_DECODE_CONFIG_FILE));

	}

	@Override
	public SimpleFeatureType[] getFeatureTypes() {
		return new SimpleFeatureType[] { schema };
	}

	@Override
	public void decodeData(boolean autoClosed) throws DecodeException, IOException, TransformException {

		try {
			initDecodeHints();
		} catch (FactoryException e) {
			logger.error(e.getMessage());

			return;
		}

		radialDataBlock = new RadialDataBlock();
		radialDataBlock.builder(this.decodeCinradXHeader.getRandomAccessFile(), -1);

		if (autoClosed) {
			this.decodeCinradXHeader.getRandomAccessFile().close();
		}

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setCRS(crs);
		builder.setName("Cinrad-X Radial Data");
		builder.add("geom", Geometry.class);
		builder.add("colorIndex", Integer.class);
		schema = builder.buildFeatureType();

		// Reset index counter
		geoIndex = 0;

		if (features == null)
			features = new DefaultFeatureCollection();
		features.clear();

		double minA = filter.getMinAzimuth();
		double maxA = filter.getMaxAzimuth();

		if (maxA - minA < 360.0) {
			while (minA >= 360.0) {
				minA -= 360.0;
				maxA -= 360.0;
			}
		}

		for (RadialData data : radialDataBlock.getRadialDatas()) {

			if (testInAzimuthRange(data, minA, maxA)) {
				double startAngle = data.getStartAngle();
				double endAngle = startAngle + data.getAngleWidth();

				// double angle1 = 90.0 - startAngle;
				// double angle2 = 90.0 - endAngle;

				if (startAngle < 0) {
					startAngle += 360;
				}
				if (endAngle < 0) {
					endAngle += 360;
				}

				// Add .00000001 to any 0, 90, 180, 270, 360 values to prevent
				// sin
				// or cos error
				if (startAngle == 0.0 || startAngle == 90.0 || startAngle == 180.0 || startAngle == 270.0
						|| startAngle == 360.0) {
					startAngle += 0.00001;
				}
				if (endAngle == 0.0 || endAngle == 90.0 || endAngle == 180.0 || endAngle == 270.0
						|| endAngle == 360.0) {
					endAngle += 0.00001;
				}

				startAngle = Math.toRadians(startAngle);
				endAngle = Math.toRadians(endAngle);

				int startRange = data.getRadialHeader().getStartRange();

				int key;
				float value;

				for (Map.Entry<Integer, Float> entry : data.getDataValueArray().entrySet()) {

					value = entry.getValue();
					if (testValueRange(value)) {
						key = entry.getKey();

						// double[] geoXY;
						double[] albX = new double[4];

						double[] albY = new double[4];

						int length1 = startRange + key * data.getRadialHeader().getResolution();
						albX[0] = length1 * Math.sin(startAngle);
						albY[0] = length1 * Math.cos(startAngle);
						albX[1] = length1 * Math.sin(endAngle);
						albY[1] = length1 * Math.cos(endAngle);

						int length2 = length1 + data.getRadialHeader().getResolution();
						albX[2] = length2 * Math.sin(endAngle);
						albY[2] = length2 * Math.cos(endAngle);
						albX[3] = length2 * Math.sin(startAngle);
						albY[3] = length2 * Math.cos(startAngle);

						Coordinate[] cArray = new Coordinate[5];
						// Add the first point
						double[] srcPts0 = { albX[0], albY[0] };
						double[] dstPts0 = new double[2];

						cinradTransform.transform(srcPts0, 0, dstPts0, 0, 1);
						cArray[0] = new Coordinate(dstPts0[0], dstPts0[1]);
						for (int nr = 1; nr < albX.length; nr++) {
							double[] srcPts = { albX[nr], albY[nr] };
							double[] dstPts = new double[2];

							cinradTransform.transform(srcPts, 0, dstPts, 0, 1);

							cArray[nr] = new Coordinate(dstPts[0], dstPts[1]);
						}

						// Add the first point again to close polygon
						cArray[4] = new Coordinate(dstPts0[0], dstPts0[1]);

						LinearRing lr = geoFactory.createLinearRing(cArray);
						Polygon poly = JTSUtilities.makeGoodShapePolygon(geoFactory.createPolygon(lr, null));

						

						//System.out.println("value:" + entry.getValue());

						polyMultimap.put(CinradXUtils.getRadialColorIndex(entry.getValue()), poly);
					}

				}

			}

		}

		if (reducePolys) {
			logger.debug("REDUCING POLYGONS!");

			GeometryCollection[] polyCollections = new GeometryCollection[16];
			for (int i = 0; i < 16; i++) {
				if (polyMultimap.get(i).size() > 0) {
					Polygon[] polyArray = new Polygon[polyMultimap.get(i).size()];
					polyCollections[i] = geoFactory
							.createGeometryCollection((Polygon[]) (polyMultimap.get(i).toArray(polyArray)));
					Geometry union = polyCollections[i].buffer(geometryBuffer);

					union = TopologyPreservingSimplifier.simplify(union, geometrySimplify);

					logger.debug("Geometry Type:" + union.getGeometryType());

					polyCollections[i] = null;

					polyMultimap.get(i).clear();
					// Geometry union = (Geometry)polyCollections[i];

					Integer color = new Integer(i);

					// create the feature
					SimpleFeature feature = SimpleFeatureBuilder.build(schema, new Object[] { (Geometry) union, color },
							new Integer(geoIndex++).toString());

					features.add(feature);
				}

			}

		} else {

			for (int i = 0; i < 16; i++) {
				if (polyMultimap.get(i).size() > 0) {

					for (Polygon poly : polyMultimap.get(i)) {
						Integer color = new Integer(i);

						// create the feature
						SimpleFeature feature = SimpleFeatureBuilder.build(schema,
								new Object[] { (Geometry) poly, color }, new Integer(geoIndex++).toString());

						features.add(feature);

					}

				}
			}

		}

	}

	private boolean testValueRange(float value) {
		if (value >= filter.getMinValue() && value <= filter.getMaxValue())
			return true;
		return false;
	}

	private boolean testInAzimuthRange(RadialData data, double minA, double maxA) {

		if (maxA - minA >= 360.0) {
			return true;
		}

		if (data.getStartAngle() >= minA && data.getStartAngle() <= maxA) {

			return true;
		}

		float endAngle = data.getStartAngle() + data.getAngleWidth();
		if (endAngle >= 360.0)
			endAngle -= 360.0;

		if (endAngle >= minA && endAngle <= maxA) {
			return true;
		}

		if (data.getStartAngle() < minA && maxA - data.getStartAngle() > 360.0) {
			return true;
		}

		if (endAngle < minA && maxA - endAngle > 360.0) {
			return true;
		}

		return false;
	}

	private void initDecodeHints() throws FactoryException {
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

		reducePolys = configuration.getBoolean(REDUCE_POLYGONS, true);

		crs = MQProjections.getInstance().getCoordinateSystemByProjectionType(
				CinradXUtils.getProjectionByName(configuration.getString(CRS_TARGET, "WGS84").trim()),
				getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLongitude(),
				getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLatitude());

		geoFactory = new GeometryFactory(new PrecisionModel(configuration.getInt(GEOMETRY_FACTORY_PRECISION, 1000000)),
				configuration.getInt(GEOMETRY_FACTORY_SRID, 0));

		cinradTransform = CRS.findMathTransform(MQProjections.getInstance().getCoordinateSystemByProjectionType(
				CinradXUtils.getProjectionByType(getICinradXHeader().getProductHeader().getProjectionType()),
				getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLongitude(),
				getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLatitude()), crs);

	}

	@Override
	public void setDecodeHint(String hintKey, Object hintValue) throws DecodeHintNotSupportedException {
		configuration.addProperty(hintKey, hintValue);

	}

	@Override
	public SimpleFeatureCollection getFeatures() {

		return features;
	}

	@Override
	public MathTransform getMathTransform() {

		return cinradTransform;
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

		return radialDataBlock;
	}

	@Override
	public ICinradXHeader getICinradXHeader() {

		return this.decodeCinradXHeader.getICinradXHeader();
	}

}
