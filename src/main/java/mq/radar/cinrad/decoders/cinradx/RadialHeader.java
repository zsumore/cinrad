package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;

import ucar.unidata.io.RandomAccessFile;

/*
 * Radial Format data has 2 sections - Radial Header Block and Radial Data. The Radial Header
 * Block has Block Divider and Block Identifier, but the Radial Data is actually not a block because
 * it has not such block beginning flags.
 * Noticed that data saved for range bins are not values, but code of the value. The coding formula is
 * expressed as,
 * Code = (Value*Scale)+Offset
 * Where Code is range bin code data, Value is the raw weather data (for example reflectivity),
 * Scale and Offset is 2 parameters defined in Radial Header Block.
 * The Maximum and Minimum data in Radial Header Block is also in coded format.
 * Radial Header Block is described in Table 4-2. And Radial Data is described in Table 4-3.
 * 
 */
public class RadialHeader implements ICinradXHeaderBuilder {

	/*
	 * NO 01; TYPE INT; UNIT N/A; RANGE 1 to 64 ;Moment data type, See Table2-7;
	 */
	private int dataType;
	/*
	 * NO 02; TYPE INT; UNIT N/A; RANGE N/A;Data coding scale Code =
	 * value*scale+offset;
	 */
	private int scale;
	/*
	 * NO 03; TYPE INT; UNIT N/A; RANGE N/A;Data coding offset Code =
	 * value*scale+offset;
	 */
	private int offset;
	/*
	 * NO 04; TYPE SHORT; UNIT Bytes; RANGE 1 to 2;Bytes to save each bin of
	 * data;
	 */
	private short binLength;
	/*
	 * NO 05; TYPE SHORT; UNIT N/A ; RANGE N/A ;Bit Mask of flags for data.
	 * Reserved now;
	 */
	private short flags;
	/*
	 * NO 06; TYPE INT; UNIT Meter ; RANGE 1 to 20,000 ;Resolution of range bin
	 * of radial data;
	 */
	private int resolution;
	/*
	 * NO 07; TYPE INT; UNIT Meter; RANGE 0 to 500,000 ;Start range of data as
	 * user requested
	 */
	private int startRange;
	/*
	 * NO 08; TYPE INT; UNIT Meter; RANGE 0 to 500,000 ;Maximum range of data as
	 * requested
	 */
	private int maxRange;
	/*
	 * NO 09; TYPE INT; UNIT N/A; RANGE 1 to 32768 ;Number of radials in data
	 * block
	 */
	private int numberOfRadials;
	/*
	 * NO 10; TYPE INT; UNIT N/A; RANGE N/A ;Maximum coded data in data block
	 */
	private int maximumValue;
	/*
	 * NO 11; TYPE INT; UNIT Meter; RANGE 0 to 500,000 ;Range of Maximum Value
	 */
	private int rangeOfMaximumValue;
	/*
	 * NO 12; TYPE FLOAT; UNIT Degree; RANGE 0 to 360;Azimuth of Maximum Value
	 */
	private float azimuthOfMaximumValue;
	/*
	 * NO 13; TYPE INT; UNIT N/A; RANGE N/A;Minimum coded data in data block
	 */
	private int minimumValue;
	/*
	 * NO 14; TYPE INT; UNIT Meter; RANGE 0 to 500,000;Range of Minimum Value
	 */
	private int rangeOfMinimumValue;
	/*
	 * NO 15; TYPE FLOAT; UNIT Degree; RANGE 0 to 360;Azimuth of Minimum Value
	 */
	private float azimuthOfMinimumValue;
	/*
	 * NO 16; reserved Range 8 Bytes;
	 */
	private byte[] reserved;

	@Override
	public String toString() {
		return "RadialHeader [dataType=" + dataType + ", scale=" + scale + ", offset=" + offset + ", binLength="
				+ binLength + ", flags=" + flags + ", resolution=" + resolution + ", startRange=" + startRange
				+ ", maxRange=" + maxRange + ", numberOfRadials=" + numberOfRadials + ", maximumValue=" + maximumValue
				+ ", rangeOfMaximumValue=" + rangeOfMaximumValue + ", azimuthOfMaximumValue=" + azimuthOfMaximumValue
				+ ", minimumValue=" + minimumValue + ", rangeOfMinimumValue=" + rangeOfMinimumValue
				+ ", azimuthOfMinimumValue=" + azimuthOfMinimumValue + ", reserved=" + Arrays.toString(reserved) + "]";
	}

	public int getDataType() {
		return dataType;
	}

	public int getScale() {
		return scale;
	}

	public int getOffset() {
		return offset;
	}

	public short getBinLength() {
		return binLength;
	}

	public short getFlags() {
		return flags;
	}

	public int getResolution() {
		return resolution;
	}

	public int getStartRange() {
		return startRange;
	}

	public int getMaxRange() {
		return maxRange;
	}

	public int getNumberOfRadials() {
		return numberOfRadials;
	}

	public int getMaximumValue() {
		return maximumValue;
	}

	public int getRangeOfMaximumValue() {
		return rangeOfMaximumValue;
	}

	public float getAzimuthOfMaximumValue() {
		return azimuthOfMaximumValue;
	}

	public int getMinimumValue() {
		return minimumValue;
	}

	public int getRangeOfMinimumValue() {
		return rangeOfMinimumValue;
	}

	public float getAzimuthOfMinimumValue() {
		return azimuthOfMinimumValue;
	}

	public byte[] getReserved() {
		return reserved;
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		/*
		 * NO 01; TYPE INT; UNIT N/A; RANGE 1 to 64 ;Moment data type, See
		 * Table2-7;
		 */
		dataType = file.readInt();
		/*
		 * NO 02; TYPE INT; UNIT N/A; RANGE N/A;Data coding scale Code =
		 * value*scale+offset;
		 */
		scale = file.readInt();
		/*
		 * NO 03; TYPE INT; UNIT N/A; RANGE N/A;Data coding offset Code =
		 * value*scale+offset;
		 */
		offset = file.readInt();
		/*
		 * NO 04; TYPE SHORT; UNIT Bytes; RANGE 1 to 2;Bytes to save each bin of
		 * data;
		 */
		binLength = file.readShort();
		/*
		 * NO 05; TYPE SHORT; UNIT N/A ; RANGE N/A ;Bit Mask of flags for data.
		 * Reserved now;
		 */
		flags = file.readShort();
		/*
		 * NO 06; TYPE INT; UNIT Meter ; RANGE 1 to 20,000 ;Resolution of range
		 * bin of radial data;
		 */
		resolution = file.readInt();
		/*
		 * NO 07; TYPE INT; UNIT Meter; RANGE 0 to 500,000 ;Start range of data
		 * as user requested
		 */
		startRange = file.readInt();
		/*
		 * NO 08; TYPE INT; UNIT Meter; RANGE 0 to 500,000 ;Maximum range of
		 * data as requested
		 */
		maxRange = file.readInt();
		/*
		 * NO 09; TYPE INT; UNIT N/A; RANGE 1 to 32768 ;Number of radials in
		 * data block
		 */
		numberOfRadials = file.readInt();
		/*
		 * NO 10; TYPE INT; UNIT N/A; RANGE N/A ;Maximum coded data in data
		 * block
		 */
		maximumValue = file.readInt();
		/*
		 * NO 11; TYPE INT; UNIT Meter; RANGE 0 to 500,000 ;Range of Maximum
		 * Value
		 */
		rangeOfMaximumValue = file.readInt();
		/*
		 * NO 12; TYPE FLOAT; UNIT Degree; RANGE 0 to 360;Azimuth of Maximum
		 * Value
		 */
		azimuthOfMaximumValue = file.readFloat();
		/*
		 * NO 13; TYPE INT; UNIT N/A; RANGE N/A;Minimum coded data in data block
		 */
		minimumValue = file.readInt();
		/*
		 * NO 14; TYPE INT; UNIT Meter; RANGE 0 to 500,000;Range of Minimum
		 * Value
		 */
		rangeOfMinimumValue = file.readInt();
		/*
		 * NO 15; TYPE FLOAT; UNIT Degree; RANGE 0 to 360;Azimuth of Minimum
		 * Value
		 */
		azimuthOfMinimumValue = file.readFloat();
		/*
		 * NO 16; reserved Range 8 Bytes;
		 */
		reserved = file.readBytes(8);

	}
}
