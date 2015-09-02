package mq.radar.cinrad.decoders;

public class StreamingProcessException extends Exception {
   
    /**
	 * 
	 */
	private static final long serialVersionUID = -1802798517029169204L;

	/**
     * Constructor with message argument.
     *
     * @param message Reason for the exception being thrown
     */
    public StreamingProcessException(String message) {
        super(message);
    }
    
 }
