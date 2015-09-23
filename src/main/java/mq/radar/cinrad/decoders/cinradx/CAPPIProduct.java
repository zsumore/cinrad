package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.RandomAccessFile;

public class CAPPIProduct implements ICinradXBuilder, ICinradXProduct {

	private CommonBlocks commonBlocks;

	private ProductHeader productHeader;

	private ProductDependentParameter productDependentParameter;

	private List<RadialDataBlock> radialDataBlocks;

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

	public List<RadialDataBlock> getRadialDataBlocks() {
		return radialDataBlocks;
	}

	@Override
	public String toString() {
		return "CAPPIProduct [commonBlocks=" + commonBlocks + ", productHeader=" + productHeader
				+ ", productDependentParameter=" + productDependentParameter + ", radialDataBlocks=" + radialDataBlocks
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

		radialDataBlocks = new ArrayList<>();
		for (int i = 0; i < (Integer) productDependentParameter.getProductParamValues()[0]; i++) {
			RadialDataBlock radialDataBlock = new RadialDataBlock();
			radialDataBlock.builder(file, -1);
		}

	}

}
