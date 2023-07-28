package de.funknetz.client;

// --- Importe
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: FunknetzClient --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Client--
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- SYSTEM ENVIRONMENT 					--
	-- OS		Ubuntu 9.10 (Linux 2.6.31)	--
	-- SOFTWARE 	JDK 1.6.15 				--
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Stellt die Hauptfunktionen des Clients bereit.
*	Eine Instanz der Klasse FunknetzClient dient als Hauptschnittstelle 
*	fuer die Klasse MainGui und uebertraegt unter anderem die Daten
*	an den Funknetz-Server.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class FunknetzClient {
	
	// --- Attribute
	private static IniHelfer helfer_class;			// Referenz auf das Datei Ein-/Ausgabe Helfer-Objekt
	private static String status_class = "";		// Auskunft ueber den Erfolg der Uebertragung an den Server
	private static String ip_class;
	private static int port;
	private static String[] kanalNamen_class;
	private static Device[][] deviceList_class;
	private static int selectedChan;
	private MainGui gui_ref;
	/** 	
	*	Erstellt eine neue Instanz der Klasse 
	*	FunknetzClient und ruft die start()-Methode auf.
	*	@see #start()
	**/
	public static void main (String[] args) {
		FunknetzClient client_ref = new FunknetzClient();
		client_ref.start();
	} //endmethod main
	
	/**
	 * Initialisiert die vom Client benoetigten Objekte
	 * und ruft die draw() Methode der MainGui-Klasse auf.
	 */
	private void start() {
		helfer_class = new IniHelfer();
		updateIni();
		gui_ref = new MainGui();
		WindowUtilities.setJavaLookAndFeel();
		gui_ref.draw();
	} //endmethod start
	
	/**
	 * Liefer die momentan im Client genutzte IP zurueck.
	 * @return Server IP-Adresse.
	 */
	public static String getIP() {
		updateIni();
		return ip_class;
	} //endmethod getIP
	
	/**
	 * Setzt die Server-IP auf den uebergebenen Wert.
	 * @param ip_ref die zu setzende Server-IP.
	 */
	public static void setIP (String ip_ref) {
		ip_class = ip_ref;
	} //endmethod setIP
	
	/**
	 * Liefert den momentan im Client genutzten Port zurueck.
	 * @return Server Port.
	 */
	public static int getPort() {
		updateIni();
		return port;
	} //endmethod getPort
	
	/**
	 * Setzt den Server-Port auf den uebergebenen Wert.
	 * @param port der zu setzende Server-Port.
	 */
	public static void setPort (int port) {
		FunknetzClient.port = port;
	} //endmethod setPort
	
	/**
	 * Liefert die momentan im Client gehaltene Liste mit Kanalnamen zurueck.
	 * @return Liste mit Kanalnamen.
	 */
	public static String[] getKNamen() {
		updateIni();
		return kanalNamen_class;
	} //endmethod getKNamen
	
	/**
	 * Setzt die Kanalnamen auf das uebergebene Array.
	 * @param kNamen_ref die zu setzenden Kanalnamen.
	 */
	public static void setKNamen(String[] kNamen_ref) {
		kanalNamen_class = kNamen_ref;
	} //endmethod setKNamen
	
	/**
	 * Liefert die momentan im Client gehaltene Liste mit Schalternamen zurueck.
	 * @return Liste mit Schalternamen.
	 */
	public static Device[][] getDevices() {
		try {
			Socket s_ref = new Socket();
			s_ref.bind(null);
			s_ref.connect(new InetSocketAddress(ip_class, port), 2000);
			ObjectOutputStream ous_ref = new ObjectOutputStream(s_ref.getOutputStream());
			ObjectInputStream ois_ref = new ObjectInputStream(s_ref.getInputStream());
			ous_ref.writeObject("devicelist");
			ous_ref.writeObject("get");
			deviceList_class = (Device[][]) ois_ref.readObject();
		} catch (ConnectException e_ref) {
			if ("Connection refused".equals(e_ref.getMessage())) {
				status_class = "Die Verbindung wurde abgelehnt.";
			} //endif
		} catch (Exception ex_ref) {
			status_class = "Der Server ist nicht erreichbar.";
		} //endtry
		if (status_class.equals("Die Verbindung wurde abgelehnt.") || status_class.equals("Der Server ist nicht erreichbar.")) {
			deviceList_class = null;
		} //endif
		return deviceList_class;
	} //endmethod getDevices
	
	/**
	 * Setzt die Schalternamen auf das uebergebene Array.
	 * @param sNamen_ref die zu setzenden Schalternamen.
	 */
	public static void setDevices(Device[][] tempDeviceList_ref) {
		deviceList_class = tempDeviceList_ref;
		try {
			Socket s_ref = new Socket();
			s_ref.bind(null);
			s_ref.connect(new InetSocketAddress(ip_class, port), 2000);
			ObjectOutputStream ous_ref = new ObjectOutputStream(s_ref.getOutputStream());
			ous_ref.writeObject("devicelist");
			ous_ref.writeObject("set");
			ous_ref.writeObject(deviceList_class);
			s_ref.close();
		} catch (ConnectException e_ref) {
			if ("Connection refused".equals(e_ref.getMessage())) {
				status_class = "Die Verbindung wurde abgelehnt.";
			} //endif
		} catch (Exception ex_ref) {
			status_class = "Der Server ist nicht erreichbar.";
		} //endtry
	} //endmethod setSNamen
	
	/**
	 * Liefert den im FernbedienungsGUI zuletzt genutzten Kanal zurueck.
	 * @return zuletzt benutzer Kanal im Fernsteuerungs-GUI.
	 */
	public static int getSelectedChan() {
		updateIni();
		return selectedChan;
	} //endmethod getSelectedChan
	
	public static void setSelectedChan(int selchan) {
		selectedChan = selchan;
	} //endmethod setSelectedChan
	
	public static int[] readCoffeeSettings() {
		return helfer_class.readCoffeeSettings();
	} //endmethod readCoffeeSettings
	
	public static void writeCoffeeSettings(int[] settings_ref) {
		helfer_class.writeCoffeeSettings(settings_ref);
	} //endmethod writeCoffeeSettings
	
	/**
	 * Liest die ini-Datei neu ein und aktualisiert die relevanten Attribute.
	 * Zum Einlesen wird die Methode readIni() der Klasse IniHelfer benutzt.
	 * @see IniHelfer.readIni()
	 */
	private static void updateIni() {
		ArrayList<Object> iniArrayList_ref = helfer_class.readIni();
		if (iniArrayList_ref == null) {
			iniArrayList_ref = helfer_class.readIni();
		} //endif
		kanalNamen_class = (String[]) iniArrayList_ref.get(0);
		selectedChan = ((Integer) iniArrayList_ref.get(3)).intValue();
		ip_class = (String) iniArrayList_ref.get(1);
		port = (Integer) iniArrayList_ref.get(2);
		getDevices();
	} //endmethod updateIni
	
	/** 	
	*	Speichert die zuletzt im GUI ausgewaehlten Werte
	*	und Bezeichnungen mit Hilfe einer Instanz des 
	*	IniHelfers in einer Datei ab.
	*	@param chans_ref Bezeichner fuer die einzelnen Kanaele
	*	@param schalts_ref Bezeichner fuer die einzelnen Schalter bzw. Geraete
	*	@param ipi_ref zuletzt erfolgreich verwendete IP-Adresse
	*	@param porti zuletzt erfolgreich verwendeter Port
	*	@param selection zuletzt ausgewaehlter Kanal
	*	@see IniHelfer
	**/
	public static void setIni () {
		Integer sel_ref = new Integer(selectedChan);
		helfer_class.writeIni(kanalNamen_class, ip_class, port, sel_ref);
	} //endmethod setIni
	
	/**	
	*	Erstellt mit Hilfe einer Instanz des IniHelfer 
	*	eine neue ini-Datei.
	*	@see IniHelfer
	**/
	public static void newIni() {
		helfer_class.makeNewIni();
	} //endmethod
	
	/** 	
	*	Uebermittel die uebergebenen Werte an den FunknetzServer.
	*	@return Status der Uebertragung
	*	@param type_ref Art der zu uebertragenden Daten
	*	@param ip_ref IP-Adresse des Servers
	*	@param port Port des Servers
	*	@param kanal zu schaltender Kanal
	*	@param schalter zu schaltender Schalter
	*	@param befehl auszufuehrender Befehl (On/Off)
	**/
	public static String transmit (String ip_ref, int port, int kanal, int schalter, int befehl) {
		String returnArg_ref = "";

		int aufrufWert = kanal * 6;
		aufrufWert += schalter *2;
		aufrufWert += befehl;
		
		int[] meinArray_ref = makeIt(aufrufWert);
	
		
		try {
			Socket s_ref = new Socket();
			s_ref.bind(null);
			s_ref.connect(new InetSocketAddress(ip_ref, port), 2000);
			ObjectOutputStream ous_ref = new ObjectOutputStream(s_ref.getOutputStream());
			ous_ref.writeObject("xxxxclient");
			ous_ref.writeObject(meinArray_ref);
			String check_ref = "OK "; 
			String wert_ref = "";
			for (int i = 0; i < meinArray_ref.length; i++) {
				wert_ref += Integer.toHexString(meinArray_ref[i]) + " ";
			} //endfor
			status_class = check_ref + wert_ref;
			ous_ref.writeObject("done");
			s_ref.close();
			returnArg_ref = status_class;
		} catch (ConnectException e_ref) {
			if ("Connection refused".equals(e_ref.getMessage())) {
				status_class = "Die Verbindung wurde abgelehnt.";
				returnArg_ref = status_class;
			} //endif
		} catch (Exception ex_ref) {
			status_class = "Der Server ist nicht erreichbar.";
			returnArg_ref = status_class;
		} //endtry
		
		return returnArg_ref;
	} //endmethod transmit
	
	/** 	
	*	Erstellt ein int-Feld, welches den an den Mikroprozessor 
	*	zu uebertragenden Code beinhaltet.
	*	@return Code fuer den Mikroprozessor
	*	@param wert Nummer des Schalters bzw. Befehls
	**/
	public static int[] makeIt(int wert) {
		int[] rr_ref = {0x23,0x91,0x11,0x02,0};
		if (wert < 6)  {
			rr_ref[1] = 0x81;
			rr_ref[2] = 0x01;
		} //endif
		if (wert >= 6) {
			rr_ref[1] = 0x82;
			rr_ref[2] = 0x02;
		}
		if (wert >= 12) {
			rr_ref[1] = 0x84;
			rr_ref[2] = 0x04;
		} //endif
		if (wert >= 18) {
			rr_ref[1] = 0x88;
			rr_ref[2] = 0x08;
		} //endif

		if ((wert & 0xFE) % 3 == 0)  {
			rr_ref[1] = rr_ref[1] | 0x10;
			rr_ref[2] = rr_ref[2] | 0x10;
		} //endif
		if ((wert & 0xFE) % 3 == 2)  {
			rr_ref[1] = rr_ref[1] | 0x20;
			rr_ref[2] = rr_ref[2] | 0x20;
		} //endif
		if ((wert & 0xFE) % 3 == 1)  {
			rr_ref[1] = rr_ref[1] | 0x40;
			rr_ref[2] = rr_ref[2] | 0x40;
		}//endif

		if ((wert & 1) == 0) {
			rr_ref[3] = 1;
		} //endif

		int g = 0x3F;
		for (int i = 0; i < 4; i++){
			g = g ^ rr_ref[i];
		} //endfor 
		rr_ref[4] = g;
		
		return rr_ref;
	} //endmethod makeIt
} //endclass FunknetzClient