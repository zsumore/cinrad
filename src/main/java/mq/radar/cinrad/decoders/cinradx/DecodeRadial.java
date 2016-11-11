package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

import mq.radar.cinrad.decoders.DecodeException;

public class DecodeRadial extends BaseDecoder {

	private final Logger logger = LoggerFactory.getLogger(DecodeRadial.class);

	private Multimap<Float, Polygon> polyMultimap;

	private RadialDataBlock radialDataBlock;

	private int geoIndex = 0;

	public DecodeRadial(IDecodeCinradXHeader decodeHeader) throws ConfigurationException {
		super(decodeHeader);

	}

	@Override
	public void decodeData(boolean autoClosed) throws DecodeException, IOException, TransformException {
		super.decodeData(autoClosed);

		polyMultimap = ArrayListMultimap.create();

		radialDataBlock = new RadialDataBlock();
		radialDataBlock.builder(this.decodeCinradXHeader.getRandomAccessFile(), -1);

		if (autoClosed) {
			this.decodeCinradXHeader.getRandomAccessFile().close();
		}

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setCRS(crs);
		builder.setName("Cinrad-X Radial Data");
		builder.add("geom", Geometry.class);
		builder.add("colorIndex", Float.class);
		builder.add("value", Float.class);
		schema = builder.buildFeatureType();

		// Reset index counter
		geoIndex = 0;

		if (getPlaneFeatures() == null) {
			planeFeatures = new DefaultFeatureCollection();
		}
		planeFeatures.clear();

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

						// System.out.println("value:" + entry.getValue());

						if (configuration.getBoolean(COLOR_MODE, true)) {

							polyMultimap.put(CinradXUtils.getRadialColorIndex(entry.getValue()) * 1.0f, poly);
						} else {
							polyMultimap.put(entry.getValue(), poly);
						}
					}

				}

			}

		}

		Set<Float> valueSet = polyMultimap.keySet();
		// System.out.println(valueSet.size());
		if (valueSet.size() > 0) {
			for (Float v : valueSet) {

				Float color = new Float(v);

				Float value = color;
				if (configuration.getBoolean(COLOR_MODE, true)) {
					value = color * 5;
				}

				if (configuration.getBoolean(REDUCE_POLYGONS, true)) {
					logger.debug("REDUCING POLYGONS!");

					if (polyMultimap.get(v).size() > 0) {
						Polygon[] polyArray = new Polygon[polyMultimap.get(v).size()];

						GeometryCollection polyCollection = geoFactory
								.createGeometryCollection(polyMultimap.get(v).toArray(polyArray));

						Geometry union = polyCollection.buffer(geometryBuffer);

						union = TopologyPreservingSimplifier.simplify(union, geometrySimplify);

						logger.debug("Geometry Type:" + union.getGeometryType());

						// polyMultimap.get(v).clear();

						if (union.getGeometryType().equalsIgnoreCase("MultiPolygon")) {

							// logger.debug(union.toString());
							if (configuration.getBoolean(MULTIPOLYGON_MODE, true)) {
								SimpleFeature feature = SimpleFeatureBuilder.build(schema,
										new Object[] { union, color, value }, new Integer(geoIndex++).toString());

								planeFeatures.add(feature);
							} else {

								MultiPolygon multiPolygon = (MultiPolygon) union;
								for (int j = 0; j < multiPolygon.getNumGeometries(); j++) {

									// create the feature
									SimpleFeature feature = SimpleFeatureBuilder.build(schema,
											new Object[] { (Geometry) multiPolygon.getGeometryN(j), color, value },
											new Integer(geoIndex++).toString());

									planeFeatures.add(feature);

									// logger.debug(feature.toString());

								}
							}

						} else if (union.getGeometryType().equalsIgnoreCase("Polygon")) {
							if (configuration.getBoolean(MULTIPOLYGON_MODE, true)) {
								// create the feature
								Polygon[] pa = { (Polygon) union };
								SimpleFeature feature = SimpleFeatureBuilder.build(schema,
										new Object[] { (Geometry) new MultiPolygon(pa, geoFactory), color, value },
										new Integer(geoIndex++).toString());

								planeFeatures.add(feature);

							} else {

								// create the feature
								SimpleFeature feature = SimpleFeatureBuilder.build(schema,
										new Object[] { (Geometry) union, color, value },
										new Integer(geoIndex++).toString());

								planeFeatures.add(feature);
							}

							// logger.debug(feature.toString());
						}

					}

				}

				else {

					for (Polygon poly : polyMultimap.get(v)) {

						SimpleFeature feature = SimpleFeatureBuilder.build(schema, new Object[] { poly, color, value },
								new Integer(geoIndex++).toString());

						planeFeatures.add(feature);

						// logger.debug(feature.toString());
					}

				}
			}
		}

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

	public RadialDataBlock getRadialDataBlock() {

		return radialDataBlock;
	}

	@Override
	public void close() {
		super.close();

		if (null != polyMultimap) {
			polyMultimap.clear();
		}

		radialDataBlock = null;

	}

}
