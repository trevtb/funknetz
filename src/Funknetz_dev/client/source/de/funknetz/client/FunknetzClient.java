package de.funknetz.client;

// --- Importe
import java.io.*;
import java.net.*;

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
	-- SYSTEM ENVIRONMENT --
	-- OS		Linux 2.6.28-14
	-- SOFTWARE 	JDK 1.6.14 --
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Stellt die Hauptfunktionen des Clients bereit.
*	Eine Instanz der Klasse FunknetzClient dient als Hauptschnittstelle 
*	fuer die Klasse FunknetzGui und uebertraegt unter anderem die Daten
*	an den Funknetz-Server.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
public class FunknetzClient {
	
	// --- Attribute
	private static IniHelfer helfer_class;			// Referenz auf das Datei Ein-/Ausgabe Helfer-Objekt
	private static String status_class = "";		// Auskunft ueber den Erfolg der Uebertragung an den Server
	
	// --- Methoden
	
	/** 	
	*	Erstellt eine neue Instanz der Klasse 
	*	FunknetzGui bei Programmaufruf.
	*	@see FunknetzGui
	**/
	public static void main (String[] args) {
		FunknetzGui gui_ref = new FunknetzGui();
		gui_ref.draw();
	} //endmethod main
	
	/** 	
	*	Erstellt eine neue Instanz der Klasse
	*	IniHelfer und liefert diese zurueck.
	*	@return IniHelfer Objekt
	*	@see IniHelfer
	**/
	public static IniHelfer getIniHelfer() {
		helfer_class = new IniHelfer();
		return helfer_class;
	} //endmethod getIniHelfer
	
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
	public static void setIni (String[] chans_ref, String[][] schalts_ref, String ipi_ref, int porti, int selection) {
		Integer sel_ref = new Integer(selection);
		helfer_class.writeIni(chans_ref, schalts_ref, ipi_ref, porti, sel_ref);
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
			ous_ref.writeObject("direct");
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
		} catch (Exception ex_ref) {
			status_class = "Der Server ist nicht erreichbar";
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