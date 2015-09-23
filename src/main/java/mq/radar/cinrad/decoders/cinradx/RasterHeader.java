package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;

import ucar.unidata.io.RandomAccessFile;

/*
 * Raster Format data has 2 sections - Raster Header Block and Raster Data. The
 * Raster Header Block has Block Divider and Block Identifier, but the Raster
 * Data is actually not a block because it has not such block beginning flags.
 * 
 * Like Radial format, the data saved for each grid is not value, but code of
 * the value. The coding formula is expressed as, 
 * Code = (Value*Scale)+Offset
 * Where Code is range bin code data, Value is the raw weather data (for example
 * reflectivity), 
 * Scale and Offset is 2 parameters defined in Raster Header
 * Block. The Maximum and Minimum data in Raster Header Block is also in coded
 * format. 
 * Raster Header Block is described in Table 4-4. And Raster Data is
 * described in Table 4-5.
 */
public class RasterHeader implements ICinradXBuilder {
	/*
	 * NO 01; TYPE INT; UNIT N/A; RANGE 1 to 64 ;Moment data type, See Table2-7;
	 */
	private int dataType;

	/*
	 * NO 02; TYPE INT; UNIT N/A; RANGE 0.0 to 1.0;Data coding scale Code =
	 * value*scale+offset;
	 */
	private int scale;
	/*
	 * NO 03; TYPE INT; UNIT N/A; RANGE 0 to 32768;Data coding offset Code =
	 * value*scale+offset;
	 */
	private int offset;

	/*
	 * NO 04; TYPE SHORT; UNIT Bytes; RANGE 1 to 2;Bytes to save each bin of
	 * data
	 */
	private short binLength;
	/*
	 * NO 05; TYPE SHORT; UNIT N/A ; RANGE N/A ;Bit Mask of flags for data.
	 * Reserved now;
	 */
	private short flags;
	/*
	 * NO 06; TYPE INT; UNIT Meter ; RANGE N/A ; Resolution of row of Raster
	 * data
	 */
	private int rowResolution;
	/*
	 * NO 07; TYPE INT; UNIT Meter ; RANGE N/A ;Resolution of column of raster
	 * data
	 */
	private int columnResolution;

	/*
	 * NO 08; TYPE INT; UNIT N/A ; RANGE N/A ;Side Length of Row
	 */
	private int rowSideLength;
	/*
	 * NO 09; TYPE INT; UNIT N/A ; RANGE N/A ;Side Length of Column
	 */
	private int columnSideLength;

	/*
	 * NO 10; TYPE INT; UNIT N/A ; RANGE N/A ;Maximum coded data in data block
	 */
	private int maximumData;
	/*
	 * NO 11; TYPE INT; UNIT Meter ; RANGE 0 to 500,000 ;Range of Maximum Value
	 */
	private int rangeOfMaximumValue;
	/*
	 * NO 12; TYPE FLOAT; UNIT Degree ; RANGE 0 to 360 ;Azimuth of Maximum Value
	 */
	private float azimuthOfMaximumValue;

	/*
	 * NO 13; TYPE INT; UNIT N/A ; RANGE N/A ;Minimum coded data in data block
	 */
	private int minimumData;

	/*
	 * NO 14; TYPE INT; UNIT Meter ; RANGE 0 to 500,000 ;Range of Minimum Value
	 */
	private int rangeOfMinimumValue;
	/*
	 * NO 15; TYPE FLOAT; UNIT Degree ; RANGE 0 to 360 ;Azimuth of Minimum Value
	 */
	private float azimuthOfMinimumValue;
	/*
	 * NO 16; reserved Range 8 Bytes;
	 */
	private byte[] reserved;

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

	public int getRowResolution() {
		return rowResolution;
	}

	public int getColumnResolution() {
		return columnResolution;
	}

	public int getRowSideLength() {
		return rowSideLength;
	}

	public int getColumnSideLength() {
		return columnSideLength;
	}

	public int getMaximumData() {
		return maximumData;
	}

	public int getRangeOfMaximumValue() {
		return rangeOfMaximumValue;
	}

	public float getAzimuthOfMaximumValue() {
		return azimuthOfMaximumValue;
	}

	public int getMinimumData() {
		return minimumData;
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
	public String toString() {
		return "RasterHeader [dataType=" + dataType + ", scale=" + scale + ", offset=" + offset + ", binLength="
				+ binLength + ", flags=" + flags + ", rowResolution=" + rowResolution + ", columnResolution="
				+ columnResolution + ", rowSideLength=" + rowSideLength + ", columnSideLength=" + columnSideLength
				+ ", maximumData=" + maximumData + ", rangeOfMaximumValue=" + rangeOfMaximumValue
				+ ", azimuthOfMaximumValue=" + azimuthOfMaximumValue + ", minimumData=" + minimumData
				+ ", rangeOfMinimumValue=" + rangeOfMinimumValue + ", azimuthOfMinimumValue=" + azimuthOfMinimumValue
				+ ", reserved=" + Arrays.toString(reserved) + "]";
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
		 * NO 02; TYPE INT; UNIT N/A; RANGE 0.0 to 1.0;Data coding scale Code =
		 * value*scale+offset;
		 */
		scale = file.readInt();
		/*
		 * NO 03; TYPE INT; UNIT N/A; RANGE 0 to 32768;Data coding offset Code =
		 * value*scale+offset;
		 */
		offset = file.readInt();

		/*
		 * NO 04; TYPE SHORT; UNIT Bytes; RANGE 1 to 2;Bytes to save each bin of
		 * data
		 */
		binLength = file.readShort();
		/*
		 * NO 05; TYPE SHORT; UNIT N/A ; RANGE N/A ;Bit Mask of flags for data.
		 * Reserved now;
		 */
		flags = file.readShort();
		/*
		 * NO 06; TYPE INT; UNIT Meter ; RANGE N/A ; Resolution of row of Raster
		 * data
		 */
		rowResolution = file.readInt();
		/*
		 * NO 07; TYPE INT; UNIT Meter ; RANGE N/A ;Resolution of column of
		 * raster data
		 */
		columnResolution = file.readInt();

		/*
		 * NO 08; TYPE INT; UNIT N/A ; RANGE N/A ;Side Length of Row
		 */
		rowSideLength = file.readInt();
		/*
		 * NO 09; TYPE INT; UNIT N/A ; RANGE N/A ;Side Length of Column
		 */
		columnSideLength = file.readInt();

		/*
		 * NO 10; TYPE INT; UNIT N/A ; RANGE N/A ;Maximum coded data in data
		 * block
		 */
		maximumData = file.readInt();
		/*
		 * NO 11; TYPE INT; UNIT Meter ; RANGE 0 to 500,000 ;Range of Maximum
		 * Value
		 */
		rangeOfMaximumValue = file.readInt();
		/*
		 * NO 12; TYPE FLOAT; UNIT Degree ; RANGE 0 to 360 ;Azimuth of Maximum
		 * Value
		 */
		azimuthOfMaximumValue = file.readFloat();
		/*
		 * NO 13; TYPE INT; UNIT N/A ; RANGE N/A ;Minimum coded data in data
		 * block
		 */
		minimumData = file.readInt();

		/*
		 * NO 14; TYPE INT; UNIT Meter ; RANGE 0 to 500,000 ;Range of Minimum
		 * Value
		 */
		rangeOfMinimumValue = file.readInt();
		/*
		 * NO 15; TYPE FLOAT; UNIT Degree ; RANGE 0 to 360 ;Azimuth of Minimum
		 * Value
		 */
		azimuthOfMinimumValue = file.readFloat();
		/*
		 * NO 16; reserved Range 8 Bytes;
		 */
		reserved = file.readBytes(8);

	}

}
