package mq.radar.cinrad.decoders.cinrad;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import mq.radar.cinrad.MQFilter;
import mq.radar.cinrad.MQProjections;
import mq.radar.cinrad.common.Hex;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;
import mq.radar.cinrad.decoders.StreamingProcess;
import mq.radar.cinrad.event.DataDecodeEvent;
import mq.radar.cinrad.event.DataDecodeListener;

import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

/**
 * Decodes CinRAD Level-III Data into OGC Geotools Features. Data is stored in
 * memory as a FeatureCollection of polygons.
 * 
 */
public class DecodeStreamingRadial extends BaseCindarDecoder {
	private final Logger logger = LoggerFactory.getLogger(DecodeStreamingRadial.class);

	private final double geometryBuffer = 0.00006;
	private final double geometrySimplify = 0.0001;

	private Map<Integer, Vector<Polygon>> polyVector;

	private Vector<Coordinate> coordinates;

	// The list of event listeners.
	private Vector<DataDecodeListener> listeners;

	// Global variables
	private ucar.unidata.io.RandomAccessFile f;
	private DataDecodeEvent event;
	private MQFilter nxfilter;
	private int geoIndex;

	private MathTransform cinradTransform;

	// private String[] supplementalData;

	/**
	 * Constructor for the DecodeL3Nexrad object
	 * 
	 * @param header
	 *            A DecodeL3Header object
	 * @throws FactoryException
	 * @throws IOException
	 * @throws DecodeException
	 */
	public DecodeStreamingRadial(CinradHeader header) throws DecodeException, IOException, FactoryException {
		super(header);
		this.decoderName = "DecodeStreamingRadial";

		// to define the attributes managed by the NexradFilter class
		decodeHints.put("cinradFilter", new MQFilter());
		// Use JTS Geometry.buffer(0.0) to combine adjacent polygons
		decodeHints.put("reducePolygons", true);
	}

	@Override
	public String[] getSupplementalDataArray() throws IOException {
		logger.debug("LOADING SUPPLEMENTAL DATA... URL=" + header.getCinradURL().toString());

		NetcdfFile ncfile = NetcdfFile.open(header.getCinradURL().toString());
		try {

			Variable var = ncfile.findVariable("TabMessagePage");
			if (var == null) {
				return null;
			}
			Array data = var.read();
			Index index = data.getIndex();
			int[] shape = data.getShape();
			// logger.fine("Data Array Dimensions: ");
			if (logger.isDebugEnabled()) {
				for (int n = 0; n < shape.length; n++) {
					logger.debug("TabMessagePage Dimension[" + n + "] " + shape[n]);
				}
			}
			supplementalData = new String[shape[0]];

			for (int n = 0; n < shape[0]; n++) {
				// logger.fine("-------------- n="+n);
				String pageString = data.getObject(index.set(n)).toString();
				// logger.fine(pageString);

				supplementalData[n] = pageString;
			}

		} finally {
			// System.out.println("CLOSING NETCDF SUP DATA READ");
			ncfile.close();
		}

		return supplementalData;
	}

	@Override
	public void decodeData(StreamingProcess[] processArray, boolean autoClose) throws DecodeException, IOException {

		polyVector = new HashMap<Integer, Vector<Polygon>>();
		coordinates = new Vector<Coordinate>();
		listeners = new Vector<DataDecodeListener>();
		try {
			SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
			builder.setCRS(crs);
			builder.setName("Radial or Raster Data");
			builder.add("geom", Geometry.class);
			builder.nillable(true).length(5).add("value", Float.class);
			builder.nillable(true).length(4).add("colorIndex", Integer.class);
			schema = builder.buildFeatureType();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		this.nxfilter = (MQFilter) decodeHints.get("cinradFilter");
		boolean reducePolys = (Boolean) decodeHints.get("reducePolygons");

		event = new DataDecodeEvent(this);

		// Start decode
		// --------------
		for (int i = 0; i < listeners.size(); i++) {
			System.out.println(listeners.size());
			event.setProgress(0);
			listeners.get(i).decodeStarted(event);
		}

		if (features == null)
			features = new DefaultFeatureCollection();
		features.clear();

		for (int i = 0; i < 16; i++) {
			polyVector.put(i, new Vector<Polygon>());
		}

		// Reset index counter
		geoIndex = 0;

		try {
			cinradTransform = MQProjections.getInstance().getRadarTransform(header);
		} catch (FactoryException e1) {
			logger.error("FactoryException", e1);
			throw new DecodeException("PROJECTION TRANSFORM ERROR", header.getCinradURL());
		}

		// Initiate binary buffered read
		f = header.getRandomAccessFile();

		int pcode = header.getProductCode();

		try {

			logger.debug("======== DECODE DATA 0 =========: pcode=" + pcode);
			logger.debug("======== DECODE DATA 0 =========: pcode=" + header.getProduct().getProductType());

			ProductType pType = header.getProduct().getProductType();

			switch (pType) {
			case L3RADIAL:
				decodeRadial();
				break;
			case L3RASTER:
				decodeRaster();
				break;
			default:

			}

			// logger.fine("======== DECODE DATA 2 =========");
			if (autoClose)
				f.close();
			// Close connection;

			if (reducePolys) {
				logger.debug("REDUCING POLYGONS!");
				// Reduce number of polygons by applying a 0 distance buffer to
				// each vector of polygons
				GeometryCollection[] polyCollections = new GeometryCollection[16];
				for (int i = 0; i < 16; i++) {
					if (polyVector.get(i).size() > 0) {
						Polygon[] polyArray = new Polygon[polyVector.get(i).size()];
						polyCollections[i] = geoFactory
								.createGeometryCollection((Polygon[]) (polyVector.get(i).toArray(polyArray)));
						Geometry union = polyCollections[i].buffer(geometryBuffer);

						union = TopologyPreservingSimplifier.simplify(union, geometrySimplify);

						logger.debug("Geometry Type:" + union.getGeometryType());

						polyCollections[i] = null;
						// Geometry union = (Geometry)polyCollections[i];

						Float value = getFloatDataThreshold(i);
						Integer color = new Integer(i);

						try {
							// create the feature
							SimpleFeature feature = SimpleFeatureBuilder.build(schema,
									new Object[] { (Geometry) union, value, color },
									new Integer(geoIndex++).toString());
							for (int n = 0; n < processArray.length; n++) {
								processArray[n].addFeature(feature);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			} else {
				// GeometryCollection[] polyCollections = new
				// GeometryCollection[16];
				for (int i = 0; i < polyVector.size(); i++) {
					Integer color = new Integer(i);
					Float value = getFloatDataThreshold(i);

					logger.debug(header.getDataThresholdString(i) + "   " + value);

					logger.debug("polyVector {} size:{}", i, polyVector.get(i).size());

					logger.debug("schema {} attributeCount:{}", i, schema.getAttributeCount());

					for (int j = 0; j < polyVector.get(i).size(); j++) {
						try {
							// logger.fine(color);
							// create the feature
							SimpleFeature feature = SimpleFeatureBuilder.build(schema,
									new Object[] { (Geometry) polyVector.get(i).elementAt(j), value, color },
									new Integer(geoIndex++).toString());

							for (int n = 0; n < processArray.length; n++) {
								processArray[n].addFeature(feature);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
		// END try
		catch (Exception e) {
			logger.error("CAUGHT EXCEPTION:  " + e);
			e.printStackTrace();

			try {
				f.close();
			} catch (Exception eee) {
				e.printStackTrace();
			}

			e.printStackTrace();
			throw new DecodeException("CAUGHT EXCEPTION:  \n" + e + "\n--- THIS DATA IS POSSIBLY CORRUPT ---",
					header.getCinradURL());

		} finally {

			for (int n = 0; n < processArray.length; n++) {
				try {
					processArray[n].close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// End decode
			// --------------
			for (int i = 0; i < listeners.size(); i++) {
				event.setProgress(100);
				listeners.get(i).decodeEnded(event);
			}

		}

	}

	private void decodeRaster() throws IOException, TransformException {
		logger.info("Decode Raster Starting ");
		// rewind
		f.seek(0);

		while (f.readShort() != -1) {
			;
		}

		// ADVANCE TO BEGINNING OF PRODUCT SYMBOLOGY BLOCK (BLOCK DIVIDER)
		while (f.readShort() != -1) {
			;
		}

		logger.info("FILE POS: " + f.getFilePointer());

		short blockID = f.readShort();
		logger.debug("blockID: " + blockID);
		int blockLen = f.readInt();
		logger.debug("blockLen: " + blockLen);
		short numLayers = f.readShort();
		logger.debug("numLayers: " + numLayers);
		// ADVANCE TO LAYER DIVIDER
		while (f.readShort() != -1) {
			;
		}
		int layerLen = f.readInt();
		logger.debug("layerLen: " + layerLen);
		/*
		 * / Decode Date and Time hour = (short)(uniqueInfo[4]/60.0); minute =
		 * (short)(uniqueInfo[4]%60.0); yyyymmdd =
		 * convertJulianDate(uniqueInfo[3]); logger.fine("LAT: "+lat);
		 * logger.fine("LON: "+lon); logger.fine("ALT: "+alt); logger.fine(
		 * "MAX: "+uniqueInfo[0]); logger.fine("BIAS: "+uniqueInfo[1]);
		 * logger.fine("ERR.VAR.: "+uniqueInfo[2]); logger.fine(yyyymmdd+" "
		 * +hour+":"+minute);
		 */
		// -------------------------------*
		// Decode the actual RLE data
		// -------------------------------*
		// dataHeader[0] = PACKET CODE -- SHOULD == ?
		// dataHeader[1] = PACKET CODE FOR OP FLAG 1
		// dataHeader[2] = PACKET CODE FOR OP FLAG 2
		// dataHeader[3] = I START COORDINATE
		// dataHeader[4] = J START COORDINATE
		// dataHeader[5] = X SCALE (INT)
		// dataHeader[6] = X SCALE (FRACTION)
		// dataHeader[7] = Y SCALE (INT)
		// dataHeader[8] = Y SCALE (FRACTION)
		// dataHeader[9] = NUMBER OF ROWS
		// dataHeader[10] = PACKING DESCRIPTOR (Always == 2)
		short[] dataHeader = new short[11];
		for (int i = 0; i < 11; i++) {
			dataHeader[i] = f.readShort();
		}

		String packetCodeHex = Hex.toHex(dataHeader[0]);
		logger.debug("RASTER: dataHeader[0] HEX: " + packetCodeHex);
		logger.debug("RASTER: dataHeader[1] HEX: " + Hex.toHex(dataHeader[1]));
		logger.debug("RASTER: dataHeader[2] HEX: " + Hex.toHex(dataHeader[2]));

		if (logger.isDebugEnabled()) {
			for (int i = 3; i < 11; i++) {
				logger.debug("RASTER dataHeader[" + i + "] = " + dataHeader[i]);
			}
		}

		int range = 230000;

		double startingX;

		double startingY;
		if (dataHeader[5] == 4) {
			startingX = ((double) dataHeader[3] + 0.5) * 1000.0 * (double) dataHeader[5];
		} else {
			startingX = ((double) dataHeader[3] + 1.0) * 1000.0 * (double) dataHeader[5];
		}

		if (dataHeader[7] == 4) {
			startingY = ((double) dataHeader[4] + 0.5) * 1000.0 * (double) dataHeader[7];
		} else {
			startingY = ((double) dataHeader[4] + 1.0) * 1000.0 * (double) dataHeader[7];
		}

		if (header.getProductCode() == 38) {
			range *= 2;
			// startingX *= 2;
			// startingY *= 2;
			// startingY += 4000;
			dataHeader[5] *= 2;
			dataHeader[7] *= 2;
		}
		logger.debug("Product Range: " + range);
		short numBytesInRow;
		int data;
		int numBins;
		int colorCode;
		short runTotal;
		// double[] geoXY;
		double[] albX;
		double[] albY;
		// Begin OUTER LOOP for ROWS

		for (int y = 0; y < dataHeader[9]; y++) {
			logger.debug("Radar Create: " + y);
			numBytesInRow = f.readShort();

			// logger.fine("numBytesInRow = "+numBytesInRow);

			runTotal = 0;
			for (int n = 0; n < numBytesInRow; n++) {

				// Read a byte
				data = f.readUnsignedByte();

				// ========================================================================/
				// Extract the 4-bit Run and Code values (0-15) from the 8-bit
				// data values
				// ========================================================================/
				// *** Example *****************************************
				// *** 1010 0011 ***************************************
				// *** numBins = 1010 = 10 ***
				// *** colorCode = 0011 = 3 ***
				// *** data[n] >> 4 == 1010 == numBins ***
				// *** numBins << 4 == 1010 0000 ***
				// *** 1010 0000 - 1010 0011 ***
				// *** == 0000 0011 == colorCode == 3 ***
				// ========================================================================/
				// Isolate the first 4 bits by knocking off the last 4
				numBins = (int) data >> 4;
				// Isolate the last 4 bits by adding 4 blank bits and
				// subtracting
				colorCode = (int) data - (numBins << 4);
				
				System.out.println("------------------colorCode-------------------------");
				System.out.println(colorCode);

				boolean colorCodeTest;
				// Check category filter from NexradFilter
				if (nxfilter == null) {
					colorCodeTest = (colorCode > 0);
				} else {
					colorCodeTest = false;
					int[] categoryIndices = nxfilter.getValueIndices();
					if (categoryIndices == null || categoryIndices.length == 0) {
						colorCodeTest = (colorCode > 0);
					} else {
						for (int z = 0; z < categoryIndices.length; z++) {
							// add 1 because colorCode(0) is not in pop list
							if (categoryIndices[z] == colorCode) {
								colorCodeTest = true;
							}
						}
					}
				}
				// Only create polygons if colorCode is > 0
				if (colorCodeTest) {
					// if (colorCode > 0) {

					// Vector coordinates = new Vector();
					coordinates.clear();

					// logger.fine("numBins= "+numBins+" ***** colorCode=
					// "+colorCode);

					// If xrun > 1 then create polygon from to encircle each
					// grid cell during run
					// 2 grid cells = 6 points ; 3 grid cells = 8 points ; 4 gc
					// = 10 pnts ; etc...
					// This eliminates empty slivers caused from the projection
					// transformation between >1 cell polygons
					// Add points in this order: 2 3 4 5 (if xrun==4)
					// 1 8 7 6

					albX = new double[2 + numBins * 2];
					albY = new double[2 + numBins * 2];

					albX[0] = runTotal * 1000 * dataHeader[5] - range - startingX;
					albY[0] = range + startingY - (y + 1) * 1000 * dataHeader[7];
					albX[1] = runTotal * 1000 * dataHeader[5] - range - startingX;
					albY[1] = range + startingY - y * 1000 * dataHeader[7];

					for (int nr = 0; nr < numBins; nr++) {
						albX[2 + nr] = (runTotal + nr + 1) * 1000 * dataHeader[5] - range - startingX;
						albY[2 + nr] = range + startingY - y * 1000 * dataHeader[7];
					}
					for (int nr = numBins - 1; nr >= 0; nr--) {
						albX[2 + numBins + (numBins - 1 - nr)] = (runTotal + nr + 1) * 1000 * dataHeader[5] - range
								- startingX;
						albY[2 + numBins + (numBins - 1 - nr)] = range + startingY - (y + 1) * 1000 * dataHeader[7];
					}

					for (int nr = 0; nr < 2 + numBins * 2; nr++) {
						// Avoid NaN errors
						if (Math.abs(albY[nr]) < .05) {
							albY[nr] = .05;
						}
						if (Math.abs(albX[nr]) < .05) {
							albX[nr] = .05;
						}
						// Convert to Lat/Lon and add to vector list
						double[] srcPts = { albX[nr], albY[nr] };
						double[] dstPts = new double[2];
						cinradTransform.transform(srcPts, 0, dstPts, 0, 1);
						coordinates.addElement(new Coordinate(dstPts[0], dstPts[1]));
					}
					// Add the first point again to close polygon

					double[] srcPts = { albX[0], albY[0] };
					double[] dstPts = new double[2];
					cinradTransform.transform(srcPts, 0, dstPts, 0, 1);

					coordinates.addElement(new Coordinate(dstPts[0], dstPts[1]));

					try {

						Coordinate[] cArray = new Coordinate[coordinates.size()];
						logger.debug("Coordinate Size: " + coordinates.size());
						LinearRing lr = geoFactory.createLinearRing(coordinates.toArray(cArray));
						Polygon poly = geoFactory.createPolygon(lr, null);

						if (nxfilter == null || nxfilter.accept(poly)) {

							if (nxfilter != null) {
								poly = (Polygon) (nxfilter.clipToExtentFilter(poly));
							}

							if (poly != null) {
								polyVector.get(colorCode).addElement(poly);
							}

						}

					} catch (Exception e) {
						logger.error("Exception: " + e);
						e.printStackTrace();
					}

					// y+=1000000; // END LOOP AFTER DRAWING ONE ROW

				}
				// END if (colorCode > 0)
				runTotal += numBins;

			}
			// END Bin Loop
			// y=100000000;

			// Decode progress
			// --------------
			for (int n = 0; n < listeners.size(); n++) {
				event.setProgress((int) ((((double) y) / dataHeader[9]) * 100.0));
				listeners.get(n).decodeProgress(event);
			}
		}

	}

	private void decodeRadial() throws TransformException, IOException {
		// rewind
		f.seek(0);

		// ADVANCE TO BEGINNING OF PRODUCT SYMBOLOGY BLOCK (BLOCK DIVIDER)
		while (f.readShort() != -1) {
			;
		}

		// f.seek(152);

		logger.debug("FILE POS: " + f.getFilePointer());
		short blockID = f.readShort();
		// int blockLen = f.readInt();
		f.readInt();
		short numLayers = f.readShort();

		if (blockID < 0 || blockID > 20 || numLayers < 0 || numLayers > 100) {
			// we must be one divider short
			// this sometimes occurs with N*S files
			while (f.readShort() != -1) {
				;
			}
			blockID = f.readShort();
			// blockLen = f.readInt();
			f.readInt();
			numLayers = f.readShort();
		}

		// ADVANCE TO LAYER DIVIDER
		while (f.readShort() != -1) {
			;
		}
		// int layerLen = f.readInt();
		f.readInt();
		short[] dataHeader = new short[7];
		for (int i = 0; i < 7; i++) {
			dataHeader[i] = f.readShort();
			logger.debug("dataHeader[" + i + "]=" + dataHeader[i]);
		}

		String packetCodeHex = Hex.toHex(dataHeader[0]);
		logger.debug("RADIAL: dataHeader[0] HEX: " + packetCodeHex);
		if (packetCodeHex.equalsIgnoreCase("BA0F") || packetCodeHex.equalsIgnoreCase("BA07")) {

			logger.debug("SENDING TO RASTER DECODER");

			f.seek(0);
			decodeRaster();
			return;
		}

		double binSpacing = dataHeader[5];

		if (header.getProductCode() == 25 || header.getProductCode() == 28 || header.getProductCode() == 44) {
			binSpacing /= 3.875;
		}

		if (header.getProductCode() == 26) {
			binSpacing /= 2;
		}

		if (header.getProductCode() == 46) {
			binSpacing /= 8;
		}

		if (header.getProductCode() == 20) {
			binSpacing *= 2;
		}

		logger.info("------------binSpacing:{}", binSpacing);

		// if (true) return (CalcNexradExtent.getNexradExtent(header));

		short numHalfwords;
		short startAngle;
		short deltaAngle;
		int data;
		int numBins;
		int colorCode;
		short runTotal;
		// double[] geoXY;
		boolean first = true;
		double savedStartAngle = 0.0;
		double angle1;
		double angle2;
		double[] albX;
		double[] albY;

		// logger.fine("======== DECODE DATA 1 =========");

		// Add distance filter - find starting and ending bin numbers
		boolean colorCodeTest;
		int startbin;
		int endbin;
		int startRun;
		if (nxfilter != null) {
			startbin = (int) (((nxfilter.getMinDistance() * 1000 - binSpacing) / binSpacing) + 0.01);
			endbin = (int) (((nxfilter.getMaxDistance() * 1000 - binSpacing) / binSpacing) + 0.01);
			if (startbin < 0 || nxfilter.getMinDistance() == MQFilter.NO_MIN_DISTANCE) {
				startbin = 0;
			}
			if (nxfilter.getMaxDistance() == MQFilter.NO_MAX_DISTANCE) {
				endbin = 1000000;
			}
			startbin++;
		} else {
			startbin = 0;
			endbin = 1000000;
		}

		// Begin OUTER LOOP for RADIALS
		for (int y = 0; y < dataHeader[6]; y++) {
			numHalfwords = f.readShort();
			startAngle = f.readShort();
			deltaAngle = f.readShort();

			if (first) {
				first = false;
				savedStartAngle = 90.0 - (double) startAngle / 10.0;
				if (savedStartAngle < 0) {
					savedStartAngle += 360;
				}
			}

			angle1 = 90.0 - (double) startAngle / 10.0;
			angle2 = 90.0 - ((double) startAngle / 10.0 + (double) deltaAngle / 10.0);

			if (angle1 < 0) {
				angle1 += 360;
			}
			if (angle2 < 0) {
				angle2 += 360;
			}

			// When we are done with 360 degrees STOP! (Files keep going
			// sometimes)
			if (angle2 < savedStartAngle && y > 300 && Math.abs(angle2 - savedStartAngle) < 100) {
				angle2 = savedStartAngle;
				y = 10000000;
			}

			// Add .00000001 to any 0, 90, 180, 270, 360 values to prevent sin
			// or cos error
			if (angle1 == 0.0 || angle1 == 90.0 || angle1 == 180.0 || angle1 == 270.0 || angle1 == 360.0) {
				angle1 += 0.00001;
			}
			if (angle2 == 0.0 || angle2 == 90.0 || angle2 == 180.0 || angle2 == 270.0 || angle2 == 360.0) {
				angle2 += 0.00001;
			}

			angle1 = Math.toRadians(angle1);
			angle2 = Math.toRadians(angle2);

			runTotal = 0;
			for (int n = 0; n < 2 * numHalfwords; n++) {
				// Read a byte
				data = f.readUnsignedByte();
				// ========================================================================/
				// Extract the 4-bit Run and Code values (0-15) from the 8-bit
				// data values
				// ========================================================================/
				// *** Example *****************************************/
				// *** 1010 0011 ***************************************/
				// *** numBins = 1010 = 10 ***/
				// *** colorCode = 0011 = 3 ***/
				// *** data[n] >> 4 == 1010 == numBins ***/
				// *** numBins << 4 == 1010 0000 ***/
				// *** 1010 0000 - 1010 0011 ***/
				// *** == 0000 0011 == colorCode == 3 ***/
				// ========================================================================/
				// Isolate the first 4 bits by knocking off the last 4
				numBins = (int) data >> 4;
				// Isolate the last 4 bits by adding 4 blank bits and
				// subtracting
			    //System.out.println("--------------------------------colorcode");
			    //TODO
				colorCode = (int) data - (numBins << 4) -2;
				//System.out.println(colorCode);
				// Ignore the first bin from the wsr
				if (runTotal == 0) {
					numBins--;
					runTotal = 1;
				}
				// -------------- FILTER STUFF -----------------------
				// set the minimum distance limit
				if (runTotal < startbin && (runTotal + numBins) >= startbin) {
					startRun = startbin - runTotal;
				} else {
					startRun = 0;
				}

				// Only create polygons if colorCode is > 0
				// if (colorCode > 0 && numBins > 0) {

				if (nxfilter == null) {
					colorCodeTest = (colorCode > 0);
				}

				else {
					colorCodeTest = false;
					int[] categoryIndices = nxfilter.getValueIndices();
					if (categoryIndices == null || categoryIndices.length == 0) {
						colorCodeTest = (colorCode > 0);
					} else {
						for (int z = 0; z < categoryIndices.length; z++) {
							// add 1 because colorCode(0) is not in pop list
							if (categoryIndices[z] == colorCode) {
								colorCodeTest = true;
							}
						}
					}
				}

				if (colorCodeTest && numBins > 0 && (runTotal + numBins) >= startbin && runTotal < endbin
						&& numBins - startRun > 0) {
					coordinates.clear();

					// set the maximum distance limit
					if (runTotal + numBins > endbin) {
						numBins = endbin - runTotal;
					}

					// logger.fine("numBins= "+numBins+" ***** colorCode=
					// "+colorCode);

					// If xrun > 1 then create polygon from to encircle each
					// grid cell during run
					// 2 grid cells = 6 points ; 3 grid cells = 8 points ; 4 gc
					// = 10 pnts ; etc...
					// This eliminates empty slivers caused from the projection
					// transformation between >1 cell polygons
					// Add points in this order: 2 3 4 5 (if xrun==4)
					// 1 8 7 6

					albX = new double[2 + (numBins - startRun) * 2];
					albY = new double[2 + (numBins - startRun) * 2];

					albX[0] = (runTotal + startRun) * binSpacing * Math.cos(angle2);
					albY[0] = (runTotal + startRun) * binSpacing * Math.sin(angle2);
					albX[1] = (runTotal + startRun) * binSpacing * Math.cos(angle1);
					albY[1] = (runTotal + startRun) * binSpacing * Math.sin(angle1);

					for (int nr = 0; nr < numBins - startRun; nr++) {
						albX[2 + nr] = (runTotal + startRun + nr + 1) * binSpacing * Math.cos(angle1);
						albY[2 + nr] = (runTotal + startRun + nr + 1) * binSpacing * Math.sin(angle1);
					}
					for (int nr = numBins - startRun - 1; nr >= 0; nr--) {
						albX[2 + numBins - startRun + (numBins - startRun - 1 - nr)] = (runTotal + startRun + nr + 1)
								* binSpacing * Math.cos(angle2);
						albY[2 + numBins - startRun + (numBins - startRun - 1 - nr)] = (runTotal + startRun + nr + 1)
								* binSpacing * Math.sin(angle2);
					}
					for (int nr = 0; nr < 2 + (numBins - startRun) * 2; nr++) {
						// Convert to Lat/Lon and add to vector list
						double[] srcPts = { albX[nr], albY[nr] };
						double[] dstPts = new double[2];
						cinradTransform.transform(srcPts, 0, dstPts, 0, 1);
						coordinates.addElement(new Coordinate(dstPts[0], dstPts[1]));

					}
					// Add the first point again to close polygon

					double[] srcPts = { albX[0], albY[0] };
					double[] dstPts = new double[2];
					cinradTransform.transform(srcPts, 0, dstPts, 0, 1);

					coordinates.addElement(new Coordinate(dstPts[0], dstPts[1]));

					// Create polygon
					try {
						Coordinate[] cArray = new Coordinate[coordinates.size()];
						LinearRing lr = geoFactory.createLinearRing(coordinates.toArray(cArray));
						Polygon poly = JTSUtilities.makeGoodShapePolygon(geoFactory.createPolygon(lr, null));

						if (nxfilter == null || nxfilter.accept(poly)) {

							if (nxfilter != null) {
								poly = (Polygon) (nxfilter.clipToExtentFilter(poly));
							}

							if (poly != null) {
								polyVector.get(colorCode).addElement(poly);
							}

						}
						coordinates.clear();
						cArray = null;
					} catch (Exception e) {
						// e.printStackTrace();
						logger.error(e.toString());
						// y=100000000;
					}
				}
				// END if (colorCode > 0)
				runTotal += numBins;

			}
			// END Bin Loop
			// y=100000000;

			if (y > dataHeader[6]) {
				y = dataHeader[6];
			}
			// Decode progress
			// --------------
			for (int n = 0; n < listeners.size(); n++) {
				event.setProgress((int) ((((double) y) / dataHeader[6]) * 100.0));
				listeners.get(n).decodeProgress(event);
			}

		} // OUTER LOOP end

	} // END Radial loop

	/**
	 * Gets the floatDataThreshold attribute of the DecodeL3Nexrad object
	 * 
	 * @param i
	 *            Description of the Parameter
	 * @return The floatDataThreshold value
	 */
	private Float getFloatDataThreshold(int i) {

		Float value = null;
		try {
			String dataThreshold = header.getDataThresholdString(i);
			if (dataThreshold == null) {
				return new Float(-9999);
			}
			dataThreshold.trim();
			if (dataThreshold.length() > 0) {
				if (dataThreshold.equals("RF")) {
					value = new Float(-100);
				} else if (dataThreshold.equals("TH")) {
					value = new Float(-200);
				} else if (dataThreshold.equals("ND")) {
					value = new Float(-999);
				} else if (dataThreshold.charAt(0) == '-') {
					value = new Float("-" + dataThreshold.substring(2, dataThreshold.length()));
				} else if (dataThreshold.charAt(0) == '+') {
					value = new Float("+" + dataThreshold.substring(2, dataThreshold.length()));
				} else {
					value = new Float(dataThreshold);
				}
			}
		} catch (Exception e) {
			// catch exception if value is not a number or "RF" or "ND"
			value = new Float(-9999);

			// e.printStackTrace();
		}
		return value;
	}

	@Override
	public void setDecodeHint(String hintKey, Object hintValue) throws DecodeHintNotSupportedException {
		if (!decodeHints.keySet().contains(hintKey)) {
			throw new DecodeHintNotSupportedException(this.decoderName, hintKey, decodeHints);
		}
		decodeHints.put(hintKey, hintValue);

	}

}
