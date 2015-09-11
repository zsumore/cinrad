package mq.radar.cinrad.decoders.cinradx.productparams;

import java.io.IOException;

import mq.radar.cinrad.decoders.cinradx.ProductType;
import ucar.unidata.io.InMemoryRandomAccessFile;

public interface ProductDependentParameter {

	ProductType getProductType();

	Object[] getProductParamValues();

	default void buildProductParameter(byte[] paramBytes) throws IOException {
		if (null != getProductParamValues() && getProductParamValues().length == getProductType().getParamSize()) {

			InMemoryRandomAccessFile dataInputStream = new InMemoryRandomAccessFile("Cinrad-X Product Params",
					paramBytes);
			dataInputStream.seek(0);

			int size = getProductType().getParamSize();

			for (int i = 0; i < size; i++) {
				getProductParamValues()[i] = ParamUtils.buildParamValue(dataInputStream,
						getProductType().getParamTypes()[i]);
			}
			dataInputStream.flush();
			dataInputStream.close();
		}

	}

}
