package mq.radar.cinrad;

import java.io.File;

public class Cinrad110Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String file110 = "data/110/20150509.000600.10.110.200";
		File file = new File(file110);
		System.out.println(Short.valueOf(file.getName().substring(16, 18)));

	}

}
