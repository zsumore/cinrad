package mq.radar.cinrad.projection;

import com.vividsolutions.jts.geom.Envelope;

public class AlbersEquidistant implements Projection {

	public final static int CHINACENTER = 0;
	public final static int CHINAEAST = 1;
	public final static int CHINAWEST = 2;
	
	public final static int GUANGDONG = 3;

	private final double R = 6371007;
	private double phi1, phi2, lamb0, S, latorigin;
	private double correction;

	// --------------------------------------------------------------------------------
	/**
	 * Construct default conversion using preset parameters for ConUS, AL and HI
	 * 
	 * @param dlat
	 *            Input Latitude
	 * @param dlon
	 *            Input Longitude
	 * @param Region
	 *            Preset Region to define Albers Projection ("Alaska", "Hawaii"
	 *            or "ConUS" (default))
	 */
	public AlbersEquidistant(int region) {
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
			
		case GUANGDONG:
			phi1 = 22.004;
			phi2 = 24.004;
			lamb0 = 113.355;
			S = 1.0;
			latorigin = 23.004;
			break;

		default:
			phi1 = 25;
			phi2 = 47;
			lamb0 = 110;
			S = 1.0;
			latorigin = 36;
			break;
		}
		correction = findCorrectionFactor();
	}

	// --------------------------------------------------------------------------------
	/**
	 * Construct conversion using custom parameters.<br>
	 * 
	 * @param lat
	 *            Input Latitude
	 * @param lon
	 *            Input Longitude
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
	// public AlbersEquidistant (double dlat, double dlon, double p1, double p2,
	// double lt0, double ln0, double S0)
	public AlbersEquidistant(double p1, double p2, double lt0, double ln0,
			double S0) {
		// Set up custom region
		phi1 = p1;
		phi2 = p2;
		lamb0 = ln0;
		latorigin = lt0;
		S = S0;
		correction = findCorrectionFactor();
	}

	private double findCorrectionFactor() {

		// Check for a latitude of origin that differs from the midpoint of the
		// two standard parallels
		if (latorigin != (phi1 + phi2) / 2) {
			// Find correction factor to adjust for latitude of origin

			double dlamb = 0; // (lamb0 - lamb0)

			// Convert everything to radians for use with sin and cos
			double dtlat = Math.toRadians(latorigin);
			// double dtdlon = Math.toRadians(lamb0);
			double tphi1 = Math.toRadians(phi1);
			double tphi2 = Math.toRadians(phi2);
			// double tlamb0 = Math.toRadians(lamb0);

			// Perform calculation upon initialization so it's only done once
			double rho1 = (2 * R * Math.cos(tphi1))
					/ (Math.sin(tphi1) + Math.sin(tphi2));
			double rho2 = (2 * R * Math.cos(tphi2))
					/ (Math.sin(tphi1) + Math.sin(tphi2));

			double f1 = (rho1 + rho2) / 2;
			double f2 = (Math.sin(tphi1) + Math.sin(tphi2)) / 2;
			double f3 = Math.sqrt((rho1 * rho1)
					+ (4 * R * R * (Math.sin(tphi1) - Math.sin(dtlat)) / (Math
							.sin(tphi1) + Math.sin(tphi2))));

			double work = dlamb * f2;
			work = Math.toRadians(work);

			return (S * (f1 - f3 * Math.cos(work)));
		} else
			return (0);
	} // END METHOD findCorrectionFactor

	public double[] project(double lon, double lat) {
		// do magic stuff
		double p[] = new double[2];

		double dlamb = lon - lamb0;

		// Convert everything to radians for use with sin and cos
		lat = Math.toRadians(lat);
		lon = Math.toRadians(lon);
		double phi1r = Math.toRadians(phi1);
		double phi2r = Math.toRadians(phi2);
		// double lamb0r=Math.toRadians(lamb0);

		// Perform calculation upon initialization so it's only done once
		double rho1 = (2 * R * Math.cos(phi1r))
				/ (Math.sin(phi1r) + Math.sin(phi2r));
		double rho2 = (2 * R * Math.cos(phi2r))
				/ (Math.sin(phi1r) + Math.sin(phi2r));

		double f1 = (rho1 + rho2) / 2;
		double f2 = (Math.sin(phi1r) + Math.sin(phi2r)) / 2;
		double f3 = Math.sqrt((rho1 * rho1)
				+ (4 * R * R * (Math.sin(phi1r) - Math.sin(lat)) / (Math
						.sin(phi1r) + Math.sin(phi2r))));

		double work = dlamb * f2;
		work = Math.toRadians(work);

		p[0] = S * f3 * Math.sin(work);
		p[1] = S * (f1 - f3 * Math.cos(work)) - correction;

		return p;
	}

	public double[] unproject(double x, double y) {

		double p[] = new double[2];
		// p[0] = lon
		// p[1] = lat

		// Convert everything to radians for use with sin and cos
		double phi1r = Math.toRadians(phi1);
		double phi2r = Math.toRadians(phi2);
		double lamb0r = Math.toRadians(lamb0);

		// Perform calculation upon initialization so it's only done once
		double rho1 = (2 * R * Math.cos(phi1r))
				/ (Math.sin(phi1r) + Math.sin(phi2r));
		double rho2 = (2 * R * Math.cos(phi2r))
				/ (Math.sin(phi1r) + Math.sin(phi2r));

		double f1 = (rho1 + rho2) / 2;
		double f2 = (Math.sin(phi1r) + Math.sin(phi2r)) / 2;

		// Adjust for the correction factor
		y += correction;
		// Convert to lat/lon
		p[0] = (1.0 / f2) * Math.atan(x / (S * f1 - y)) + lamb0r;
		double dlamb = p[0] - lamb0r;

		double work0 = Math.sin(phi1r);
		double work1 = f2 / (2.0 * R * R);
		double work2 = x / (S * Math.sin(dlamb * f2));

		p[1] = Math.asin(work0 - work1 * (work2 * work2 - rho1 * rho1));

		p[0] = Math.toDegrees(p[0]);
		p[1] = Math.toDegrees(p[1]);

		return p;

	}

	/**
	 * given a geographical extent work out the minimum bounding rectangle that
	 * contains that rectangle when projected - you may clip the rectangle
	 * returned to reflect what is sensible for this projection
	 */
	public Envelope projectedExtent(Envelope r) {

		double x = r.getMinX();
		double y = r.getMinY();
		double w = r.getWidth();
		double h = r.getHeight();
		double ll[] = project(x, y); // lower-left
		double ul[] = project(x, y + h); // upper-left
		double ur[] = project(x + w, y + h); // upper-right
		double lr[] = project(x + w, y); // lower-right

		// Get min and max of x and y coordinates: this defines the corners in
		// new projection
		// We only have to test two points to find the max and mins
		double minX = (ll[0] < ul[0]) ? ll[0] : ul[0];
		double minY = (ll[1] < lr[1]) ? ll[1] : lr[1];
		double maxX = (lr[0] > ur[0]) ? lr[0] : ur[0];
		double maxY = (ul[1] > ur[1]) ? ul[1] : ur[1];

		return (new Envelope(minX, minY, maxX, maxY));

	}

	public Envelope unprojectedExtent(Envelope r) {

		/*
		 * double x = r.getX(); double y = r.getY(); double w = r.getWidth();
		 * double h = r.getHeight(); double []lq = project(lamb0,latorigin);
		 * double upper=y+h; double b1[] = unproject(lq[0],Math.min(y,upper));
		 * double b2[] = unproject(x,y); double base = Math.max(b1[1],b2[1]);
		 * if(Double.isNaN(b1[1])) base = b2[1]; if(Double.isNaN(b2[1])) base =
		 * b1[1]; //System.out.println("base "+base); double l1[] =
		 * unproject(x,lq[1]); double l2[] = unproject(x,y); double left =
		 * Math.max(l1[0],l2[0]); double t1[] = unproject(lq[0],upper); double
		 * t2[] = unproject(x,upper); double top = Math.min(t1[1],t2[1]);
		 * if(Double.isNaN(upper)) top= 90.0; double r1[] =
		 * unproject(x+w,lq[1]); double r2[] = unproject(x+w,Math.min(y,upper));
		 * double right = Math.min(r1[0],r2[0]);
		 * 
		 * //System.out.println("unproj "+left+","+base+":"+right+","+top);
		 * GeoRectangle gr = new GeoRectangle(); gr.add(left,base);
		 * gr.add(right,top); return gr;
		 */

		double x = r.getMinX();
		double y = r.getMinY();
		double w = r.getWidth();
		double h = r.getHeight();
		double ll[] = unproject(x, y); // lower-left
		double ul[] = unproject(x, y + h); // upper-left
		double ur[] = unproject(x + w, y + h); // upper-right
		double lr[] = unproject(x + w, y); // lower-right

		// Get min and max of x and y coordinates: this defines the corners in
		// new projection
		// We only have to test two points to find the max and mins
		double minX = (ll[0] < ul[0]) ? ll[0] : ul[0];
		double minY = (ll[1] < lr[1]) ? ll[1] : lr[1];
		double maxX = (lr[0] > ur[0]) ? lr[0] : ur[0];
		double maxY = (ul[1] > ur[1]) ? ul[1] : ur[1];

		return (new Envelope(minX, minY, maxX, maxY));

	}

	public Envelope clipToSafe(Envelope r) {
		/*
		 * double x = r.getX(); double y = r.getY(); double w = r.getWidth();
		 * double h = r.getHeight(); double upper; if(y<-85) y=-85; if((y+h)>85)
		 * upper=85; else upper=y+h; GeoRectangle gr = new GeoRectangle();
		 * gr.add(x,y); gr.add(x+w,upper); return gr;
		 */
		return r;
	}
}
