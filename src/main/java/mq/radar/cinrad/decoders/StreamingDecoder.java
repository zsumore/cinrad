package mq.radar.cinrad.decoders;

import java.io.IOException;

import org.opengis.feature.simple.SimpleFeatureType;

public interface StreamingDecoder {

	/**
	 * The FeatureTypes or 'schemas' used for this decoder. This represents the
	 * geometry of, and the attributes present in the features. Most of the time
	 * this will return one FeatureType, but the support is there for multiple.
	 * It is up to the user to determine in their StreamingProcess classes how
	 * to handle different FeatureTypes.
	 */
	public SimpleFeatureType[] getFeatureTypes();

	/**
	 * Decode the data using the provided LiteProcess. The LiteProcess will
	 * provide the 'addFeature' method which is called for each feature and does
	 * any desired processing for that feature.
	 */
	public void decodeData(StreamingProcess[] streamingProcessArray)
			throws DecodeException, IOException;

}
