package mq.radar.cinrad.decoders;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Interface that defines the necessary "addFeature" method that receives a
 * single Feature and does some process on it. The 'close' method is only used
 * when I/O is present such as writing a Shapefile.
 * 
 * @author steve.ansari
 */
public interface StreamingProcess {

	/**
	 * Receives a feature and does a process on it. It is up to the LiteProcess
	 * implementor to decide what to do with this feature. This could be to
	 * export it to shapefile, rasterize it, etc...
	 * 
	 * @param feature
	 *            The feature to be added to the Feature attribute
	 * @exception StreamingProcessException
	 *                Description of the Exception
	 */
	public void addFeature(SimpleFeature feature) throws StreamingProcessException;

	/**
	 * Only used when I/O is present such as writing a Shapefile (in
	 * ExportShapefileLite).
	 * 
	 * @exception StreamingProcessException
	 *                Description of the Exception
	 */
	public void close() throws StreamingProcessException;

}
