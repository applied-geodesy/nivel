package org.applied_geodesy.instrument.inclination.leica;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.applied_geodesy.instrument.inclination.InclinationParameterType;
import org.applied_geodesy.instrument.inclination.InclinationParameters;
import org.applied_geodesy.io.rxtx.ReceiveDataType;
import org.applied_geodesy.io.rxtx.ReceiverExchangeable;
import org.applied_geodesy.io.rxtx.RxTx;
import org.applied_geodesy.io.rxtx.RxTxReturnable;

public class Nivel implements RxTxReturnable, ReceiverExchangeable {
	private final static String REQUEST = (char)22 +""+ (char)2 +""+ "N0C0 G A" +""+ (char)3 +""+ (char)13 +""+ (char)10,
			RESPONSE_SUFIX = String.valueOf((char)3),
			RESPONSE_PREFIX  = (char)22 + "" + (char)2;
	
	// Auswerten der Antwort, wobei <22><2> am Anfang und <3><02hex><D1hex> am Ende bereits entfernt sein muessen
	// C5N7 X:-12.43 Y:587.54 T: 23.3
	private Pattern pattern = Pattern.compile("C\\d{1}N\\d{1}\\s+X:\\s*([\\d\\.+-]+)\\s+Y:\\s*([\\d\\.+-]+)\\s+T:\\s*([\\d\\.+-]+)");
	
	private long timeOut = 2000L;
	private RxTx connRxTx;
	private StringBuffer responseMessage = new StringBuffer();
	private String message = new String();
	
	public Nivel(RxTx connRxTx) {
		this.connRxTx = connRxTx;
		this.connRxTx.setReceiveDataType(ReceiveDataType.BYTE_ARRAY);
	}
	
	/**
	 * Frage aktuelle Messwerte ab
	 * 
	 * @throws IOException
	 */
	private void detectMeasuredValues() throws IOException {
		this.connRxTx.transmit(REQUEST.getBytes());
	}
	
	private InclinationParameters getReadings(String message) throws NumberFormatException {
		// Auswerten der Antwort, wobei <22><2> am Anfang und <3><02hex><D1hex> am Ende bereits entfernt wurde.
		//C5N7 X:-12.43 Y:587.54 T: 23.3
		Matcher matcher = this.pattern.matcher(message);
		double incX = 0, incY = 0, temp = 0;
		if (matcher != null && matcher.find() && matcher.groupCount() == 3) {
			incX = Double.parseDouble(matcher.group(1));
			incY = Double.parseDouble(matcher.group(2));
			temp = Double.parseDouble(matcher.group(3));
			
			InclinationParameters params = new InclinationParameters();
	    	params.add(InclinationParameterType.INCLINATION_X, incX);
	    	params.add(InclinationParameterType.INCLINATION_Y, incY);
	    	params.add(InclinationParameterType.TEMPERATURE,   temp);
	    	
	    	return params;
		}
		return null;
	}
	
	public InclinationParameters getInclinationParameters() throws IOException, InterruptedException, NumberFormatException {
		this.responseMessage.setLength(0);
		this.message = new String();
		// Ermittle Messwerte
		this.detectMeasuredValues();
		synchronized( this.connRxTx ) {
			if (this.timeOut > 0)
				this.connRxTx.wait(this.timeOut);
			else
				this.connRxTx.wait();
		}
		// Zerlege die Antwort in Messwerte
		return this.getReadings(this.message);
	}

	@Override
	public void receive(byte[] bytesRX) throws IOException {
		this.responseMessage.append(new String(bytesRX));
		
		int startPos = this.responseMessage.lastIndexOf( RESPONSE_PREFIX );
        int endPos   = this.responseMessage.lastIndexOf( RESPONSE_SUFIX );

        if (endPos >= 0 && startPos >= 0 && startPos < endPos) {
        	this.message = this.responseMessage.substring(startPos, endPos);
        	this.responseMessage.setLength(0);
        	synchronized(this.connRxTx){
        		this.connRxTx.notify();
        	}
        }
	}

	@Override
	public void receive(int intRX) throws IOException {
		throw new IOException("Error, unsupported method call. Use receive(byte[] bytesRX) for data transfer.");
	}

	@Override
	public RxTx getRxTx() {
		return this.connRxTx;
	}
}
