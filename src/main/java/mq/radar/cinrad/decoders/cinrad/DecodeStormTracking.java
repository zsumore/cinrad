package mq.radar.cinrad.decoders.cinrad;

import java.io.IOException;
import java.util.Vector;

import mq.radar.cinrad.MQProjections;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.StreamingProcess;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Decodes NST NEXRAD Level-III Storm Tracking alphanumeric product.
 * 
 * From 2620003J.pdf 18.1:
 * 
 * "This product shall provide information concerning the past, present and
 * future positions of each identified storm cell. This product shall be
 * generated from the output of the Storm Cell Tracking and Storm Position
 * Forecast algorithms. It shall be produced in a tabular format of alphanumeric
 * values, as a stand alone graphic product, and in a format for generating
 * graphic overlays to other products. This product shall be updated once per
 * volume scan time. Each product shall include a standard set of total
 * annotations and number of identified storm cells for which tracking is
 * available. Upon user request, all site adaptable parameters identified as
 * inputs to the algorithm(s) used to generate data for this product shall be
 * available at the alphanumeric display."
 * 
 */
public class DecodeStormTracking extends BaseCindarDecoder {
	private final Logger logger = LoggerFactory
			.getLogger(DecodeStormTracking.class);

	private String numStorms;
	private String avgStormSpeed;
	private String avgStormDirection;

	/**
	 * Constructor
	 * 
	 * @param header
	 *            Description of the Parameter
	 * @throws IOException
	 * @throws FactoryException
	 */
	public DecodeStormTracking(CinradHeader header) throws DecodeException,
			IOException, FactoryException {
		super(header);
		this.decoderName = "DecodeStormTracking";
	}

	@Override
	public void decodeData(StreamingProcess[] processArray, boolean autoClose)
			throws DecodeException, IOException {

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		SimpleFeatureTypeBuilder lineBuilder = new SimpleFeatureTypeBuilder();

		{
			builder.setCRS(crs);
			builder.setName("Storm Tracking Point Data");
			builder.add("geom", Point.class);
			builder.nillable(true).length(5).add("wsrid", Integer.class);
			builder.nillable(true).length(15).add("datetime", String.class);
			builder.nillable(true).length(10).add("lat", Double.class);
			builder.nillable(true).length(10).add("lon", Double.class);
			builder.nillable(true).length(3).add("id", String.class);
			builder.nillable(true).length(5).add("time", Integer.class);
			builder.nillable(true).length(7).add("range", Double.class);
			builder.nillable(true).length(7).add("azim", Double.class);
			builder.nillable(true).length(5).add("movedeg", String.class);
			builder.nillable(true).length(5).add("movekts", String.class);
			schema = builder.buildFeatureType();
		}
		{
			lineBuilder.setCRS(crs);
			lineBuilder.setName("Storm Tracking Line Data");
			lineBuilder.add("geom", LineString.class);
			lineBuilder.nillable(true).length(5).add("wsrid", Integer.class);
			lineBuilder.nillable(true).length(15).add("datetime", String.class);
			lineBuilder.nillable(true).length(10).add("lat", Double.class);
			lineBuilder.nillable(true).length(10).add("lon", Double.class);
			lineBuilder.nillable(true).length(3).add("id", String.class);
			lineBuilder.nillable(true).length(5).add("time", Integer.class);
			lineBuilder.nillable(true).length(7).add("range", Double.class);
			lineBuilder.nillable(true).length(7).add("azim", Double.class);
			lineBuilder.nillable(true).length(5).add("movedeg", String.class);
			lineBuilder.nillable(true).length(5).add("movekts", String.class);
			lineSchema = lineBuilder.buildFeatureType();
		}

		// Decode the text blocks (block 2 and 3)
		DecodeAlphaGeneric decoder = new DecodeAlphaGeneric();
		// decoder.setVerbose(verbose);
		decoder.decode(header);

		logger.info("----------- VERSION: " + header.getVersion()
				+ " ------------ \n");
		logger.info("----------- BLOCK 2 ----------- \n"
				+ decoder.getBlock2Text() + "\n------ END BLOCK 2 ------ \n");
		logger.info("----------- BLOCK 3 ----------- \n"
				+ decoder.getBlock3Text() + "\n------ END BLOCK 3 ------ \n");

		StringBuffer sb = new StringBuffer();

		// Lets make a custom legend for this block
		sb.append("  STORM TRACKING SUPPLEMENTAL DATA 1\n\n");
		sb.append("  ABBREVIATIONS:\n");
		sb.append("  AZ       = Azimuth Angle From Radar \n");
		sb.append("             (In Degrees where 0 deg = North, 90 = East, 180 = South, etc...)\n");
		sb.append("  RAN      = Range (Distance) From Radar (In Nautical Miles (nmi))\n");
		sb.append("  FCST MVT = Predicted Movement Direction/Speed (deg/kts)\n");
		sb.append("  ERR      = Forecast Error (nmi)\n");
		sb.append("  MEAN     = Mean Error (nmi)\n");
		sb.append("  DBZM     = Max Reflectivity of Storm Cell (dBZ)\n\n");

		sb.append(decoder.getBlock2Text());
		supplementalData[0] = sb.toString();
		sb.append("\n\n");

		// Build text for block 3 data
		sb = new StringBuffer();
		sb.append("  STORM TRACKING SUPPLEMENTAL DATA 2\n\n");
		// sb.append("    NOT APPLICABLE\n\n");
		sb.append(decoder.getBlock3Text());
		sb.append("\n\n");
		supplementalData[1] = sb.toString();

		// Convert the text to features
		try {
			convertSupplementalDataToFeatures(decoder.getBlock3Text());
		} catch (IllegalAttributeException e) {
			logger.error("IllegalAttributeException:", e);
			e.printStackTrace();
		} catch (TransformException e) {
			logger.error("TransformException:", e);
			e.printStackTrace();
		}

		makeMetaLabelStrings();
		// END try

	}

	@Override
	public String getDefaultSymbol() {
		return org.geotools.styling.StyleBuilder.MARK_CROSS;
	}

	/**
	 * Description of the Method
	 */
	@SuppressWarnings("unused")
	private void makeMetaLabelStrings() {

		SimpleFeatureIterator fi = features.features();
		if (fi.hasNext()) {
			// only use first and thus strongest reading
			SimpleFeature f = fi.next();
			metaLabelString[0] = numStorms + " STORM CELLS";
			metaLabelString[1] = "AVG SPEED: " + avgStormSpeed + " KTS";
			metaLabelString[2] = "AVG DIRECTION: " + avgStormDirection + " DEG";
		} else {
			metaLabelString[0] = "NO STORM CELLS PRESENT";
		}

	}

	@SuppressWarnings("unused")
	private void convertSupplementalDataToFeatures(String block3Text)
			throws DecodeException, TransformException,
			IllegalAttributeException {

		double[] azim = new double[5];
		double[] range = new double[5];
		int linenum = 0;
		int lineIndex = 0;
		String movedeg;
		String movekts;
		Vector<Coordinate> pointVector = new Vector<Coordinate>();
		int geoIndex = 0;

		MathTransform cinradTransform;
		try {
			cinradTransform = MQProjections.getInstance().getRadarTransform(
					header);
		} catch (FactoryException e1) {
			logger.error("FactoryException", e1);
			throw new DecodeException("PROJECTION TRANSFORM ERROR",
					header.getCinradURL());
		}

		String[] lines = block3Text.split("\n");

		if (lines.length == 0) {
			metaLabelString[0] = "NO STORMS PRESENT";
			return;
		}

		logger.info("FOUND VERSION " + header.getVersion() + " DATA");

		// FOR NOW, WE ARE NOT SUPPORTED THE VERSION 0 STORM TRACKING PRODUCT
		if (header.getVersion() != 1.0) {
			throw new DecodeException(
					"UNSUPPORTED NEXRAD STORM TRACKING FILE VERSION: "
							+ header.getVersion()
							+ ".  CURRENTLY ONLY VERSION 0 IS SUPPORTED.",
					header.getCinradURL());
		}

		for (int x = 0; x < lines.length; x++) {

			String str = lines[x];
			// advance past empty lines
			if (str.trim().length() == 0) {
				continue;
			}

			// if (str.startsWith("PAGE ")) {
			// //0123456
			// // PAGE 1
			// pageNum = Integer.parseInt(str.substring(5, 6));
			// }

			logger.info(linenum + " ::: " + str);
			// System.out.println("header vsesion:" + header.getVersion());
			if (header.getVersion() == 0) {

				// FOR NOW, WE ARE NOT SUPPORTED THE VERSION 0 STORM TRACKING
				// PRODUCT

				if (str.trim().startsWith("RADAR ID")) {
					numStorms = str.substring(74, 76);
					avgStormSpeed = "N/A";
					avgStormDirection = "N/A";
				}

				// ----------------- VERSION 0 ---------------------
				// RADAR ID: 313 DATE/TIME 06:09:95/00:02:20 NUMBER OF STORMS 5
				// CURRENT POSITION SPEED FORECAST POSITIONS FORCAST TRACKVAR
				// STM AZRAN MOVEMENT X/Y 15 MIN 30 MIN 45 MIN 60 MIN ERR/MEAN
				// X/Y
				// ID (DEG-NM) (DEG-KT) (KT) (X/Y) (X/Y) (X/Y) (X/Y) (NM) (NM)
				// 0 1 2 3 4 5 6 7 8
				// 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345
				// 28 74/ 63 198/ 15 5 62 63 64 65 1.2/ 0.9 3.6
				// 15 21 25 28 32 0.5
				// 63 56/139 198/ 19 6 NO DAT NO DAT NO DAT NO DAT 0.0/ 0.0 0.0
				// 18 NO DAT NO DAT NO DAT NO DAT 0.0

				if (str.charAt(9) == '/' && str.charAt(18) == '/') {
					// logger.info(hitCount+" "+new String(data));

					// Get next line
					String str2 = lines[++x];

					// Do current position
					azim[0] = Double.parseDouble(str.substring(6, 9));
					range[0] = Double.parseDouble(str.substring(10, 13));
					if (str.substring(21, 24).equals("NEW")) {
						movedeg = "NEW";
						movekts = "NEW";
					} else {
						movedeg = str.substring(19, 22);
						movekts = str.substring(23, 26);
					}

					if (str.substring(31, 38).equals("NO DATA")) {
						azim[1] = -999.9;
						range[1] = -999.9;
					} else {
						azim[1] = Double.parseDouble(str.substring(31, 34));
						range[1] = Double.parseDouble(str.substring(35, 38));
					}

					if (str.substring(41, 48).equals("NO DATA")) {
						azim[2] = -999.9;
						range[2] = -999.9;
					} else {
						azim[2] = Double.parseDouble(str.substring(41, 44));
						range[2] = Double.parseDouble(str.substring(45, 48));
					}

					if (str.substring(51, 58).equals("NO DATA")) {
						azim[3] = -999.9;
						range[3] = -999.9;
					} else {
						azim[3] = Double.parseDouble(str.substring(51, 54));
						range[3] = Double.parseDouble(str.substring(55, 58));
					}

					if (str.substring(61, 68).equals("NO DATA")) {
						azim[4] = -999.9;
						range[4] = -999.9;
					} else {
						azim[4] = Double.parseDouble(str.substring(61, 64));
						range[4] = Double.parseDouble(str.substring(65, 68));
					}

					String id = str.substring(2, 4);
					// Create point features
					pointVector.clear();
					for (int n = 0; n < 5; n++) {

						if (azim[n] != -999.9 && range[n] != -999.9) {

							// Correct for an azim of 0
							if (azim[n] == 0.0 || azim[n] == 180.0
									|| azim[n] == 360.0) {
								azim[n] += 0.000001;
							}
							// Convert from nautical mi to meters

							double[] srcPts = {
									range[n]
											* Math.sin(Math.toRadians(azim[n]))
											* 1852.0,
									range[n]
											* Math.cos(Math.toRadians(azim[n]))
											* 1852.0 };
							double[] dstPts = new double[2];
							try {
								cinradTransform.transform(srcPts, 0, dstPts, 0,
										1);
							} catch (TransformException e1) {
								logger.error("FactoryException", e1);
								throw new DecodeException(
										"PROJECTION TRANSFORM ERROR",
										header.getCinradURL());
							}
							Coordinate coordinate = new Coordinate(dstPts[0],
									dstPts[1]);

							pointVector.addElement(coordinate);

							// create the feature

							SimpleFeature feature = SimpleFeatureBuilder.build(
									schema,
									new Object[] {
											geoFactory.createPoint(pointVector
													.elementAt(n)),
											header.getRadarStationID(),
											datetime, coordinate.y,
											coordinate.x, id.trim(),
											new Integer(n * 15),
											new Double(range[n]),
											new Double(azim[n]),
											movedeg.trim(), movekts.trim() },
									new Integer(geoIndex++).toString());
							// add to collection
							features.add(feature);

						}
					}
					// Create Line Feature
					if (pointVector.size() > 1) {
						Coordinate[] lineCoords = new Coordinate[pointVector
								.size()];
						SimpleFeature feature = SimpleFeatureBuilder
								.build(lineSchema,
										new Object[] {
												geoFactory
														.createLineString((Coordinate[]) (pointVector
																.toArray(lineCoords))),
												header.getRadarStationID(),
												datetime,
												pointVector.elementAt(0).y,
												pointVector.elementAt(0).x,
												id.trim(), new Integer(-1),
												new Double(range[0]),
												new Double(azim[0]),
												movedeg.trim(), movekts.trim() },
										new Integer(lineIndex++).toString());

						// add to collection
						lineFeatures.add(feature);
					}
				}

			} else if (header.getVersion() == 1) {

				if (str.trim().startsWith("RADAR ID")) {
					numStorms = str.substring(70, str.length()).trim();
				}
				if (str.trim().startsWith("AVG SPEED")) {
					avgStormSpeed = str.substring(28, 32).trim();
					avgStormDirection = str.substring(52, 57).trim();
				}

				// ----------------- VERSION 1 ---------------------
				// RADAR ID 340 DATE/TIME 11:10:02/22:41:34 NUMBER OF STORM
				// CELLS 54
				// AVG SPEED 43 KTS AVG DIRECTION 223 DEG
				// ID AZRAN MOVEMENT 15 MIN 30 MIN 45 MIN 60 MIN FCST/MEAN
				// 0 1 2 3 4 5 6 7 8
				// 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345
				// U6 235/ 70 240/ 45 234/ 59 233/ 48 231/ 37 226/ 25 0.9/ 0.8
				// P3 257/ 41 243/ 46 262/ 30 NO DATA NO DATA NO DATA 4.0/ 1.2

				if (str.charAt(2) != ' ' && str.charAt(12) == '/') {
					// logger.info(hitCount+" "+new String(data));

					// Do current position
					azim[0] = Double.parseDouble(str.substring(9, 12));
					range[0] = Double.parseDouble(str.substring(13, 16));
					if (str.substring(21, 24).equals("NEW")) {
						movedeg = "NEW";
						movekts = "NEW";
					} else {
						movedeg = str.substring(19, 22);
						movekts = str.substring(23, 26);
					}

					if (str.substring(31, 38).equals("NO DATA")) {
						azim[1] = -999.9;
						range[1] = -999.9;
					} else {
						azim[1] = Double.parseDouble(str.substring(31, 34));
						range[1] = Double.parseDouble(str.substring(35, 38));
					}

					if (str.substring(41, 48).equals("NO DATA")) {
						azim[2] = -999.9;
						range[2] = -999.9;
					} else {
						azim[2] = Double.parseDouble(str.substring(41, 44));
						range[2] = Double.parseDouble(str.substring(45, 48));
					}

					if (str.substring(51, 58).equals("NO DATA")) {
						azim[3] = -999.9;
						range[3] = -999.9;
					} else {
						azim[3] = Double.parseDouble(str.substring(51, 54));
						range[3] = Double.parseDouble(str.substring(55, 58));
					}

					if (str.substring(61, 68).equals("NO DATA")) {
						azim[4] = -999.9;
						range[4] = -999.9;
					} else {
						azim[4] = Double.parseDouble(str.substring(61, 64));
						range[4] = Double.parseDouble(str.substring(65, 68));
					}

					String id = str.substring(2, 4);

					// Create point features
					pointVector.clear();
					for (int n = 0; n < 5; n++) {

						if (azim[n] != -999.9 && range[n] != -999.9) {

							// Correct for an azim of 0
							if (azim[n] == 0.0 || azim[n] == 180.0
									|| azim[n] == 360.0) {
								azim[n] += 0.000001;
							}
							// Convert from nautical mi to meters

							double[] srcPts = {
									range[n]
											* Math.sin(Math.toRadians(azim[n]))
											* 1852.0,
									range[n]
											* Math.cos(Math.toRadians(azim[n]))
											* 1852.0 };
							double[] dstPts = new double[2];
							try {
								cinradTransform.transform(srcPts, 0, dstPts, 0,
										1);
							} catch (TransformException e1) {
								logger.error("FactoryException", e1);
								throw new DecodeException(
										"PROJECTION TRANSFORM ERROR",
										header.getCinradURL());
							}
							Coordinate coordinate = new Coordinate(dstPts[0],
									dstPts[1]);

							pointVector.addElement(coordinate);
							// create the feature
							SimpleFeature feature = SimpleFeatureBuilder.build(
									schema,
									new Object[] {
											geoFactory.createPoint(pointVector
													.elementAt(n)),
											header.getRadarStationID(),
											datetime, coordinate.y,
											coordinate.x, id.trim(),
											new Integer(n * 15),
											new Double(range[n]),
											new Double(azim[n]),
											movedeg.trim(), movekts.trim() },
									new Integer(geoIndex++).toString());

							// add to collection
							features.add(feature);

						}
					}
					// Create Line Feature
					if (pointVector.size() > 1) {
						Coordinate[] lineCoords = new Coordinate[pointVector
								.size()];

						SimpleFeature feature = SimpleFeatureBuilder
								.build(lineSchema,
										new Object[] {
												geoFactory
														.createLineString((Coordinate[]) (pointVector
																.toArray(lineCoords))),
												header.getRadarStationID(),
												datetime,
												pointVector.elementAt(0).y,
												pointVector.elementAt(0).x,
												id.trim(), new Integer(-1),
												new Double(range[0]),
												new Double(azim[0]),
												movedeg.trim(), movekts.trim() },
										new Integer(lineIndex++).toString());

						// add to collection
						lineFeatures.add(feature);
					}
				}

				linenum++;

			}

		}
	}

}
