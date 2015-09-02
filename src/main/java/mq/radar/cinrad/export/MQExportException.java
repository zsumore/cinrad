package mq.radar.cinrad.export;

public class MQExportException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1918738890261285351L;

	/**
	 * Constructor with message argument.
	 * 
	 * @param message
	 *            Reason for the exception being thrown
	 */
	public MQExportException(String message) {
		super(message);
	}

}
