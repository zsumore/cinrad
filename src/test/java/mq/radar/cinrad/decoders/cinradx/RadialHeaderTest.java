package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.RandomAccessFile;

public class RadialHeaderTest {

	String dbz = "data/cinradx/ppi/KDP/Z9757_20161021235204Z_PPI_04_KDP";

	String cappi = "data/cinradx/cappi/Z9757_20161021080221Z_CAPPI_00_default";

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
		//assertTrue(taskConfiguration.getCutNumber() == 8);
		for (int i = 0; i < taskConfiguration.getCutNumber(); i++) {
			CutConfiguration cutConfiguration = new CutConfiguration();
			cutConfiguration.builder(dbzFile, -1);
			System.out.println(cutConfiguration);

			System.out.println(dbzFile.getFilePointer());
		}
		ProductHeader productHeader = new ProductHeader();
		productHeader.builder(dbzFile, -1);
		System.out.println(productHeader);
		//assertTrue(productHeader.getProductNumber() == 1);

		ProductDependentParameter productDependentParameter = new ProductDependentParameter(
				CinradXUtils.getProductType(productHeader.getProductNumber()), dbzFile.readBytes(64));
		//assertTrue(productDependentParameter.getProductType().name().equalsIgnoreCase("PPI"));
		// productDependentParameter.buildProductParameter(dbzFile.readBytes(64));
		System.out.println(productDependentParameter.getProductType());
		System.out.println(productDependentParameter);
		
		RadialHeader radialHeader=new RadialHeader();
		radialHeader.builder(dbzFile, -1);
		
		System.out.println(radialHeader);

	}

}
