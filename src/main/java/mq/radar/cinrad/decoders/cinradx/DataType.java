package mq.radar.cinrad.decoders.cinradx;

public enum DataType {
	INT(4), SHORT(2), CHAR(1), CHAR16(16), FLOAT(4), LONG(8);

	private int dataLength;

	private DataType(int length) {
		this.dataLength = length;
	}

	public int getDataLength() {
		return dataLength;
	}

}
