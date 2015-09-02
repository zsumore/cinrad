package mq.radar.cinrad.decoders;

import mq.radar.cinrad.decoders.cinrad.CindarProducts;
import mq.radar.cinrad.decoders.cinrad.CinradHeader;
import mq.radar.cinrad.decoders.cinrad.MQProjections;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

public class MaxGeographicExtent {
	/**
	 * Get Lat/Lon bounds for a Cinrad Level-3 Product given the header object
	 * 
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws NoSuchAuthorityCodeException
	 */
	public static Envelope getCinradExtent(CinradHeader header)
			throws FactoryException, TransformException {
		return getCinradExtent(header.getLat(), header.getLon(),
				header.getProductCode());
	}

	/**
	 * Get Lat/Lon bounds for Default 230km Range Products (Base Reflectivity,
	 * etc...)
	 * 
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws NoSuchAuthorityCodeException
	 */
	public static Envelope getCinradExtent(double wsrLat, double wsrLon)
			throws FactoryException, TransformException {
		return getCinradExtent(wsrLat, wsrLon, 19);
	}

	/**
	 * Get Lat/Lon bounds for a provided product code
	 * 
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws NoSuchAuthorityCodeException
	 */
	public static Envelope getCinradExtent(double wsrLat, double wsrLon,
			int pcode) throws FactoryException, TransformException {

		// AlbersEquidistant radarProjection = new AlbersEquidistant(wsrLat +
		// 1.0,
		// wsrLat - 1.0, wsrLat, wsrLon, 1.0);
		MathTransform transform = CRS.findMathTransform(MQProjections
				.getInstance().getRadarCoordinateSystem(wsrLon, wsrLat),
				MQProjections.getInstance().getWGS84CoordinateSystem());

		double[] wsrAlbers = new double[2];
		double[] srcPts = { wsrLon, wsrLat };
		transform.transform(srcPts, 0, wsrAlbers, 0, 1);
		// double[] wsrAlbers = radarProjection.project(wsrLon, wsrLat);

		// Return a rectangle describing the 230km radius of the WSR Radars
		// Must check 4 corners to get true rectangle of desired projection -
		// Start with LL Corner
		double range = 245000.0;
		// Check for products with a range of 460km
		if (pcode == CindarProducts.BASE_REFLECTIVITY_20.getCode()
				|| pcode == CindarProducts.COMPOSITE_REFLECTIVITY_36.getCode()
				|| pcode == CindarProducts.COMPOSITE_REFLECTIVITY_38.getCode()) {
			range *= 2;
		} else if (pcode == CindarProducts.BASE_VELOCITY_26.getCode()) {
			range /= 2;
		} else if (pcode == CindarProducts.SEVERE_WEATHER_SHEAR_46.getCode()) {
			range /= 4.592;
		} else if (pcode == CindarProducts.BASE_VELOCITY_25.getCode()

		|| pcode == CindarProducts.BASE_SPECTRUM_WIDTH_28.getCode()) {
			range /= 3.875;
		} else if (pcode == CindarProducts.STORM_TRACKING_INFORMATION_58
				.getCode()) {
			range *= 1.5;
		}

		double[] cornersX = new double[4];
		double[] cornersY = new double[4];
		cornersX[0] = wsrAlbers[0] - range; // LL X
		cornersY[0] = wsrAlbers[1] - range; // LL Y
		cornersX[1] = wsrAlbers[0] - range; // UL X
		cornersY[1] = wsrAlbers[1] + range; // UL Y
		cornersX[2] = wsrAlbers[0] + range; // UR X
		cornersY[2] = wsrAlbers[1] + range; // UR Y
		cornersX[3] = wsrAlbers[0] + range; // LR X
		cornersY[3] = wsrAlbers[1] - range; // LR Y

		double[][] cvtBounds = new double[4][2];
		for (int i = 0; i < 4; i++) {
			double[] src = { cornersX[i], cornersY[i] };
			transform.transform(src, 0, cvtBounds[i], 0, 1);
			// cvtBounds[i] = radarProjection.unproject(cornersX[i],
			// cornersY[i]);
		}

		// Get min and max of x and y coordinates: this defines the corners in
		// new projection
		// We only have to test two points to find the max and mins
		double minX = (cvtBounds[0][0] < cvtBounds[1][0]) ? cvtBounds[0][0]
				: cvtBounds[1][0];
		double minY = (cvtBounds[0][1] < cvtBounds[3][1]) ? cvtBounds[0][1]
				: cvtBounds[3][1];
		double maxX = (cvtBounds[2][0] > cvtBounds[3][0]) ? cvtBounds[2][0]
				: cvtBounds[3][0];
		double maxY = (cvtBounds[1][1] > cvtBounds[2][1]) ? cvtBounds[1][1]
				: cvtBounds[2][1];

		// System.out.println("MIN" + minX + " , " + minY);
		// System.out.println("MAX" + maxX + " , " + maxY);

		return (new Envelope(minX, minY, maxX, maxY));

	}

}
