package mq.radar.cinrad.decoders.cinradx.productparams;

import java.io.IOException;
import java.util.Arrays;

import mq.radar.cinrad.decoders.cinradx.ProductType;
import mq.radar.cinrad.decoders.cinradx.CinradXUtils;
import ucar.unidata.io.InMemoryRandomAccessFile;

public class VWPParameter implements IProductDependentParameter {

	ProductType productType;

	Object[] productParamValues;

	Short[] heightArray;

	public VWPParameter(byte[] paramBytes) throws IOException {
		productType = ProductType.VWP;
		productParamValues = new Object[1];
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

	public Short[] getHeightArray() {
		return heightArray;
	}

	

	@Override
	public String toString() {
		return "VWPParameter [productType=" + productType + ", productParamValues="
				+ Arrays.toString(productParamValues) + ", heightArray=" + Arrays.toString(heightArray) + "]";
	}

	@Override
	public void buildProductParameter(byte[] paramBytes) throws IOException {
		if (null != getProductParamValues()) {

			InMemoryRandomAccessFile dataInputStream = new InMemoryRandomAccessFile("Cinrad-X Product Params",
					paramBytes);
			dataInputStream.seek(0);

			getProductParamValues()[0] = CinradXUtils.buildProductParamValue(dataInputStream,
					getProductType().getParamTypes()[0]);

			int heightN = (int) getProductParamValues()[0];
		
			if (heightN > 0 && heightN <= 30) {
				heightArray = new Short[heightN];
				for (int i = 0; i < heightN; i++) {
					heightArray[i] = (Short) CinradXUtils.buildProductParamValue(dataInputStream,
							getProductType().getParamTypes()[1]);
				}
			} else {
				heightArray = new Short[1];
				heightArray[0] = -999;
			}
			dataInputStream.flush();
			dataInputStream.close();
		}
	}
}
