package mq.radar.cinrad.decoders.cinradx;

import java.net.URL;

import mq.radar.cinrad.decoders.DecodeException;

public interface IDecodeCinradXHeader {
	
	/**
	 * Gets the randomAccessFile attribute of the CinradHeader object
	 * 
	 * @return The randomAccessFile value
	 */
	ucar.unidata.io.RandomAccessFile getRandomAccessFile();

	/**
	 * Description of the Method
	 * 
	 * @param url
	 *            Description of the Parameter
	 */
	void decodeHeader(URL url) throws DecodeException;
	
	
	Long getCinradXHeaderLength();
	
	ICinradXHeader getICinradXHeader();

}
