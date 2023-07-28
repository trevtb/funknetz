package de.funknetz.server;

// --- Importe
import java.io.*;
import gnu.io.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: ComConnect --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Server --
	-- --
	---------------------------------------------------------------------------------
	-- 	--
	-- SYSTEM ENVIRONMENT						--
	-- OS			Ubuntu 9.10 (Linux 2.6.31) 	--
	-- SOFTWARE 	JDK 1.6.15 					--
	-- 	--
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Stellt Ein- und Ausgabewerkzeuge fuer die Kommunikation mit der Funknetz-Box bereit.
*	Eine Instanz der Klasse ComConnect wird genutzt, um die vom Client oder vom Server
*	selbst generierten Steuerungsbefehle an den Mikrokontroller bzw. die Funknetz-Box
*	zu uebermitteln.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class ComConnect {
	
	// --- Attribute
	private CommPort commPort_ref;						// Symbolisiert rohen COM-Port
	private CommPortIdentifier portIdentifier_ref;		// Der Objekttyp fuer den betriebssystem-sezifischen Serialportbezeichner
	private SerialPort serialPort_ref;					// Symbolisiert den eigentlichen Serialport (COM-Port)
	public static String portIdent_class;				// Der betriebssystem-spezifische Serialportbezeichner in String-Form
	private static boolean isBlocked_class;				// Lock-Variable fuer 
	
	// --- Konstruktoren
	
	/** 	
	*	Standardkonstruktor: erstellt eine neue Instanz der Klasse ComConnect.
	*	Zuvor muss jedoch die statische Variable portIdent_class
	*	gesetzt werden. Geschieht dies nicht, wird Versucht den betriebssystem-spezifischen
	*	default-Port, also den ersten Comport, zu bestimmen. Geling dies nicht, wird 
	*	/dev/ttyS0 verwendet.
	*	@throws Exception Falls der Serialport nicht geoeffnet werden kann.
	**/
	public ComConnect() throws Exception {
		String osname_ref = null;
		if (ComConnect.portIdent_class == null) {
			osname_ref = System.getProperty("os.name","").toLowerCase();
			if ( osname_ref.startsWith("windows") ) {
				ComConnect.portIdent_class = "COM1";
			} else if (osname_ref.startsWith("linux")) {
				ComConnect.portIdent_class = "/dev/ttyS0";
			} else if ( osname_ref.startsWith("mac") ) {
				ComConnect.portIdent_class = "/dev/tty.usbserial0";
			} else {
				System.out.println("Fehler: Konnte das Betriebssystem nicht genau bestimmen,\nder Serialport wird daher auf /dev/ttyS0 gesetzt");
				ComConnect.portIdent_class = "/dev/ttyS0";
			} //endif
		} //endif
	
		portIdentifier_ref = CommPortIdentifier.getPortIdentifier(ComConnect.portIdent_class);
		
		if (portIdentifier_ref.isCurrentlyOwned()) {
			System.out.println("Fehler: Port wird bereits benutzt");
		} //endif
	} //endkonstruktor ComConnect
	
	// --- Innere Klassen
	
	/**
	*	Baut die komplette Verbindung fuer die Kommunikation mit der 
	*	Funknetz-Box auf, uebertraegt die dem Konstruktor uebergebene
	*	Befehlsliste und gibt anschliessend den Serialport wieder fuer
	*	andere Prozesse frei.
	**/
	private class SerialWriter implements Runnable {
		int[] trans_ref = null;
		OutputStream outP_ref = null;
		
		/**
		 * Konstruiert eine neue SerialWriter-Instanz.
		 * @param trans_ref die an den uC zu Uebertragenden Steuerbefehle als int[]-Array
		 */
		public SerialWriter (int[] trans_ref) {
			this.trans_ref = trans_ref.clone();
		} //endkonstruktor SerialWriter
    
		public void run () {
			if (ComConnect.isBlocked_class) {
				while (ComConnect.isBlocked_class) {
					try {
						Thread.sleep(1000L);
					} catch (Exception ex_ref) {
						ex_ref.printStackTrace();
					} //endtry
				} //endwhile
				ComConnect.isBlocked_class = true;
			} else {
				ComConnect.isBlocked_class = true;
			} //endif
		
			try {
				Thread.sleep(1000L);
				commPort_ref = portIdentifier_ref.open(this.getClass().getName(),2000);
			} catch (Exception ex_ref) {
				ex_ref.printStackTrace();
			} //endtry
		
			if (commPort_ref instanceof SerialPort) {
				try {
					serialPort_ref = (SerialPort) commPort_ref;
					serialPort_ref.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
					outP_ref = serialPort_ref.getOutputStream();
				} catch (Exception ex_ref) {
					ex_ref.printStackTrace();
				} //endtry
			} //endif
			
			byte[] werte_ref = new byte[trans_ref.length];
			for (int i = 0; i < werte_ref.length; i++) {
				byte temp = (byte) trans_ref[i];
				werte_ref[i] = temp;
			} //endfor
				
			try {
				for (int u = 0; u < 2; u++) {
					for (int i = 0; i < werte_ref.length; i++) {
						outP_ref.write(werte_ref[i]);
					} //endfor
				} //endfor
			} catch (Exception ex_ref) {
				ex_ref.printStackTrace();
			} //endtry
			
			try {
				outP_ref.close();
				commPort_ref.close();
			} catch (Exception e_ref) {
				e_ref.printStackTrace();
			} //endtry
			ComConnect.isBlocked_class = false;
		} //endmethod run
	} //endclass SerialWriter
	

	/**
	*	Uebertraegt den uebergebenen Code an den Mikroprozessor.
	*	Hierfuer wird eine neue Instanz der Klasse SerialWriter
	*	als Prozess gestartet.
	*	@param trans_ref Der an den Mikroprozessor zu sendende Code.
	*	@see SerialWriter
	**/
	public void transmit (int[] trans_ref) {
		try {
			Thread t_ref = new Thread(new SerialWriter(trans_ref));
			t_ref.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
	} //endmethod transmit
} //endclass ComConnect

