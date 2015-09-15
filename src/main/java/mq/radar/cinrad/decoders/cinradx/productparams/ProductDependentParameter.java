package mq.radar.cinrad.decoders.cinradx.productparams;

import java.io.IOException;
import java.util.Arrays;

import mq.radar.cinrad.decoders.cinradx.ProductType;

/*
 * Product Dependent Parameters Block contains all product related parameters. Different products
 * have different set of parameters.
 * Notice that the number of parameters for different products can be different. But the length of the
 * block is fixed to 64 bytes. If the parameters can not fill up the block, blanks are used to fill up the
 * spare.
 */
public class ProductDependentParameter implements IProductDependentParameter {

	ProductType productType;

	Object[] productParamValues;

	public ProductDependentParameter(ProductType type,byte[] paramBytes) throws IOException {
		this.productType = type;
		this.productParamValues = new Object[type.getParamSize()];
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

	@Override
	public String toString() {
		return "ProductDependentParameter [productType=" + productType + ", productParamValues="
				+ Arrays.toString(productParamValues) + "]";
	}
	

}
