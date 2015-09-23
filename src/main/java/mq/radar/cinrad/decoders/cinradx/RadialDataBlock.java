package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ucar.unidata.io.RandomAccessFile;

public class RadialDataBlock implements ICinradXBuilder {

	private RadialHeader radialHeader;

	private List<RadialData> radialDatas;

	public RadialHeader getRadialHeader() {
		return radialHeader;
	}

	public List<RadialData> getRadialDatas() {
		return radialDatas;
	}

	@Override
	public String toString() {
		return "RadialDataBlock [radialHeader=" + radialHeader + ", radialDatas=" + radialDatas + "]";
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {

		if (pos >= 0)
			file.seek(pos);

		radialHeader = new RadialHeader();
		radialHeader.builder(file, -1);

		radialDatas = new ArrayList<>();
		for (int j = 0; j < radialHeader.getNumberOfRadials(); j++) {

			RadialData radialData = new RadialData(radialHeader);
			radialData.builder(file, -1);
			radialDatas.add(radialData);
		}

	}

}
