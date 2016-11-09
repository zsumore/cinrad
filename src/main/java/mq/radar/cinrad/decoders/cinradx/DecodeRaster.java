package mq.radar.cinrad.decoders.cinradx;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.referencing.operation.TransformException;

import mq.radar.cinrad.decoders.DecodeException;

public class DecodeRaster extends BaseDecoder {

	public DecodeRaster(IDecodeCinradXHeader decodeHeader) throws ConfigurationException {
		super(decodeHeader);

	}

	@Override
	public void decodeData(boolean autoClosed) throws DecodeException, IOException, TransformException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
