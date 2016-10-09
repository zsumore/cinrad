package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mq.radar.cinrad.ProjectionType;
import ucar.unidata.io.InMemoryRandomAccessFile;

public class CinradXUtils {
	private static Map<Integer, ProductType> productMap = null;

	private static Map<String, ProjectionType> projectionMapByName = null;

	private static Map<Integer, ProjectionType> projectionMap = null;

	final public static String AUTO_DECODE_CONFIG_FILE = "decode.properties";
	final public static String DEFAULT_DECODE_CONFIG_FILE = "mq/radar/cinrad/decoders/cinradx/decode.properties";

	public static ProjectionType getProjectionByType(int number) {
		if (null == projectionMap) {
			projectionMap = new HashMap<>();
			for (ProjectionType projectionType : ProjectionType.values()) {
				projectionMap.put(projectionType.getType(), projectionType);
			}
		}

		return projectionMap.get(number);
	}

	public static ProjectionType getProjectionByName(String name) {
		if (null == projectionMapByName) {
			projectionMapByName = new HashMap<>();
			for (ProjectionType projectionType : ProjectionType.values()) {
				projectionMapByName.put(projectionType.name(), projectionType);
			}
		}

		return projectionMapByName.get(name);
	}

	public static ProductType getProductType(int number) {
		if (null == productMap) {
			productMap = new HashMap<Integer, ProductType>();
			for (ProductType productType : ProductType.values()) {
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

	public static Integer getRadialColorIndex(Float value) {
		if (value >= 0 && value < 5)
			return 0;

		if (value >= 5 && value < 10)
			return 1;

		if (value >= 10 && value < 15)
			return 2;

		if (value >= 15 && value < 20)
			return 3;

		if (value >= 20 && value < 25)
			return 4;

		if (value >= 25 && value < 30)
			return 5;

		if (value >= 30 && value < 35)
			return 6;

		if (value >= 35 && value < 40)
			return 7;

		if (value >= 40 && value < 45)
			return 8;

		if (value >= 45 && value < 50)
			return 9;

		if (value >= 50 && value < 55)
			return 10;

		if (value >= 55 && value < 60)
			return 11;

		if (value >= 60 && value < 65)
			return 12;

		if (value >= 65 && value < 70)
			return 13;

		if (value >= 70 && value < 75)
			return 14;

		if (value >= 75)
			return 15;

		return -999;
	}

}
