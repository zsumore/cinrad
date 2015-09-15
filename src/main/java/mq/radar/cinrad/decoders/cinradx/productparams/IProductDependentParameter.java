package mq.radar.cinrad.decoders.cinradx.productparams;

import java.io.IOException;

import mq.radar.cinrad.decoders.cinradx.ProductType;
import mq.radar.cinrad.decoders.cinradx.ProductUtils;
import ucar.unidata.io.InMemoryRandomAccessFile;

public interface IProductDependentParameter {

	ProductType getProductType();

	Object[] getProductParamValues();

	default void buildProductParameter(byte[] paramBytes) throws IOException {
		if (null != getProductParamValues() && getProductParamValues().length == getProductType().getParamSize()) {

			InMemoryRandomAccessFile dataInputStream = new InMemoryRandomAccessFile("Cinrad-X Product Params",
					paramBytes);
			dataInputStream.seek(0);

			int size = getProductType().getParamSize();

			for (int i = 0; i < size; i++) {
				getProductParamValues()[i] = ProductUtils.buildProductParamValue(dataInputStream,
						getProductType().getParamTypes()[i]);
			}
			dataInputStream.flush();
			dataInputStream.close();
		}

	}

}
