package mq.radar.cinrad.decoders.cinrad;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class CinradUtils {
	public static final DecimalFormat fmt1 = new DecimalFormat("0.0");
	public static final DecimalFormat fmt2 = new DecimalFormat("0.00");
	public static final DecimalFormat fmt02 = new DecimalFormat("00");
	public static final DateFormat datetime = new SimpleDateFormat(
			"yyyy MM dd HH:mm:ss");
	public static final DateFormat yyyyMMddHHmmss = new SimpleDateFormat(
	"yyyyMMddHHmmss");

	public static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	public static float getHexDecodeValue(short val) {
		float deco;

		int s = (val >> 15) & 1;
		int e = (val >> 10) & (31);
		int f = (val) & (1023);

		if (e == 0) {
			deco = (float) Math.pow(-1, s) * 2 * (0.f + (float) (f / 1024.f));
		} else {
			deco = (float) (Math.pow(-1, s) * Math.pow(2, e - 16) * (1 + (f / 1024.f)));
		}

		return deco;
	}
	
	public static double stripCharsDouble(String str) {
	       str = str.replaceAll("<", "").replaceAll(">", "").replaceAll("=", "");
	       try {
	           return Double.parseDouble(str);
	       } catch (Exception e) {
	           return -999;
	       }
	   }
}
