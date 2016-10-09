package mq.radar.cinrad.decoders.cinradx;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;

public class CinradXHeader implements ICinradXHeader {

	private CommonBlocks commonBlocks;

	private ProductHeader productHeader;

	private ProductDependentParameter productDependentParameter;

	public CinradXHeader() {

	}

	public CinradXHeader(CommonBlocks commonBlocks, ProductHeader productHeader,
			ProductDependentParameter productDependentParameter) {

		this.commonBlocks = commonBlocks;
		this.productHeader = productHeader;
		this.productDependentParameter = productDependentParameter;
	}

	public CommonBlocks getCommonBlocks() {
		return commonBlocks;
	}

	public void setCommonBlocks(CommonBlocks commonBlocks) {
		this.commonBlocks = commonBlocks;
	}

	public ProductHeader getProductHeader() {
		return productHeader;
	}

	public void setProductHeader(ProductHeader productHeader) {
		this.productHeader = productHeader;
	}

	public ProductDependentParameter getProductDependentParameter() {
		return productDependentParameter;
	}

	public void setProductDependentParameter(ProductDependentParameter productDependentParameter) {
		this.productDependentParameter = productDependentParameter;
	}

}
