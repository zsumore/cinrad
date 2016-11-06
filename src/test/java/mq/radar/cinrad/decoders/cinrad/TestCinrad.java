package mq.radar.cinrad.decoders.cinrad;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import mq.radar.cinrad.MQFilter;
import mq.radar.cinrad.MQProjections;
import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.DecodeHintNotSupportedException;

import org.geotools.data.simple.SimpleFeatureIterator;
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
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.MultiPolygon;

public class TestCinrad {

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
	public static void main(String[] args) throws DecodeException, IOException, FactoryException,
			DecodeHintNotSupportedException, CQLException, TransformException {

		String file19 = "data/cinrad/20110916/R/19/20110916.043001.01.19.200";
		String file20 = "data/20110916/R/20/20110916.043001.01.20.200";
		String file26 = "data/20111013.220001.01.26.200";
		String file27 = "data/cinrad/20110916/V/27/20110916.043001.01.27.200";
		String file41 = "data/20110916/ET/41/20110916.000001.00.41.200";
		String file46 = "data/20110916/SWS/46/20110916.000001.03.46.200";
		String file58 = "data/20110916/STI/58/20110916.000001.00.58.200";
		String file110 = "data/110/20150509.000600.03.110.200";
		String file119 = "data/cinrad/20160420.084800.02.19.200";

		String file80 = "data/20120709.000000.00.80.200";
		String file53 = "data/53/20150509.000600.01.53.200";
		
		String file99="data/cinrad/Z_RADR_I_Z9200_20161102141800_P_DOR_SA_V_10_230_15.200.bin";
		String file758="data/cinrad/Z_RADR_I_Z9758_20161101030000_P_DOR_SA_R_20_460_5.758.bin";
		File file = new File(file19);
		CinradHeader header = new DecodeCinradHeader();
		header.decodeHeader(file.toURI().toURL());
		System.out.println(header.toString());
		// System.out.println(header.getElevNumber());
		// System.out.println(header.getProductCode()+"----------------------");

		CindarDecoder decode = new DecodeStreamingRadial(header);
		MQFilter filter = new MQFilter();
		// valueIndices是多边形color的值，color值的范围为0到15，分别对应反射率因子值0到75dBZ
		// color值为1到15都显示
		int[] valueIndices = { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		// int[] valueIndices = { 2, 3, 4, 7, 8, 9, 10, 13, 14,15 };
		filter.setValueIndices(valueIndices);
		decode.setDecodeHint("cinradFilter", filter);

		// 是否减少多边形，如果设为true，会把颜色等级相同而且相邻的多边形合并为一个新的多边形，但是速度会变慢
		decode.setDecodeHint("reducePolygons", true);
		// decode.setDecodeHint("reducePolygons", true);
		// System.out.println(System.currentTimeMillis());
		decode.decodeData();
		System.out.println("--------------"+decode.getFeatures().size());
		System.out.println(decode.getFeatures().features().next().getDefaultGeometry());

		MapContent context = new MapContent();
		context.setTitle("Quickstart");
		context.getViewport().setCoordinateReferenceSystem(MQProjections.getInstance().getWGS84CoordinateSystem());
		context.getViewport().setBounds(new ReferencedEnvelope(header.getCinradBounds(),
				MQProjections.getInstance().getWGS84CoordinateSystem()));

		Color[] colors = CinradColorFactory.getColors(header.getProduct());
		context.addLayer(new FeatureLayer(decode.getFeatures(), createPolygonStyle(colors)));
		// context.addLayer(decode.getFeatures(), createPolygonStyle(colors));

		GTRenderer draw = new StreamingRenderer();
		draw.setMapContent(context);

		JMapFrame mapFrame = new JMapFrame(context);
		mapFrame.enableToolBar(true);
		mapFrame.enableStatusBar(true);
		mapFrame.setSize(800, 600);
		mapFrame.getMapPane().reset();
		mapFrame.setVisible(true);
		// JMapFrame.showMap(context);

		// System.out.println(header.getLat());
		// System.out.println(header.getLon());
		// System.out.println(header.getCinradBounds());

		// SimpleFeatureIterator iterator = decode.getFeatures().features();
		// while (iterator.hasNext()) {
		// SimpleFeature feature = iterator.next();
		// System.out.println(((MultiPolygon) feature.getDefaultGeometry())
		// .getNumGeometries()
		// + ";Points:"
		// + ((MultiPolygon) feature.getDefaultGeometry())
		// .getNumPoints());

		// }

	}

	private static Style createPolygonStyle(Color[] colors) throws CQLException {
		StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(null);
		FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);

		int size = colors.length;
		Rule[] rules = new Rule[size];
		for (int i = 0; i < size; i++) {
			Rule r = createRule(styleFactory, filterFactory, colors[i]);
			r.setFilter(CQL.toFilter("colorIndex = " + i));
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
