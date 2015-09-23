package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import ucar.unidata.io.RandomAccessFile;

public interface ICinradXBuilder {
	void builder(RandomAccessFile file, long pos) throws IOException;
}
