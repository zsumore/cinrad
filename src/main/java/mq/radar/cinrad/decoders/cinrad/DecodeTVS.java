package mq.radar.cinrad.decoders.cinrad;

import java.io.IOException;

import mq.radar.cinrad.MQProjections;
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
 * Decodes NTV NEXRAD Level-III Tornado Vortex Signature alphanumeric product.
 * 
 * From 2620003J.pdf 21.1:
 * 
 * "This product shall provide information regarding the existence and location
 * of an identified Tornado Vortex Signature (TVS). This product shall be
 * produced from the output of the Tornado Detection Algorithm. The product
 * shall produce an alphanumeric tabular display and a graphic overlay of the
 * algorithm output data for each identified TVS (and Elevated TVS (ETVS))
 * signature information when such is identified. This product shall be updated
 * once per volume scan time. This product shall include annotations for the
 * product name, radar ID, time and date of volume scan, radar position, radar
 * elevation above MSL, and radar operational mode. Upon user request, all site
 * adaptable parameters identified as inputs to the algorithm(s) used to
 * generate data for this product shall be available at the alphanumeric
 * display."
 * 
 */
public class DecodeTVS extends BaseCindarDecoder {
	private final Logger logger = LoggerFactory.getLogger(DecodeTVS.class);

	public DecodeTVS(CinradHeader header) throws DecodeException, IOException,
			FactoryException {
		super(header);
		this.decoderName = "DecodeTVS";
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
			builder.setName("Tornado Vortex Signature Data");
			builder.add("geom", Point.class);
			builder.nillable(true).length(5).add("wsrid", Integer.class);
			builder.nillable(true).length(15).add("datetime", String.class);
			builder.nillable(true).length(10).add("lat", Double.class);
			builder.nillable(true).length(10).add("lon", Double.class);
			builder.nillable(true).length(3).add("id", String.class);
			builder.nillable(true).length(5).add("type", String.class);
			builder.nillable(true).length(7).add("range", Double.class);
			builder.nillable(true).length(7).add("azim", Double.class);
			builder.nillable(true).length(5).add("avgdv", String.class);
			builder.nillable(true).length(5).add("lldv", String.class);
			builder.nillable(true).length(5).add("mxdv", String.class);
			builder.nillable(true).length(5).add("mxdvhgt", String.class);
			builder.nillable(true).length(5).add("depth", String.class);
			builder.nillable(true).length(5).add("base", String.class);
			builder.nillable(true).length(5).add("top", String.class);
			builder.nillable(true).length(5).add("mxshr", String.class);
			builder.nillable(true).length(5).add("mxshrhgt", String.class);
			schema = builder.buildFeatureType();
		}

		// Reset feature index counter
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

		// Lets make a custom legend for block 2
		sb.append("  TVS SUPPLEMENTAL DATA 1\n\n");
		sb.append("  ABBREVIATIONS:\n");
		sb.append("  AZ    = Azimuth Angle From Radar \n");
		sb.append("          (In Degrees where 0 deg = North, 90 = East, 180 = South, etc...)\n");
		sb.append("  RAN   = Range (Distance) From Radar (In Nautical Miles (nmi))\n");
		sb.append("  BASE  = Elevation of TVS Base (kft)\n");
		sb.append("  TOP   = Elevation of TVS Top (kft)\n");
		sb.append("  RAD   = Radius of TVS (nmi)\n");
		sb.append("  AZDIA = Radius of TVS (nmi)\n\n");

		sb.append(decoder.getBlock2Text());
		supplementalData[0] = sb.toString();
		sb.append("\n\n");

		// Build text for block 3 data
		sb = new StringBuffer();
		sb.append("  TVS SUPPLEMENTAL DATA 2\n\n");
		sb.append(decoder.getBlock3Text());
		sb.append("\n\n");
		supplementalData[1] = sb.toString();

		String block3Text = decoder.getBlock3Text();
		String[] lines = block3Text.split("\n");

		if (lines.length == 0) {
			metaLabelString[0] = "NO TVS PRESENT";
			return;
		}

		if (header.getVersion() > 1.0) {
			throw new DecodeException("UNKNOWN NEXRAD TVS FILE VERSION: "
					+ header.getVersion(), header.getCinradURL());
		}

		for (int n = 0; n < lines.length; n++) {

			String str = lines[n];
			// advance past empty lines
			if (str.trim().length() == 0) {
				continue;
			}

			// VERSION 0
			// 0 1 2 3 4 5 6 7
			// 012345678901234567890123456789012345678901234567890123456789012345678901234567
			// TVS MESO STORM BASE HGT AZRAN MAX SHEAR HGT AZRAN SHEAR ORI ROT
			// ID ID ID (KFT) (DEG-NM) (KFT) (DEG-NM) (E-3/S) (DEG) (RAD)

			// 1 1 28 5.7 80/ 65 5.7 80/ 65 35 36.65 .017
			// 012345678901234567890123456789012345678901234567890123456789012345678901234567
			// 0 1 2 3 4 5 6 7

			if (header.getVersion() == 0) {

				if (str.charAt(32) == '/' && str.charAt(55) == '/') {
					// logger.info(hitCount+" "+new String(data));

					double azim = Double.parseDouble(str.substring(29, 32));
					double range = Double.parseDouble(str.substring(33, 36));

					if (azim == 0.0 || azim == 180.0 || azim == 360.0) {
						azim += 0.000001;
					}
					// Convert from nautical mi to meters

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
						// create the feature
						// {geom, wsrid, datetime, lat, lon, id, type, range,
						// azim, avgdv, lldv, mxdv, mxdvhgt, depth, base, top,
						// mxshr, mxshrhgt};

						SimpleFeature feature = SimpleFeatureBuilder.build(
								schema,
								new Object[] {
										geoFactory.createPoint(coordinate),
										header.getRadarStationID(), datetime,
										coordinate.y,
										coordinate.x,
										str.substring(2, 4).trim(), // id
										str.substring(7, 9).trim(), // type
										new Double(range), new Double(azim),
										"N/A", // avgdv not defined in version 0
												// of this product
										"N/A", // lldv not defined in version 0
												// of this product
										"N/A", // mxdv not defined in version 0
												// of this product
										"N/A", // mxdvhgt not defined in version
												// 0 of this product
										"N/A", // depth not defined in version 0
												// of this product
										str.substring(21, 25).trim(), // base
										"N/A", // top not defined in version 0
												// of this product
										str.substring(61, 65).trim(), // mxshr
										str.substring(41, 45).trim() // mxshrhgt
								}, new Integer(geoIndex++).toString());
						// add to collection
						for (int s = 0; s < processArray.length; s++) {
							processArray[s].addFeature(feature);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else {

				// VERSION 1
				// 0 1 2 3 4 5 6 7
				// 012345678901234567890123456789012345678901234567890123456789012345678901234567
				// Feat Storm AZ/RAN AVGDV LLDV MXDV/Hgt Depth Base/Top
				// MXSHR/Hgt
				// Type ID (deg,nm) (kt) (kt) (kt,kft) (kft) (kft) (E-3/s,kft)
				// TVS J5 202/ 32 37 56 60/ 5.7 > 5.9 < 2.3/ 8.2 31/ 5.7

				if (str.charAt(17) == '/' && str.charAt(39) == '/'
						&& str.charAt(59) == '/' && str.charAt(71) == '/') {
					// logger.info(hitCount+" "+new String(data));

					double azim = Double.parseDouble(str.substring(14, 17));
					double range = Double.parseDouble(str.substring(18, 21));

					if (azim == 0.0 || azim == 180.0 || azim == 360.0) {
						azim += 0.000001;
					}
					// Convert from nautical mi to meters
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
						// create the feature
						// {geom, wsrid, datetime, lat, lon, id, type, range,
						// azim, avgdv, lldv, mxdv, mxdvhgt, depth, base, top,
						// mxshr, mxshrhgt};
						SimpleFeature feature = SimpleFeatureBuilder.build(
								schema,
								new Object[] {
										geoFactory.createPoint(coordinate),
										header.getRadarStationID(), datetime,
										coordinate.y,
										coordinate.x,
										str.substring(9, 11).trim(), // id
										str.substring(1, 7).trim(), // type
										new Double(range), new Double(azim),
										str.substring(22, 28).trim(),// avgdv
										str.substring(29, 34).trim(),// lldv
										str.substring(35, 39).trim(),// mxdv
										str.substring(40, 45).trim(),// mxdvhgt
										str.substring(46, 53).trim(),// depth
										str.substring(54, 59).trim(),// base
										str.substring(60, 65).trim(),// top
										str.substring(66, 71).trim(),// mxshr
										str.substring(72, str.length()).trim() // mxshrhgt
								}, new Integer(geoIndex++).toString());
						// add to collection
						for (int s = 0; s < processArray.length; s++) {
							processArray[s].addFeature(feature);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		makeMetaLabelStrings();

	}

	private void makeMetaLabelStrings() {

		SimpleFeatureIterator fi = features.features();
		if (fi.hasNext()) { // only use first and thus strongest reading
			SimpleFeature f = fi.next();
			metaLabelString[0] = "MAX ID: "
					+ f.getAttribute("id").toString().trim();
			metaLabelString[1] = "BASE / TOP: "
					+ f.getAttribute("base").toString().trim() + "/"
					+ f.getAttribute("top").toString().trim() + " (kft)";
			metaLabelString[2] = "MAX SHEAR: "
					+ f.getAttribute("mxshr").toString().trim() + " (E-3/s)";
		} else {
			metaLabelString[0] = "NO TVS PRESENT";
		}

	}

}
