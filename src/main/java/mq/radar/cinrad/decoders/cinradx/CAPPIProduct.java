package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.RandomAccessFile;

public class CAPPIProduct implements ICinradXBuilder, ICinradXProduct {

	private ICinradXHeader cinradXHeader;

	private List<RadialDataBlock> radialDataBlocks;

	public List<RadialDataBlock> getRadialDataBlocks() {
		return radialDataBlocks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CAPPIProduct [cinradXHeader=");
		builder.append(cinradXHeader);
		builder.append(", radialDataBlocks=");
		builder.append(radialDataBlocks);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		cinradXHeader = new CinradXHeader();

		CommonBlocks commonBlocks = new CommonBlocks();
		commonBlocks.builder(file, -1);
		cinradXHeader.setCommonBlocks(commonBlocks);

		ProductHeader productHeader = new ProductHeader();
		productHeader.builder(file, -1);
		cinradXHeader.setProductHeader(productHeader);

		ProductDependentParameter productDependentParameter = new ProductDependentParameter(
				CinradXUtils.getProductType(productHeader.getProductNumber()), file.readBytes(64));
		cinradXHeader.setProductDependentParameter(productDependentParameter);

		radialDataBlocks = new ArrayList<>();
		for (int i = 0; i < (Integer) productDependentParameter.getProductParamValues()[0]; i++) {
			RadialDataBlock radialDataBlock = new RadialDataBlock();
			radialDataBlock.builder(file, -1);
		}

	}

	@Override
	public ICinradXHeader getICinradXHeader() {

		return this.cinradXHeader;
	}

}
