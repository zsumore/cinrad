package mq.radar.cinrad.decoders.cinradx;

import static mq.radar.cinrad.decoders.cinradx.DataType.*;

import java.util.Arrays;

/**
 * see table 3-5
 */
public enum ProductType {
	PPI(new String[] { "Elevation" },new DataType[]{FLOAT},1), 
	RHI(new String[] {"Azimuth","Top","Bottom" },new DataType[]{FLOAT,INT,INT},2), 
	CAPPI(new String[] {"Layers","Top","Bottom","CAPPIFill" },new DataType[]{INT,INT,INT,INT},3), 
	MAX(new String[] {"Top","Bottom" },new DataType[]{INT,INT},4), 
	ET(new String[] {"dBZContour" },new DataType[]{FLOAT},6), 
	BEAM(new String[] {"NA" },new DataType[]{INT},7), 
	VCS(new String[] {"AzimuthofStart","RangeofStart","AzimuthofEnd","RangeofEnd","Top","Bottom" },new DataType[]{FLOAT,INT,FLOAT,INT,INT,INT},8), 
	LRA(new String[] {"Top","Bottom" },new DataType[]{INT,INT},9), 
	LRM(new String[] {"Top","Bottom" },new DataType[]{INT,INT},10), 
	SRR(new String[] {"Elevation","RangeofCenter","AzimuthofCenter","SideLength","SpeedofWind","DirectionofWind" },new DataType[]{FLOAT,INT,FLOAT,INT,FLOAT,FLOAT},13), 
	SRM(new String[] {"Elevation","SpeedofWind","DirectionofWind" },new DataType[]{FLOAT,FLOAT,FLOAT},14), 
	SWA(new String[] {"Elevation","RangeofCenter","AzimuthofCenter","SideLength" },new DataType[]{FLOAT,INT,FLOAT,INT},15), 
	//CM(new String[] {"NA" },new DataType[]{INT},13),
	WER(new String[] {"Range","Azimuth","SideLength","Levels" },new DataType[]{INT,FLOAT,INT,INT},20),
	VIL(new String[] {"NA" },new DataType[]{INT},23), 
	HSR(new String[] {"NA" },new DataType[]{INT},24), 
	OHP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment"},new DataType[]{INT,INT,INT,INT},25), 
	THP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment","Hours"},new DataType[]{INT,INT,INT,INT,INT},26), 
	STP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment"},new DataType[]{INT,INT,INT,INT},27), 
	USP(new String[] {"BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment","Hours"},new DataType[]{INT,INT,INT,INT,INT},28), 
	CATCH(new String[] {"NA" },new DataType[]{INT},29), 
	SPD(new String[] {"MaxRange","BaseProduct","CAPPIHeight","CAPPIFill","RainGageAdjustment" },new DataType[]{INT,INT,INT,INT,INT},30), 
	VAD(new String[] {"Layers","Height" },new DataType[]{INT,SHORT},31), 
	VWP(new String[] {"Layers","Height" },new DataType[]{INT,SHORT},32),
	WIND(new String[] {"NA" },new DataType[]{INT},33), 
	SHEAR(new String[] {"Elevation","RadialShear","AzimuthShear","ElevationShear" },new DataType[]{FLOAT,INT,INT,INT},34),
	SWP(new String[] {"MaxRange" },new DataType[]{INT},36), 
	STI(new String[] {"MaxRange" },new DataType[]{INT},37),
	HI(new String[] {"MaxRange" },new DataType[]{INT},38),
	M(new String[] {"MaxRange" },new DataType[]{INT},39), 
	TVS(new String[] {"MaxRange" },new DataType[]{INT},40), 
	SS(new String[] {"MaxRange" },new DataType[]{INT},41), 
	UAM(new String[] {"MaxRange","BaseProduct1","DataType1","BaseProduct2","DataType2","BaseProduct3","DataType3","Area","ForecastTime","LogicalOperation" },new DataType[]{INT,INT,INT,INT,INT,INT,INT,FLOAT,INT,INT},44), 
	FTM(new String[] {"NA" },new DataType[]{INT},45), 
	HMAX(new String[] {"dBZMinimum" },new DataType[]{FLOAT},46), 
	EB(new String[] {"dBZMinimum" },new DataType[]{FLOAT},47), 
	GAGE(new String[] {"MaxRange" },new DataType[]{INT},48), 
	CONTOUR(new String[] {"MaxRange","BaseProduct","Levels","Start","Interval" },new DataType[]{INT,INT,INT,FLOAT,FLOAT},49),
	ML(new String[] {"NA" },new DataType[]{INT},50), 
	HCL(new String[] {"NA" },new DataType[]{INT},51),
	USER(new String[] {"Algorithm","DescriptionofParameter","DescriptionofParameter","DescriptionofParameter" },new DataType[]{INT,CHAR16,CHAR16,CHAR16},91);

	private String[] paramNames;
	private DataType[] paramTypes;
	private int productNumber;
	

	private ProductType(String[] paramNames,DataType[] paramTypes,int number) {
		this.paramNames = paramNames;
		this.paramTypes=paramTypes;
		this.productNumber=number;
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
	
	


	public int getProductNumber() {
		return productNumber;
	}

	@Override
	public String toString() {
		
		return "ProductType [name="+this.name() + ",productNumber="+productNumber+",paramNames=" + Arrays.toString(paramNames) + ",paramTypes=" + Arrays.toString(paramTypes) + "]";
	}
	
	
	
}
