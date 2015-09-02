package mq.radar.cinrad.decoders.cinrad;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import mq.radar.cinrad.decoders.DecodeException;
import mq.radar.cinrad.decoders.cinrad.DecodeVAD.VADTextPacket;
import mq.radar.cinrad.decoders.cinrad.DecodeVAD.VADWindBarbPacket;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.referencing.FactoryException;

public class TestCinrad48 {

	public static void main(String[] args) throws DecodeException, IOException,
			FactoryException {
		String file48 = "data/20110916/VWP/48/20110916.000001.00.48.200";
		File file = new File(file48);
		CinradHeader header = new DecodeCinradHeader();
		header.decodeHeader(file.toURI().toURL());

		// CindarDecoder decode = new DecodeVADText(header);

		DecodeVAD decoder = new DecodeVAD();
		decoder.decodeVAD(header);
		
		Vector<VADWindBarbPacket> vWindBarbs=decoder.getWindBarbs();
		Vector<VADTextPacket> vText=decoder.getText();
		
		for(VADWindBarbPacket p:vWindBarbs){
			System.out.println(p);
		}
		for(VADTextPacket t:vText){
			System.out.println(t);
		}
		
		
		
		System.out.println(decoder.getMaxI());
		System.out.println(decoder.getMaxJ());
		header.close();
		
		
		short maxWindIPos=-1;
		for(VADWindBarbPacket v:vWindBarbs){
			if(v.ipos>maxWindIPos){
				maxWindIPos=v.ipos;
			}
		}
		System.out.println(maxWindIPos);

	}

}
