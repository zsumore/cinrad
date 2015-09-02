package mq.radar.cinrad.decoders.cinrad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.StreamingProcess;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class DecodeVADText extends BaseCindarDecoder {
	private final Logger logger = LoggerFactory.getLogger(DecodeVADText.class);

	public DecodeVADText(CinradHeader header) throws DecodeException,
			IOException, FactoryException {
		super(header);
		this.decoderName = "DecodeVADText";
	}

	@Override
	public void decodeData(StreamingProcess[] processArray, boolean autoClose)
			throws DecodeException, IOException {
		int geoIndex = 0;

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		{
			builder.setCRS(crs);
			builder.setName("Tornado Vortex Signature Data");
			builder.add("geom", Point.class);
			builder.nillable(true).length(5).add("wsrid", Integer.class);
			builder.nillable(true).length(15).add("datetime", String.class);
			builder.nillable(true).length(10).add("lon", Double.class);
			builder.nillable(true).length(10).add("lat", Double.class);

			builder.nillable(true).length(5).add("alt", Double.class);
			builder.nillable(true).length(5).add("u", Double.class);
			builder.nillable(true).length(5).add("v", Double.class);
			builder.nillable(true).length(5).add("w", Double.class);

			builder.nillable(true).length(5).add("dir", Double.class);
			builder.nillable(true).length(5).add("spd", Double.class);
			builder.nillable(true).length(5).add("rms", Double.class);
			builder.nillable(true).length(5).add("div", Double.class);

			builder.nillable(true).length(5).add("srng", String.class);
			builder.nillable(true).length(5).add("elev", String.class);
			schema = builder.buildFeatureType();
		}
		if (autoClose) {
			header.close();
		}

		NetcdfFile ncfile = NetcdfFile.open(header.getCinradURL().toString());
		Variable var = ncfile.findVariable("TabMessagePage");
		Array data = var.read();
		Index index = data.getIndex();
		int[] shape = data.getShape();
		logger.debug("Data Array Dimensions: ");
		for (int n = 0; n < shape.length; n++) {
			logger.debug("Dimension[" + n + "] " + shape[n]);
		}

		supplementalData = new String[shape[0]];

		for (int n = 0; n < shape[0]; n++) {
			logger.debug("-------------- n=" + n);
			String pageString = data.getObject(index.set(n)).toString();
			logger.debug(pageString);

			supplementalData[n] = pageString;

			if (!pageString.contains("VAD Algorithm Output")) {
				continue;
			}

			BufferedReader pageReader = new BufferedReader(new StringReader(
					pageString));
			// skip first three lines
			pageReader.readLine();
			pageReader.readLine();
			pageReader.readLine();

			String str = null;
			while ((str = pageReader.readLine()) != null) {

				logger.debug("str: " + str);

				// VAD Algorithm Output 08/29/05 09:17
				// ALT U V W DIR SPD RMS DIV SRNG ELEV
				// 100ft m/s m/s cm/s deg kts kts E-3/s nm deg
				// 006 -19.4 -3.0 NA 081 038 3.0 NA 5.67 0.5
				// 0 1 2 3 4 5 6 7 8
				// 012345678901234567890123456789012345678901234567890123456789012345678901234567890

				try {
					// AttributeType[] attTypes = {geom, wsrid, datetime, lat,
					// lon, alt, u, v, w, dir, spd, rms, div, srng, elev};
					SimpleFeature feature = SimpleFeatureBuilder.build(
							schema,
							new Object[] {
									geoFactory.createPoint(new Coordinate(
											header.getLon(), header.getLat())), // geom
									header.getRadarStationID(), // wsrid
									datetime,
									new Double(header.getLon()),
									new Double(header.getLat()),
									new Double(CinradUtils.stripCharsDouble(str
											.substring(3, 7))), // alt
									new Double(CinradUtils.stripCharsDouble(str
											.substring(9, 15))), // u
									new Double(CinradUtils.stripCharsDouble(str
											.substring(16, 23))), // v
									new Double(CinradUtils.stripCharsDouble(str
											.substring(24, 32))), // w
									new Double(CinradUtils.stripCharsDouble(str
											.substring(33, 38))), // dir
									new Double(CinradUtils.stripCharsDouble(str
											.substring(38, 44))), // spd
									new Double(CinradUtils.stripCharsDouble(str
											.substring(44, 49))), // rms
									new Double(CinradUtils.stripCharsDouble(str
											.substring(50, 58))), // div
									new Double(CinradUtils.stripCharsDouble(str
											.substring(60, 69))), // srng
									new Double(CinradUtils.stripCharsDouble(str
											.substring(69, 75))) // elev
							}, new Integer(geoIndex++).toString());
					// add to streaming processes
					for (int s = 0; s < processArray.length; s++) {
						processArray[s].addFeature(feature);
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new DecodeException(
							"DECODE EXCEPTION IN VAD FILE - CODE 1 ",
							header.getCinradURL());
				}

			}
		}

	}

}
