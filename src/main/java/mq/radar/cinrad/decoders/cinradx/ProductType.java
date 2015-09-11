package mq.radar.cinrad.decoders.cinradx;

import static mq.radar.cinrad.decoders.cinradx.DataType.*;

/**
 * see table 3-5
 */
public enum ProductType {
	PPI(new String[] { "Elevation" },new DataType[]{FLOAT}), 
	RHI(new String[] {"Azimuth","Top","Bottom" },new DataType[]{FLOAT,INT,INT}), 
	CAPPI(new String[] {"Layers","Top","Bottom","CAPPIFill" },new DataType[]{INT,INT,INT,INT}), 
	MAX(new String[] {"Top","Bottom" },new DataType[]{INT,INT}), 
	ET(new String[] {"dBZContour" },new DataType[]{FLOAT}), 
	BEAM(new String[] {"NA" },new DataType[]{INT}), 
	VCS(new String[] {"AzimuthofStart","RangeofStart","AzimuthofEnd","RangeofEnd","Top","Bottom" },new DataType[]{FLOAT,INT,FLOAT,INT,INT,INT}), 
	LRA(new String[] {"Top","Bottom" },new DataType[]{INT,INT}), 
	LRM(new String[] {"Top","Bottom" },new DataType[]{INT,INT}), 
	SRR(new String[] {"Elevation","RangeofCenter","AzimuthofCenter","SideLength","SpeedofWind","DirectionofWind" },new DataType[]{FLOAT,INT,FLOAT,INT,FLOAT,FLOAT}), 
	SRM(new String[] {"Elevation","SpeedofWind","DirectionofWind" },new DataType[]{FLOAT,FLOAT,FLOAT}), 
	SWA(new String[] {"Elevation","RangeofCenter","AzimuthofCenter","SideLength" },new DataType[]{FLOAT,INT,FLOAT,INT}), 
	CM(new String[] {"NA" },new DataType[]{INT}),
	WER(new String[] {"Range","Azimuth","SideLength","Levels" },new DataType[]{INT,FLOAT,INT,INT}),
	VIL(new String[] {"NA" },new DataType[]{INT}), 
	HSR(new String[] {"NA" },new DataType[]{INT}), 
	OHP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment"},new DataType[]{INT,INT,INT,INT}), 
	THP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment","Hours"},new DataType[]{INT,INT,INT,INT,INT}), 
	STP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment"},new DataType[]{INT,INT,INT,INT}), 
	USP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment","Hours"},new DataType[]{INT,INT,INT,INT,INT}), 
	SPD(new String[] {"MaxRange","BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment" },new DataType[]{INT,INT,INT,INT,INT}), 
	VAD(new String[] {"Layers","Height" },new DataType[]{INT,SHORT}), 
	VWP(new String[] {"Layers","Height" },new DataType[]{INT,SHORT}), 
	SHEAR(new String[] {"Elevation","RadialShear","AzimuthShear","ElevationShear" },new DataType[]{FLOAT,INT,INT,INT}),
	SWP(new String[] {"MaxRange" },new DataType[]{INT}), 
	STI(new String[] {"MaxRange" },new DataType[]{INT}),
	HI(new String[] {"MaxRange" },new DataType[]{INT}),
	M(new String[] {"MaxRange" },new DataType[]{INT}), 
	TVS(new String[] {"MaxRange" },new DataType[]{INT}), 
	SS(new String[] {"MaxRange" },new DataType[]{INT}), 
	UAM(new String[] {"MaxRange","BaseProduct1","DataType1","BaseProduct2","DataType2","BaseProduct3","DataType3","Area","ForecastTime","LogicalOperation" },new DataType[]{INT,INT,INT,INT,INT,INT,INT,FLOAT,INT,INT}), 
	FTM(new String[] {"NA" },new DataType[]{INT}), 
	HMAX(new String[] {"dBZMinimum" },new DataType[]{FLOAT}), 
	EB(new String[] {"dBZMinimum" },new DataType[]{FLOAT}), 
	GAGE(new String[] {"MaxRange" },new DataType[]{INT}), 
	CONTOUR(new String[] {"MaxRange","BaseProduct","Levels","Start","Interval" },new DataType[]{INT,INT,INT,FLOAT,FLOAT}),
	ML(new String[] {"NA" },new DataType[]{INT}), 
	HCL(new String[] {"NA" },new DataType[]{INT}),
	USER(new String[] {"Algorithm","DescriptionofParameter","DescriptionofParameter","DescriptionofParameter" },new DataType[]{INT,CHAR16,CHAR16,CHAR16});

	private String[] paramNames;
	private DataType[] paramTypes;

	private ProductType(String[] paramNames,DataType[] paramTypes) {
		this.paramNames = paramNames;
		this.paramTypes=paramTypes;
	}

	public int getParamSize() {
		if (null == paramNames)
			return -1;

		return paramNames.length;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public DataType[] getParamTypes() {
		return paramTypes;
	}
	
}
