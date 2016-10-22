package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import ucar.unidata.io.RandomAccessFile;

public class RasterDataBlock implements ICinradXBuilder {
	private RasterHeader rasterHeader;

	private RasterData rasterData;

	public RasterHeader getRasterHeader() {
		return rasterHeader;
	}

	public void setRasterHeader(RasterHeader rasterHeader) {
		this.rasterHeader = rasterHeader;
	}

	public RasterData getRasterData() {
		return rasterData;
	}

	public void setRasterData(RasterData rasterData) {
		this.rasterData = rasterData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RasterDataBlock [rasterHeader=");
		builder.append(rasterHeader);
		builder.append(", rasterData=");
		builder.append(rasterData);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		rasterHeader = new RasterHeader();
		rasterHeader.builder(file, -1);

		rasterData = new RasterData(rasterHeader);
		rasterData.builder(file, -1);

	}

}
