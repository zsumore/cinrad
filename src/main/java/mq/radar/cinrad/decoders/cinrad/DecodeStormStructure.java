package mq.radar.cinrad.decoders.cinrad;

import java.io.IOException;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.StreamingProcess;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

/**
 * Decodes NSS NEXRAD Level-III Storm Structure alphanumeric product.
 * 
 * From 2620003J.pdf 22.1:
 * 
 * "This product shall provide, for each identified storm cell, information
 * regarding the structure of the storm cell. This product shall be produced
 * from and contain the values that are output by the Storm Cell Centroids
 * Algorithm. This product shall be updated once per volume scan time. This
 * product shall be produced in a tabular alphanumeric format and shall include
 * annotations for the product name, radar ID, time and date of volume scan, and
 * the total number of identified storm cells. Upon user request, all site
 * adaptable parameters identified as inputs to the algorithm(s) used to
 * generate data for this product shall be available at the alphanumeric
 * display."
 * 
 * 
 */
public class DecodeStormStructure extends BaseCindarDecoder {
	private final Logger logger = LoggerFactory
			.getLogger(DecodeStormStructure.class);

	public DecodeStormStructure(CinradHeader header) throws DecodeException,
			IOException, FactoryException {
		super(header);
		this.decoderName = "DecodeStormStructure";
	}

	@Override
	public String getDefaultSymbol() {
		return org.geotools.styling.StyleBuilder.MARK_CIRCLE;
	}

	@Override
	public void decodeData(StreamingProcess[] processArray, boolean autoClose)
			throws DecodeException, IOException {
		MathTransform cinradTransform;
		try {
			cinradTransform = MQProjections.getInstance().getRadarTransform(
					header);
		} catch (FactoryException e1) {
			logger.error("FactoryException", e1);
			throw new DecodeException("PROJECTION TRANSFORM ERROR",
					header.getCinradURL());
		}

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		{
			builder.setCRS(crs);
			builder.setName("Storm Structure Data");
			builder.add("geom", Point.class);
			builder.nillable(true).length(5).add("wsrid", Integer.class);
			builder.nillable(true).length(15).add("datetime", String.class);
			builder.nillable(true).length(10).add("lat", Double.class);
			builder.nillable(true).length(10).add("lon", Double.class);
			builder.nillable(true).length(3).add("id", String.class);
			builder.nillable(true).length(7).add("range", Double.class);
			builder.nillable(true).length(7).add("azim", Double.class);

			builder.nillable(true).length(5).add("basehgt", String.class);
			builder.nillable(true).length(5).add("tophgt", String.class);
			builder.nillable(true).length(5).add("vil", String.class);
			builder.nillable(true).length(5).add("maxref", String.class);
			builder.nillable(true).length(5).add("height", String.class);
			schema = builder.buildFeatureType();
		}

		// Reset GeoData index counter
		int geoIndex = 0;
		// Decode the text blocks (block 2 and 3)
		DecodeAlphaGeneric decoder = new DecodeAlphaGeneric();
		decoder.decode(header);

		logger.info("----------- VERSION: " + header.getVersion()
				+ " ------------ \n");
		logger.info("----------- BLOCK 2 ----------- \n"
				+ decoder.getBlock2Text() + "\n------ END BLOCK 2 ------ \n");
		logger.info("----------- BLOCK 3 ----------- \n"
				+ decoder.getBlock3Text() + "\n------ END BLOCK 3 ------ \n");

		// Build text for block 2 data
		StringBuffer sb = new StringBuffer();
		sb.append("  STORM STRUCTURE SUPPLEMENTAL DATA 1\n\n");
		sb.append("    NOT APPLICABLE\n\n");
		supplementalData[0] = sb.toString();
		sb.append("\n\n");

		// Build text for block 3 data
		sb = new StringBuffer();
		sb.append("  STORM STRUCTURE SUPPLEMENTAL DATA 2\n\n");
		sb.append("  ABBREVIATIONS:\n");
		sb.append("  AZ      = Azimuth Angle From Radar \n");
		sb.append("            (In Degrees where 0 deg = North, 90 = East, 180 = South, etc...)\n");
		sb.append("  RAN     = Range (Distance) From Radar (In Nautical Miles, Max=124)\n");
		sb.append("  BASE    = Elevation of Storm Base (kft)\n");
		sb.append("  TOP     = Elevation of Storm Top (kft)\n");
		sb.append("  VIL     = Vertically Integrated Liquid (kg/m2)\n");
		sb.append("  MAX REF = Max Reflectivity of Storm Cell (dBZ)\n");
		sb.append("  HEIGHT  = Height of Storm (kft)\n\n");

		sb.append(decoder.getBlock3Text());
		sb.append("\n\n");
		supplementalData[1] = sb.toString();

		String block3Text = decoder.getBlock3Text();
		String[] lines = block3Text.split("\n");

		if (lines.length == 0) {
			metaLabelString[0] = "NO STORMS PRESENT";
			return;
		}

		logger.info("FOUND VERSION " + header.getVersion() + " DATA");

		if (header.getVersion() > 1.0) {
			throw new DecodeException(
					"UNKNOWN NEXRAD STORM STRUCTURE FILE VERSION: "
							+ header.getVersion(), header.getCinradURL());
		}

		boolean lineSwitch = false;

		for (int n = 0; n < lines.length; n++) {

			String str = lines[n];
			// advance past empty lines
			if (str.trim().length() == 0) {
				continue;
			}

			logger.info("n=" + n + " STRING DUMP: " + str);

			if (header.getVersion() == 0) {

				if (lines[n].startsWith("  ID X NM Y NM  KFT")) {
					lineSwitch = true;
					continue; // skip to next line
				}
				if (lineSwitch && lines[n].trim().length() > 0
						&& lines[n].trim().length() < 70) {
					lineSwitch = false;
					continue; // skip to next line
				}

				if (lineSwitch && str.trim().length() > 70) {

					// 0 1 2 3 4 5 6 7 8
					// 012345678901234567890123456789012345678901234567890123456789012345678901234567890
					// ------------ VERSION 0 -----------------------------
					// STM CTRD CTRD BASE TOP VOL TILT(DEG) OVH ORI MAXZ HGT MAX
					// SW HGT LOW V
					// ID X NM Y NM KFT KFT NM*3 TOT X Y NM DEG DBZ KFT KT KFT
					// KT
					// 62 148 84 29.2 44.8 4221 65 52 60 -2.0 216 59 29.2 0 29.2
					// 0

					double stormCenterX = Double.parseDouble(str
							.substring(6, 9));
					double stormCenterY = Double.parseDouble(str.substring(11,
							14));
					double range = Math.sqrt(stormCenterX * stormCenterX
							+ stormCenterY * stormCenterY);
					double azim = 90 - Math.toDegrees(Math.sin(stormCenterY
							/ range));

					// Convert from nautical mi to lat/lon
					double[] srcPts = {
							range * Math.sin(Math.toRadians(azim)) * 1852.0,
							range * Math.cos(Math.toRadians(azim)) * 1852.0 };
					double[] dstPts = new double[2];
					try {
						cinradTransform.transform(srcPts, 0, dstPts, 0, 1);
					} catch (TransformException e1) {
						logger.error("FactoryException", e1);
						throw new DecodeException("PROJECTION TRANSFORM ERROR",
								header.getCinradURL());
					}
					Coordinate coordinate = new Coordinate(dstPts[0], dstPts[1]);

					try {
						// logger.info(coords[n]);
						// create the feature
						// AttributeType[] attTypes = {geom, lat, lon, id,
						// range, azim, basehgt, tophgt, vil, maxref, height};

						SimpleFeature feature = SimpleFeatureBuilder.build(
								schema,
								new Object[] {
										geoFactory.createPoint(coordinate),
										header.getRadarStationID(), datetime,
										coordinate.y, coordinate.x,
										str.substring(1, 4).trim(),
										new Double(range), new Double(azim),
										str.substring(15, 19).trim(),
										str.substring(21, 25).trim(), "N/A",
										str.substring(53, 56).trim(),
										str.substring(57, 61).trim() },
								new Integer(geoIndex++).toString());
						// add to collection
						for (int s = 0; s < processArray.length; s++) {
							processArray[s].addFeature(feature);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// 0 1 2 3 4 5 6 7 8
			// 012345678901234567890123456789012345678901234567890123456789012345678901234567890
			// ------------ VERSION 1 -----------------------------
			// STORM AZRAN BASE TOP CELL BASED VIL MAX REF HEIGHT
			// ID DEG/NM KFT KFT KG/M**2 DBZ KFT
			// U4 183/ 18 < 1.4 8.6 17 55 4.9
			else if (header.getVersion() == 1) {
				// if (linecnt > 6 && n < numPages - 3 && str.trim().length() >
				// 0) {
				if (str.charAt(16) == '/'
						&& !str.substring(13, 19).equals("DEG/NM")) {

					// logger.info("FOUND LINE OF VERSION 1 DATA");

					double azim = Double.parseDouble(str.substring(13, 16));
					double range = Double.parseDouble(str.substring(17, 20));

					// Correct for an azim of 0
					if (azim == 0.0 || azim == 180.0 || azim == 360.0) {
						azim += 0.000001;
					}
					// Convert from nautical mi to lat/lon
					double[] srcPts = {
							range * Math.sin(Math.toRadians(azim)) * 1852.0,
							range * Math.cos(Math.toRadians(azim)) * 1852.0 };
					double[] dstPts = new double[2];
					try {
						cinradTransform.transform(srcPts, 0, dstPts, 0, 1);
					} catch (TransformException e1) {
						logger.error("FactoryException", e1);
						throw new DecodeException("PROJECTION TRANSFORM ERROR",
								header.getCinradURL());
					}
					Coordinate coordinate = new Coordinate(dstPts[0], dstPts[1]);

					try {
						// logger.info(coords[n]);
						// create the feature

						SimpleFeature feature = SimpleFeatureBuilder.build(
								schema,
								new Object[] {
										geoFactory.createPoint(coordinate),
										header.getRadarStationID(), datetime,
										coordinate.y, coordinate.x,
										str.substring(5, 7).trim(),
										new Double(range), new Double(azim),
										str.substring(23, 29).trim(),
										str.substring(31, 37).trim(),
										str.substring(46, 49).trim(),
										str.substring(61, 64).trim(),
										str.substring(70, 75).trim() },
								new Integer(geoIndex++).toString());
						// add to collection
						for (int s = 0; s < processArray.length; s++) {
							processArray[s].addFeature(feature);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} else {
				throw new DecodeException(
						"UNKNOWN NEXRAD STORM STRUCTURE FILE VERSION: "
								+ header.getVersion(), header.getCinradURL());
			}

		}
		makeMetaLabelStrings();

	}

	/**
	 * Description of the Method
	 */
	private void makeMetaLabelStrings() {

		SimpleFeatureIterator fi = features.features();
		if (fi.hasNext()) { // only use first and thus strongest reading
			SimpleFeature f = fi.next();
			metaLabelString[0] = "MAX ID: "
					+ f.getAttribute("id").toString().trim();
			metaLabelString[1] = "VIL: "
					+ f.getAttribute("vil").toString().trim() + " KG/M2";
			metaLabelString[2] = "MAX REF: "
					+ f.getAttribute("maxref").toString().trim() + " dBZ";
		} else {
			metaLabelString[0] = "NO STORM STRUC. PRESENT";
		}

	}

}
