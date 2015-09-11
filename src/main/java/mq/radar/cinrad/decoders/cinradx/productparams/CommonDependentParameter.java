package mq.radar.cinrad.decoders.cinradx.productparams;

import mq.radar.cinrad.decoders.cinradx.ProductType;

public class CommonDependentParameter implements ProductDependentParameter {

	ProductType productType;

	Object[] productParamValues;

	public CommonDependentParameter(ProductType type) {
		productType = type;
		productParamValues = new Object[type.getParamSize()];
	}

	@Override
	public ProductType getProductType() {

		return productType;
	}

	@Override
	public Object[] getProductParamValues() {

		return productParamValues;
	}

}
