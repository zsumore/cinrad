package mq.radar.cinrad.decoders.cinradx.productparams;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mq.radar.cinrad.decoders.cinradx.CutConfiguration;
import mq.radar.cinrad.decoders.cinradx.ProductHeader;
import mq.radar.cinrad.decoders.cinradx.TaskConfiguration;
import ucar.unidata.io.RandomAccessFile;

public class VWPParameterTest {

	String vwp = "data/cinradx/ppi/dBZ/FSNH_20150830000024Z_PPI_01_dBZ";

	RandomAccessFile vwpFile;

	@Before
	public void setUp() throws Exception {
		vwpFile = new RandomAccessFile(vwp, "r");

	}

	@After
	public void tearDown() throws Exception {
		vwpFile.flush();
		vwpFile.close();

	}

	@Test
	public void testDBZ() throws IOException {
		TaskConfiguration taskConfiguration = new TaskConfiguration();
		taskConfiguration.builder(vwpFile, 160);
		System.out.println(vwpFile.getFilePointer());
		System.out.println(taskConfiguration);
		assertTrue(taskConfiguration.getCutNumber() == 8);
		for (int i = 0; i < taskConfiguration.getCutNumber(); i++) {
			CutConfiguration cutConfiguration = new CutConfiguration();
			cutConfiguration.builder(vwpFile, -1);
			System.out.println(cutConfiguration);

			System.out.println(vwpFile.getFilePointer());
		}
		ProductHeader productHeader = new ProductHeader();
		productHeader.builder(vwpFile, -1);
		System.out.println(productHeader);
		assertTrue(productHeader.getProductNumber() == 1);
		System.out.println(vwpFile.getFilePointer());
		
		VWPParameter productDependentParameter = new VWPParameter(vwpFile.readBytes(64));
		assertTrue(productDependentParameter.getProductType().name().equalsIgnoreCase("VWP"));
		System.out.println(productDependentParameter.getProductType());
		System.out.println(productDependentParameter);

	}

}
