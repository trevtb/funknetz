package de.funknetz.server;

import java.util.*;

public class Jammer {
	private ComConnect connection;
	private boolean validation;
	private boolean useConsole;
	
	public void startJam(String kanal, String schalter, String port) {
		String[] testStringA = {kanal, schalter, port};
		for (int i = 0; i < 2; i++) {
			try {
				int test = Integer.parseInt(testStringA[i]);
				if ((i == 0 && Integer.parseInt(testStringA[i]) > 0 && Integer.parseInt(testStringA[i]) < 5) | (i == 1 && Integer.parseInt(testStringA[i]) > 0 && Integer.parseInt(testStringA[i]) < 4)) {
					validation = true;
				} //endif
			} catch (Exception ex) {
				if (useConsole) {
					System.out.println("FEHLER: Bei den eingegebenen Werten handelt es sich nicht um ganzzahlige Werte");
				} //endif
			} //endtry
		} //endfor
		
		if (!validation && useConsole) {
			System.out.println("Ungueltiger Kanal- oder ungueltige Schalter-Nummer.");
		} //endif
		
		if (validation) {
			if (useConsole) {
				System.out.println("\nJammer gestartet ...");
				System.out.println("=========================================");
			} //endif
			try {
				ComConnect.portIdent = testStringA[2];
				connection = new ComConnect();
			} catch (Exception ex) {
				if (useConsole) {
					System.out.println("FEHLER:\nEs gab Probleme mit der Serialport-API.\n");
				} //endif
				validation = false;
			} //endtry
			Object[] tempArray = getJamCommands((Integer.parseInt(testStringA[0])), (Integer.parseInt(testStringA[1])));
			while (validation) {
				if (useConsole) {
					System.out.println(new Date() + ": Sende 'ON' auf Kanal " +  testStringA[0] + ", Schalter: " + testStringA[1]);
				} //endif
				for (int i = 0; i < 2; i++) {
					connection.transmit((int[]) tempArray[0]);
				} //endfor
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {ex.printStackTrace();}
				if (useConsole) {
					System.out.println(new Date() + ": Sende 'OFF' auf Kanal " +  testStringA[0] + ", Schalter: " + testStringA[1]);
				} //endif
				for (int i = 0; i < 2; i++) {
					connection.transmit((int[]) tempArray[1]);
				} //endfor
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {
					ex.printStackTrace();
				} //endtry
			} //endwhile
		} //endif
	} //endmethod startJam
	
	public void stopJam() {
		validation = false;
		connection = null;
	} //endmethod stopJam
	
	public Object[] getJamCommands(int kanal, int schalter) {
		int aufrufWert = (kanal-1)*6;
		aufrufWert += (schalter-1)*2;
		
		int[] switchON = FunknetzServer.makeIt(aufrufWert+1);
		int[] switchOFF = FunknetzServer.makeIt(aufrufWert);
		
		Object[] returnA = {switchON, switchOFF};
		return returnA;
	} //endmethod getJamCommands
	
	public boolean getStatus() {
		return validation;
	} //endmethod getStatus
	
	public Jammer (boolean useConsole) {
		this.useConsole = useConsole;
	} //endconstructor

} //endclass Jammer