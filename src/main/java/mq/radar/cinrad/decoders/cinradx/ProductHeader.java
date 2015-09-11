package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;

import ucar.unidata.io.RandomAccessFile;

/*
 * Product Header;
 * Range 128 Bytes;
 * No 5;
 */
public class ProductHeader implements CinradXHeaderBuilder {

	/*
	 * NO 01; TYPE INT; UNIT N/A; RANGE 1 to 100; Product type, See Table 3-3
	 */
	private int productType;

	/*
	 * NO 02; TYPE CHAR*32; UNIT N/A; RANGE ASCII; User defined name of product
	 */
	private String productName;

	/*
	 * NO 03; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Generation time of
	 * Product. Seconds from midnight of 1970/01/01 UTC.
	 */
	private int productGenerationTime;
	/*
	 * NO 04; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Volume scan start
	 * time of the current task. Seconds from midnight of 1970/01/01 UTC.
	 */
	private int volumeStartTime;
	/*
	 * NO 05; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Data start time of
	 * Product. For PPI, it’s cut start time. Seconds from midnight of
	 * 1970/01/01 UTC.
	 */
	private int dataStartTime;
	/*
	 * NO 06; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Data end time of
	 * Product. For PPI, it’s cut end time. Seconds from midnight of 1970/01/01
	 * UTC.
	 */
	private int dataEndTime;
	/*
	 * NO 07; TYPE INT; UNIT N/A; RANGE 1 to 18 ; Geographical Mapping 15
	 * Projection used. See Table 3-4
	 */
	private int projectionType;
	/*
	 * NO 08; TYPE INT; UNIT N/A; RANGE 1 to 64; Primary Moment Type of product
	 * based on. See Table 2-7
	 */
	private int primaryMomentType;
	/*
	 * NO 09; TYPE INT; UNIT N/A; RANGE 1 to 64; Secondary Moment Type of
	 * product based on. See Table 2-7
	 */
	private int secondaryMomentType;

	/*
	 * NO 10; TYPE 64 Bytes; UNIT N/A ; RANGE N/A ;
	 */
	private byte[] reserved;

	public ProductHeader() {

	}

	@Override
	public String toString() {
		return "ProductHeader [productType=" + productType + ", productName=" + productName + ", productGenerationTime="
				+ productGenerationTime + ", volumeStartTime=" + volumeStartTime + ", dataStartTime=" + dataStartTime
				+ ", dataEndTime=" + dataEndTime + ", projectionType=" + projectionType + ", primaryMomentType="
				+ primaryMomentType + ", secondaryMomentType=" + secondaryMomentType + ", reserved="
				+ Arrays.toString(reserved) + "]";
	}

	public int getProductType() {
		return productType;
	}

	public String getProductName() {
		return productName;
	}

	public int getProductGenerationTime() {
		return productGenerationTime;
	}

	public int getVolumeStartTime() {
		return volumeStartTime;
	}

	public int getDataStartTime() {
		return dataStartTime;
	}

	public int getDataEndTime() {
		return dataEndTime;
	}

	public int getProjectionType() {
		return projectionType;
	}

	public int getPrimaryMomentType() {
		return primaryMomentType;
	}

	public int getSecondaryMomentType() {
		return secondaryMomentType;
	}

	public byte[] getReserved() {
		return reserved;
	}

	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);

		/*
		 * NO 01; TYPE INT; UNIT N/A; RANGE 1 to 100; Product type, See Table
		 * 3-3
		 */
		productType = file.readInt();

		/*
		 * NO 02; TYPE CHAR*32; UNIT N/A; RANGE ASCII; User defined name of
		 * product
		 */
		productName = file.readString(32).trim();

		/*
		 * NO 03; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Generation time
		 * of Product. Seconds from midnight of 1970/01/01 UTC.
		 */
		productGenerationTime = file.readInt();
		/*
		 * NO 04; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Volume scan
		 * start time of the current task. Seconds from midnight of 1970/01/01
		 * UTC.
		 */
		volumeStartTime = file.readInt();
		/*
		 * NO 05; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Data start time
		 * of Product. For PPI, it’s cut start time. Seconds from midnight of
		 * 1970/01/01 UTC.
		 */
		dataStartTime = file.readInt();
		/*
		 * NO 06; TYPE INT; UNIT Seconds; RANGE 0 to 0xFFFFFFFF; Data end time
		 * of Product. For PPI, it’s cut end time. Seconds from midnight of
		 * 1970/01/01 UTC.
		 */
		dataEndTime = file.readInt();
		/*
		 * NO 07; TYPE INT; UNIT N/A; RANGE 1 to 18 ; Geographical Mapping 15
		 * Projection used. See Table 3-4
		 */
		projectionType = file.readInt();
		/*
		 * NO 08; TYPE INT; UNIT N/A; RANGE 1 to 64; Primary Moment Type of
		 * product based on. See Table 2-7
		 */
		primaryMomentType = file.readInt();
		/*
		 * NO 09; TYPE INT; UNIT N/A; RANGE 1 to 64; Secondary Moment Type of
		 * product based on. See Table 2-7
		 */
		secondaryMomentType = file.readInt();

		/*
		 * NO 10; TYPE 64 Bytes; UNIT N/A ; RANGE N/A ;
		 */
		reserved = file.readBytes(64);
	}

}
