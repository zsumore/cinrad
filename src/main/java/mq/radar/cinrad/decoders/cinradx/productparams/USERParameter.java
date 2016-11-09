package mq.radar.cinrad.decoders.cinradx.productparams;

import java.io.IOException;
import java.util.Arrays;

import mq.radar.cinrad.decoders.cinradx.ProductType;
import mq.radar.cinrad.decoders.cinradx.CinradXUtils;
import ucar.unidata.io.InMemoryRandomAccessFile;

public class USERParameter implements IProductDependentParameter {

	ProductType productType;

	Object[] productParamValues;

	Integer[] algorithmArray;

	public USERParameter(byte[] paramBytes) throws IOException {
		productType = ProductType.USER;
		productParamValues = new Object[4];
		algorithmArray = new Integer[4];
		buildProductParameter(paramBytes);
	}

	@Override
	public ProductType getProductType() {

		return productType;
	}

	@Override
	public Object[] getProductParamValues() {

		return productParamValues;
	}

	public Integer[] getAlgorithmArray() {
		return algorithmArray;
	}

	@Override
	public String toString() {
		return "USERParameter [productType=" + productType + ", productParamValues="
				+ Arrays.toString(productParamValues) + ", algorithmArray=" + Arrays.toString(algorithmArray) + "]";
	}

	@Override
	public void buildProductParameter(byte[] paramBytes) throws IOException {
		if (null != getProductParamValues()) {

			InMemoryRandomAccessFile dataInputStream = new InMemoryRandomAccessFile("Cinrad-X Product Params",
					paramBytes);
			dataInputStream.seek(0);

			for (int i = 0; i < algorithmArray.length; i++) {
				algorithmArray[i] = (Integer) CinradXUtils.buildProductParamValue(dataInputStream,
						getProductType().getParamTypes()[0]);
			}

			getProductParamValues()[0] = algorithmArray[0];
			for (int j = 1; j < getProductParamValues().length; j++) {
				getProductParamValues()[j] = CinradXUtils.buildProductParamValue(dataInputStream,
						getProductType().getParamTypes()[j]);
			}

			dataInputStream.flush();
			dataInputStream.close();
			dataInputStream=null;
		}
	}

}
