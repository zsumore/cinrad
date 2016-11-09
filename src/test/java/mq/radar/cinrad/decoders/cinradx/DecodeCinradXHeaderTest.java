package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DecodeCinradXHeaderTest {

	String dbzf = "data/cinradx/ppi/dBZ/Z9757_20161021000203Z_PPI_01_dBZ";

	String kdpf = "data/cinradx/ppi/KDP/Z9757_20161021235204Z_PPI_04_KDP";

	String cappif = "data/cinradx/cappi/Z9757_20161021080221Z_CAPPI_00_default";

	DecodeCinradXHeader decodeCinradXHeader;

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		decodeCinradXHeader = new DecodeCinradXHeader();
		decodeCinradXHeader.decodeHeader(new File(cappif).toURL());
	}

	@After
	public void tearDown() throws Exception {

		decodeCinradXHeader.getRandomAccessFile().close();
		decodeCinradXHeader = null;
	}

	@Test
	public void testGetCommonBlocks() {
		
		System.out.println(decodeCinradXHeader.getICinradXHeader().getCommonBlocks().toString());
		System.out.println(decodeCinradXHeader.getICinradXHeader().getProductHeader().toString());

		assertTrue(decodeCinradXHeader.getICinradXHeader().getCommonBlocks().getGenericHeader().getProductType() == 1);

		assertTrue(decodeCinradXHeader.getICinradXHeader().getProductHeader().getProductNumber() == 1);

		

	}

}
