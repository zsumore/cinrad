package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import mq.radar.cinrad.decoders.DecodeException;

public class DecodeRaster extends BaseDecoder {

	private final Logger logger = LoggerFactory.getLogger(DecodeRaster.class);

	private Multimap<Float, Polygon> polyMultimap;

	private RasterDataBlock rasterDataBlock;

	private int geoIndex = 0;

	public DecodeRaster(IDecodeCinradXHeader decodeHeader) throws ConfigurationException {
		super(decodeHeader);

	}

	@Override
	public void decodeData(boolean autoClosed) throws DecodeException, IOException, TransformException {
		// TODO Auto-generated method stub
		super.decodeData(autoClosed);

		polyMultimap = ArrayListMultimap.create();

		rasterDataBlock = new RasterDataBlock();
		rasterDataBlock.builder(this.decodeCinradXHeader.getRandomAccessFile(), -1);

		if (autoClosed) {
			this.decodeCinradXHeader.getRandomAccessFile().close();
		}

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setCRS(crs);
		builder.setName("Cinrad-X Raster Data");
		builder.add("geom", Geometry.class);
		builder.add("colorIndex", Float.class);
		builder.add("value", Float.class);
		schema = builder.buildFeatureType();

		// Reset index counter
		geoIndex = 0;

		if (getPlaneFeatures() == null) {
			planeFeatures = new DefaultFeatureCollection();
		}
		planeFeatures.clear();

	}

	@Override
	public void close() {
		super.close();

		if (null != polyMultimap) {
			polyMultimap.clear();
		}

		rasterDataBlock = null;
	}

	public RasterDataBlock getRasterDataBlock() {
		return rasterDataBlock;
	}

}
