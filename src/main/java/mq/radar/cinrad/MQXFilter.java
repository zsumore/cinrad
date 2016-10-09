package mq.radar.cinrad;

public class MQXFilter {

	public static final double NO_MIN_VALUE = Double.NEGATIVE_INFINITY;
	public static final double NO_MAX_VALUE = Double.POSITIVE_INFINITY;

	public static final double NO_MIN_AZIMUTH = 0.0;
	public static final double NO_MAX_AZIMUTH = 360.0;

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

	public void setAzimuthRange(double minA, double maxA) {
		if (Math.abs(maxA - minA) < 360) {
			if (maxA >= minA) {
				this.minAzimuth = minA;
				this.maxAzimuth = maxA;
			} else {
				this.minAzimuth = maxA;
				this.maxAzimuth = minA;
			}
			if (minAzimuth < 0) {
				minAzimuth = minAzimuth % 360 + 360;
			}
			if (maxAzimuth < 0) {
				maxAzimuth = maxAzimuth % 360 + 360;
			}
			if (minAzimuth > 360) {
				minAzimuth = minAzimuth % 360;
			}
			if (maxAzimuth > 360) {
				maxAzimuth = maxAzimuth % 360;
			}
			if (minAzimuth > maxAzimuth) {

				maxAzimuth += 360;
			}
		}
	}

	public double getMinAzimuth() {
		return minAzimuth;
	}

	public double getMaxAzimuth() {
		return maxAzimuth;
	}

}
