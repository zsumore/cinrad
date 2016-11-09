package mq.radar.cinrad.decoders.cinradx;

import static org.junit.Assert.fail;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.JMapFrame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import mq.radar.cinrad.MQProjections;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;
import mq.radar.cinrad.decoders.cinrad.CindarProducts;
import mq.radar.cinrad.decoders.cinrad.CinradColorFactory;

public class DecodeRadialTest {

	IDecodeCinradXHeader cinradXHeader;

	DecodeRadial decodeRadial;

	static String dbzf = "data/cinradx/ppi/dBZ/FSNH_20150830000024Z_PPI_01_dBZ";

	@Before
	public void setUp() throws Exception {

		cinradXHeader = new DecodeCinradXHeader();
		cinradXHeader.decodeHeader(new File(dbzf).toURI().toURL());
	}

	@After
	public void tearDown() throws Exception {
		cinradXHeader = null;
		decodeRadial = null;
	}

	@Test
	public void test() throws ConfigurationException, DecodeException, IOException, TransformException {

		System.out.println(new File(dbzf).toURI().toURL());

		decodeRadial = new DecodeRadial(cinradXHeader);

		decodeRadial.decodeData(true);

		System.out.println(decodeRadial.getFeatures().size());

		fail("Not yet implemented");
	}

	public static void main(String[] args) throws DecodeException, IOException, FactoryException,
			DecodeHintNotSupportedException, CQLException, TransformException, ConfigurationException {
		IDecodeCinradXHeader cinradXHeader = new DecodeCinradXHeader();
		cinradXHeader.decodeHeader(new File(dbzf).toURI().toURL());

		DecodeRadial decode = new DecodeRadial(cinradXHeader);

		// MQXFilter filter = new MQXFilter();
		// valueIndices是多边形color的值，color值的范围为0到15，分别对应反射率因子值0到75dBZ
		// color值为1到15都显示
		// int[] valueIndices = { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		// int[] valueIndices = { 2, 3, 4, 7, 8, 9, 10, 13, 14,15 };
		// filter.setMinValue(15);
		// filter.setMaxValue();
		decode.setDecodeHint(DecodeRadial.MIN_VALUE, 20);

		// 是否减少多边形，如果设为true，会把颜色等级相同而且相邻的多边形合并为一个新的多边形，但是速度会变慢
		decode.setDecodeHint(DecodeRadial.REDUCE_POLYGONS, true);
		boolean colorMode = true;
		decode.setDecodeHint(DecodeRadial.COLOR_MODE, colorMode);

		decode.setDecodeHint(DecodeRadial.MULTIPOLYGON_MODE, true);
		// decode.setDecodeHint("reducePolygons", true);
		// System.out.println(System.currentTimeMillis());
		decode.decodeData(true);
		// System.out.println(System.currentTimeMillis());

		MapContent context = new MapContent();
		context.setTitle("Quickstart");
		context.getViewport().setCoordinateReferenceSystem(MQProjections.getInstance().getWGS84CoordinateSystem());
		context.getViewport().setBounds(new ReferencedEnvelope(
				XMaxGeographicExtent.getCinradExtent(
						cinradXHeader.getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLatitude(),
						cinradXHeader.getICinradXHeader().getCommonBlocks().getSiteConfiguration().getLongitude(),
						80000),
				MQProjections.getInstance().getWGS84CoordinateSystem()));

		Color[] colors = CinradColorFactory.getColors(CindarProducts.BASE_REFLECTIVITY_19);
		context.addLayer(new FeatureLayer(decode.getFeatures(), createPolygonStyle(colors, colorMode)));
		// context.addLayer(decode.getFeatures(), createPolygonStyle(colors));

		GTRenderer draw = new StreamingRenderer();
		draw.setMapContent(context);

		JMapFrame mapFrame = new JMapFrame(context);
		mapFrame.enableToolBar(true);
		mapFrame.enableStatusBar(true);
		mapFrame.setSize(800, 600);
		mapFrame.getMapPane().reset();
		mapFrame.setVisible(true);

		System.out.println(decode.getFeatures().size());

	}

	private static Style createPolygonStyle(Color[] colors, boolean colorMode) throws CQLException {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
		FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

		int size = colors.length;
		Rule[] rules = new Rule[size];
		for (int i = 0; i < size; i++) {
			Rule r = createRule(styleFactory, filterFactory, colors[i]);
			if (colorMode) {

				r.setFilter(CQL.toFilter("colorIndex = " + i));
			} else {
				StringBuffer buffer = new StringBuffer("colorIndex >= " + i * 5);
				if (!(i == size - 1)) {
					buffer.append(" and colorIndex < " + (i + 1) * 5);
				}
				r.setFilter(CQL.toFilter(buffer.toString()));
			}
			rules[i] = r;
		}
		FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rules);
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	private static Rule createRule(StyleFactory sf, FilterFactory ff, Color fillColor) {
		Symbolizer symbolizer = null;
		Fill fill = null;
		Stroke stroke = sf.createStroke(ff.literal(fillColor), ff.literal(1.0f));

		fill = sf.createFill(ff.literal(fillColor), ff.literal(1.0f));
		symbolizer = sf.createPolygonSymbolizer(stroke, fill, "geom");

		Rule rule = sf.createRule();
		rule.symbolizers().add(symbolizer);
		return rule;
	}

}
