package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CinradXUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		System.out.println(CinradXUtils.getProjectionByName("WGS84"));
		assertTrue(null != CinradXUtils.getProjectionByName("WGS84"));
	}

}
