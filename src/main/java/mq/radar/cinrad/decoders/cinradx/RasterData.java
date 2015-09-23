package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ucar.unidata.io.RandomAccessFile;

public class RasterData implements ICinradXBuilder {

	private RasterHeader rasterHeader;

	/*
	 * NO 01; TYPE float;
	 */
	private Map<Long, Float> dataValueArray;

	public RasterData(RasterHeader rasterHeader) {
		super();
		this.rasterHeader = rasterHeader;
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		dataValueArray = new HashMap<>();
		for (int i = 0; i < rasterHeader.getRowSideLength(); i++) {
			for (int j = 0; j < rasterHeader.getColumnSideLength(); j++) {
				long k = i * j * 1L + j;

				int code;
				if (rasterHeader.getBinLength() == 1) {
					code = file.readUnsignedByte();

				} else {
					code = file.readUnsignedShort();
				}
				float value = (code - this.rasterHeader.getOffset())*1.00f / this.rasterHeader.getScale();

				if (value > 0) {
					dataValueArray.put(k, value);
				}

			}

		}

	}

}
