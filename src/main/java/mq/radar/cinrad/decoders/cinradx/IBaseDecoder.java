package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.operation.TransformException;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;

public interface IBaseDecoder {
	

	/**
	 * The FeatureTypes or 'schemas' used for this decoder. This represents the
	 * geometry of, and the attributes present in the features. Most of the time
	 * this will return one FeatureType, but the support is there for multiple.
	 * It is up to the user to determine in their StreamingProcess classes how
	 * to handle different FeatureTypes.
	 */
	public SimpleFeatureType[] getFeatureTypes();
	

	/**
	 * Decode the data
	 */
	public void decodeData(boolean autoClosed) throws DecodeException, IOException, TransformException;

	public Configuration getDecodeHintsConfig();

	public void setDecodeHintsConfig(Configuration conf);

	public void setDecodeHint(String hintKey, Object hintValue) throws DecodeHintNotSupportedException;

}
