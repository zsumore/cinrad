package mq.radar.cinrad.decoders.cinradx.productparams;

import java.io.IOException;

import mq.radar.cinrad.decoders.cinradx.DataType;
import ucar.unidata.io.InMemoryRandomAccessFile;

public class ParamUtils {
	public static Object buildParamValue(InMemoryRandomAccessFile dataInputStream, DataType dataType)
			throws IOException {

		switch (dataType) {
		case INT:
			return dataInputStream.readInt();
		case FLOAT:
			return dataInputStream.readFloat();
		case SHORT:
			return dataInputStream.readShort();
		case CHAR16:
			return dataInputStream.readString(16).trim();
		case LONG:
			return dataInputStream.readLong();
		case CHAR:
			return dataInputStream.readByte();
		default:
			return "Error";
		}

	}

}
