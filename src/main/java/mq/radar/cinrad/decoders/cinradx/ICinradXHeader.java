package mq.radar.cinrad.decoders.cinradx;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;

public interface ICinradXHeader {

	public CommonBlocks getCommonBlocks();

	public void setCommonBlocks(CommonBlocks commonBlocks);

	public ProductHeader getProductHeader();

	public void setProductHeader(ProductHeader productHeader);

	public ProductDependentParameter getProductDependentParameter();

	public void setProductDependentParameter(ProductDependentParameter productDependentParameter);

}
