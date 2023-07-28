package de.funknetz.client;

//--- Importe
import java.net.*;
import java.util.*;
import java.io.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: TimeEventHelper --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Client--
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- SYSTEM ENVIRONMENT 						--
	-- OS			Ubuntu 9.10 (Linux 2.6.31)	--
	-- SOFTWARE 	JDK 1.6.15 					--
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
* 	Stellt das Helferobjekt fuer das Senden und Abfragen der Zeitereignisse bereit.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class TimeEventHelper {
	// --- Attribute
	private String commandType_ref;
	private ObjectOutputStream oos_ref;
	private ObjectInputStream ois_ref;
	private String[][] felderMatrix_ref;
	private String status_ref;
	private Calendar serverTime_ref;
	private String connectionStat_ref;
	
	// --- Konstruktoren
	public TimeEventHelper() {
		connectionStat_ref = "";
	} //endconstructor
	
	// --- Methoden
	/**
	 * Liefert die aktuelle Liste mit Zeitereignissen als 2-dimensionales
	 * String-Array zurueck.
	 * @return Liste mit Zeitereignissen und Status der Uebertragung
	 */
	public Object[] getTimeEvents() {
		commandType_ref = "get";
		Object[] returnArray_ref = new Object[3];
		try { 
			Thread t_ref = new Thread(new ServerConnector());
			t_ref.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
		while (!(connectionStat_ref.equals("done"))) {
		} //endwhile
		connectionStat_ref = "wrong";
		returnArray_ref[0] = status_ref;
		returnArray_ref[1] = serverTime_ref;
		returnArray_ref[2] = felderMatrix_ref;
		return returnArray_ref;
	} //endmethod getTimeEvents
	
	/**
	 * Uebertraegt das uebergebene Array mit Zeitereignissen an den Server
	 * und liefert einen String zurueck, der Auskunft ueber den Erfolg der
	 * Uebertragung gibt.
	 * @param events_ref Liste mit Zeitereignissen
	 * @return Status der Uebertragung
	 */
	public String setTimeEvents(String[][] events_ref) {
		felderMatrix_ref = events_ref;
		commandType_ref = "set";
		try { 
			Thread t_ref = new Thread(new ServerConnector());
			t_ref.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
		while (!(connectionStat_ref.equals("done"))) {
		} //endwhile
		connectionStat_ref = "wrong";
		return status_ref;
	} //endmethod setTimeEvents
	
	/**
	*	Liest die Tabellendaten vom Server ein oder sendet die aktuellen.
	*	Was die Methode macht, haengt vom Wert der Variablen commandType_ref ab.
	*	Ist diese auf "set" gesetzt, wird die aktuelle Tabelle an den Server gesendet.
	*	Ist diese auf "get" gesetzt, wird die aktuelle Tabelle vom Server eingelesen.
	**/
	private void connect() {
		if (commandType_ref.equals("get")) {
			try {
				oos_ref.writeObject("get");
				felderMatrix_ref = (String[][]) ois_ref.readObject();
				if (felderMatrix_ref[0] == null) {
					felderMatrix_ref = null;
				} //endif
				status_ref = "success";
			} catch (Exception ex_ref) {
				ex_ref.printStackTrace();
				status_ref = "connection_error";
			} //endtry
		} else if (commandType_ref.equals("set")) {
			try {
				oos_ref.writeObject("set");
				oos_ref.writeObject(felderMatrix_ref);
				oos_ref.writeObject("done");
				status_ref = "success";
			} catch (Exception ex) {
				status_ref = "connection_error";
			} //endtry
		} //endif
	} //endmethod connect
	
	// --- Innere Klassen
	/**
	*	Kuemmert sich um die Kommunikation mit dem Server als Hintergrundprozess.
	*	Zur Protokollabwicklung mit dem Server wird die Methode connect() verwendet.
	*	@see #connect()
	**/
	class ServerConnector implements Runnable {
		public void run() {
			try {
				Socket s_ref = new Socket();
				s_ref.bind(null);
				s_ref.connect(new InetSocketAddress(MainGui.getIP(), MainGui.getPort()), 5000);
				oos_ref = new ObjectOutputStream(s_ref.getOutputStream());
				ois_ref = new ObjectInputStream(s_ref.getInputStream());
				oos_ref.writeObject("timeevent");
				Date tempDate_ref = (Date)ois_ref.readObject();
				Calendar tempC_ref = Calendar.getInstance();
				tempC_ref.setTime(tempDate_ref);
				serverTime_ref = tempC_ref;
				connect();
				ois_ref.close();
				oos_ref.close();
				s_ref.close();
				connectionStat_ref = "done";
			} catch (ConnectException e_ref) {
				status_ref = "connection_error";
				connectionStat_ref = "done";
			} catch (Exception ex_ref) {	
				ex_ref.printStackTrace();
				status_ref = "connection_error";
				connectionStat_ref = "done";
			} //endtry
		} //endmethod run
	} //endclass ServerConnector
} //endclass TimeEventHelper
