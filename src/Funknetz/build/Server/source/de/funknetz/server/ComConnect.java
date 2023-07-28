package de.funknetz.server;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;

public class ComConnect {
	private static CommPort commPort;
	private static int[] transmission;
	private static OutputStream outP;
	private static CommPortIdentifier portIdentifier;
	private static SerialPort serialPort;
	public static String portIdent;
	
	public ComConnect() throws Exception {
		super();
		portIdentifier = CommPortIdentifier.getPortIdentifier(ComConnect.portIdent);
		
		if (portIdentifier.isCurrentlyOwned() ) {
			System.out.println("Fehler: Port wird bereits benutzt");
		} else {
			commPort = portIdentifier.open(this.getClass().getName(),2000);
			
			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
				
				outP = serialPort.getOutputStream();
			} //endif
		} //endif     
	} //endkonstruktor ComConnect

	public static class SerialWriter implements Runnable {
		OutputStream out;
        
		public SerialWriter (OutputStream out) {
			this.out = out;
		} //endkonstruktor SerialWriter
        
		public void run () {
			byte[] werte = new byte[transmission.length];
			
			for (int i = 0; i < werte.length; i++) {
				byte temp = (byte) transmission[i];
				werte[i] = temp;
			} //endfor
			
			for (int u = 0; u < 2; u++) {
				for (int i = 0; i < werte.length; i++) {
					try {
						this.out.write(werte[i]);
					} catch (Exception ex) {
						ex.printStackTrace();
					} //endtry
				} //endfor
			} //endfor
			
			try {
				this.out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} //endtry
		} //endmethod run
	} //endclass SerialWriter
    
	public void transmit (int[] trans) {
		transmission = trans;
		try {
			Thread t = new Thread(new SerialWriter(outP));
			t.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
	} //endmethod transmit
} //endclass ComConnect
