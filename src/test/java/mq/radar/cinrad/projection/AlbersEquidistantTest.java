package mq.radar.cinrad.projection;

import static org.junit.Assert.*;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import mq.radar.cinrad.MQProjections;

public class AlbersEquidistantTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProject() throws NoSuchAuthorityCodeException, FactoryException, TransformException {
		double lon =114.0;
		double lat=24.0;
		 AlbersEquidistant mqAlbers=new  AlbersEquidistant (3);
		 double[] albers1=mqAlbers.project(lon, lat);
		 
		 MathTransform transform=CRS.findMathTransform(MQProjections
					.getInstance().getWGS84CoordinateSystem(),  MQProjections.getInstance().getRadarCoordinateSystem(113.355, 23.004));
		 double[] src={lon,lat};
		 double[] albers2=new double[2];
		 DirectPosition2D ptSrc=new DirectPosition2D(lon,lat);
		 DirectPosition2D ptDst=new DirectPosition2D();
		 transform.transform(ptSrc, ptDst);
		 System.out.println(ptDst.x+";"+ptDst.y);
		 transform.transform(src, 0,  albers2, 0, 1);
		 System.out.println("albers1[0]:"+albers1[0]+"; albers2[0]: "+albers2[0]);
		 System.out.println("albers1[1]:"+albers1[1]+"; albers2[1]: "+albers2[1]);
		 assertFalse(albers1[0]==albers2[0]);
		 assertFalse(albers1[1]==albers2[1]);
		
	}

	
	public void testUnproject() {
		fail("Not yet implemented");
	}

}
