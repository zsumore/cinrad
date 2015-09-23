/**
 * 
 */
package org.apache.commons.config;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author qxj-hjc
 *
 */
public class ConfigTest {
	Configuration config;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		ClassLoader loader = this.getClass().getClassLoader();

		config = new PropertiesConfiguration(loader.getResource("org/apache/commons/config/decodeHints.properties"));

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		config = null;
	}

	@Test
	public void test() {
		Iterator<String> iterator = config.getKeys();

		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
		System.out.println(config.getDouble("geometry.buffer"));
		assertTrue(config.getDouble("geometry.buffer") == 0.000001);
	}

}
