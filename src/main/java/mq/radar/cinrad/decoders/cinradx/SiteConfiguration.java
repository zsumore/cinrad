package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;

import ucar.unidata.io.RandomAccessFile;

/*
 * Site Config
 * Range 128 Bytes
 * 
 * No 2
 */
public class SiteConfiguration implements ICinradXHeaderBuilder {

	/*
	 * NO 01; TYPE CHAR*8; UNIT N/A; RANGE ASCII; Site Code in characters;
	 */
	private String siteCode;

	/*
	 * NO 02; TYPE CHAR*32; UNIT N/A; RANGE ASCII; Site Name or description in
	 * characters;
	 */
	private String siteName;

	/*
	 * NO 03; TYPE FLOAT; UNIT Degree; RANGE -90.0 to 90.0; Latitude of Radar
	 * Site;
	 */
	private float latitude;

	/*
	 * NO 04; TYPE FLOAT; UNIT Degree; RANGE -180.0 to 180.0; Longitude of Radar
	 * Site;
	 */
	private float longitude;

	/*
	 * NO 05; TYPE INT; UNIT Meters; RANGE 0 to 65536; Height of antenna in
	 * meters;
	 */
	private int height;

	/*
	 * NO 06; TYPE INT; UNIT Meters; RANGE 0 to 65536; Height of ground in
	 * meters;
	 */
	private int ground;

	/*
	 * NO 07; TYPE FLOAT; UNIT MHz; RANGE 1.0 to 999,000.0; Radar operation
	 * frequency in MHz;
	 */
	private float frequency;

	/*
	 * NO 08; TYPE FLOAT; UNIT Degree; RANGE 0.1 to 2.0; Antenna Beam Width;
	 */
	private float beamWidth;

	/*
	 * NO 09; reserved Range 64 Bytes;
	 */
	private byte[] reserved;

	public SiteConfiguration() {

	}

	public String getSiteCode() {
		return siteCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public int getHeight() {
		return height;
	}

	public int getGround() {
		return ground;
	}

	public float getFrequency() {
		return frequency;
	}

	public float getBeamWidth() {
		return beamWidth;
	}

	public byte[] getReserved() {
		return reserved;
	}

	@Override
	public String toString() {
		return "SiteConfig [siteCode=" + siteCode + ", siteName=" + siteName + ", latitude=" + latitude + ", longitude="
				+ longitude + ", height=" + height + ", ground=" + ground + ", frequency=" + frequency + ", beamWidth="
				+ beamWidth + ", reserved=" + Arrays.toString(reserved) + "]";
	}

	/*
	 * if pos<0,do not seek.
	 */
	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);
		/*
		 * NO 01; TYPE CHAR*8; UNIT N/A; RANGE ASCII; Site Code in characters;
		 */
		siteCode = file.readString(8).trim();
		/*
		 * NO 02; TYPE CHAR*32; UNIT N/A; RANGE ASCII; Site Name or description
		 * in characters;
		 */
		siteName = file.readString(32).trim();
		/*
		 * NO 03; TYPE FLOAT; UNIT Degree; RANGE -90.0 to 90.0; Latitude of
		 * Radar Site;
		 */
		latitude = file.readFloat();
		/*
		 * NO 04; TYPE FLOAT; UNIT Degree; RANGE -180.0 to 180.0; Longitude of
		 * Radar Site;
		 */
		longitude = file.readFloat();
		/*
		 * NO 05; TYPE INT; UNIT Meters; RANGE 0 to 65536; Height of antenna in
		 * meters;
		 */
		height = file.readInt();
		/*
		 * NO 06; TYPE INT; UNIT Meters; RANGE 0 to 65536; Height of ground in
		 * meters;
		 */
		ground = file.readInt();
		/*
		 * NO 07; TYPE FLOAT; UNIT MHz; RANGE 1.0 to 999,000.0; Radar operation
		 * frequency in MHz;
		 */
		frequency = file.readFloat();
		/*
		 * NO 08; TYPE FLOAT; UNIT Degree; RANGE 0.1 to 2.0; Antenna Beam Width;
		 */
		beamWidth = file.readFloat();
		/*
		 * NO 09; reserved Range 64 Bytes;
		 */
		reserved = file.readBytes(64);
	}

}
