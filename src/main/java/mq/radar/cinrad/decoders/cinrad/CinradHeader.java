package mq.radar.cinrad.decoders.cinrad;

import java.net.URL;
import java.util.Calendar;

import mq.radar.cinrad.decoders.DecodeException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public interface CinradHeader {
	/**
	 * 产品代号
	 * 
	 * @return Short
	 */
	public short getProductCode();

	/**
	 * 文件包含的字节数
	 * 
	 * @return int
	 */
	public int getFileSize();

	/**
	 * 雷达站代号
	 * 
	 * @return short
	 */
	public short getRadarStationCode();
	
	/**
	 * 雷达站代号
	 * 
	 * @return short
	 */
	public int getRadarStationID();

	/**
	 * 接收站代号
	 * 
	 * @return short
	 */
	public short getReceiveStationCode();

	/**
	 * 文件包含的字节数
	 * 
	 * @return short
	 */
	public short getDataBlockSize();

	/**
	 * 雷达站纬度
	 * 
	 * @return double
	 */
	public double getLat();

	/**
	 * 雷达站经度
	 * 
	 * @return double
	 */
	public double getLon();

	/**
	 * 雷达站海拔高度，产品默认单位为英尺，但该方法经过转换返回单位为米（1英尺=0.3048米）
	 * 
	 * @return double
	 */
	public double getAlt();

	/**
	 * 操作模式：0表示维护模式，1表示晴空模式，2表示降雨模式
	 * 
	 * @return short
	 */
	public short getOperatingMode();

	/**
	 * 体扫号
	 * 
	 * @return short
	 */
	public short getVcp();

	/**
	 * 体扫开始日期时间
	 * 
	 * @return java.util.Calendar
	 */
	public Calendar getScanCalendar();

	/**
	 * 产品依赖项，共10双字节
	 * 
	 * @return short[]
	 */
	public short[] getProductSpecific();

	/**
	 * 仰角代号
	 * 
	 * @return short
	 */
	public short getElevNumber();
	
	public void setElevNumber(short elev);

	/**
	 * 共16个双字节，每个双字节代表一级数据门槛(Data Level Threshold)
	 * 
	 * @return byte[]
	 */
	public byte[] getDataThresholdBytes();

	public String[] getDataThresholdString();

	public String getDataThresholdString(int index);

	/**
	 * 地图号或产品版本号
	 * 
	 * @return int
	 */
	public int getVersion();

	/**
	 * 信息头块到产品符号表示块间相隔的双字节数，等于0时表示没有相应的数据块
	 * 
	 * @return int
	 */
	public int getSymbologyBlockOffset();

	/**
	 * 信息头块到图像数字文本块间相隔的双字节数，等于0时表示没有相应的数据块
	 * 
	 * @return int
	 */
	public int getGraphicBlockOffset();

	/**
	 * 信息头块到文本列表块间相隔的双字节数，等于0时表示没有相应的数据块
	 * 
	 * @return int
	 */
	public int getTabularBlockOffset();

	/**
	 * Description of the Method
	 * 
	 * @param url
	 *            Description of the Parameter
	 */
	public void decodeHeader(URL url) throws DecodeException;

	/**
	 * Gets the cinradURL attribute of the CinradHeader object
	 * 
	 * @return The cinradURL value
	 */
	public URL getCinradURL();

	/**
	 * Gets the randomAccessFile attribute of the CinradHeader object
	 * 
	 * @return The randomAccessFile value
	 */
	public ucar.unidata.io.RandomAccessFile getRandomAccessFile();

	/**
	 * Gets the max bounds for this Cinrad file
	 * 
	 * @return The bounds value
	 */
	public Envelope getCinradBounds() ;

	/**
	 * Gets lon, lat location of Radar site as Coordinate
	 * 
	 * @return The radar location as a Coordinate object
	 */
	public Coordinate getRadarCoordinate();

	public boolean isValidFile();

	public short getScanNumber();

	public short getSerialNumber();

	public void close();

	public CindarProducts getProduct();
}
