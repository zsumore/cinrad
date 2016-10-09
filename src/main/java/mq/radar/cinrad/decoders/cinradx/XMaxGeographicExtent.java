package mq.radar.cinrad.decoders.cinradx;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

import mq.radar.cinrad.MQProjections;

public class XMaxGeographicExtent {

	/**
	 * Get Lat/Lon bounds for a provided product code
	 * 
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws NoSuchAuthorityCodeException
	 */
	public static Envelope getCinradExtent(double wsrLat, double wsrLon, double range)
			throws FactoryException, TransformException {

		// AlbersEquidistant radarProjection = new AlbersEquidistant(wsrLat +
		// 1.0,
		// wsrLat - 1.0, wsrLat, wsrLon, 1.0);
		MathTransform transform = CRS.findMathTransform(
				MQProjections.getInstance().getRadarCoordinateSystem(wsrLon, wsrLat),
				MQProjections.getInstance().getWGS84CoordinateSystem());

		double[] wsrAlbers = new double[2];
		double[] srcPts = { wsrLon, wsrLat };
		transform.transform(srcPts, 0, wsrAlbers, 0, 1);
		// double[] wsrAlbers = radarProjection.project(wsrLon, wsrLat);

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
		double minX = (cvtBounds[0][0] < cvtBounds[1][0]) ? cvtBounds[0][0] : cvtBounds[1][0];
		double minY = (cvtBounds[0][1] < cvtBounds[3][1]) ? cvtBounds[0][1] : cvtBounds[3][1];
		double maxX = (cvtBounds[2][0] > cvtBounds[3][0]) ? cvtBounds[2][0] : cvtBounds[3][0];
		double maxY = (cvtBounds[1][1] > cvtBounds[2][1]) ? cvtBounds[1][1] : cvtBounds[2][1];

		// System.out.println("MIN" + minX + " , " + minY);
		// System.out.println("MAX" + maxX + " , " + maxY);

		return (new Envelope(minX, minY, maxX, maxY));

	}

}
