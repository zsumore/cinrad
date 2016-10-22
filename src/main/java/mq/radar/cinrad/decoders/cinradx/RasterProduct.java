package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.RandomAccessFile;

public class RasterProduct implements ICinradXBuilder, ICinradXProduct {

	private ICinradXHeader cinradXHeader;

	private RasterDataBlock rasterDataBlock;

	public RasterDataBlock getRasterDataBlock() {
		return rasterDataBlock;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RasterProduct [cinradXHeader=");
		builder.append(cinradXHeader);
		builder.append(", rasterDataBlock=");
		builder.append(rasterDataBlock);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public ICinradXHeader getICinradXHeader() {

		return cinradXHeader;
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

		rasterDataBlock = new RasterDataBlock();
		rasterDataBlock.builder(file, -1);

	}

}
