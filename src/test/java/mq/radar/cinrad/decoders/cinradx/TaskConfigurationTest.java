package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ucar.unidata.io.RandomAccessFile;

public class TaskConfigurationTest {
	String dbz = "data/cinradx/ppi/dBZ/FSNH_20150830000024Z_PPI_01_dBZ";

	String cappi= "data/cinradx/cappi/FSNH_20150831000348Z_CAPPI_00_default";

	RandomAccessFile dbzFile,cappiFile;

	@Before
	public void setUp() throws Exception {
		dbzFile = new RandomAccessFile(dbz, "r");
		cappiFile=new RandomAccessFile(cappi, "r");
	}

	@After
	public void tearDown() throws Exception {
		dbzFile.flush();
		dbzFile.close();
		
		cappiFile.flush();
		cappiFile.close();
	}

	@Test
	public void testRebuildCAPPI() throws IOException {
		TaskConfiguration config = new TaskConfiguration();
		config.builder(cappiFile, 160);
		System.out.println(config);

		assertTrue(config.getCutNumber()==8);
	}
	
	@Test
	public void testRebuildDBZ() throws IOException {
		TaskConfiguration config = new TaskConfiguration();
		config.builder(dbzFile, 160);
		System.out.println(config);

		assertTrue(config.getCutNumber()==8);
	}
}
