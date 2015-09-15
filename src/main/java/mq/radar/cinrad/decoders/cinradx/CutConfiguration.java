package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;

import ucar.unidata.io.RandomAccessFile;

/*
 * Cut Configuration;
 * Range 256 Bytes;
 * No 4;
 */
public class CutConfiguration implements ICinradXHeaderBuilder {

	/*
	 * NO 01; TYPE INT; UNIT N/A; RANGE 1 to 2; Main processing mode of signal
	 * processing algorithm. 1 - PPP 2 - FFT
	 */
	private int processMode;
	/*
	 * NO 02; TYPE INT; UNIT N/A; RANGE 1 to 7; WSR-88D defined wave form 0 – CS
	 * 1 – CD 2 – CDX 3 – Rx Test 4 – BATCH 5 – Dual PRF 6 – Random Phase 7 – SZ
	 */
	private int waveForm;

	/*
	 * NO 03; TYPE FLOAT; UNIT Hz ; RANGE 1 to 3000 ; Pulse Repetition Frequency
	 * #1. For wave form Batch and Dual PRF mode, it’s the high PRF, for other
	 * modes it’s the only PRF.
	 */
	private float prf1;

	/*
	 * NO 04; TYPE FLOAT; UNIT Hz ; RANGE 1 to 3000 ; Pulse Repetition Frequency
	 * #2. For wave form Batch and Dual PRF mode, it’s the high PRF, for other
	 * modes it’s the only PRF.
	 */
	private float prf2;
	/*
	 * NO 05; TYPE INT; UNIT N/A; RANGE 1~4; Dual PRF mode 1 – Single PRF 2 –
	 * 3:2 mode 3 – 4:3 mode 4 – 5:4 mode
	 */
	private int unfoldMode;

	/*
	 * NO 06; TYPE FLOAT; UNIT Degree ; RANGE 0.0 to 360.0 ; Azimuth degree for
	 * RHI scan mode
	 */
	private float azimuth;

	/*
	 * NO 07; TYPE FLOAT; UNIT Degree ; RANGE -10.0 to 360.0 ; Elevation degree
	 * for PPI scan mode
	 */
	private float elevation;
	/*
	 * NO 08; TYPE FLOAT; UNIT Degree ; RANGE -10.0 to 360.0 ; Start azimuth
	 * angle for PPI Sector mode. Start (High) Elevation for RHI mode.
	 */
	private float startAngle;
	/*
	 * NO 09; TYPE FLOAT; UNIT Degree ; RANGE -10.0 to 360.0 ; Stop azimuth
	 * angle for PPI Sector mode. Stop (Low) Elevation for RHI mode.
	 */
	private float endAngle;
	/*
	 * NO 10; TYPE FLOAT; UNIT Degree ; RANGE 0.0 to 10.0 ; Radial angular
	 * resolution for PPI scan.
	 */
	private float angularResolution;
	/*
	 * NO 11; TYPE FLOAT; UNIT Deg/sec ; RANGE 0.0 to 36.0 ; Azimuth scan speed
	 * for PPI scan, Elevation scan speed for RHI mode.
	 */
	private float scanSpeed;
	/*
	 * NO 12; TYPE INT; UNIT Meter ; RANGE 1 to 5,000 ; Range bin resolution for
	 * surveillance data, reflectivity and ZDR, etc.
	 */
	private int logResolution;

	/*
	 * NO 13; TYPE INT; UNIT Meter ; RANGE 1 to 5,000 ; Range bin resolution for
	 * Doppler data, velocity and spectrum, etc.
	 */
	private int dopplerResolution;

	/*
	 * NO 14; TYPE INT; UNIT Meter ; RANGE 1 to 500,000 ; Maximum range of scan
	 */
	private int maximumRange;
	/*
	 * NO 15; TYPE INT; UNIT Meter ; RANGE 1 to 500,000 ; Maximum range of scan
	 */
	private int maximumRange2;
	/*
	 * NO 16; TYPE INT; UNIT Meter ; RANGE 1 to 500,000 ; Start range of scan
	 */
	private int startRange;
	/*
	 * NO 17; TYPE INT; UNIT N/A ; RANGE 2 to 512 ; Pulse sampling number #1.
	 * For wave form Batch and Dual PRF mode, it’s for high PRF, for other modes
	 * it’s for only PRF.
	 */
	private int sample1;

	/*
	 * NO 18; TYPE INT; UNIT N/A ; RANGE 2 to 512 ; Pulse sampling number #2.
	 * For wave form Batch and Dual PRF mode, it’s for low PRF, for other modes
	 * it’s not used.
	 */
	private int sample2;
	/*
	 * NO 19; TYPE INT; UNIT N/A ; RANGE 1 to 3; Phase modulation mode. 1 –
	 * Fixed Phase 2 – Random Phase 3 – SZ Phase
	 */
	private int phaseMode;

	/*
	 * NO 20; TYPE FLOAT; UNIT dB/km ; RANGE 0.0 to 10.0; two-way atmospheric
	 * attenuation factor
	 */
	private float atmosphericLoss;
	/*
	 * NO 21; TYPE FLOAT; UNIT m/s ; RANGE 0-100;
	 */
	private float nyquistSpeed;
	/*
	 * NO 22; TYPE LONG; UNIT N/A ; RANGE 0 to 127;Bit mask indicates which
	 * moments are involved in the scan. See Table 2-7
	 */
	private long momentsMask;
	/*
	 * NO 23; TYPE LONG; UNIT N/A ; RANGE 0 to 0xFFFFFFFF;Bit mask indicates
	 * range length for moment data in Table 2-7. 0 for 1 byte, 1 for 2 bytes
	 */
	private long momentsSizeMask;

	/*
	 * NO 24; TYPE INT; UNIT N/A ; RANGE 0 to 0xFFFF;Refer to Table 2-8
	 */
	private int miscFilterMask;
	/*
	 * NO 25; TYPE FLOAT; UNIT N/A ;RANGE 0.0 to 1.0 ; SQI Threshold for the
	 * scan
	 */
	private float sqiThreshold;
	/*
	 * NO 26; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 20.0 ;SIG Threshold for the
	 * scan
	 */
	private float sigThreshold;
	/*
	 * NO 27; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 100.0 ;CSR Threshold for the
	 * scan
	 */
	private float csrThreshold;
	/*
	 * NO 28; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 20.0 ;LOG Threshold for the
	 * scan
	 */
	private float logThreshold;
	/*
	 * NO 29; TYPE FLOAT; UNIT N/A ; RANGE 0.0 to 100.0 ;CPA Threshold for the
	 * scan
	 */
	private float cpaThreshold;
	/*
	 * NO 30; TYPE FLOAT; UNIT N/A ; RANGE 0.0 to 1.0 ;PMI Threshold for the
	 * scan
	 */
	private float pmiThreshold;
	/*
	 * NO 31; TYPE 8 Bytes; UNIT N/A ; RANGE N/A ;
	 */
	private byte[] thresholdsReserved;

	/*
	 * NO 32; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for total
	 * reflectivity data. Bits mask start from “SQI Threshold”, take is as LSB.
	 */
	private int dBTMask;
	/*
	 * NO 33; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for reflectivity
	 * data. Bits mask start from “SQI Threshold”, take is as LSB.
	 */
	private int dBZMask;
	/*
	 * NO 34; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for velocity data.
	 * Bits mask start from “SQI Threshold”, take is as LSB.
	 */
	private int velocityMask;
	/*
	 * NO 35; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for reflectivity
	 * data. Bits mask start from “SQI Threshold”, take is as LSB.
	 */
	private int spectrumWidthMask;
	/*
	 * NO 36; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for ZDR data. Bits
	 * mask start from “SQI Threshold”, take is as LSB.
	 */
	private int zdrMask;

	/*
	 * NO 37; TYPE 12 Bytes; UNIT N/A ; RANGE N/A ;Reserved for mask
	 */
	private byte[] maskReserved;
	/*
	 * NO 38; TYPE INT; UNIT N/A ; RANGE N/A; Reserved
	 */
	private int scanSync;
	/*
	 * NO 39; TYPE INT; UNIT N/A ; RANGE 1,2; Antenna rotate direction, 1=
	 * clockwise, 2=counter clockwise
	 */
	private int direction;
	/*
	 * NO 40; TYPE SHORT; UNIT N/A ; RANGE N/A ; 1 - All data is passed 2 - No
	 * data is passed 3 – Use Real Time GC Classifier 4 – use bypass map
	 */
	private short groundClutterClassifierType;
	/*
	 * NO 41; TYPE SHORT; UNIT N/A ; RANGE N/A ; 0- none 1 -Adaptive FFT 4 - IIR
	 */
	private short groundClutterFilterType;
	/*
	 * NO 42; TYPE SHORT; UNIT m/s ; RANGE 0.1-10 ; Scaled by 10
	 */
	private short groundClutterFilterNotchWidth;
	/*
	 * NO 43; TYPE SHORT; UNIT N/A ; RANGE N/A ; -1-none 0 - rect 1- Hamming 2-
	 * Blackman 3- Adaptive
	 */
	private short groundClutterFilterWindow;

	/*
	 * NO 44; TYPE CHAR; UNIT N/A ; RANGE 1-32 ; Number of similar cut
	 * configurations, reserved.
	 */
	private byte twins;

	/*
	 * NO 45; TYPE 71 Bytes; UNIT N/A ; RANGE N/A ;
	 */
	private byte[] spare;

	public CutConfiguration() {

	}

	@Override
	public String toString() {
		return "CutConfiguration [processMode=" + processMode + ", waveForm=" + waveForm + ", prf1=" + prf1 + ", prf2="
				+ prf2 + ", unfoldMode=" + unfoldMode + ", azimuth=" + azimuth + ", elevation=" + elevation
				+ ", startAngle=" + startAngle + ", endAngle=" + endAngle + ", angularResolution=" + angularResolution
				+ ", scanSpeed=" + scanSpeed + ", logResolution=" + logResolution + ", dopplerResolution="
				+ dopplerResolution + ", maximumRange=" + maximumRange + ", maximumRange2=" + maximumRange2
				+ ", startRange=" + startRange + ", sample1=" + sample1 + ", sample2=" + sample2 + ", phaseMode="
				+ phaseMode + ", atmosphericLoss=" + atmosphericLoss + ", nyquistSpeed=" + nyquistSpeed
				+ ", momentsMask=" + momentsMask + ", momentsSizeMask=" + momentsSizeMask + ", miscFilterMask="
				+ miscFilterMask + ", sqiThreshold=" + sqiThreshold + ", sigThreshold=" + sigThreshold
				+ ", csrThreshold=" + csrThreshold + ", logThreshold=" + logThreshold + ", cpaThreshold=" + cpaThreshold
				+ ", pmiThreshold=" + pmiThreshold + ", thresholdsReserved=" + Arrays.toString(thresholdsReserved)
				+ ", dBTMask=" + dBTMask + ", dBZMask=" + dBZMask + ", velocityMask=" + velocityMask
				+ ", spectrumWidthMask=" + spectrumWidthMask + ", zdrMask=" + zdrMask + ", maskReserved="
				+ Arrays.toString(maskReserved) + ", scanSync=" + scanSync + ", direction=" + direction
				+ ", groundClutterClassifierType=" + groundClutterClassifierType + ", groundClutterFilterType="
				+ groundClutterFilterType + ", groundClutterFilterNotchWidth=" + groundClutterFilterNotchWidth
				+ ", groundClutterFilterWindow=" + groundClutterFilterWindow + ", twins=" + twins + ", spare="
				+ Arrays.toString(spare) + "]";
	}

	public int getProcessMode() {
		return processMode;
	}

	public int getWaveForm() {
		return waveForm;
	}

	public float getPrf1() {
		return prf1;
	}

	public float getPrf2() {
		return prf2;
	}

	public int getUnfoldMode() {
		return unfoldMode;
	}

	public float getAzimuth() {
		return azimuth;
	}

	public float getElevation() {
		return elevation;
	}

	public float getStartAngle() {
		return startAngle;
	}

	public float getEndAngle() {
		return endAngle;
	}

	public float getAngularResolution() {
		return angularResolution;
	}

	public float getScanSpeed() {
		return scanSpeed;
	}

	public int getLogResolution() {
		return logResolution;
	}

	public int getDopplerResolution() {
		return dopplerResolution;
	}

	public int getMaximumRange() {
		return maximumRange;
	}

	public int getMaximumRange2() {
		return maximumRange2;
	}

	public int getStartRange() {
		return startRange;
	}

	public int getSample1() {
		return sample1;
	}

	public int getSample2() {
		return sample2;
	}

	public int getPhaseMode() {
		return phaseMode;
	}

	public float getAtmosphericLoss() {
		return atmosphericLoss;
	}

	public float getNyquistSpeed() {
		return nyquistSpeed;
	}

	public long getMomentsMask() {
		return momentsMask;
	}

	public long getMomentsSizeMask() {
		return momentsSizeMask;
	}

	public int getMiscFilterMask() {
		return miscFilterMask;
	}

	public float getSqiThreshold() {
		return sqiThreshold;
	}

	public float getSigThreshold() {
		return sigThreshold;
	}

	public float getCsrThreshold() {
		return csrThreshold;
	}

	public float getLogThreshold() {
		return logThreshold;
	}

	public float getCpaThreshold() {
		return cpaThreshold;
	}

	public float getPmiThreshold() {
		return pmiThreshold;
	}

	public byte[] getThresholdsReserved() {
		return thresholdsReserved;
	}

	public int getdBTMask() {
		return dBTMask;
	}

	public int getdBZMask() {
		return dBZMask;
	}

	public int getVelocityMask() {
		return velocityMask;
	}

	public int getSpectrumWidthMask() {
		return spectrumWidthMask;
	}

	public int getZdrMask() {
		return zdrMask;
	}

	public byte[] getMaskReserved() {
		return maskReserved;
	}

	public int getScanSync() {
		return scanSync;
	}

	public int getDirection() {
		return direction;
	}

	public short getGroundClutterClassifierType() {
		return groundClutterClassifierType;
	}

	public short getGroundClutterFilterType() {
		return groundClutterFilterType;
	}

	public short getGroundClutterFilterNotchWidth() {
		return groundClutterFilterNotchWidth;
	}

	public short getGroundClutterFilterWindow() {
		return groundClutterFilterWindow;
	}

	public byte getTwins() {
		return twins;
	}

	public byte[] getSpare() {
		return spare;
	}

	/*
	 * if pos<0,do not seek.
	 */
	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		
		if (pos >= 0)
			file.seek(pos);

		/*
		 * NO 01; TYPE INT; UNIT N/A; RANGE 1 to 2; Main processing mode of
		 * signal processing algorithm. 1 - PPP 2 - FFT
		 */
		processMode = file.readInt();
		/*
		 * NO 02; TYPE INT; UNIT N/A; RANGE 1 to 7; WSR-88D defined wave form 0
		 * – CS 1 – CD 2 – CDX 3 – Rx Test 4 – BATCH 5 – Dual PRF 6 – Random
		 * Phase 7 – SZ
		 */
		waveForm = file.readInt();

		/*
		 * NO 03; TYPE FLOAT; UNIT Hz ; RANGE 1 to 3000 ; Pulse Repetition
		 * Frequency #1. For wave form Batch and Dual PRF mode, it’s the high
		 * PRF, for other modes it’s the only PRF.
		 */
		prf1 = file.readFloat();

		/*
		 * NO 04; TYPE FLOAT; UNIT Hz ; RANGE 1 to 3000 ; Pulse Repetition
		 * Frequency #2. For wave form Batch and Dual PRF mode, it’s the high
		 * PRF, for other modes it’s the only PRF.
		 */
		prf2 = file.readFloat();
		/*
		 * NO 05; TYPE INT; UNIT N/A; RANGE 1~4; Dual PRF mode 1 – Single PRF 2
		 * – 3:2 mode 3 – 4:3 mode 4 – 5:4 mode
		 */
		unfoldMode = file.readInt();

		/*
		 * NO 06; TYPE FLOAT; UNIT Degree ; RANGE 0.0 to 360.0 ; Azimuth degree
		 * for RHI scan mode
		 */
		azimuth = file.readFloat();

		/*
		 * NO 07; TYPE FLOAT; UNIT Degree ; RANGE -10.0 to 360.0 ; Elevation
		 * degree for PPI scan mode
		 */
		elevation = file.readFloat();
		/*
		 * NO 08; TYPE FLOAT; UNIT Degree ; RANGE -10.0 to 360.0 ; Start azimuth
		 * angle for PPI Sector mode. Start (High) Elevation for RHI mode.
		 */
		startAngle = file.readFloat();
		/*
		 * NO 09; TYPE FLOAT; UNIT Degree ; RANGE -10.0 to 360.0 ; Stop azimuth
		 * angle for PPI Sector mode. Stop (Low) Elevation for RHI mode.
		 */
		endAngle = file.readFloat();
		/*
		 * NO 10; TYPE FLOAT; UNIT Degree ; RANGE 0.0 to 10.0 ; Radial angular
		 * resolution for PPI scan.
		 */
		angularResolution = file.readFloat();
		/*
		 * NO 11; TYPE FLOAT; UNIT Deg/sec ; RANGE 0.0 to 36.0 ; Azimuth scan
		 * speed for PPI scan, Elevation scan speed for RHI mode.
		 */
		scanSpeed = file.readFloat();
		/*
		 * NO 12; TYPE INT; UNIT Meter ; RANGE 1 to 5,000 ; Range bin resolution
		 * for surveillance data, reflectivity and ZDR, etc.
		 */
		logResolution = file.readInt();

		/*
		 * NO 13; TYPE INT; UNIT Meter ; RANGE 1 to 5,000 ; Range bin resolution
		 * for Doppler data, velocity and spectrum, etc.
		 */
		dopplerResolution = file.readInt();

		/*
		 * NO 14; TYPE INT; UNIT Meter ; RANGE 1 to 500,000 ; Maximum range of
		 * scan
		 */
		maximumRange = file.readInt();
		/*
		 * NO 15; TYPE INT; UNIT Meter ; RANGE 1 to 500,000 ; Maximum range of
		 * scan
		 */
		maximumRange2 = file.readInt();
		/*
		 * NO 16; TYPE INT; UNIT Meter ; RANGE 1 to 500,000 ; Start range of
		 * scan
		 */
		startRange = file.readInt();
		/*
		 * NO 17; TYPE INT; UNIT N/A ; RANGE 2 to 512 ; Pulse sampling number
		 * #1. For wave form Batch and Dual PRF mode, it’s for high PRF, for
		 * other modes it’s for only PRF.
		 */
		sample1 = file.readInt();

		/*
		 * NO 18; TYPE INT; UNIT N/A ; RANGE 2 to 512 ; Pulse sampling number
		 * #2. For wave form Batch and Dual PRF mode, it’s for low PRF, for
		 * other modes it’s not used.
		 */
		sample2 = file.readInt();
		/*
		 * NO 19; TYPE INT; UNIT N/A ; RANGE 1 to 3; Phase modulation mode. 1 –
		 * Fixed Phase 2 – Random Phase 3 – SZ Phase
		 */
		phaseMode = file.readInt();

		/*
		 * NO 20; TYPE FLOAT; UNIT dB/km ; RANGE 0.0 to 10.0; two-way
		 * atmospheric attenuation factor
		 */
		atmosphericLoss = file.readFloat();
		/*
		 * NO 21; TYPE FLOAT; UNIT m/s ; RANGE 0-100;
		 */
		nyquistSpeed = file.readFloat();
		/*
		 * NO 22; TYPE LONG; UNIT N/A ; RANGE 0 to 127;Bit mask indicates which
		 * moments are involved in the scan. See Table 2-7
		 */
		momentsMask = file.readLong();
		/*
		 * NO 23; TYPE LONG; UNIT N/A ; RANGE 0 to 0xFFFFFFFF;Bit mask indicates
		 * range length for moment data in Table 2-7. 0 for 1 byte, 1 for 2
		 * bytes
		 */
		momentsSizeMask = file.readLong();

		/*
		 * NO 24; TYPE INT; UNIT N/A ; RANGE 0 to 0xFFFF;Refer to Table 2-8
		 */
		miscFilterMask = file.readInt();
		/*
		 * NO 25; TYPE FLOAT; UNIT N/A ;RANGE 0.0 to 1.0 ; SQI Threshold for the
		 * scan
		 */
		sqiThreshold = file.readFloat();
		/*
		 * NO 26; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 20.0 ;SIG Threshold for the
		 * scan
		 */
		sigThreshold = file.readFloat();
		/*
		 * NO 27; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 100.0 ;CSR Threshold for
		 * the scan
		 */
		csrThreshold = file.readFloat();
		/*
		 * NO 28; TYPE FLOAT; UNIT dB ; RANGE 0.0 to 20.0 ;LOG Threshold for the
		 * scan
		 */
		logThreshold = file.readFloat();
		/*
		 * NO 29; TYPE FLOAT; UNIT N/A ; RANGE 0.0 to 100.0 ;CPA Threshold for
		 * the scan
		 */
		cpaThreshold = file.readFloat();
		/*
		 * NO 30; TYPE FLOAT; UNIT N/A ; RANGE 0.0 to 1.0 ;PMI Threshold for the
		 * scan
		 */
		pmiThreshold = file.readFloat();
		/*
		 * NO 31; TYPE 8 Bytes; UNIT N/A ; RANGE N/A ;
		 */
		thresholdsReserved = file.readBytes(8);

		/*
		 * NO 32; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for total
		 * reflectivity data. Bits mask start from “SQI Threshold”, take is as
		 * LSB.
		 */
		dBTMask = file.readInt();
		/*
		 * NO 33; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for
		 * reflectivity data. Bits mask start from “SQI Threshold”, take is as
		 * LSB.
		 */
		dBZMask = file.readInt();
		/*
		 * NO 34; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for velocity
		 * data. Bits mask start from “SQI Threshold”, take is as LSB.
		 */
		velocityMask = file.readInt();
		/*
		 * NO 35; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for
		 * reflectivity data. Bits mask start from “SQI Threshold”, take is as
		 * LSB.
		 */
		spectrumWidthMask = file.readInt();
		/*
		 * NO 36; TYPE INT; UNIT N/A ; RANGE N/A; Thresholds used for ZDR data.
		 * Bits mask start from “SQI Threshold”, take is as LSB.
		 */
		zdrMask = file.readInt();

		/*
		 * NO 37; TYPE 12 Bytes; UNIT N/A ; RANGE N/A ;Reserved for mask
		 */
		maskReserved = file.readBytes(12);
		/*
		 * NO 38; TYPE INT; UNIT N/A ; RANGE N/A; Reserved
		 */
		scanSync = file.readInt();
		/*
		 * NO 39; TYPE INT; UNIT N/A ; RANGE 1,2; Antenna rotate direction, 1=
		 * clockwise, 2=counter clockwise
		 */
		direction = file.readInt();
		/*
		 * NO 40; TYPE SHORT; UNIT N/A ; RANGE N/A ; 1 - All data is passed 2 -
		 * No data is passed 3 – Use Real Time GC Classifier 4 – use bypass map
		 */
		groundClutterClassifierType = file.readShort();
		/*
		 * NO 41; TYPE SHORT; UNIT N/A ; RANGE N/A ; 0- none 1 -Adaptive FFT 4 -
		 * IIR
		 */
		groundClutterFilterType = file.readShort();
		/*
		 * NO 42; TYPE SHORT; UNIT m/s ; RANGE 0.1-10 ; Scaled by 10
		 */
		groundClutterFilterNotchWidth = file.readShort();
		/*
		 * NO 43; TYPE SHORT; UNIT N/A ; RANGE N/A ; -1-none 0 - rect 1- Hamming
		 * 2- Blackman 3- Adaptive
		 */
		groundClutterFilterWindow = file.readShort();

		/*
		 * NO 44; TYPE CHAR; UNIT N/A ; RANGE 1-32 ; Number of similar cut
		 * configurations, reserved.
		 */
		twins = file.readByte();

		/*
		 * NO 45; TYPE 71 Bytes; UNIT N/A ; RANGE N/A ;
		 */
		spare = file.readBytes(71);

	}
}
