package mq.radar.cinrad.decoders.cinrad;

public class CinradStation {
	private double lat;
	private double lon;
	private double alt;
	private short radarStationID;

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
	}

	public short getRadarStationID() {
		return radarStationID;
	}

	public void setRadarStationID(short radarStationID) {
		this.radarStationID = radarStationID;
	}

}
