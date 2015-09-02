package mq.radar.cinrad.decoders.cinrad;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.StreamingProcess;
import mq.radar.cinrad.decoders.cinrad.DecodeVAD.VADTextPacket;
import mq.radar.cinrad.decoders.cinrad.DecodeVAD.VADWindBarbPacket;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class DecodeVWP extends BaseCindarDecoder {
	private final Logger logger = LoggerFactory.getLogger(DecodeVWP.class);

	private Map<Short, String> VADJposHeight;
	private Set<Short> keys;

	public DecodeVWP(CinradHeader header) throws DecodeException, IOException,
			FactoryException {
		super(header);
		this.decoderName = "DecodeVWP";
	}

	@Override
	public void decodeData(StreamingProcess[] processArray, boolean autoClose)
			throws DecodeException, IOException {

		DecodeVAD vad = new DecodeVAD();
		vad.decodeVAD(header);

		if (autoClose) {
			header.close();
		}

		int geoIndex = 0;
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		{
			builder.setCRS(crs);
			builder.setName("Wind Profiles Data");
			builder.add("geom", Point.class);
			builder.nillable(true).length(5).add("sid", Integer.class);
			builder.nillable(true).length(15).add("datetime", String.class);
			builder.nillable(true).length(10).add("lat", Double.class);
			builder.nillable(true).length(10).add("lon", Double.class);
			builder.nillable(true).length(5).add("alt", Double.class);

			builder.nillable(true).length(5).add("dir", Integer.class);
			builder.nillable(true).length(5).add("spd", Double.class);
			builder.nillable(true).length(5).add("rms", Integer.class);

			schema = builder.buildFeatureType();
		}

		VADJposHeight = new HashMap<Short, String>();

		Vector<VADTextPacket> vText = vad.getText();
		for (VADTextPacket p : vText) {
			if (p.textString.contains(".")) {
				VADJposHeight.put(p.jpos, p.textString);
			}
		}

		keys = VADJposHeight.keySet();

		Vector<VADWindBarbPacket> vWindBarbs = vad.getWindBarbs();
		short maxWindIPos = -1;
		for (VADWindBarbPacket v : vWindBarbs) {
			if (v.ipos > maxWindIPos) {
				maxWindIPos = v.ipos;
			}
		}

		Vector<VADGisPacket> tempGis = new Vector<VADGisPacket>();

		for (VADWindBarbPacket v : vWindBarbs) {
			if (v.ipos == maxWindIPos) {
				tempGis.add(genGisPacket(v));
			}
		}

		for (VADGisPacket p : tempGis) {
			try {
				// AttributeType[] attTypes = {geom, wsrid, datetime, lat,
				// lon, alt, u, v, w, dir, spd, rms, div, srng, elev};
				SimpleFeature feature = SimpleFeatureBuilder.build(
						schema,
						new Object[] {
								geoFactory.createPoint(new Coordinate(header
										.getLon(), header.getLat())), // geom
								header.getRadarStationID(), // wsrid
								datetime, new Double(header.getLat()),
								new Double(header.getLon()), p.alt,
								p.direction, p.speed, p.rms

						}, new Integer(geoIndex++).toString());
				// add to streaming processes
				for (int s = 0; s < processArray.length; s++) {
					processArray[s].addFeature(feature);
				}

			} catch (Exception e) {
				logger.error("Exception:{}", e);
				throw new DecodeException(
						"DECODE EXCEPTION IN VAD FILE - CODE 1 ",
						header.getCinradURL());
			}
		}

		keys.clear();
		VADJposHeight.clear();
		tempGis.clear();
		vad.clearAllVectors();

	}

	private VADGisPacket genGisPacket(VADWindBarbPacket v) {

		int del = 10000;
		Short key = null;
		// Set<Short> keys = VADJposHeight.keySet();
		for (Short k : keys) {
			int ab = Math.abs(v.jpos - k);
			if (ab < del) {
				del = ab;
				key = k;
			}
		}
		if (key == null) {
			return null;
		}
		String text = VADJposHeight.get(key);
		VADGisPacket p = new VADGisPacket(v.rms, v.direction,
				v.speed * 0.514444, Double.valueOf(text));
		return p;
	}

	public class VADGisPacket {
		/**
		 * Description of the Field
		 */
		public short rms, direction;
		public double alt, speed;

		/**
		 * Constructor for the VADWindBarbPacket object
		 * 
		 * @param rms
		 *            Description of the Parameter
		 * @param ipos
		 *            Description of the Parameter
		 * @param jpos
		 *            Description of the Parameter
		 * @param direction
		 *            Description of the Parameter
		 * @param speed
		 *            Description of the Parameter
		 */
		public VADGisPacket(short rms, short direction, double speed, double alt) {
			this.rms = rms;
			this.direction = direction;
			this.speed = speed;
			this.alt = alt;
		}

	}

}
