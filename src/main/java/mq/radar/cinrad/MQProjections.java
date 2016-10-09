package mq.radar.cinrad;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import mq.radar.cinrad.decoders.cinrad.CinradHeader;

public class MQProjections {

	private MQProjections() {

	}

	public static final String WGS84 = "GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Geodetic longitude\", EAST], AXIS[\"Geodetic latitude\", NORTH], AUTHORITY[\"EPSG\",\"4326\"]]";
	public static final String Beijing1954_WKT = "GEOGCS[\"Beijing 1954\", DATUM[\"Beijing 1954\", SPHEROID[\"Krassowsky 1940\", 6378245.0, 298.3, AUTHORITY[\"EPSG\",\"7024\"]], TOWGS84[15.8, -154.4, -82.3, 0.0, 0.0, 0.0, 0.0], AUTHORITY[\"EPSG\",\"6214\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], UNIT[\"degree\", 0.017453292519943295], AXIS[\"Geodetic longitude\", EAST], AXIS[\"Geodetic latitude\", NORTH], AUTHORITY[\"EPSG\",\"4214\"]]";
	private static MQProjections projection = null;

	/**
	 * Standard WGS84 Geographic Coordinate System
	 */
	// ESRI .prj file:
	// GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137.0,298.257223563]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]]
	public static final String WGS84_WKT = "GEOGCS[\"WGS84\", DATUM[\"WGS84\", SPHEROID[\"WGS84\", 6378137.0, 298.257223563]], "
			+ "PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\",0.017453292519943295], AXIS[\"Longitude\",EAST], AXIS[\"Latitude\",NORTH]]";

	public final static String WGS84_ESRI_PRJ = "GEOGCS[\"GCS_WGS_1984\"," + "DATUM[\"D_WGS_1984\","
			+ "SPHEROID[\"WGS_1984\",6378137.0,298.257223563]]," + "PRIMEM[\"Greenwich\",0.0],"
			+ "UNIT[\"Degree\",0.0174532925199433]]";

	/**
	 * Standard NAD83 Geographic Coordinate System - ESRI .prj string
	 */
	public static final String NAD83_ESRI_PRJ = "GEOGCS[\"GCS_North_American_1983\","
			+ "DATUM[\"D_North_American_1983\",SPHEROID[\"GRS_1980\",6378137,298.257222101]],"
			+ "PRIMEM[\"Greenwich\",0],UNIT[\"Degree\",0.0174532925199433]]";
	/**
	 * Standard NAD83 Geographic Coordinate System - NOAA STANDARD
	 */
	// ESRI .prj file:
	// GEOGCS["GCS_North_American_1983",DATUM["D_North_American_1983",SPHEROID["GRS_1980",6378137,298.257222101]],PRIMEM["Greenwich",0],UNIT["Degree",0.0174532925199433]]
	public static final String NAD83_WKT = "GEOGCS[\"NAD83\", DATUM[\"NAD83\", SPHEROID[\"GRS_1980\", 6378137.0, 298.25722210100002],TOWGS84[0,0,0,0,0,0,0]], "
			+ "PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\",0.017453292519943295], AXIS[\"Longitude\",EAST], AXIS[\"Latitude\",NORTH]]";

	/**
	 * Standard NAD27 Geographic Coordinate System
	 */
	// ESRI .prj file:
	// GEOGCS["GCS_North_American_1927",DATUM["D_North_American_1927",SPHEROID["Clarke_1866",6378206.4,294.9786982]],PRIMEM["Greenwich",0.0],UNIT["Degree",0.0174532925199433]]
	public static final String NAD27_WKT = "GEOGCS[\"NAD27\", DATUM[\"NAD27\", SPHEROID[\"Clarke_1866\", 6378206.4, 294.9786982],TOWGS84[0,0,0,0,0,0,0]], "
			+ "PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\",0.0174532925199433], AXIS[\"Longitude\",EAST], AXIS[\"Latitude\",NORTH]]";

	public final static String HRAP_POLAR_STEREOGRAPHIC_PRJ = "PROJCS[\"User_Defined_Stereographic_North_Pole\","
			+ "GEOGCS[\"GCS_User_Defined\",DATUM[\"D_User_Defined\",SPHEROID[\"User_Defined_Spheroid\",6371200.0,0.0]],"
			+ "PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],"
			+ "PROJECTION[\"Stereographic_North_Pole\"],PARAMETER[\"False_Easting\",0.0],"
			+ "PARAMETER[\"False_Northing\",0.0],PARAMETER[\"Central_Meridian\",-105.0],"
			+ "PARAMETER[\"Standard_Parallel_1\",60.0],UNIT[\"Meter\",1.0]]";

	/**
	 * HRAP Polar Stereographic Projection with Spherical Earth (6371007
	 * meters). <br>
	 * Parameters: <br>
	 * <br>
	 * latitude_of_origin = 60.0 <br>
	 * central_meridian = -105.0 <br>
	 */
	// public static final String HRAPSTEREO_WKT =
	// "PROJCS[\"Polar_Stereographic\",GEOGCS[\"Sphere\","+
	public static final String HRAPSTEREO_WKT = "PROJCS[\"Stereographic_North_Pole\",GEOGCS[\"Sphere\","
			+ "DATUM[\"Sphere\",SPHEROID[\"Sphere\",6371200.0,0],TOWGS84[0,0,0,0,0,0,0]],"
			+ "PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]]," + "PROJECTION[\"Polar_Stereographic\"],"
			+
			// "PROJECTION[\"Stereographic_North_Pole\"],"+
			"PARAMETER[\"false_easting\",0.0],PARAMETER[\"false_northing\",0.0],"
			+ "PARAMETER[\"central_meridian\",-105.0]," + "PARAMETER[\"latitude_of_origin\",60.0],UNIT[\"metre\",1.0]]";
			// "PARAMETER[\"latitude_of_origin\",23.117],UNIT[\"metre\",1.0]]";

	/*
	 * public static final String HRAPSTEREO_WKT =
	 * "PROJCS[\"User_Defined_Stereographic_North_Pole\","+
	 * "GEOGCS[\"GCS_User_Defined\",DATUM[\"D_User_Defined\",SPHEROID[\"User_Defined_Spheroid\",6371200.0,0.0]],"
	 * +
	 * "PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Stereographic_North_Pole\"],"
	 * + "PARAMETER[\"False_Easting\",0.0],PARAMETER[\"False_Northing\",0.0],"+
	 * "PARAMETER[\"Central_Meridian\",-105.0],"+
	 * //"PARAMETER[\"Standard_Parallel_1\",60.0],"+
	 * "PARAMETER[\"latitude_of_origin\",60.0],"+ "UNIT[\"Meter\",1.0]]";
	 */
	/**
	 * Albers Equal-Area ConUS Projection with NAD83 Datum. <br>
	 * Parameters: <br>
	 * <br>
	 * standard_parallel_1 = 29.5 <br>
	 * standard_parallel_2 = 45.5 <br>
	 * latitude_of_origin = 37.5 <br>
	 * central_meridian = -96.0 <br>
	 */
	public static final String ALBERS_EQUALAREA_CONUS_NAD83_WKT = "PROJCS[\"Albers_Conic_Equal_Area\",GEOGCS[\"NAD83\","
			+ "DATUM[\"NAD83\",SPHEROID[\"GRS_1980\",6378137.0,298.25722210100002],TOWGS84[0,0,0,0,0,0,0]],"
			+ "PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.017453292519943295]],"
			+ "PROJECTION[\"Albers_Conic_Equal_Area\"],"
			+ "PARAMETER[\"false_easting\",0.0],PARAMETER[\"false_northing\",0.0],"
			+ "PARAMETER[\"central_meridian\",-96.0]," + "PARAMETER[\"standard_parallel_1\",29.5],"
			+ "PARAMETER[\"standard_parallel_2\",45.5],"
			+ "PARAMETER[\"latitude_of_origin\",37.5],UNIT[\"metre\",1.0]]";

	public static MQProjections getInstance() {
		if (projection == null)
			projection = new MQProjections();

		return projection;
	}

	public CoordinateReferenceSystem getWGS84CoordinateSystem() throws FactoryException {

		return CRS.parseWKT(WGS84_WKT);
	}

	public CoordinateReferenceSystem getBeijing1954CoordinateSystem() throws FactoryException {

		return CRS.parseWKT(Beijing1954_WKT);
	}

	/**
	 * Gets a CoordinateSystem given a CinradHeader object that provides lat and
	 * lon of the Radar site. <br>
	 * <br>
	 * The CoordinateSystem is an AlbersEqualArea projection with the secant
	 * latitudes at +- 1 deg from the Radar site to a WGS84 unprojected
	 * (Lon,Lat) Geographic Coordinate System.
	 * 
	 * @param lon
	 *            lon for radar site.
	 * @param lat
	 *            lat for radar site.
	 * @return The MathTransform object
	 */

	public CoordinateReferenceSystem getRadarCoordinateSystem(CinradHeader header) throws FactoryException {

		return getRadarCoordinateSystem(header.getLon(), header.getLat());
	}

	/**
	 * Gets a CoordinateSystem given a CinradHeader object that provides lat and
	 * lon of the Radar site. <br>
	 * <br>
	 * The CoordinateSystem is an AlbersEqualArea projection with the secant
	 * latitudes at +- 1 deg from the Radar site to a WGS84 unprojected
	 * (Lon,Lat) Geographic Coordinate System.
	 * 
	 * @param header
	 *            CinradHeader object that provides central lon and lat
	 *            coordinates for radar site.
	 * @return The MathTransform object
	 * @throws FactoryException
	 */
	// EPSG:3786 5072 3513
	public CoordinateReferenceSystem getRadarCoordinateSystem(double lon, double lat) throws FactoryException {
		String wsrWKT = "PROJCS[\"Albers_Conic_Equal_Area\",GEOGCS[\"NAD83\","
				+ "DATUM[\"NAD83\",SPHEROID[\"GRS_1980\",6378137.0,298.25722210100002],TOWGS84[0,0,0,0,0,0,0]],"
				+ "PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.017453292519943295]],"
				+ "PROJECTION[\"Albers_Conic_Equal_Area\"],"
				+ "PARAMETER[\"false_easting\",0.0],PARAMETER[\"false_northing\",0.0],"
				+ "PARAMETER[\"central_meridian\"," + lon + "],PARAMETER[\"standard_parallel_1\"," + (lat - 1.0)
				+ "],PARAMETER[\"standard_parallel_2\"," + (lat + 1.0) + "],PARAMETER[\"latitude_of_origin\"," + lat
				+ "],UNIT[\"metre\",1.0]]";

		return CRS.parseWKT(wsrWKT);
	}

	/**
	 * Gets a MathTransform given a NexradHeader object that provides lat and
	 * lon of the Radar site. <br>
	 * <br>
	 * MathTransform designates a the transform from a AlbersEqualArea
	 * projection with the secant latitudes at +- 1 deg from the Radar site to a
	 * WGS84 unprojected (Lon,Lat) Geographic Coordinate System.
	 * 
	 * @param header
	 *            NexradHeader object that provides central lon and lat
	 *            coordinates for radar site.
	 * @return The MathTransform object
	 * @throws FactoryException
	 */
	public MathTransform getRadarTransform(CinradHeader header) throws FactoryException {

		return getRadarTransform(header.getLon(), header.getLat());
	}

	/**
	 * Gets a MathTransform given the WGS84 lat and lon of the Radar site. <br>
	 * <br>
	 * MathTransform designates a the transform from a AlbersEqualArea
	 * projection with the secant latitudes at +- 1 deg from the Radar site to a
	 * WGS84 unprojected (Lon,Lat) Geographic Coordinate System.
	 * 
	 * @param header
	 *            NexradHeader object that provides central lon and lat
	 *            coordinates for radar site.
	 * @return The MathTransform object
	 * @throws FactoryException
	 */
	public MathTransform getRadarTransform(double lon, double lat) throws FactoryException {

		CoordinateReferenceSystem inCS = getRadarCoordinateSystem(lon, lat);
		CoordinateReferenceSystem outCS = getWGS84CoordinateSystem();

		return CRS.findMathTransform(inCS, outCS);

	}

	public CoordinateReferenceSystem getCoordinateSystemByProjectionType(ProjectionType type, double lon, double lat)
			throws FactoryException {
		switch (type) {
		case AZIMUTHAL_EQUIDISTANT:
			return getRadarCoordinateSystem(lon, lat);

		case WGS84:

			return getWGS84CoordinateSystem();

		case BEIJING_1954:
			return getBeijing1954CoordinateSystem();

		default:
			return null;

		}

	}

}
