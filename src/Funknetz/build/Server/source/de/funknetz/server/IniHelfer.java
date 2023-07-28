package de.funknetz.server;

import java.io.*;
import java.util.*;

public class IniHelfer {
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
	
	public void writeIni(String auswahl) {
		try {
			FileOutputStream fos = new FileOutputStream("settings.ini");
			ObjectOutputStream ois = new ObjectOutputStream(fos);
			ois.writeObject(auswahl);
			ois.close();
		} catch (Exception ex) {ex.printStackTrace();}
	} //endmethod writeIni
		
	public void makeNewIni() {
		String auswahl = "/dev/ttyS0";
		writeIni(auswahl);
	} //endmethod makeNewIni
} //endclass IniHelfer