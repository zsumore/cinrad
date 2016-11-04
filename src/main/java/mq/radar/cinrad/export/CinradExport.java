package mq.radar.cinrad.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import mq.radar.cinrad.decoders.cinrad.CindarDecoder;

import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class CinradExport {

	/**
	 * Save Cinrad Alphanumeric data to ESRI Shapefile with additional .prj
	 * projection file
	 * 
	 * @param outFile
	 *            Destination file
	 * @param decoder
	 *            Alphanumeric Nexrad decoder
	 
	public static void saveShapefile(File outFile, CindarDecoder decoder)
			throws MQExportException, MQExportNoDataException, IOException,
			IllegalAttributeException {
		if (decoder.getFeatures().size() == 0) {
			throw new MQExportNoDataException(
					"No Data Present in CINRAD File: " + outFile.toString());
		}

		// Write .shp , .shx and .dbf files
		ShapefileDataStore store = new ShapefileDataStore(outFile.toURI()
				.toURL());
		store.createSchema(decoder.getFeatureTypes()[0]);
		FeatureWriter<SimpleFeatureType, SimpleFeature> fw = store
				.getFeatureWriter(store.getTypeNames()[0],
						Transaction.AUTO_COMMIT);

		SimpleFeatureIterator fci = decoder.getFeatures().features();
		SimpleFeature feature;

		while (fci.hasNext()) {
			feature = fci.next();
			fw.next().setAttributes(feature.getAttributes());
			fw.write();
		}
		fw.close();

		// Write .prj file --
		// Projection
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile + ".prj"));
		String prj = decoder.getCRS().toWKT();
		bw.write(prj);
		bw.close();
	}
	*/

	/**
	 * Save Cinrad Alphanumeric data to ESRI Line Shapefile with additional .prj
	 * projection file. This is currently only applicable for the Storm Tracking
	 * Level-III Product (NST)
	 * 
	 * @param outFile
	 *            Destination file
	 * @param decoder
	 *            Alphanumeric Nexrad decoder
	
	public static void saveLineShapefile(File outFile, CindarDecoder decoder)
			throws MQExportException, MQExportNoDataException, IOException,
			IllegalAttributeException {
		// 2. Write line shapefile if applicable
		if (decoder.getLineFeatures() != null) {
			// Write .shp , .shx and .dbf files
			outFile = new File(outFile.toString() + "_line");
			ShapefileDataStore store = new ShapefileDataStore(outFile.toURI()
					.toURL());
			store.createSchema(decoder.getLineFeatureType());
			FeatureWriter<SimpleFeatureType, SimpleFeature> fw = store
					.getFeatureWriter(store.getTypeNames()[0],
							Transaction.AUTO_COMMIT);

			SimpleFeatureIterator fci = decoder.getLineFeatures().features();
			SimpleFeature feature;

			while (fci.hasNext()) {
				feature = fci.next();
				fw.next().setAttributes(feature.getAttributes());
				fw.write();
			}
			fw.close();

			// Write .prj file --
			// Projection
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile
					+ ".prj"));
			String prj = decoder.getCRS().toWKT();
			bw.write(prj);
			bw.close();
		}
	}
	 */

	/**
	 * Save Cinrad Alphanumeric data to Well-Known Text with additional .prj
	 * projection file. This is currently only applicable for the Storm Tracking
	 * Level-III Product (NST)
	 * 
	 * @param outFile
	 *            Destination file
	 * @param decoder
	 *            Alphanumeric Nexrad decoder
	 
	public static void saveLineWKT(File outFile, CindarDecoder decoder)
			throws MQExportException, MQExportNoDataException, IOException,
			IllegalAttributeException {

		// 2. Write line shapefile if applicable
		if (decoder.getLineFeatures() != null) {

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					outFile.toString() + "_line.txt")));
			try {
				ShapefileDataStore store = new ShapefileDataStore(outFile
						.toURI().toURL());
				store.createSchema(decoder.getLineFeatureType());
				SimpleFeatureIterator fi = decoder.getLineFeatures().features();
				// int i = 0;
				while (fi.hasNext()) {
					bw.write(fi.next().toString());
					bw.newLine();
					// i++;
				}

			} finally {
				bw.flush();
				bw.close();
			}

		}

	}
	

	public static void saveWKT(File outFile, CindarDecoder decoder)
			throws MQExportException, MQExportNoDataException, IOException,
			IllegalAttributeException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				outFile.toString() + ".txt")));

		try {
			SimpleFeatureCollection features = decoder.getFeatures();
			SimpleFeatureIterator fi = features.features();
			// int i = 0;
			while (fi.hasNext()) {
				bw.write(fi.next().toString());
				bw.newLine();
				// i++;
			}

		} finally {
			bw.flush();
			bw.close();
		}

	}
*/
	/**
	 * Save Nexrad Alphanumeric data to Comma-delimited file
	 * 
	 * @param outFile
	 *            Destination file
	 * @param decoder
	 *            Alphanumeric Nexrad decoder
	 
	public static void saveCSV(File outFile, CindarDecoder decoder)
			throws MQExportException, MQExportNoDataException, IOException,
			IllegalAttributeException {

		DecimalFormat format = new DecimalFormat("0.000");
		saveCSV(outFile, decoder, format);
	}
	*/

	/**
	 * Save Nexrad Alphanumeric data to Comma-delimited file
	 * 
	 * @param outFile
	 *            Destination file
	 * @param decoder
	 *            Alphanumeric Nexrad decoder
	 * @param format
	 *            Output text format for numeric data
	
	public static void saveCSV(File outFile, CindarDecoder decoder,
			DecimalFormat format) throws MQExportException,
			MQExportNoDataException, IOException, IllegalAttributeException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				outFile.toString() + ".csv")));

		try {
			SimpleFeatureType schema = decoder.getFeatureTypes()[0];
			SimpleFeatureCollection features = decoder.getFeatures();
			bw.write(schema.getType(1).getName().toString());
			for (int n = 1; n < schema.getAttributeCount() - 1; n++) {
				bw.write("," + schema.getType(n + 1).getName());
			}
			bw.newLine();
			SimpleFeatureIterator fi = features.features();
			SimpleFeature f = null;
			// int i = 0;
			while (fi.hasNext()) {
				f = fi.next();
				for (int n = 0; n < schema.getAttributeCount() - 1; n++) {
					// System.out.println(schema.getAttributeType(n+1).getType());
					if (schema.getType(n + 1).getBinding().toString()
							.equals("class java.lang.Double")) {
						bw.write(format.format(Double.parseDouble(f
								.getAttribute(n + 1).toString())));
					} else {
						bw.write(f.getAttribute(n + 1).toString());
					}
					if (n == schema.getAttributeCount() - 2) {
						bw.newLine();
					} else {
						bw.write(",");
					}
				}
				// i++;
			}

		} finally {
			bw.flush();
			bw.close();
		}
	}

	public static void saveSupplementalData(File outFile, CindarDecoder decoder)
			throws IOException {

		String[] supArray = decoder.getSupplementalDataArray();
		if (supArray == null) {
			return;
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				outFile.toString() + "_sup.txt")));
		for (int n = 0; n < supArray.length; n++) {
			bw.write(supArray[n]);
			bw.newLine();
		}
		bw.flush();
		bw.close();

	}
 */
}
