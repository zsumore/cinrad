package mq.radar.cinrad.decoders.cinrad;

import java.awt.Color;

public class CinradColorFactory {
	public static Color[] getColors(CindarProducts product) {
		return getColors(product, true);
	}

	public static Color[] getColors(CindarProducts product, boolean classify) {
		return getColors(product, classify, 0.0f);
	}

	public static Color[] getColors(CindarProducts productCode,
			boolean classify, float productVersion) {

		Color[] c;
		switch (productCode) {

		case UNKNOWN:
			c = new Color[] {

			new Color(0, 0, 0, 0), Color.BLUE, Color.CYAN, Color.GREEN,
					Color.YELLOW, Color.ORANGE, Color.RED };
			break;

		// case CinradHeaderTem.XMRG:
		// case CinradHeaderTem.L3PC_DPA:

		case BASE_VELOCITY_25:
		case BASE_VELOCITY_26:
		case BASE_VELOCITY_27:
		case STORM_RELATIVE_MEAN_RADIAL_VELOCITY_56:
			c = new Color[16];
			c[0] = new Color(0, 0, 0, 0);
			c[1] = new Color(0, 224, 255);
			c[2] = new Color(0, 138, 255);
			c[3] = new Color(50, 0, 150);
			c[4] = new Color(0, 251, 144);
			c[5] = new Color(0, 187, 0);
			c[6] = new Color(0, 143, 0);
			c[7] = new Color(205, 192, 159);
			c[8] = new Color(118, 118, 118);
			c[9] = new Color(248, 135, 0);
			c[10] = new Color(255, 207, 0);
			c[11] = new Color(255, 255, 0);
			c[12] = new Color(174, 0, 0);
			c[13] = new Color(208, 122, 0);
			c[14] = new Color(255, 0, 0);
			c[15] = new Color(119, 0, 125);
			break;
		case BASE_SPECTRUM_WIDTH_28:
		case BASE_SPECTRUM_WIDTH_30:
			c = new Color[8];
			c[0] = new Color(0, 0, 0, 0);
			c[1] = new Color(118, 118, 118);
			c[2] = new Color(156, 156, 156);
			c[3] = new Color(0, 187, 0);
			c[4] = new Color(255, 0, 0);
			c[5] = new Color(208, 112, 0);
			c[6] = new Color(255, 255, 0);
			c[7] = new Color(119, 0, 125);
			break;
		case ONE_HOUR_PRECIPITATION_78:
		case THREE_HOUR_PRECIPITATION_79:
		case STORM_TOTAL_PRECIPITATION_80:
			c = new Color[16];
			c[0] = new Color(0, 0, 0, 0);
			c[1] = new Color(170, 170, 170);
			c[2] = new Color(118, 118, 118);
			c[3] = new Color(0, 255, 255);
			c[4] = new Color(0, 175, 175);
			c[5] = new Color(0, 255, 0);
			c[6] = new Color(0, 143, 0);
			c[7] = new Color(255, 0, 255);
			c[8] = new Color(175, 50, 125);
			c[9] = new Color(0, 0, 255);
			c[10] = new Color(50, 0, 150);
			c[11] = new Color(255, 255, 0);
			c[12] = new Color(255, 170, 0);
			c[13] = new Color(255, 0, 0);
			c[14] = new Color(174, 0, 0);
			c[15] = new Color(255, 255, 255);
			break;

		case COMPOSITE_REFLECTIVITY_36:
			c = new Color[8];
			c[0] = new Color(0, 0, 0, 0);
			c[1] = new Color(1, 160, 246);
			c[2] = new Color(0, 255, 0);
			c[3] = new Color(0, 144, 0);
			c[4] = new Color(231, 192, 0);
			c[5] = new Color(255, 0, 0);
			c[6] = new Color(192, 0, 0);
			c[7] = new Color(153, 85, 201);
			break;
		// VAD Wind profile
		case VAD_WIND_PROFILE_48:
			c = new Color[6];
			c[0] = new Color(0, 0, 0, 0);
			c[1] = new Color(0, 100, 0);
			c[2] = new Color(230, 176, 46);
			c[3] = new Color(240, 0, 0);
			c[4] = new Color(0, 161, 230);
			c[5] = new Color(220, 0, 99);
			break;
		// LOW / MID & HI LEVEL REFLECTIVITY
		case LAYER_COMPOSITE_REFLECTIVITY_65:
		case LAYER_COMPOSITE_REFLECTIVITY_66:

			// Vertically Integrated Liquid
		case VERTICALLY_INTEGRATED_LIQUID_57:
			c = new Color[16];
			c[0] = new Color(0, 0, 0, 0);
			c[1] = new Color(156, 156, 156);
			c[2] = new Color(118, 118, 118);
			c[3] = new Color(255, 170, 170);
			c[4] = new Color(238, 140, 140);
			c[5] = new Color(201, 112, 112);
			c[6] = new Color(0, 251, 144);
			c[7] = new Color(0, 187, 0);
			c[8] = new Color(255, 255, 112);
			c[9] = new Color(208, 208, 96);
			c[10] = new Color(255, 96, 96);
			c[11] = new Color(218, 0, 0);
			c[12] = new Color(174, 0, 0);
			c[13] = new Color(0, 0, 255);
			c[14] = new Color(255, 255, 255);
			c[15] = new Color(231, 0, 255);
			break;
			
		case SEVERE_WEATHER_SHEAR_46:
			c = new Color[16];
			c[15] = new Color(118, 0, 124);
			c[14] = new Color(254, 0, 254);
			c[13] = new Color(254, 254, 238);
			c[12] = new Color(202, 202, 254);
			c[11] = new Color(130, 130, 254);
			c[10] = new Color(0, 0, 254);
			c[9] = new Color(118, 118, 144);
			c[8] = new Color(136, 0, 0);
			c[7] = new Color(186, 186, 186);
			c[6] = new Color(210, 118, 152);
			c[5] = new Color(242, 170, 170);
			c[4] = new Color(220, 50, 50);
			c[3] = new Color(254, 34, 0);
			c[2] = new Color(238, 144, 0);
			c[1] = new Color(254, 238, 0);
			c[0] = new Color(207, 254, 0);
			break;

		// Echo Tops
		case ECHO_TOPS_41:

			// Default to BREF Color Table
		default:
			c = new Color[16];
			c[0] = new Color(0, 0, 0, 0);
			c[1] = new Color(0, 236, 236);
			c[2] = new Color(1, 160, 246);
			c[3] = new Color(0, 0, 246);
			c[4] = new Color(0, 255, 0);
			c[5] = new Color(0, 200, 0);
			c[6] = new Color(0, 144, 0);
			c[7] = new Color(255, 255, 0);
			c[8] = new Color(231, 192, 0);
			c[9] = new Color(255, 144, 0);
			c[10] = new Color(255, 0, 0);
			c[11] = new Color(214, 0, 0);
			c[12] = new Color(192, 0, 0);
			c[13] = new Color(255, 0, 255);
			c[14] = new Color(153, 85, 201);
			c[15] = new Color(235, 235, 235);

		}

		return c;

	}
}
