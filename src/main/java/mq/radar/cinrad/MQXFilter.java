package mq.radar.cinrad;

public class MQXFilter {

	public static final double NO_MIN_VALUE = Double.NEGATIVE_INFINITY;
	public static final double NO_MAX_VALUE = Double.POSITIVE_INFINITY;

	public static final double NO_MIN_AZIMUTH = 0.0;
	public static final double NO_MAX_AZIMUTH = 10000.0;

	private double minValue = NO_MIN_VALUE;
	private double maxValue = NO_MAX_VALUE;

	private double minAzimuth = NO_MIN_AZIMUTH;
	private double maxAzimuth = NO_MAX_AZIMUTH;

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public void setValueRange(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public void setAzimuthRange(double minAzimuth, double maxAzimuth) {
		this.minAzimuth = minAzimuth;
		this.maxAzimuth = maxAzimuth;
		if (minAzimuth < 0) {
			minAzimuth += 360;
		}
		if (maxAzimuth < 0) {
			maxAzimuth += 360;
		}
		if (minAzimuth > 360) {
			minAzimuth = minAzimuth % 360;
		}
		if (maxAzimuth > 360) {
			maxAzimuth = maxAzimuth % 360;
		}
		if (minAzimuth > maxAzimuth) {
			// double work = minAzimuth;
			minAzimuth = maxAzimuth;
			maxAzimuth = minAzimuth;
		}
	}

	public double getMinAzimuth() {
		return minAzimuth;
	}

	public double getMaxAzimuth() {
		return maxAzimuth;
	}

}
