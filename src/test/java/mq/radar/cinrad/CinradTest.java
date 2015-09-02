package mq.radar.cinrad;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CinradTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	public void testGetHeader() {
		fail("Not yet implemented");
	}

	
	public void testGetDecoder() {
		fail("Not yet implemented");
	}

	
	public void testIsSupport() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testSwich() {
		int a=3;
		Integer b=null;
		switch(a){
		case 1:
		case 0:
			b=2;
			break;
		case 3:
			b=1;
			break;
		}
		assertTrue(b==1);
		//fail("Not yet implemented");
	}

}
