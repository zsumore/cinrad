package mq.radar.cinrad.decoders.cinrad;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;

import mq.radar.cinrad.decoders.DecodeException;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

public class DecodeCindarHeaderTest {

	
	public void testDecodeHeader_19() throws MalformedURLException,
			DecodeException, FactoryException, TransformException {
		File file = new File(
				"/mnt/radar/Guangzhou/20110426/R/19/20110426.163001.01.19.200");
		CinradHeader header = new DecodeCinradHeader();
		header.decodeHeader(file.toURI().toURL());
		header.close();
		System.out.println("testDecodeHeader_19:");
		System.out.println(header.toString());
		System.out.println("Bound:" + header.getCinradBounds().toString());
		assertTrue(19 == header.getProductCode());
		assertTrue(CinradUtils.datetime.format(header.getScanCalendar().getTime())
				.equals("2011 04 26 16:30:01"));
	}

	
	public void testDecodeHeader_20() throws MalformedURLException,
			DecodeException, FactoryException, TransformException {
		File file = new File(
				"/mnt/radar/Guangzhou/20110426/R/20/20110426.163001.01.20.200");
		CinradHeader header = new DecodeCinradHeader();
		header.decodeHeader(file.toURI().toURL());
		header.close();
		System.out
				.println("---------------------------------------------------");
		System.out.println("testDecodeHeader_20:");
		System.out.println(header.toString());
		System.out.println("Bound:" + header.getCinradBounds().toString());
		assertTrue(20 == header.getProductCode());
		assertTrue(CinradUtils.datetime.format(header.getScanCalendar().getTime())
				.equals("2011 04 26 16:30:01"));
	}

	@SuppressWarnings("unused")
	
	public void testSimpleFeatureType() {
		AttributeType geom = new AttributeTypeBuilder().name("geom")
				.binding(Geometry.class).buildType();

		AttributeType value = new AttributeTypeBuilder().name("value")
				.binding(Float.class).nillable(true).length(5).buildType();
		AttributeType colorIndex = new AttributeTypeBuilder()
				.name("colorIndex").binding(Integer.class).nillable(true)
				.length(4).buildType();

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("CinradAttributes");
		//builder.addBinding(geom);
		//builder.addBinding(value);
		//builder.addBinding(colorIndex);
		builder.add("geom", Geometry.class);
		builder.nillable(true).length(5).add("value", Float.class);
		builder.nillable(true).length(4).add("colorIndex", Integer.class);
		SimpleFeatureType schema = builder.buildFeatureType();
		System.out.println("schema.getAttribute:"+schema.getAttributeCount());
		assertTrue(true);
	}

}
