package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ucar.unidata.io.InMemoryRandomAccessFile;

public class ProductUtils {
	private static Map<Integer,ProductType> productMap;
	
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
