package mq.radar.cinrad.decoders.cinradx;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;

public interface ICinradXProduct {
	public CommonBlocks getCommonBlocks();

	public ProductHeader getProductHeader();

	public ProductDependentParameter getProductDependentParameter();

}
