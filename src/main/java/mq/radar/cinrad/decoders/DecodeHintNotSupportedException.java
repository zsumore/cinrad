package mq.radar.cinrad.decoders;

import java.util.Map;

public class DecodeHintNotSupportedException extends Exception {

	private static final long serialVersionUID = -4623218453312459261L;
	private String offendingDecoder;
	private String offendingHintKey;
	private Map<String, Object> defaultSupportedHints;

	public DecodeHintNotSupportedException(String offendingDecoder,
			String offendingHintKey, Map<String, Object> defaultSupportedHints) {
		this.offendingDecoder = offendingDecoder;
		this.offendingHintKey = offendingHintKey;
		this.defaultSupportedHints = defaultSupportedHints;
	}

	public String getMessage() {
		return "Decoder: " + offendingDecoder.toString()
				+ " does not supported the decode hint '" + offendingHintKey
				+ "'." + "  Supported hints and default values are: "
				+ defaultSupportedHints.toString();
	}

	public String toString() {
		return getMessage();
	}
}
