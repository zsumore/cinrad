package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;

import ucar.unidata.io.RandomAccessFile;

/*
 * Task Configuration;
 * Range 256 Bytes;
 * No 3;
 */
public class TaskConfiguration implements ICinradXHeaderBuilder {

	/*
	 * NO 01; TYPE CHAR*32; UNIT N/A; RANGE ASCII; Name of the Task
	 * Configuration;
	 */
	private String taskName;

	/*
	 * NO 02; TYPE CHAR*128; UNIT N/A; RANGE ASCII; Description of Task;
	 */
	private String taskDescription;

	/*
	 * NO 03; TYPE INT; UNIT N/A; RANGE 1 to 4; Polarization Type: 1 -
	 * Horizontal 2 - Vertical 3 - Simultaneously 4 - Alternation
	 * 
	 */
	private int polarizationType;

	/*
	 * NO 04; TYPE INT; UNIT N/A; RANGE 1 to 3; Antenna Scan Type 1 - PPI Full 2
	 * - PPI Sector 3 - RHI
	 * 
	 */
	private int scanType;

	/*
	 * NO 05; TYPE INT; UNIT Nanoseconds; RANGE 1 to 10000; Pulse Width
	 * 
	 */
	private int pulseWidth;

	/*
	 * NO 06; TYPE INT; UNIT Seconds ; RANGE UTC; Start time of volume scan
	 * 
	 */
	private int volumeStartTime;

	/*
	 * NO 07; TYPE INT; UNIT N/A ; RANGE 1 to 256; Number of Elevation or
	 * Azimuth cuts in the task
	 * 
	 */
	private int cutNumber;

	/*
	 * NO 08; TYPE FLOAT; UNIT dBm ; RANGE -100.0 to 0.0; Noise level of
	 * horizontal channel
	 */
	private float horizontalNoise;

	/*
	 * NO 09; TYPE FLOAT; UNIT dBm ; RANGE -100.0 to 0.0; Noise level of
	 * vertical channel
	 */
	private float verticalNoise;

	/*
	 * NO 10; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 200.0; System Reflectivity
	 * Calibration Const for horizontal channel.
	 */
	private float horizontalCalibration;
	/*
	 * NO 11; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 200.0; System Reflectivity
	 * Calibration Const for vertical channel.
	 */
	private float verticalCalibration;
	/*
	 * NO 12; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 800.0; System Reflectivity
	 * Calibration Const for horizontal channel.
	 */
	private float horizontalNoiseTemperature;
	/*
	 * NO 13; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 800.0; System Reflectivity
	 * Calibration Const for vertical channel.
	 */
	private float verticalNoiseTemperature;

	/*
	 * NO 14; TYPE FLOAT; UNIT dB ; RANGE -10.0 to 10.0; Reflectivity
	 * calibration difference of horizontal and vertical channel
	 */
	private float zdrCalibration;
	/*
	 * NO 15; TYPE FLOAT; UNIT Degree ; RANGE -180.0 to 180.0; Phase calibration
	 * difference of horizontal and vertical channel
	 */
	private float phaseCalibration;
	/*
	 * NO 16; TYPE FLOAT; UNIT dB ; RANGE ; LDR calibration difference of
	 * horizontal and vertical channel
	 */
	private float ldrCalibration;

	/*
	 * NO 17; reserved Range 40 Bytes;
	 */
	private byte[] reserved;

	public String getTaskName() {
		return taskName;
	}

	public String getTaskDescription() {
		return taskDescription;
	}

	public int getPolarizationType() {
		return polarizationType;
	}

	public int getScanType() {
		return scanType;
	}

	public int getPulseWidth() {
		return pulseWidth;
	}

	public int getVolumeStartTime() {
		return volumeStartTime;
	}

	public int getCutNumber() {
		return cutNumber;
	}

	public float getHorizontalNoise() {
		return horizontalNoise;
	}

	public float getVerticalNoise() {
		return verticalNoise;
	}

	public float getHorizontalCalibration() {
		return horizontalCalibration;
	}

	public float getVerticalCalibration() {
		return verticalCalibration;
	}

	public float getHorizontalNoiseTemperature() {
		return horizontalNoiseTemperature;
	}

	public float getVerticalNoiseTemperature() {
		return verticalNoiseTemperature;
	}

	public float getZdrCalibration() {
		return zdrCalibration;
	}

	public float getPhaseCalibration() {
		return phaseCalibration;
	}

	public float getLdrCalibration() {
		return ldrCalibration;
	}

	public byte[] getReserved() {
		return reserved;
	}

	@Override
	public String toString() {
		return "TaskConfiguration [taskName=" + taskName + ", taskDescription=" + taskDescription
				+ ", polarizationType=" + polarizationType + ", scanType=" + scanType + ", pulseWidth=" + pulseWidth
				+ ", volumeStartTime=" + volumeStartTime + ", cutNumber=" + cutNumber + ", horizontalNoise="
				+ horizontalNoise + ", verticalNoise=" + verticalNoise + ", horizontalCalibration="
				+ horizontalCalibration + ", verticalCalibration=" + verticalCalibration
				+ ", horizontalNoiseTemperature=" + horizontalNoiseTemperature + ", verticalNoiseTemperature="
				+ verticalNoiseTemperature + ", zdrCalibration=" + zdrCalibration + ", phaseCalibration="
				+ phaseCalibration + ", ldrCalibration=" + ldrCalibration + ", reserved=" + Arrays.toString(reserved)
				+ "]";
	}

	/*
	 * if pos<0,do not seek.
	 */
	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		/*
		 * NO 01; TYPE CHAR*32; UNIT N/A; RANGE ASCII; Name of the Task
		 * Configuration;
		 */
		taskName = file.readString(32).trim();
		/*
		 * NO 02; TYPE CHAR*128; UNIT N/A; RANGE ASCII; Description of Task;
		 */
		taskDescription = file.readString(128).trim();
		/*
		 * NO 03; TYPE INT; UNIT N/A; RANGE 1 to 4; Polarization Type: 1 -
		 * Horizontal 2 - Vertical 3 - Simultaneously 4 - Alternation
		 * 
		 */
		polarizationType = file.readInt();
		/*
		 * NO 04; TYPE INT; UNIT N/A; RANGE 1 to 3; Antenna Scan Type 1 - PPI
		 * Full 2 - PPI Sector 3 - RHI
		 * 
		 */
		scanType = file.readInt();
		/*
		 * NO 05; TYPE INT; UNIT Nanoseconds; RANGE 1 to 10000; Pulse Width
		 * 
		 */
		pulseWidth = file.readInt();
		/*
		 * NO 06; TYPE INT; UNIT Seconds ; RANGE UTC; Start time of volume scan
		 * 
		 */
		volumeStartTime = file.readInt();
		/*
		 * NO 07; TYPE INT; UNIT N/A ; RANGE 1 to 256; Number of Elevation or
		 * Azimuth cuts in the task
		 * 
		 */
		cutNumber = file.readInt();
		/*
		 * NO 08; TYPE FLOAT; UNIT dBm ; RANGE -100.0 to 0.0; Noise level of
		 * horizontal channel
		 */
		horizontalNoise = file.readFloat();
		/*
		 * NO 09; TYPE FLOAT; UNIT dBm ; RANGE -100.0 to 0.0; Noise level of
		 * vertical channel
		 */
		verticalNoise = file.readFloat();
		/*
		 * NO 10; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 200.0; System Reflectivity
		 * Calibration Const for horizontal channel.
		 */
		horizontalCalibration = file.readFloat();
		/*
		 * NO 11; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 200.0; System Reflectivity
		 * Calibration Const for vertical channel.
		 */
		verticalCalibration = file.readFloat();
		/*
		 * NO 12; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 800.0; System Reflectivity
		 * Calibration Const for horizontal channel.
		 */
		horizontalNoiseTemperature = file.readFloat();

		/*
		 * NO 13; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 800.0; System Reflectivity
		 * Calibration Const for vertical channel.
		 */
		verticalNoiseTemperature = file.readFloat();
		/*
		 * NO 14; TYPE FLOAT; UNIT dB ; RANGE -10.0 to 10.0; Reflectivity
		 * calibration difference of horizontal and vertical channel
		 */
		zdrCalibration = file.readFloat();
		/*
		 * NO 15; TYPE FLOAT; UNIT Degree ; RANGE -180.0 to 180.0; Phase
		 * calibration difference of horizontal and vertical channel
		 */
		phaseCalibration = file.readFloat();
		/*
		 * NO 16; TYPE FLOAT; UNIT dB ; RANGE ; LDR calibration difference of
		 * horizontal and vertical channel
		 */
		ldrCalibration = file.readFloat();
		/*
		 * NO 17; reserved Range 40 Bytes;
		 */
		reserved = file.readBytes(40);
	}

}
