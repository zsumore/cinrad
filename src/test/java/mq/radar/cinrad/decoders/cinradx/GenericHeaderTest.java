package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ucar.unidata.io.RandomAccessFile;

public class GenericHeaderTest {
	RandomAccessFile file1,file3;
	
	String dbzf="data/cinradx/ppi/dBZ/FSNH_20150830000024Z_PPI_01_dBZ";
	
	String kdpf="data/cinradx/ppi/KDP/FSNH_20150831000348Z_PPI_01_KDP";
	
	String cappif="data/cinradx/cappi/FSNH_20150831000348Z_CAPPI_00_default";

	@Before
	public void setUp() throws Exception {
		file1 = new RandomAccessFile(kdpf, "r");
		
		file3 = new RandomAccessFile(cappif, "r");
	}

	@After
	public void tearDown() throws Exception {
		file1.flush();
		file1.close();
		
		file3.flush();
		file3.close();
	}

	@Test
	public void testRebuildPPI() throws IOException {
		GenericHeader genericHeader=new GenericHeader();
		genericHeader.builder(file1, 0);
		System.out.println(genericHeader);
		
		assertTrue(genericHeader.getMajorVersion()==1);
		assertTrue(genericHeader.getMinorVersion()==1);
		assertTrue(genericHeader.getGenericType()==2);
		assertTrue(genericHeader.getProductType()==1);
	}
	
	@Test
	public void testRebuildCAPPI() throws IOException {
		GenericHeader genericHeader=new GenericHeader();
		genericHeader.builder(file3, 0);
		System.out.println(genericHeader);
		
		assertTrue(genericHeader.getMajorVersion()==1);
		assertTrue(genericHeader.getMinorVersion()==1);
		assertTrue(genericHeader.getGenericType()==2);
		assertTrue(genericHeader.getProductType()==3);
	}


}
