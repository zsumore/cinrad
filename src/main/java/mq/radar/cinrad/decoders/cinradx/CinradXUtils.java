package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ucar.unidata.io.InMemoryRandomAccessFile;

public class CinradXUtils {
	private static Map<Integer,ProductType> productMap;
	
	final public static String AUTO_DECODE_CONFIG_FILE = "decode.properties";
	final public static String DEFAULT_DECODE_CONFIG_FILE = "mq/radar/cinrad/decoders/cinradx/decode.properties";
	
	public static ProductType getProductType(int number){
		if(null==productMap){
			productMap=new HashMap<Integer,ProductType>();
			for(ProductType productType:ProductType.values()){
				productMap.put(productType.getProductNumber(), productType);
			}
		}
			
		return productMap.get(number);
	}
	
	
	public static Object buildProductParamValue(InMemoryRandomAccessFile dataInputStream, DataType dataType)
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
