package mq.radar.cinrad;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Befour use this lib ,please set System Property :  "cinrad.config.home"(cinrad config dir).
 */
public final class ResourceUtils {
	private final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

	private static ResourceUtils singleton = null;

	private Properties props = null;

	private String location = null;

	private Map<String, URL> cinradStationsURLMap = null;

	private Map<String, URL> cinradColorURLMap = null;

	private ResourceUtils() {
		props = new Properties();
		location = System.getProperty(MQConstants.CINRAD_CONFIG_HOME)
				+ File.pathSeparator;

		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(location
					+ MQConstants.CINRAD_CONFIG_FILE_NAME));
			props.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			logger.error(
					"When Reading CINRAD_CONFIG_FILE Occurred FileNotFoundException:{}.",
					e);

		} catch (IOException e) {
			logger.error(
					"When Reading CINRAD_CONFIG_FILE Occurred IOException:{}.",
					e);
		}

	}

	public static ResourceUtils getInstance() {
		if (singleton == null) {
			singleton = new ResourceUtils();
		}
		return singleton;
	}

	public String getCinradStationsListFile() {
		return location
				+ props.getProperty(MQConstants.CINRAD_STATION_LIST_FILE);
	}

	public String getCinradColormapsConfigFile() {
		return location
				+ props.getProperty(MQConstants.CINRAD_COLORMAPS_CONFIG_FILE);
	}

	public String getCinradColorSeparator() {
		return props.getProperty(MQConstants.CINRAD_COLOR_SEPARATOR);
	}

	public Map<String, URL> getCinradStationsURLMap() {
		if (cinradStationsURLMap == null) {
			cinradStationsURLMap = genStringURLMap("CinradStationsURLMap",
					this.getCinradStationsListFile());
		}
		return cinradStationsURLMap;
	}

	public Map<String, URL> getCinradProductColorURLMap() {
		if (cinradColorURLMap == null) {
			cinradColorURLMap = genStringURLMap("CinradColorURLMap",
					this.getCinradColormapsConfigFile());
		}
		return cinradColorURLMap;
	}

	private Map<String, URL> genStringURLMap(String name, String configFile) {
		Properties prop = new Properties();
		Map<String, URL> map = new HashMap<String, URL>();
		InputStream in;
		try {
			in = new BufferedInputStream(new FileInputStream(configFile));
			prop.load(in);
			in.close();

			Set<String> set = prop.stringPropertyNames();
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				map.put(key, new URL(prop.getProperty(key)));
			}
		} catch (FileNotFoundException e) {
			logger.error("When Reading {} Occurred FileNotFoundException:{}.",
					name, e);

		} catch (IOException e) {
			logger.error("When Reading {} Occurred IOException:{}.", name, e);
		}

		return map;

	}

}
