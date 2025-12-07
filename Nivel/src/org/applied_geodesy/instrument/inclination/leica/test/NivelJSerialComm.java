package org.applied_geodesy.instrument.inclination.leica.test;

import org.applied_geodesy.instrument.inclination.InclinationParameters;
import org.applied_geodesy.instrument.inclination.leica.Nivel;
import org.applied_geodesy.io.rxtx.serialcomm.JSerialCommunicator;

import com.fazecast.jSerialComm.SerialPort;

public class NivelJSerialComm {

	public static void main(String[] args) {
		System.out.println("Starte Messung via NivelJSerialComm");
		
		JSerialCommunicator comm = new JSerialCommunicator();
		comm.setSerialPort(SerialPort.getCommPort("COM11"));
		comm.setBaudRate(9600);
		comm.setDataBits(8);
		comm.setStopBits(SerialPort.ONE_STOP_BIT);
		comm.setParity(SerialPort.NO_PARITY);
		
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

