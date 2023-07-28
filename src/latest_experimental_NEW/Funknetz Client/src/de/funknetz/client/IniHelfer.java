package de.funknetz.client;

// --- Importe
import java.io.*;
import java.util.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: IniHelfer --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Client--
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- SYSTEM ENVIRONMENT 					--
	-- OS		Ubuntu 9.10 (Linux 2.6.31)	--
	-- SOFTWARE 	JDK 1.6.14 				--
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	Dient als Helfer-Objekt fuer die Datei Ein- und Ausgabe.
* 	Eine Instanz der Klasse IniHelfer dient zur Verwaltung
*	der ini-Datei und stellt Input-/Output-Werkezeuge bzw.
*	Methoden hierfuer bereit.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class IniHelfer {
	
	// --- Methoden
	/** 	
	*	Bekommt die gaenderten ini-Parameter uebergeben
	*	und erstellt an Hand derer eine neue ini-Datei fuer das Kaffee-Modul.
	*	@param kanaele_ref die Kanal- und Schalternummer der Kaffeemaschine als int[].
	**/
	public void writeCoffeeSettings(int[] coffeeSettings_ref) {
		try {
			FileOutputStream fos_ref = new FileOutputStream("coffee.ini");
			ObjectOutputStream ois_ref = new ObjectOutputStream(fos_ref);
			ois_ref.writeObject(coffeeSettings_ref);
			ois_ref.close();
		} catch (Exception ex_ref) {ex_ref.printStackTrace();}
	} //endmethod writeIni
	
	/**	
	*	Erstellt eine neue ini-Datei fuer das Kaffeemodul mit Standardwerten.
	**/
	public void makeNewCoffeeSettings() {
		int[] coffeeSettings_ref = new int[2];
		coffeeSettings_ref[0] = 0;
		coffeeSettings_ref[1] = 0;
		writeCoffeeSettings(coffeeSettings_ref);
	} //endmethod makeNewIni
	
	/**
	 * Liest die Einstellungen fuer das Kaffee-Modul von der Festplatte 
	 * und liefert sie zurueck.
	 * @return Kanal- und Schalter-Nummer der Kaffeemaschine.
	 */
	public int[] readCoffeeSettings() {
		int[] coffeeSettings_ref = null;
		try {
			FileInputStream input_ref = new FileInputStream("coffee.ini");
			ObjectInputStream ois_ref = new ObjectInputStream(input_ref);
			coffeeSettings_ref = (int[]) ois_ref.readObject();
			ois_ref.close();
		} catch (FileNotFoundException e_ref) {
			makeNewCoffeeSettings();
		} catch (Exception ex_ref) {ex_ref.printStackTrace();}
		return coffeeSettings_ref;
	} //endmethod readIni
	
	/** 	
	*	Liefert den Inhalt der ini-Datei in aufbereiteter
	*	Form zurueck und erstellt eine neue, falls bisher
	*	keine ini-Datei existiert.
	*	@return Inhalt der ini-Datei als Feld.
	**/
	public ArrayList<Object> readIni() {
		ArrayList<Object> array_ref = null;
		String[] kanaele_ref = null;
		String ip_ref = null;
		int sele = 0;
		try {
			FileInputStream input_ref = new FileInputStream("settings.ini");
			ObjectInputStream ois_ref = new ObjectInputStream(input_ref);
			kanaele_ref = (String[]) ois_ref.readObject();
			ip_ref = (String) ois_ref.readObject();
			Integer portT_ref = (Integer) ois_ref.readObject();
			sele = (Integer) ois_ref.readObject();
			ois_ref.close();
			array_ref = new ArrayList<Object>();
			array_ref.add(kanaele_ref);
			array_ref.add(ip_ref);
			array_ref.add(portT_ref);
			array_ref.add(sele);
		} catch (FileNotFoundException e_ref) {
			makeNewIni();
		} catch (Exception ex_ref) {ex_ref.printStackTrace();}
		return array_ref;
	} //endmethod readIni
	
	/** 	
	*	Bekommt die gaenderten ini-Parameter uebergeben
	*	und erstellt an Hand derer eine neue ini-Datei.
	*	@param kanaele_ref die Bezeichner fuer die einzelnen Kanaele
	*	@param schalter_ref die Bezeichner fuer die einzelnen Schalter bzw. Geraete
	*	@param ip_ref die zuletzt erfolgreich benutze IP-Adresse
	*	@param port der zuletzt erfolgreich benutzte Port
	*	@param sel_ref der zuletzt ausgewaehlte Kanal
	**/
	public void writeIni(String[] kanaele_ref, String ip_ref, int port, Integer sel_ref) {
		try {
			FileOutputStream fos_ref = new FileOutputStream("settings.ini");
			ObjectOutputStream ois_ref = new ObjectOutputStream(fos_ref);
			ois_ref.writeObject(kanaele_ref);
			ois_ref.writeObject(ip_ref);
			Integer porti_ref = new Integer(port);
			ois_ref.writeObject(porti_ref);
			ois_ref.writeObject(sel_ref);
			ois_ref.close();
		} catch (Exception ex_ref) {ex_ref.printStackTrace();}
	} //endmethod writeIni
	
	/**	
	*	Erstellt eine neue ini-Datei mit Standardwerten.
	**/
	public void makeNewIni() {
		String[] kanaele_ref = new String[4];
		String ip_ref = "127.0.0.1";
		int port = 5000;
		
		// Namen der Kanaele auf Standartwerte setzen
		for (int i = 0; i < 4; i++) {
			kanaele_ref[i] = "Kanal " + "0" + (i+1);
		} //endfor
		
		Integer selected_ref = new Integer(0);
		writeIni(kanaele_ref, ip_ref, port, selected_ref);
	} //endmethod makeNewIni
} //endclass IniHelfer