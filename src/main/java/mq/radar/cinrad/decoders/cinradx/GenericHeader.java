package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;
import java.util.Arrays;

import ucar.unidata.io.RandomAccessFile;

/*
 * Generic Header 
 * Range 32 Bytes
 * 
 * No 1
 */
public class GenericHeader implements ICinradXHeaderBuilder{

	/*
	 * Magic word for product; Range 0x4D545352; No 1
	 */
	private int magicWord;

	/*
	 * Major Version; Range 0 to 65536; No 2
	 */
	private short majorVersion;

	/*
	 * Minor Version Range 0 to 65536 No 3
	 */
	private short minorVersion;

	/*
	 * Type of data, see Table 2-3; Range 2; No 4
	 */
	private int genericType;

	/*
	 * Type of Product, see Table 3-3; Range 1 to 100 ;No 5
	 */
	private int productType;

	/*
	 * reserved; Range 16 Bytes ;No 6
	 */
	private byte[] reserved;

	public GenericHeader() {

	}

	public int getMagicWord() {
		return magicWord;
	}

	public short getMajorVersion() {
		return majorVersion;
	}

	public short getMinorVersion() {
		return minorVersion;
	}

	public int getGenericType() {
		return genericType;
	}

	public int getProductType() {
		return productType;
	}

	public byte[] getReserved() {
		return reserved;
	}

	@Override
	public String toString() {
		return "GenericHeader [magicWord=" + magicWord + ", majorVersion=" + majorVersion + ", minorVersion="
				+ minorVersion + ", genericType=" + genericType + ", productType=" + productType + ", reserved="
				+ Arrays.toString(reserved) + "]";
	}

	/*
	 * if pos<0,do not seek.
	 */
	@Override
	public void builder(RandomAccessFile file, long pos) throws IOException {
		if (pos >= 0)
			file.seek(pos);
		/*
		 * Magic word for product; Range 0x4D545352; No 1
		 */
		magicWord=file.readInt();
		/*
		 * Major Version; Range 0 to 65536; No 2
		 */
		majorVersion=file.readShort();
		/*
		 * Minor Version Range 0 to 65536 No 3
		 */
		minorVersion=file.readShort();
		/*
		 * Type of data, see Table 2-3; Range 2; No 4
		 */
		genericType=file.readInt();
		/*
		 * Type of Product, see Table 3-3; Range 1 to 100 ;No 5
		 */
		productType=file.readInt();
		/*
		 * reserved; Range 16 Bytes ;No 6
		 */
		reserved=file.readBytes(16);

	};

}
