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
 * Decodes NME NEXRAD Level-III legacy Mesocyclone alphanumeric product.
 * 
 * From 2620003J.pdf 20.1:
 * 
 * "The Mesocyclone (M) product shall provide information about identified shear
 * and mesocyclone features. This product shall be generated from the output of
 * the Legacy Mesocyclone Detection Algorithm. This product shall be generated
 * in a format that can be used to generate an alphanumeric tabular display for
 * an identified feature or all simultaneously, a graphic display or a graphic
 * overlay to other products. This product shall be updated once per volume scan
 * time. If on a particular volume scan there is no output from the Legacy
 * Mesocyclone Detection Algorithm (i.e., no features of any type are
 * identified), a version of the product shall be produced that exhibits the
 * negative condition. This product shall include annotations for the product
 * name, radar ID, date and time of volume scan, radar position, radar elevation
 * above MSL, and radar operational mode. Upon user request, all site adaptable
 * parameters identified as input to the algorithm(s) used to generate data for
 * this product shall be available at the alphanumeric display."
 * 
 * 
 */
public class DecodeMeso extends BaseCindarDecoder {

	private final Logger logger = LoggerFactory.getLogger(DecodeMeso.class);

	private MathTransform cinradTransform;

	public DecodeMeso(CinradHeader header) throws DecodeException, IOException,
			FactoryException {
		super(header);
		this.decoderName = "DecodeMeso";
	}

	@Override
	public String getDefaultSymbol() {
		return org.geotools.styling.StyleBuilder.MARK_CIRCLE;
	}

	@Override
	public void decodeData(StreamingProcess[] processArray, boolean autoClose)
			throws DecodeException, IOException {
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
			builder.setName("Mesocyclone Data");
			builder.add("geom", Point.class);
			builder.nillable(true).length(5).add("wsrid", Integer.class);
			builder.nillable(true).length(15).add("datetime", String.class);
			builder.nillable(true).length(10).add("lat", Double.class);
			builder.nillable(true).length(10).add("lon", Double.class);
			builder.nillable(true).length(3).add("id", String.class);
			builder.nillable(true).length(5).add("type", String.class);
			builder.nillable(true).length(7).add("range", Double.class);
			builder.nillable(true).length(7).add("azim", Double.class);
			builder.nillable(true).length(5).add("basehgt", String.class);
			builder.nillable(true).length(5).add("tophgt", String.class);
			builder.nillable(true).length(5).add("height", String.class);
			builder.nillable(true).length(5).add("radius", String.class);
			builder.nillable(true).length(5).add("azdia", String.class);
			builder.nillable(true).length(5).add("shear", String.class);

			schema = builder.buildFeatureType();
		}

		// Reset index counter
		int geoIndex = 0;

		// Decode the text blocks (block 2 and 3)
		DecodeAlphaGeneric decoder = new DecodeAlphaGeneric();
		decoder.decode(header);

		logger.info("----------- VERSION: " + header.getVersion()
				+ " ------------ \n");
		logger.info("----------- BLOCK 2 ----------- \n"
				+ decoder.getBlock2Text());
		logger.info("----------- BLOCK 3 ----------- \n"
				+ decoder.getBlock3Text());

		// Build text for block 2 data
		StringBuffer sb = new StringBuffer();

		// Lets make a custom legend for this block
		sb.append("  MESOCYCLONE SUPPLEMENTAL DATA 1\n\n");
		sb.append("  ABBREVIATIONS:\n");
		sb.append("  AZ    = Azimuth Angle From Radar \n");
		sb.append("          (In Degrees where 0 deg = North, 90 = East, 180 = South, etc...)\n");
		sb.append("  RAN   = Range (Distance) From Radar (In Nautical Miles (nmi))\n");
		sb.append("  BASE  = Elevation of Mesocyclone Base (kft)\n");
		sb.append("  TOP   = Elevation of Mesocyclone Top (kft)\n");
		sb.append("  RAD   = Radius of Mesocyclone (nmi)\n");
		sb.append("  AZDIA = Radius of Mesocyclone (nmi)\n\n");

		sb.append(decoder.getBlock2Text());
		supplementalData[0] = sb.toString();
		sb.append("\n\n");

		// Build text for block 3 data
		sb = new StringBuffer();
		sb.append("  MESOCYCLONE SUPPLEMENTAL DATA 2\n\n");
		sb.append(decoder.getBlock3Text());
		sb.append("\n\n");
		supplementalData[1] = sb.toString();

		String block3Text = decoder.getBlock3Text();
		String[] lines = block3Text.split("\n");

		if (lines.length == 0) {
			metaLabelString[0] = "NO MESO PRESENT";
			return;
		}

		if (header.getVersion() > 1.0) {
			throw new DecodeException("UNKNOWN NEXRAD MESO FILE VERSION: "
					+ header.getVersion(), header.getCinradURL());
		}

		for (int n = 0; n < lines.length; n++) {

			String str = lines[n];

			// advance past empty lines
			if (str.trim().length() == 0) {
				continue;
			}

			// 0 1 2 3 4 5 6 7
			// 012345678901234567890123456789012345678901234567890123456789012345678901234567
			// 1 - 28 MESO 0 5.5 25.1 83/ 61 5.5 1.3 2.9 30 (VERSION 0)
			// 1 - Q4 MESO 5.5 22.6 222/ 60 5.5 2.2 2.5 22 (VERSION 1)

			String featureID = null;
			String stormType = null;
			String baseKFT;
			String topKFT;
			String hgt;
			String diamRad;
			String diamAz;
			String shear;

			int type;
			// Check for a string of storm data of various types (changes with
			// date of file)
			if (str.charAt(5) == '-' && str.charAt(46) == '/') {
				type = 0;
				featureID = str.substring(7, 9);
				stormType = str.substring(12, 21);
			} else if (str.charAt(9) == '-' && str.charAt(46) == '/') {
				type = 1;
				featureID = str.substring(12, 14);
				stormType = str.substring(16, 25);
			} else {
				type = -999;
			}

			if (type >= 0) {

				logger.info("ADDING: " + featureID);

				baseKFT = str.substring(26, 32).trim();
				topKFT = str.substring(33, 40).trim();
				double azim = Double.parseDouble(str.substring(43, 46));
				double range = Double.parseDouble(str.substring(47, 50));
				hgt = str.substring(51, 57).trim();
				diamRad = str.substring(58, 64).trim();
				diamAz = str.substring(65, 70).trim();
				shear = str.substring(71, 77).trim();

				// Correct for an azim of 0
				if (azim == 0.0 || azim == 180.0) {
					azim += 0.000001;
				}

				// Convert to Lat/Lon and add to vector list
				double[] srcPts = {
						range * Math.sin(Math.toRadians(azim)) * 1852.0,
						range * Math.cos(Math.toRadians(azim)) * 1852.0 };
				double[] dstPts = new double[2];
				try {
					cinradTransform.transform(srcPts, 0, dstPts, 0, 1);
				} catch (TransformException e1) {
					throw new DecodeException("DECODE EXCEPTION IN MESO FILE",
							header.getCinradURL());
				}

				Coordinate coordinate = new Coordinate(dstPts[0], dstPts[1]);

				try {
					// create the feature
					SimpleFeature feature = SimpleFeatureBuilder.build(schema,
							new Object[] { geoFactory.createPoint(coordinate),
									header.getRadarStationID(), datetime,
									coordinate.y, coordinate.x, featureID,
									stormType, new Double(range),
									new Double(azim), baseKFT, topKFT, hgt,
									diamRad, diamAz, shear }, new Integer(
									geoIndex++).toString());
					// add to collection
					for (int s = 0; s < processArray.length; s++) {
						processArray[s].addFeature(feature);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		makeMetaLabelStrings();

	}

	/**
	 * Description of the Method
	 */
	private void makeMetaLabelStrings() {

		SimpleFeatureIterator fi = features.features();
		if (fi.hasNext()) {
			// only use first and thus strongest reading
			SimpleFeature f = fi.next();
			metaLabelString[0] = "MAX ID: "
					+ f.getAttribute("id").toString().trim();
			metaLabelString[1] = "BASE/TOP: "
					+ f.getAttribute("basehgt").toString().trim() + "/"
					+ f.getAttribute("tophgt").toString().trim() + " (kft)";
			metaLabelString[2] = "SHEAR: "
					+ f.getAttribute("shear").toString().trim() + " (E-3/s)";
		} else {
			metaLabelString[0] = "NO MESO PRESENT";
		}

	}

}
