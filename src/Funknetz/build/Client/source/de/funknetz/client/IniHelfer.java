package de.funknetz.client;

import java.io.*;
import java.util.*;

public class IniHelfer {
	public ArrayList<Object> readIni() {
		ArrayList<Object> array = null;
		String[] kanaele = null;
		String[][] schalter = null;
		String ip = null;
		int port = -1;
		int sele = 0;
		try {
			FileInputStream fis = new FileInputStream("settings.ini");
			ObjectInputStream ois = new ObjectInputStream(fis);
			kanaele = (String[]) ois.readObject();
			schalter = (String[][]) ois.readObject();
			ip = (String) ois.readObject();
			Integer portT = (Integer) ois.readObject();
			sele = (Integer) ois.readObject();
			ois.close();
			array = new ArrayList<Object>();
			array.add(kanaele);
			array.add(schalter);
			array.add(ip);
			array.add(portT);
			array.add(sele);
		} catch (FileNotFoundException e) {
			makeNewIni();
		} catch (Exception ex) {ex.printStackTrace();}
		return array;
	} //endmethod readIni
	
	public void writeIni(String[] kanaele, String[][] schalter, String ip, int port, Integer sel) {
		try {
			FileOutputStream fos = new FileOutputStream("settings.ini");
			ObjectOutputStream ois = new ObjectOutputStream(fos);
			ois.writeObject(kanaele);
			ois.writeObject(schalter);
			ois.writeObject(ip);
			Integer porti = new Integer(port);
			ois.writeObject(port);
			ois.writeObject(sel);
			ois.close();
		} catch (Exception ex) {ex.printStackTrace();}
	} //endmethod writeIni
		
	public void makeNewIni() {
		String[] kanaele = new String[4];
		String[][] schalter = new String[4][3];
		String ip = "127.0.0.1";
		int port = 5000;
		
		// Namen der Kanaele auf Standartwerte setzen
		for (int i = 0; i < 4; i++) {
			kanaele[i] = "Kanal " + "0" + (i+1);
		} //endfor
		
		// Namen der Schalter auf Standartwerte setzen
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				schalter[i][j] = ("K"+(i+1) + " " + "Schalter " + (j+1));
			} //endfor
		} //endfor
		Integer selected = new Integer(0);
		writeIni(kanaele, schalter, ip, port, selected);
	} //endmethod makeNewIni
} //endclass IniHelfer