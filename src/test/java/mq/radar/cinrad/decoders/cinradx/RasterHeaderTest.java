package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mq.radar.cinrad.decoders.cinradx.productparams.ProductDependentParameter;
import ucar.unidata.io.RandomAccessFile;

public class RasterHeaderTest {
	
	String tops = "data/cinradx/tops/Z9757_20161021000203Z_TOPS_00_default";

	

	RandomAccessFile topsFile;

	@Before
	public void setUp() throws Exception {
		topsFile = new RandomAccessFile(tops, "r");
		
	}

	@After
	public void tearDown() throws Exception {
		topsFile.flush();
		topsFile.close();

		
	}

	@Test
	public void testDBZ() throws IOException {
		TaskConfiguration taskConfiguration = new TaskConfiguration();
		taskConfiguration.builder(topsFile, 160);
		System.out.println(topsFile.getFilePointer());
		System.out.println(taskConfiguration);
		System.out.println(taskConfiguration.getCutNumber());
		//assertTrue(taskConfiguration.getCutNumber() == 8);
		for (int i = 0; i < taskConfiguration.getCutNumber(); i++) {
			CutConfiguration cutConfiguration = new CutConfiguration();
			cutConfiguration.builder(topsFile, -1);
			System.out.println(cutConfiguration);

			System.out.println(topsFile.getFilePointer());
		}
		ProductHeader productHeader = new ProductHeader();
		productHeader.builder(topsFile, -1);
		System.out.println(productHeader);
		assertTrue(productHeader.getProductNumber() == 6);

		ProductDependentParameter productDependentParameter = new ProductDependentParameter(
				CinradXUtils.getProductType(productHeader.getProductNumber()), topsFile.readBytes(64));
		assertTrue(productDependentParameter.getProductType().name().equalsIgnoreCase("ET"));
		// productDependentParameter.buildProductParameter(dbzFile.readBytes(64));
		System.out.println(productDependentParameter.getProductType());
		System.out.println(productDependentParameter);
		
		RasterHeader header=new RasterHeader();
		header.builder(topsFile, -1);
		
		System.out.println(header);

	}

}
