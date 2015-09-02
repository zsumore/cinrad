package mq.radar.cinrad.decoders;

import java.net.URL;

public class DecodeException extends Exception {

	private static final long serialVersionUID = 8662882255449499077L;
	private URL dataURL;

	/**
	 * Constructor with message argument.
	 * 
	 * @param message
	 *            Reason for the exception being thrown
	 */
	public DecodeException(String message, URL dataURL) {
		super(message);
		this.dataURL = dataURL;
	}

	/**
	 * Constructor with message argument.
	 * 
	 * @param message
	 *            Reason for the exception being thrown
	 */
	public DecodeException(String message) {
		super(message);
	}

	public URL getDataURL() {
		return dataURL;
	}

}
