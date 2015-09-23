package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ucar.unidata.io.RandomAccessFile;

public class RadialData implements ICinradXBuilder {

	private RadialHeader radialHeader;

	/*
	 * NO 01; TYPE FLOAT; UNIT Degree; RANGE -10.0 to 360.0 ;Start Angle of
	 * radial
	 */
	private float startAngle;
	/*
	 * NO 02; TYPE FLOAT; UNIT Degree; RANGE 0.0 to 10.0 ;Radial width in degree
	 */
	private float angleWidth;
	/*
	 * NO 03; TYPE INT; UNIT N/A; RANGE 1 to 4096;Number of bins in current
	 * radial
	 */
	private int numberOfBins;
	/*
	 * NO 04; spare Range 8 Bytes;
	 */
	private byte[] spare;
	/*
	 * NO 05; TYPE float;
	 */
	private Map<Integer, Float> dataValueArray;

	public RadialData(RadialHeader radialHeader) {
		super();
		this.radialHeader = radialHeader;
	}

	public Map<Integer, Float> getDataValueArray() {
		return dataValueArray;
	}

	public RadialHeader getRadialHeader() {
		return radialHeader;
	}

	public float getStartAngle() {
		return startAngle;
	}

	public float getAngleWidth() {
		return angleWidth;
	}

	public int getNumberOfBins() {
		return numberOfBins;
	}

	public byte[] getSpare() {
		return spare;
	}

	@Override
	public String toString() {
		return "RadialData [radialHeader=" + radialHeader + ", startAngle=" + startAngle + ", angleWidth=" + angleWidth
				+ ", numberOfBins=" + numberOfBins + ", spare=" + Arrays.toString(spare) + ", dataValueArray="
				+ dataValueArray + "]";
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		/*
		 * NO 01; TYPE FLOAT; UNIT Degree; RANGE -10.0 to 360.0 ;Start Angle of
		 * radial
		 */
		startAngle = file.readFloat();
		/*
		 * NO 02; TYPE FLOAT; UNIT Degree; RANGE 0.0 to 10.0 ;Radial width in
		 * degree
		 */
		angleWidth = file.readFloat();
		/*
		 * NO 03; TYPE INT; UNIT N/A; RANGE 1 to 4096;Number of bins in current
		 * radial
		 */
		numberOfBins = file.readInt();
		/*
		 * NO 04; spare Range 20 Bytes;
		 */
		spare = file.readBytes(20);
		/*
		 * NO 05; TYPE INT;
		 */
		dataValueArray = new HashMap<>();
		for (int i = 0; i < numberOfBins; i++) {
			int code;
			if (radialHeader.getBinLength() == 1) {
				code = file.readUnsignedByte();

			} else {
				code = file.readUnsignedShort();
			}
			float value = (code - this.radialHeader.getOffset()) *1.0f/ this.radialHeader.getScale();

			if (value > 0) {
				dataValueArray.put(i, value);
			}

		}

	}

}
