package de.funknetz.client;

import java.io.*;
import java.util.*;
import java.net.*;

public class FunknetzClient {
	private static IniHelfer helfer;
	private static String status = "";
	public static FunknetzGui gui;
	
	public static void main (String[] args) {
		gui = new FunknetzGui();
	} //endmethod main
	
	public static IniHelfer getIniHelfer() {
		helfer = new IniHelfer();
		return helfer;
	} //endmethod getIniHelfer
	
	public static void setIni (String[] chans, String[][] schalts, String ipi, int porti, int selection) {
		Integer sel = new Integer(selection);
		helfer.writeIni(chans, schalts, ipi, porti, sel);
	} //endmethod setIni
	
	public static void newIni() {
		helfer.makeNewIni();
	} //endmethod
	
	public static String transmit (String ip, int port, int kanal, int schalter, int befehl) {
		int aufrufWert = kanal * 6;
		aufrufWert += schalter *2;
		aufrufWert += befehl;
		
		int[] meinArray = makeIt(aufrufWert);
		
		try {
			Socket s = new Socket();
			s.bind(null);
			s.connect(new InetSocketAddress(ip, port), 2000);
			ObjectOutputStream ous = new ObjectOutputStream(s.getOutputStream());
			ous.writeObject("REvent");
			ous.writeObject(meinArray);
			ous.close();
			s.close();
			String check = "OK "; 
			String wert = "";
			for (int i = 0; i < meinArray.length; i++) {
				wert += Integer.toHexString(meinArray[i]) + " ";
			} //endfor
			status = check + wert;
		} catch (Exception ex) {
			status = "Der Server ist nicht erreichbar";
		} finally {
			return status;
		} //endtry
	} //endmethod transmit
	
	public static int[] makeIt(int wert) {
		int[] rr = {0x23,0x91,0x11,0x02,0};
		if (wert < 6)  {
			rr[1] = 0x81;
			rr[2] = 0x01;
		} //endif
		if (wert >= 6) {
			rr[1] = 0x82;
			rr[2] = 0x02;
		}
		if (wert >= 12) {
			rr[1] = 0x84;
			rr[2] = 0x04;
		} //endif
		if (wert >= 18) {
			rr[1] = 0x88;
			rr[2] = 0x08;
		} //endif

		if ((wert & 0xFE) % 3 == 0)  {
			rr[1] = rr[1] | 0x10;
			rr[2] = rr[2] | 0x10;
		} //endif
		if ((wert & 0xFE) % 3 == 2)  {
			rr[1] = rr[1] | 0x20;
			rr[2] = rr[2] | 0x20;
		} //endif
		if ((wert & 0xFE) % 3 == 1)  {
			rr[1] = rr[1] | 0x40;
			rr[2] = rr[2] | 0x40;
		}//endif

		if ((wert & 1) == 0) {
			rr[3] = 1;
		} //endif

		int g = 0x3F;
		for (int i = 0; i < 4; i++){
			g = g ^ rr[i];
		} //endfor 
		rr[4] = g;
		
		return rr;
	} //endmethod makeIt
} //endclass FunknetzClient