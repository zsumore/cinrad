package mq.radar.cinrad.decoders.cinrad;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class TestCinrad58 {
	/**
	 * @param args
	 * @throws DecodeException
	 * @throws IOException
	 * @throws FactoryException
	 * @throws DecodeHintNotSupportedException
	 * @throws CQLException
	 * @throws TransformException
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws DecodeException, IOException,
			FactoryException, DecodeHintNotSupportedException, CQLException,
			TransformException {

		String file58 = "data/20110916/STI/58/20110916.000001.00.58.200";
		File file = new File(file58);
		CinradHeader header = new DecodeCinradHeader();
		header.decodeHeader(file.toURI().toURL());

		CindarDecoder decode = new DecodeStormTracking(header);

		decode.decodeData();

		MapContext context = new MapContext(MQProjections.getInstance()
				.getWGS84CoordinateSystem());

		context.addLayer(decode.getFeatures(), createPointStyle());

		GTRenderer draw = new StreamingRenderer();
		draw.setContext(context);

		JMapFrame mapFrame = new JMapFrame(context);
		mapFrame.setSize(800, 600);
		mapFrame.setVisible(true);

		System.out.println(header.getLat());
		System.out.println(header.getLon());
		System.out.println(header.getCinradBounds());

		SimpleFeatureIterator i = decode.getFeatures().features();
		//while (i.hasNext()) {
		//	System.out.println(i.next());
		//}

	}

	private static Style createPointStyle() {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
		FilterFactory filterFactory = CommonFactoryFinder
				.getFilterFactory(null);
		Graphic gr = styleFactory.createDefaultGraphic();

		Mark mark = styleFactory.getCircleMark();

		mark.setStroke(styleFactory.createStroke(
				filterFactory.literal(Color.BLUE), filterFactory.literal(1)));

		mark.setFill(styleFactory.createFill(filterFactory.literal(Color.CYAN)));

		gr.graphicalSymbols().clear();
		gr.graphicalSymbols().add(mark);
		gr.setSize(filterFactory.literal(5));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features
		 */
		PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, "geom");

		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

}
