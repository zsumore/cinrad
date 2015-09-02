package mq.radar.cinrad.decoders.cinrad;

import java.awt.Color;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.junit.After;
import org.junit.Before;
import org.opengis.filter.FilterFactory;

public class DecodeCinradTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	public void testDecodeData() {
		// fail("Not yet implemented");
	}

	
	/**
	public void testGetFeatures() throws FactoryException, IOException,
			DecodeException, DecodeHintNotSupportedException {

		File file = new File(
				"/mnt/radar/Guangzhou/20110426/R/19/20110426.163001.01.19.200");
		CinradHeader header = new DecodeCinradHeader();
		header.decodeHeader(file.toURI().toURL());
		DecodeStreamingRadial decode = new DecodeStreamingRadial(header);
		// decode.setDecodeHint("reducePolygons", true);
		decode.decodeData();
		MapContext context = new MapContext(MQProjections.getInstance()
				.getRadarCoordinateSystem(header));
		// MapContent content = new MapContent();
		context.addLayer(decode.getFeatures(), createPolygonStyle());

		GTRenderer draw = new StreamingRenderer();
		draw.setContext(context);

		// int width = 800;
		// int height = 500;
		// Rectangle paintArea = new Rectangle(width, height);
		// BufferedImage bufferedImage = null;

		// bufferedImage = new BufferedImage(width, height,
		// BufferedImage.TYPE_INT_RGB);
		// Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
		// graphics2D
		// .setRenderingHints(new RenderingHints(
		// RenderingHints.KEY_RENDERING,
		// RenderingHints.VALUE_RENDER_SPEED));
		// graphics2D.setColor(Color.WHITE);
		// graphics2D.fillRect(0, 0, width, height);
		// System.out.println(header.getCinradBounds());
		// System.out.println(context.getCoordinateReferenceSystem().toWKT());
		// System.out.println(decode.getFeatures().size());
		JMapFrame mapFrame = new JMapFrame(context);
		mapFrame.setSize(600, 600);
		mapFrame.setVisible(true);

		/**
		 * try { draw.paint(graphics2D, paintArea, header.getCinradBounds());
		 * 
		 * 
		 * File fileToSave = new
		 * File("/home/hjc/workspace/cinrad/jpeg/test.jpeg");
		 * ImageIO.write(bufferedImage, "jpeg", fileToSave); }catch(IOException
		 * e){
		 * 
		 * }
		 */
//		assertTrue(true);
//	}

	/**
	 * Create a Style to draw polygon features with a thin blue outline and a
	 * cyan fill
	 */
	private Style createPolygonStyle() {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
		FilterFactory filterFactory = CommonFactoryFinder
				.getFilterFactory(null);
		// create a partially opaque outline stroke
		Stroke stroke = styleFactory.createStroke(
				filterFactory.literal(Color.BLUE), filterFactory.literal(1),
				filterFactory.literal(0.5));

		// create a partial opaque fill
		Fill fill = styleFactory.createFill(filterFactory.literal(Color.CYAN),
				filterFactory.literal(0.5));

		/* Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features */
		PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke,
				fill, null);

		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

}
