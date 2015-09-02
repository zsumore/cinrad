/**
 * Converts Geographic Latitude and Longitude to Albers Equal Area Coordinates.
 * May be constructed as US, Alaska or Hawaii Albers
 * or projection parameters may be specified.<br><br>
 *
 * Separate classes used for forward and reverse conversions
 * to save time wasted from unneeded calculations.<br><br>
 *
 * Calculation is done when object is constructed.  This saves time wasted from performing 
 * the calculation each time the getX() or getY() methods are called.<br><br> 
 *
 * Equation taken from pg 114 of <br><B><U>
 * Map Projections: Theory and Applications</U></B> by <B>Frederick Pearson</B><br><br>
 *
 * All input angles must be in decimal degrees.<br>
 * Output Albers Coordinates are in meters.<br>
 * Earth Radius is defined as 6371007 meters.<br>
 *
 * @author Steve Ansari
 */

package mq.radar.cinrad.projection;

//************************************************************
public class Albers2LatLon {

	public final static int CHINACENTER = 0;
	public final static int CHINAEAST = 1;
	public final static int CHINAWEST = 2;

	private final double R = 6371007;
	private double phi1, phi2, lamb0, S, latorigin;
	private double dlamb, rho1, rho2, f1, f2, f3, work, correction;

	// private double dlat, dlon;

	/**
	 * Empty Constructor
	 */
	public Albers2LatLon() {
	}

	// --------------------------------------------------------------------------------
	/**
	 * Construct default conversion using preset parameters for ConUS, AL and HI
	 * 
	 * @param Region
	 *            Preset Region to define Albers Projection ("Alaska", "Hawaii"
	 *            or "ConUS" (default))
	 */
	// ------------------------------------------------------------------------------
	public Albers2LatLon(int region) {
		switch (region) {
		case CHINACENTER:
			phi1 = 25;
			phi2 = 47;
			lamb0 = 105;
			S = 1.0;
			latorigin = 36;
			break;

		case CHINAWEST:
			phi1 = 25;
			phi2 = 47;
			lamb0 = 95;
			S = 1.0;
			latorigin = 36;
			break;

		default:
			phi1 = 25;
			phi2 = 47;
			lamb0 = 110;
			S = 1.0;
			latorigin = 36;
			break;
		}

		// phi1=55; phi2=65; lamb0=-154; S=1.0; latorigin=50;
		// phi1=8; phi2=18; lamb0=-157; S=1.0; latorigin=13;
		// phi1=29.5; phi2=45.5; lamb0=-96; S=1.0; latorigin=37.5;

		// Check for a latitude of origin that differs from the midpoint of the
		// two standard parallels
		if (latorigin != (phi1 + phi2) / 2) {
			// Find correction factor to adjust for latitude of origin

			dlamb = 0; // (lamb0 - lamb0)

			// Convert everything to radians for use with sin and cos
			double dtlat = Math.toRadians(latorigin);
			// double dtdlon=Math.toRadians(lamb0);
			double tphi1 = Math.toRadians(phi1);
			double tphi2 = Math.toRadians(phi2);
			// double tlamb0=Math.toRadians(lamb0);

			// Perform calculation upon initialization so it's only done once
			rho1 = (2 * R * Math.cos(tphi1))
					/ (Math.sin(tphi1) + Math.sin(tphi2));
			rho2 = (2 * R * Math.cos(tphi2))
					/ (Math.sin(tphi1) + Math.sin(tphi2));

			f1 = (rho1 + rho2) / 2;
			f2 = (Math.sin(tphi1) + Math.sin(tphi2)) / 2;
			f3 = Math.sqrt((rho1 * rho1)
					+ (4 * R * R * (Math.sin(tphi1) - Math.sin(dtlat)) / (Math
							.sin(tphi1) + Math.sin(tphi2))));

			work = dlamb * f2;
			work = Math.toRadians(work);

			correction = S * (f1 - f3 * Math.cos(work));
		} else
			correction = 0;

		// Convert everything to radians for use with sin and cos
		phi1 = Math.toRadians(phi1);
		phi2 = Math.toRadians(phi2);
		lamb0 = Math.toRadians(lamb0);

		// Perform calculation upon initialization so it's only done once
		rho1 = (2 * R * Math.cos(phi1)) / (Math.sin(phi1) + Math.sin(phi2));
		rho2 = (2 * R * Math.cos(phi2)) / (Math.sin(phi1) + Math.sin(phi2));

		f1 = (rho1 + rho2) / 2;
		f2 = (Math.sin(phi1) + Math.sin(phi2)) / 2;

	}

	// --------------------------------------------------------------------------------
	/**
	 * Construct conversion using custom parameters.<br>
	 * 
	 * @param p1
	 *            Standard Parallel 1 (Must be higher latitude)
	 * @param p2
	 *            Standard Parallel 2 (p2 < p1)
	 * @param lt0
	 *            Latitude of Origin
	 * @param ln0
	 *            Central Meridian
	 * @param S0
	 *            Scale Ratio
	 */
	// ------------------------------------------------------------------------------
	public Albers2LatLon(double p1, double p2, double lt0, double ln0, double S0) {
		// Set up custom region
		phi1 = p1;
		phi2 = p2;
		lamb0 = ln0;
		latorigin = lt0;
		S = S0;

		// Check for a latitude of origin that differs from the midpoint of the
		// two standard parallels
		if (latorigin != (phi1 + phi2) / 2) {
			// Find correction factor to adjust for latitude of origin

			dlamb = 0; // (lamb0 - lamb0)

			// Convert everything to radians for use with sin and cos
			double dtlat = Math.toRadians(latorigin);
			// double dtdlon=Math.toRadians(lamb0);
			double tphi1 = Math.toRadians(phi1);
			double tphi2 = Math.toRadians(phi2);
			// double tlamb0=Math.toRadians(lamb0);

			// Perform calculation upon initialization so it's only done once
			rho1 = (2 * R * Math.cos(tphi1))
					/ (Math.sin(tphi1) + Math.sin(tphi2));
			rho2 = (2 * R * Math.cos(tphi2))
					/ (Math.sin(tphi1) + Math.sin(tphi2));

			f1 = (rho1 + rho2) / 2;
			f2 = (Math.sin(tphi1) + Math.sin(tphi2)) / 2;
			f3 = Math.sqrt((rho1 * rho1)
					+ (4 * R * R * (Math.sin(tphi1) - Math.sin(dtlat)) / (Math
							.sin(tphi1) + Math.sin(tphi2))));

			work = dlamb * f2;
			work = Math.toRadians(work);

			correction = S * (f1 - f3 * Math.cos(work));
		} else
			correction = 0;

		// Convert everything to radians for use with sin and cos
		phi1 = Math.toRadians(phi1);
		phi2 = Math.toRadians(phi2);
		lamb0 = Math.toRadians(lamb0);

		// Perform calculation upon initialization so it's only done once
		rho1 = (2 * R * Math.cos(phi1)) / (Math.sin(phi1) + Math.sin(phi2));
		rho2 = (2 * R * Math.cos(phi2)) / (Math.sin(phi1) + Math.sin(phi2));

		f1 = (rho1 + rho2) / 2;
		f2 = (Math.sin(phi1) + Math.sin(phi2)) / 2;

	}

	// ---------------------------------------------------------------------------------
	/**
	 * Method to extract the converted X-Y Coordinate. (Units=meters) Returns
	 * double[]: double[0] = lon , double[1] = lat
	 */
	// ---------------------------------------------------------------------------------
	public double[] convert(double x, double y) {
		double[] latlon = new double[2];
		// Adjust for the correction factor
		y += correction;
		// Convert to lat/lon
		latlon[0] = (1.0 / f2) * Math.atan(x / (S * f1 - y)) + lamb0;
		dlamb = latlon[0] - lamb0;

		double work0 = Math.sin(phi1);
		double work1 = f2 / (2.0 * R * R);
		double work2 = x / (S * Math.sin(dlamb * f2));

		latlon[1] = Math.asin(work0 - work1 * (work2 * work2 - rho1 * rho1));

		latlon[0] = Math.toDegrees(latlon[0]);
		latlon[1] = Math.toDegrees(latlon[1]);

		return latlon;
	}

}
