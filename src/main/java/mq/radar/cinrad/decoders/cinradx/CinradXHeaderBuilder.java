package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import ucar.unidata.io.RandomAccessFile;

public interface CinradXHeaderBuilder {
	void builder(RandomAccessFile file, long pos) throws IOException;
}
