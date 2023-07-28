package de.funknetz.server;

// --- Importe
import java.io.OutputStream;

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
	-- --
	-- SYSTEM ENVIRONMENT --
	-- OS		Linux 2.6.28-14 --
	-- SOFTWARE 	JDK 1.6.14 --
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Stellt Ein- und Ausgabewerkzeuge fuer die Kommunikation mit der Funknetz-Box bereit.
*	Eine Instanz der Klasse ComConnect wird genutzt, um die vom Client oder vom Server
*	selbst generierten Steuerungsbefehle an den Mikrokontroller bzw. die Funknetz-Box
*	zu uebermitteln.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
public class ComConnect {
	
	// --- Attribute
	private static CommPort commPort_class;				// Symbolisiert rohen COM-Port
	private static int[] transmission_class;					// der an den Mikroprozessor zu uebertragende Code als int-Feld
	private static OutputStream outP_class;					// Ausgabestrom fuer den Serialport
	private static CommPortIdentifier portIdentifier_class;		// Der Objekttyp fuer den betriebssystem-sezifischen Serialportbezeichner
	private static SerialPort serialPort_class;					// Symbolisiert den eigentlichen Serialport (COM-Port)
	public static String portIdent_class;					// Der betriebssystem-spezifische Serialportbezeichner in String-Form
	
	// --- Konstruktoren
	
	/** 	
	*	Standardkonstruktor: initialisiert alle noetigen Objekte und Variablen um
	*	ueber den Serialport Daten an die Funknetz-Box schicken zu koennen.
	*	zuvor muss jedoch die statische Variable portIdent_class
	*	gesetzt werden. Geschieht dies nicht, wird der Wert /dev/ttyS0 gesetzt.
	*	@throws Exception Falls der Serialport nicht geoeffnet werden kann.
	**/
	public ComConnect() throws Exception {
		super();
		if (portIdent_class == null) {
			portIdent_class = "/dev/ttyS0";
		} //endif
	
		portIdentifier_class = CommPortIdentifier.getPortIdentifier(ComConnect.portIdent_class);
		
		if (portIdentifier_class.isCurrentlyOwned() ) {
			System.out.println("Fehler: Port wird bereits benutzt");
		} else {
			commPort_class = portIdentifier_class.open(this.getClass().getName(),2000);
			
			if (commPort_class instanceof SerialPort) {
				serialPort_class = (SerialPort) commPort_class;
				serialPort_class.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
				
				outP_class = serialPort_class.getOutputStream();
			} //endif
		} //endif     
	} //endkonstruktor ComConnect
	
	// --- Innere Klassen
	
	/**
	*	Sendet die Daten an den Serialport und ist als eigener Prozess realisiert.
	**/
	public static class SerialWriter implements Runnable {
		OutputStream out_ref;
        
		public SerialWriter (OutputStream out_ref) {
			this.out_ref = out_ref;
		} //endkonstruktor SerialWriter
        
		public void run () {
			byte[] werte_ref = new byte[transmission_class.length];
			
			for (int i = 0; i < werte_ref.length; i++) {
				byte temp = (byte) transmission_class[i];
				werte_ref[i] = temp;
			} //endfor
			
			for (int u = 0; u < 2; u++) {
				for (int i = 0; i < werte_ref.length; i++) {
					try {
						this.out_ref.write(werte_ref[i]);
					} catch (Exception ex) {
						ex.printStackTrace();
					} //endtry
				} //endfor
			} //endfor
			
			try {
				this.out_ref.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} //endtry
		} //endmethod run
	} //endclass SerialWriter
    
	/**
	*	Uebertragt den uebergebenen Code an den Mikroprozessor.
	*	Hierfuer wird eine neue Instanz der Klasse SerialWriter
	*	als neuer Prozess gestartet.
	*	@param trans_ref Der an den Mikroprozessor zu sendende Code.
	**/
	public void transmit (int[] trans_ref) {
		transmission_class = trans_ref;
		try {
			Thread t_ref = new Thread(new SerialWriter(outP_class));
			t_ref.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
	} //endmethod transmit
} //endclass ComConnect
