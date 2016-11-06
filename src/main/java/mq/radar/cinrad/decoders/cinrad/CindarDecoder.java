package mq.radar.cinrad.decoders.cinrad;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public interface CindarDecoder extends StreamingRadialDecoder {

	public Envelope getCinradExtent();

	public SimpleFeatureCollection getLineFeatures();

	public SimpleFeatureType getLineFeatureType();

	public String getMetaLabel(int index);

	public String getDefaultSymbol();
	
	public CoordinateReferenceSystem getCRS();
	
	public void close();

}
