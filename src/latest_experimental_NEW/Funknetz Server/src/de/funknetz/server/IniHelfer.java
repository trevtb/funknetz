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
	-- PROJECT: Funknetz Server --
	-- --
	---------------------------------------------------------------------------------
	-- 	--
	-- SYSTEM ENVIRONMENT 						--
	-- OS			Ubuntu 9.10 (Linux 2.6.31)	--	
	-- SOFTWARE 	JDK 1.6.15   				--
	-- 	--
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Helferobjekt fuer die Datei Ein- und Ausgabe der ini-Datei.
*	Eine Instanz dieser Klasse wird fuer die Verwaltung der
*	ini-Datei verwendet.
*
* 	@version 0.3 von 11.2009
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
		String auswahl_ref = "";
		try {
			FileInputStream fis_ref = new FileInputStream("settings.ini");
			ObjectInputStream ois_ref = new ObjectInputStream(fis_ref);
			auswahl_ref = (String) ois_ref.readObject();
			ois_ref.close();
		} catch (FileNotFoundException e_ref) {
			makeNewIni();
		} catch (Exception ex_ref) {
			ex_ref.printStackTrace();
		} //endcatch
		return auswahl_ref;
	} //endmethod readIni
	
	/**
	*	Speichert den neuen Serialportbezeichner in der ini-Datei.
	*	Der alte Serialportbezeichner wird immer ueberschrieben.
	*	@param auswahl der neue Serialportbezeichner
	**/
	public void writeIni(String auswahl_ref) {
		try {
			FileOutputStream fos_ref = new FileOutputStream("settings.ini");
			ObjectOutputStream ous_ref = new ObjectOutputStream(fos_ref);
			ous_ref.writeObject(auswahl_ref);
			ous_ref.close();
		} catch (Exception ex_ref) {ex_ref.printStackTrace();}
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
	
	/**
	 * Speichert die uebergebene Geraeteliste.
	 * @param Geraeteliste
	 */
	public void setDeviceList(Device[][] deviceArray_ref) {
		try {
			FileOutputStream fos_ref = new FileOutputStream("devices.ini");
			ObjectOutputStream ous_ref = new ObjectOutputStream(fos_ref);
			ous_ref.writeObject(deviceArray_ref);
			ous_ref.close();
		} catch (Exception ex_ref) {
			ex_ref.printStackTrace();
		} //endtry
	} //endmethod setDeviceList
	
	/**
	 * Liest die Geraeteliste von der Festplatte ein und liefert sie zurueck.
	 * Existiert keine Geraetelist, wird eine Standardliste mit Hilfe der 
	 * Methode newDeviceList() erzeugt und gespeichert.
	 * @see #newDeviceList()
	 * @return Geraeteliste
	 */
	public Device[][] getDeviceList() {
		Device[][] deviceArray_ref = new Device[4][3];
		try {
			FileInputStream fis_ref = new FileInputStream("devices.ini");
			ObjectInputStream ois_ref = new ObjectInputStream(fis_ref);
			deviceArray_ref = (Device[][]) ois_ref.readObject();
			ois_ref.close();
		} catch (FileNotFoundException fnfe_ref){
			deviceArray_ref = newDeviceList();
		} catch (Exception ex_ref) {
			ex_ref.printStackTrace();
		} //endtry
		
		return deviceArray_ref;
	} //endmethod getDeviceList
	
	/**
	 * Erstellt eine neue Geraeteliste mit Standardnamen.
	 */
	public Device[][] newDeviceList() {
		Device[][] deviceArray_ref = new Device[4][3];
		for (int i=0; i<4; i++) {
			for (int j=0; j<3; j++) {
				deviceArray_ref[i][j] = new Device("user", "K"+(i+1)+" Schalter"+(j+1));
			} //endfor
		} //endfor
		setDeviceList(deviceArray_ref);
		return deviceArray_ref;
	} //endmethod newDeviceList
} //endclass IniHelfer