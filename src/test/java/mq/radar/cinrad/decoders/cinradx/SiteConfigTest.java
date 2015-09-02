package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ucar.unidata.io.RandomAccessFile;

public class SiteConfigTest {
RandomAccessFile file1,file3;
	
	String dbzf="data/cinradx/ppi/dBZ/FSNH_20150830000024Z_PPI_01_dBZ";
	
	String cappif="data/cinradx/cappi/FSNH_20150831000348Z_CAPPI_00_default";

	@Before
	public void setUp() throws Exception {
file1 = new RandomAccessFile(dbzf, "r");
		
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
		SiteConfig siteConfig=new SiteConfig();
		siteConfig.rebuild(file1, 32);
		System.out.println(siteConfig);
		
		assertTrue(siteConfig.getSiteCode().startsWith("FSNH"));
	}
	
	@Test
	public void testRebuildCAPPI() throws IOException {
		SiteConfig siteConfig=new SiteConfig();
		siteConfig.rebuild(file3, 32);
		System.out.println(siteConfig);
		
		assertTrue(siteConfig.getSiteName().startsWith("NANHAI"));
	}

}
