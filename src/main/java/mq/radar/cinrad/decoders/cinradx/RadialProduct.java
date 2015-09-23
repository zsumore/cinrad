package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.RandomAccessFile;

public class RadialProduct implements ICinradXBuilder, ICinradXProduct {

	private CommonBlocks commonBlocks;

	private ProductHeader productHeader;

	private ProductDependentParameter productDependentParameter;

	private RadialDataBlock radialDataBlock;

	@Override
	public CommonBlocks getCommonBlocks() {
		return commonBlocks;
	}

	@Override
	public ProductHeader getProductHeader() {
		return productHeader;
	}

	@Override
	public ProductDependentParameter getProductDependentParameter() {
		return productDependentParameter;
	}

	public RadialDataBlock getRadialDataBlock() {
		return radialDataBlock;
	}

	@Override
	public String toString() {
		return "RadialProduct [commonBlocks=" + commonBlocks + ", productHeader=" + productHeader
				+ ", productDependentParameter=" + productDependentParameter + ", radialDataBlock=" + radialDataBlock
				+ "]";
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		commonBlocks = new CommonBlocks();
		commonBlocks.builder(file, -1);

		productHeader = new ProductHeader();
		productHeader.builder(file, -1);

		productDependentParameter = new ProductDependentParameter(
				CinradXUtils.getProductType(productHeader.getProductNumber()), file.readBytes(64));

		radialDataBlock = new RadialDataBlock();
		radialDataBlock.builder(file, -1);

	}

}
