package org.applied_geodesy.instrument.inclination.leica.test;

import org.applied_geodesy.instrument.inclination.InclinationParameters;
import org.applied_geodesy.instrument.inclination.leica.Nivel;
import org.applied_geodesy.io.rxtx.rxtxcomm.RxTxCommunicator;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class NivelRxTxComm {
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starte Messung via NivelRxTxComm");

		RxTxCommunicator comm = new RxTxCommunicator();
		comm.setCommPortIdentifier(CommPortIdentifier.getPortIdentifier("COM1"));
		comm.setBaudRate(9600);
		comm.setDataBits(8);
		comm.setStopBits(SerialPort.STOPBITS_1);
		comm.setParity(SerialPort.PARITY_NONE);

		Nivel inclinationSensor = new Nivel(comm);	
		comm.addReceiver(inclinationSensor);

		if (comm.open()) {
			try {
				int i=1;
				while (i++ < 20) {
					InclinationParameters parameters = inclinationSensor.getInclinationParameters();
					System.out.println(parameters);
					Thread.sleep(500);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				comm.close();
				comm.removeReceiver(inclinationSensor);
			}
		}
	}
}
