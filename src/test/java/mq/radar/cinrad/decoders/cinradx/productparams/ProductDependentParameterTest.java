package mq.radar.cinrad.decoders.cinradx.productparams;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mq.radar.cinrad.decoders.cinradx.CutConfiguration;
import mq.radar.cinrad.decoders.cinradx.ProductHeader;
import mq.radar.cinrad.decoders.cinradx.ProductUtils;
import mq.radar.cinrad.decoders.cinradx.TaskConfiguration;
import ucar.unidata.io.RandomAccessFile;

public class ProductDependentParameterTest {

	String dbz = "data/cinradx/ppi/dBZ/FSNH_20150830000024Z_PPI_01_dBZ";

	String cappi = "data/cinradx/cappi/FSNH_20150831000348Z_CAPPI_00_default";

	RandomAccessFile dbzFile, cappiFile;

	@Before
	public void setUp() throws Exception {
		dbzFile = new RandomAccessFile(dbz, "r");
		cappiFile = new RandomAccessFile(cappi, "r");
	}

	@After
	public void tearDown() throws Exception {
		dbzFile.flush();
		dbzFile.close();

		cappiFile.flush();
		cappiFile.close();
	}

	@Test
	public void testDBZ() throws IOException {
		TaskConfiguration taskConfiguration = new TaskConfiguration();
		taskConfiguration.builder(dbzFile, 160);
		System.out.println(dbzFile.getFilePointer());
		System.out.println(taskConfiguration);
		assertTrue(taskConfiguration.getCutNumber() == 8);
		for (int i = 0; i < taskConfiguration.getCutNumber(); i++) {
			CutConfiguration cutConfiguration = new CutConfiguration();
			cutConfiguration.builder(dbzFile, -1);
			System.out.println(cutConfiguration);

			System.out.println(dbzFile.getFilePointer());
		}
		ProductHeader productHeader = new ProductHeader();
		productHeader.builder(dbzFile, -1);
		System.out.println(productHeader);
		assertTrue(productHeader.getProductNumber() == 1);

		ProductDependentParameter productDependentParameter = new ProductDependentParameter(
				ProductUtils.getProductType(productHeader.getProductNumber()), dbzFile.readBytes(64));
		assertTrue(productDependentParameter.getProductType().name().equalsIgnoreCase("PPI"));
		// productDependentParameter.buildProductParameter(dbzFile.readBytes(64));
		System.out.println(productDependentParameter.getProductType());
		System.out.println(productDependentParameter);

	}

}
