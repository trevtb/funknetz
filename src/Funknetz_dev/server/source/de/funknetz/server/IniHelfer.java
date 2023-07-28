package de.funknetz.server;

// --- Importe
import java.io.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: IniHelfer --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Server--
	-- --
	---------------------------------------------------------------------------------
	-- 							--
	-- SYSTEM ENVIRONMENT 		--
	-- OS		Linux 2.6.28-14	--	
	-- SOFTWARE 	JDK 1.6.14   		--
	-- 							--
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Helferobjekt fuer die Datei Ein- und Ausgabe der ini-Datei.
*	Eine Instanz dieser Klasse wird fuer die Verwaltung der
*	ini-Datei verwendet.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
public class IniHelfer {
	
	// --- Methoden
	
	/**
	*	Liefert die ini-Datei in String-Form zurueck.
	*	In der ini-Datei wird nur der Serialportbezeichner
	*	gespeichert. Daher ist nur ein String noetig.
	*	@return der Inhalt der ini-Datei in String-Form.
	**/
	public String readIni() {
		String auswahl = "";
		try {
			FileInputStream fis = new FileInputStream("settings.ini");
			ObjectInputStream ois = new ObjectInputStream(fis);
			auswahl = (String) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			makeNewIni();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endcatch
		return auswahl;
	} //endmethod readIni
	
	/**
	*	Speichert den neuen Serialportbezeichner in der ini-Datei.
	*	Der alte Serialportbezeichner wird immer ueberschrieben.
	*	@param auswahl der neue Serialportbezeichner
	**/
	public void writeIni(String auswahl) {
		try {
			FileOutputStream fos = new FileOutputStream("settings.ini");
			ObjectOutputStream ois = new ObjectOutputStream(fos);
			ois.writeObject(auswahl);
			ois.close();
		} catch (Exception ex) {ex.printStackTrace();}
	} //endmethod writeIni
	
	/**
	*	Erstellt eine neue iniDatei mit Standardwerten.
	*	Als Standardwert wird /dev/ttyS0 als Serialportbezeichner verwendet.
	*	Zum Schreiben wird die Methode writeIni verwendet.
	*	@see #writeIni(String auswahl)
	**/
	public void makeNewIni() {
		String auswahl = "/dev/ttyS0";
		writeIni(auswahl);
	} //endmethod makeNewIni
} //endclass IniHelfer