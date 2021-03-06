/**
 * NOAA's National Climatic Data Center
 * NOAA/NESDIS/NCDC
 * 151 Patton Ave, Asheville, NC  28801
 * 
 * THIS SOFTWARE AND ITS DOCUMENTATION ARE CONSIDERED TO BE IN THE 
 * PUBLIC DOMAIN AND THUS ARE AVAILABLE FOR UNRESTRICTED PUBLIC USE.  
 * THEY ARE FURNISHED "AS IS." THE AUTHORS, THE UNITED STATES GOVERNMENT, ITS
 * INSTRUMENTALITIES, OFFICERS, EMPLOYEES, AND AGENTS MAKE NO WARRANTY,
 * EXPRESS OR IMPLIED, AS TO THE USEFULNESS OF THE SOFTWARE AND
 * DOCUMENTATION FOR ANY PURPOSE. THEY ASSUME NO RESPONSIBILITY (1)
 * FOR THE USE OF THE SOFTWARE AND DOCUMENTATION; OR (2) TO PROVIDE
 * TECHNICAL SUPPORT TO USERS.
 */

package mq.radar.cinrad.decoders.cinrad;

import java.io.IOException;
import java.util.Set;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;
import mq.radar.cinrad.decoders.StreamingDecoder;
import mq.radar.cinrad.decoders.StreamingProcess;

import org.geotools.data.simple.SimpleFeatureCollection;

public interface StreamingRadialDecoder extends StreamingDecoder {

	public String[] getSupplementalDataArray() throws IOException;

	// public FeatureType getFeatureType();
	// now use getFeatureTypes() in StreamingDecoder

	/**
	 * Decodes data into Features and stores in an in-memory FeatureCollection
	 * 
	 * @throws DecodeException
	 * @throws IOException
	 */
	public void decodeData() throws DecodeException, IOException;

	/**
	 * Gets the Features stored after the use of 'decodeData()'
	 * 
	 * @return
	 */
	public SimpleFeatureCollection getFeatures();

	/**
	 * Decodes the data and processes each Feature with a StreamingProcess - no
	 * data is stored in-memory.
	 * 
	 * @param processArray
	 * @param autoClose
	 *            Do we call the .close() method for the StreamingProcess array
	 *            when we are finished?
	 * @throws DecodeException
	 * @throws IOException
	 */
	public void decodeData(StreamingProcess[] processArray, boolean autoClose)
			throws DecodeException, IOException;

	public Set<String> getDecodeHintsKey();

	public void setDecodeHint(String hintKey, Object hintValue)
			throws DecodeHintNotSupportedException;

}
