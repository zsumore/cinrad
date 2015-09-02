package mq.radar.cinrad.projection;

import com.vividsolutions.jts.geom.Envelope;

public interface Projection {

	public double[] project(double lon, double lat);

	public double[] unproject(double x, double y);

	/**
	 * given a geographical extent work out the minimum bounding rectangle that
	 * contains that rectangle when projected - you may clip the rectangle
	 * returned to reflect what is sensible for this projection
	 */
	public Envelope projectedExtent(Envelope r);

	public Envelope unprojectedExtent(Envelope r);

	public Envelope clipToSafe(Envelope r);
}
