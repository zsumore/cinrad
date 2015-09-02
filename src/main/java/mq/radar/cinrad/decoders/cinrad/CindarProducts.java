package mq.radar.cinrad.decoders.cinrad;

/**
 * 
 * @author hjc
 * 
 *         49号产品(COMBINED_MOMENT_49)的range实际值为13.5， 这里把它的值*10化为integer. <br>
 * <br>
 * <br>
 * <br>
 * 
 */
public enum CindarProducts {

	UNKNOWN(-1, -1, "N/A", -1, -1, MessageFormat.Unknow), BASE_REFLECTIVITY_16(
			16, 1, ".54*1 Nmi*Deg", 124, 8, MessageFormat.Radial), BASE_REFLECTIVITY_17(
			17, 1, "1.1*1 Nmi*Deg", 248, 8, MessageFormat.Radial), BASE_REFLECTIVITY_18(
			18, 1, "2.2*1 Nmi*Deg", 248, 8, MessageFormat.Radial), BASE_REFLECTIVITY_19(
			19, 1, ".54*1 Nmi*Deg", 124, 16, MessageFormat.Radial), BASE_REFLECTIVITY_20(
			20, 1, "1.1*1 Nmi*Deg", 248, 16, MessageFormat.Radial), BASE_REFLECTIVITY_21(
			21, 1, "2.2*2 Nmi*Deg", 248, 16, MessageFormat.Radial), BASE_VELOCITY_22(
			22, 2, ".13*1 Nmi*Deg", 32, 8, MessageFormat.Radial), BASE_VELOCITY_23(
			23, 2, ".27*1 Nmi*Deg", 62, 8, MessageFormat.Radial), BASE_VELOCITY_24(
			24, 2, ".54*1 Nmi*Deg", 124, 8, MessageFormat.Radial), BASE_VELOCITY_25(
			25, 2, ".13*1 Nmi*Deg", 32, 16, MessageFormat.Radial), BASE_VELOCITY_26(
			26, 2, ".27*1 Nmi*Deg", 62, 16, MessageFormat.Radial), BASE_VELOCITY_27(
			27, 2, ".54*1 Nmi*Deg", 124, 16, MessageFormat.Radial), BASE_SPECTRUM_WIDTH_28(
			28, 3, ".13*1 Nmi*Deg", 32, 8, MessageFormat.Radial), BASE_SPECTRUM_WIDTH_29(
			29, 3, ".27*1 Nmi*Deg", 62, 8, MessageFormat.Radial), BASE_SPECTRUM_WIDTH_30(
			30, 3, ".54*1 Nmi*Deg", 124, 8, MessageFormat.Radial), COMPOSITE_REFLECTIVITY_35(
			35, 6, ".54*.54 Nmi*Nmi", 124, 8, MessageFormat.Raster), COMPOSITE_REFLECTIVITY_36(
			36, 6, "2.2*2.2 Nmi*Nmi", 248, 8, MessageFormat.Raster), COMPOSITE_REFLECTIVITY_37(
			37, 6, ".54*.54 Nmi*Nmi", 124, 16, MessageFormat.Raster), COMPOSITE_REFLECTIVITY_38(
			38, 6, "2.2*2.2 Nmi*Nmi", 248, 16, MessageFormat.Raster), COMPOSITE_REFLECTIVITY_CONTOUR_39(
			39, 7, ".54*.54 Nmi*Nmi", 124, -1,
			MessageFormat.LinkedContourVector), COMPOSITE_REFLECTIVITY_CONTOUR_40(
			40, 7, "2.2*2.2 Nmi*Nmi", 248, -1,
			MessageFormat.LinkedContourVector), ECHO_TOPS_41(41, 8,
			"2.2*2.2 Nmi*Nmi", 124, 16, MessageFormat.Raster), ECHO_TOPS_CONTOUR_42(
			42, 9, "2.2*2.2 Nmi*Nmi", 124, -1,
			MessageFormat.LinkedContourVector), SEVERE_WEATHER_REFLECTIVITY_43(
			43, 10, ".54*1 Nmi*Deg", 27, 16, MessageFormat.Radial), SEVERE_WEATHER_VELOCITY_44(
			44, 10, ".13*1 Nmi*Deg", 27, 16, MessageFormat.Radial), SEVERE_WEATHER_SPECTRUM_WIDTH_45(
			45, 10, ".13*1 Nmi*Deg", 27, 8, MessageFormat.Radial), SEVERE_WEATHER_SHEAR_46(
			46, 10, ".27*1 Nmi*Deg", 27, 16, MessageFormat.Radial), SEVERE_WEATHER_PROBABILITY_47(
			47, 11, "2.2*2.2 Nmi*Nmi", 124, -1, MessageFormat.Geographic), VAD_WIND_PROFILE_48(
			48, 12, "5 Knots", -1, 5, MessageFormat.Nongeographic), COMBINED_MOMENT_49(
			49, 13, ".27*.27 Nmi*Nmi", 135, 16, MessageFormat.Raster), CROSS_SECTION_REFLECTIVITY_50(
			50, 14, ".54Horizontal*.27Vert Nmi*Nmi", 124, 16,
			MessageFormat.Raster), CROSS_SECTION_VELOCITY_51(51, 14,
			".54Horizontal*.27Vert Nmi*Nmi", 124, 16, MessageFormat.Raster), CROSS_SECTION_SPECTRUM_WIDTH_52(
			52, 14, ".54Horizontal*.27Vert Nmi*Nmi", 124, 8,
			MessageFormat.Raster), WEAK_ECHO_REGION_53(53, 15,
			".54*.54 Nmi*Nmi", 27, 8, MessageFormat.Raster), STORM_RELATIVE_MEAN_RADIAL_VELOCITY_55(
			55, 16, ".27*1 Nmi*Deg", 27, 16, MessageFormat.Radial), STORM_RELATIVE_MEAN_RADIAL_VELOCITY_56(
			56, 16, ".54*1 Nmi*Deg", 124, 16, MessageFormat.Radial), VERTICALLY_INTEGRATED_LIQUID_57(
			57, 17, "2.2*2.2 Nmi*Nmi", 124, 16, MessageFormat.Raster), STORM_TRACKING_INFORMATION_58(
			58, 18, "N/A", 248, -1, MessageFormat.GeographicAndNongeographic), HAIL_INDEX_59(
			59, 19, "N/A", 124, -1, MessageFormat.GeographicAndNongeographic), MESOCYCLONE_60(
			60, 20, "N/A", 124, -1, MessageFormat.GeographicAndNongeographic), TORNADO_VORTEX_SIGNATURE_61(
			61, 21, "N/A", 124, -1, MessageFormat.GeographicAndNongeographic), STORM_STRUCTURE_62(
			62, 22, "N/A", 248, -1, MessageFormat.Alphanumeric), LAYER_COMPOSITE_REFLECTIVITY_63(
			63, 23, "2.2*2.2 Nmi*Nmi", 124, 8, MessageFormat.Raster), LAYER_COMPOSITE_REFLECTIVITY_64(
			64, 23, "2.2*2.2 Nmi*Nmi", 124, 8, MessageFormat.Raster), LAYER_COMPOSITE_REFLECTIVITY_65(
			65, 23, "2.2*2.2 Nmi*Nmi", 124, 8, MessageFormat.Raster), LAYER_COMPOSITE_REFLECTIVITY_66(
			66, 23, "2.2*2.2 Nmi*Nmi", 124, 8, MessageFormat.Raster), LAYER_COMPOSITE_TURBULENCE_67(
			67, 24, "2.2*2.2 Nmi*Nmi", 80, 8, MessageFormat.Raster), LAYER_COMPOSITE_TURBULENCE_68(
			68, 24, "2.2*2.2 Nmi*Nmi", 80, 8, MessageFormat.Raster), LAYER_COMPOSITE_TURBULENCE_69(
			69, 24, "2.2*2.2 Nmi*Nmi", 80, 8, MessageFormat.Raster), LAYER_COMPOSITE_TURBULENCE_70(
			70, 24, "2.2*2.2 Nmi*Nmi", 80, 8, MessageFormat.Raster), LAYER_COMPOSITE_TURBULENCE_71(
			71, 24, "2.2*2.2 Nmi*Nmi", 80, 8, MessageFormat.Raster), LAYER_COMPOSITE_TURBULENCE_72(
			72, 24, "2.2*2.2 Nmi*Nmi", 80, 8, MessageFormat.Raster), USER_ALERT_MESSAGE_73(
			73, 25, "N/A", -1, -1, MessageFormat.Alphanumeric), FREE_TEXT_MESSAGE_75(
			75, 27, "N/A", -1, -1, MessageFormat.Alphanumeric), ONE_HOUR_PRECIPITATION_78(
			78, 28, "1.1*1 Nmi*Deg", 124, 16, MessageFormat.Radial), THREE_HOUR_PRECIPITATION_79(
			79, 28, "1.1*1 Nmi*Deg", 124, 16, MessageFormat.Radial), STORM_TOTAL_PRECIPITATION_80(
			80, 28, "1.1*1 Nmi*Deg", 124, 16, MessageFormat.Radial), VELOCITY_AZIMUTH_DISPLAY_84(
			84, 12, "5 Knots", -1, 8, MessageFormat.Nongeographic), CROSS_SECTION_REFLECTIVITY_85(
			85, 14, ".54Horizontal*.27Vert Nmi*Nmi", 124, 8,
			MessageFormat.Raster), CROSS_SECTION_VELOCITY_86(86, 14,
			".54Horizontal*.27Vert Nmi*Nmi", 124, 8, MessageFormat.Raster), COMBINED_SHEAR_87(
			87, 4, "Adaptable Nmi*Nmi", 62, 16, MessageFormat.Raster), COMBINED_SHEAR_CONTOUR_88(
			88, 5, "Adaptable Nmi*Nmi", 62, -1,
			MessageFormat.LinkedContourVector), CAPPI_110(110, 1,
			".54*1 Nmi*Deg", 124, 16, MessageFormat.Radial);

	private final Integer code, ntr, range, dataLevel;
	private final String resolution;
	private final MessageFormat messageFormat;

	private CindarProducts(Integer code, Integer ntr, String resolution,
			Integer range, Integer dataLevel, MessageFormat messageFormat) {
		this.code = code;
		this.ntr = ntr;
		this.resolution = resolution;
		this.range = range;
		this.dataLevel = dataLevel;
		this.messageFormat = messageFormat;
	}

	public Integer getCode() {
		return code;
	}

	public Integer getRange() {
		return range;
	}

	public Integer getDataLevel() {
		return dataLevel;
	}

	public String getResolution() {
		return resolution;
	}

	public MessageFormat getMessageFormat() {
		return messageFormat;
	}

	public Integer getNTR() {
		return ntr;
	}

	public static CindarProducts getProduct(int pcode) {

		CindarProducts[] cindarProducts = values();
		int size = cindarProducts.length;
		for (int i = 0; i < size; i++) {
			if (pcode == cindarProducts[i].getCode())
				return cindarProducts[i];
		}

		return UNKNOWN;
	}

	public ProductType getProductType() {

		if (code == 19 || code == 20 || code == 25 || code == 26 || code == 27
				|| code == 28 || code == 30 || code == 43 || code == 44
				|| code == 46 || code == 56 || code == 78 || code == 79
				|| code == 80 || code == 110) {
			return ProductType.L3RADIAL;
		} else if (code == 36 || code == 37 || code == 38 || code == 41
				|| code == 53 || code == 57 || code == 65 || code == 66) {
			return ProductType.L3RASTER;
		} else if (code == 58 || code == 59 || code == 60 || code == 61
				|| code == 62) {
			return ProductType.L3ALPHA;
		} else if (code == 48) {
			return ProductType.L3VAD;
		} else {
			return ProductType.UNKNOWN;
		}
	}

}
