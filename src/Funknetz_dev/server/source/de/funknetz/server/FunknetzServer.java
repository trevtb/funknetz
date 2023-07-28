package de.funknetz.server;

// --- Importe
import java.io.*;
import java.net.*;
import java.util.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: FunknetzServer --
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
*	Stellt die Hauptfunktionen des Servers bereit.
*	Diese Klasse wertet die ihr uebergebenen Parameter aus und
*	startet den Server dementsprechend im grafischen oder
*	nicht grafischen Modus. Zudem stellt sie viele Werkzeuge
*	fuer Servermodule bereit und bildet somit die Hauptschnittstelle
*	dieses Packages.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
public class FunknetzServer {
	
	// --- Attribute
	private static int port_class;						// der TCP/IP-Port auf dem der Server laeuft
	private static String portTemp_class;				// der vom Benutzer uebergebene Port (Konsolenmodus)
	private static FunknetzServerGui gui_class;			// Referenz auf das GUI-Interface
	private ServerSocket serverSock_ref;				// Der Serversocket, auf dem der Server lauscht
	private boolean isActive;						// Status des Servers
	private static boolean useGui_class = true;			// Ob ein GUI benutzt wird oder nicht
	private static ComConnect connection_class;			// Referenz auf das Serialport-Helfer-Objekt
	private static IniHelfer helfer_class;				// Referenz auf das Datei Ein-/Ausgabe Helfer-Objekt
	private boolean success;						// Status der Comport-Initialisierung
	private static Jammer jam_class;					// Referenz auf das Jammer-Modul
	public static ObjectInputStream ois_class;			// Eingangsstrom fuer den Serversocket
	public static ObjectOutputStream ous_class;			// Ausgabestrom fuer den Serversocket
	public static TimeServer timeServer_class;
	
	// --- Konstruktore
	
	/**
	*	Standardkonstruktor: erstellt eine neue FunknetzServer Instanz,
	*	sowie ein IniHelfer Objekt.
	**/
	public FunknetzServer() {
		isActive = false;
		helfer_class = new IniHelfer();
	} //endkonstruktor FunknetzServer
	
	// --- Methoden
	
	/**
	*	Wertet die uebergebenen Argumente aus und
	*	erzeugt dementsprechend eine neue Server-Instanz.
	* 	Diese kann sowohl im grafischen, als auch nicht grafischen
	*	Modus erzeugt werden. Zusaetzlich werden natuerlich auch
	*	alle implementierten Module instantiiert.
	*	@param args Fuer den Start im nicht-grafischen Modus werden als Argumente der TCP/IP Port des Server, sowie die Bezeichnung des Serialports erwartet.
	**/
	public static void main (String[] args) {
		FunknetzServer.helfer_class = new IniHelfer();
		ComConnect.portIdent_class = helfer_class.readIni();
		if (args.length == 1 && args[0].equals("--help")) {
			System.out.println("Syntax:\nOhne GUI: 'java -jar FunknetzServer.jar [port] [serialport]\nMit GUI: 'java -jar FunknetzServer.jar'."); 
		} else if (args.length == 0) {
			useGui_class = true;
			gui_class = new FunknetzServerGui();
			jam_class = new Jammer(useGui_class);
			timeServer_class = new TimeServer();
		} else if (args.length == 2 && !args[0].equals("--help")) {
			useGui_class = false;
			portTemp_class = args[0];
			ComConnect.portIdent_class = args[1];
			FunknetzServer server_ref = new FunknetzServer();
			server_ref.startListening();
			try {
				Thread.sleep(1000);
			} catch (Exception ex_ref) {
				ex_ref.printStackTrace();
			} //endtry
			jam_class = new Jammer();
			timeServer_class = new TimeServer();
		} else {
				System.out.println("Falsche Syntax. Benutzen Sie: 'java -jar FunknetzServer.jar [port] [serialport]'. z.B. java -jar FunknetzServer.jar 5000 COM1.");
				System.out.println("Ohne Parameter wird der Server im grafischen Modus gestartet.");
		} //endif
	} //endmethod main
	
	/**
	*	Startet den Server.
	*	Die Methode ueberprueft zudem ob der Server bereits laeuft. 
	*	Ist der Server nicht aktiv, wird ein neues StartListening Runnable 
	*	Objekt erzeugt und gestartet.
	*	@see StartListening
	**/	
	public void startListening() {
		if (isActive == false) {
			Thread serverRun_ref = new Thread(new StartListening());
			serverRun_ref.start();
		} else {
			if (useGui_class) {
				FunknetzServerGui.setStatusText("Der Server ist bereits gestartet.\n");
				FunknetzServerGui.autoScroll();
			} else {
				System.out.println("Der Server ist bereits gestartet.\n");
			} //endif
		} //endif
	} //endmethod startListening
	
	/**
	*	Stellt das Client<->Server Protokoll bereit.
	*	Diese Methode beantwortet und wertet
	*	Serveranfragen aus.
	**/
	public void connect() {
		try {
			String type_ref = (String) ois_class.readObject();
			if (type_ref.equals("direct")) {
				int[] meinArray = (int[]) ois_class.readObject();
				execute(meinArray);
			} else if (type_ref.equals("jammer")) {
				String retArg = "";
				String com_ref = (String) ois_class.readObject();
				
				if (com_ref.equals("start")) {
					if (jam_class.getStatus()) {
						retArg = "error";
					} else if (!jam_class.getStatus()){
						retArg = "ok";
						Object[] jamIntFelder_ref = (Object[]) ois_class.readObject();
						int[] onCom_ref = (int[]) jamIntFelder_ref[0];
						int[] offCom_ref = (int[]) jamIntFelder_ref[1];
						jam_class.startJam(onCom_ref, offCom_ref);
					} //endif
				} else if (com_ref.equals("stop")) {
					if (jam_class.getStatus()) {
						jam_class.stopJam();
						retArg = "ok";
					} else {
						retArg = "error";
					} //endif
				} //endif
				ous_class.writeObject(retArg);
				ous_class.writeObject("done");
			} else if (type_ref.equals("timeevent")) {
				ous_class.writeObject(new Date());
				String readWrite_ref = (String) ois_class.readObject();
				if (readWrite_ref.equals("set")) {
					String[][] tempTimeTable_ref = (String[][]) ois_class.readObject();
					if (!useGui_class) {
						System.out.println(new Date() + ": Die Zeitereignisse wurden aktualisiert.");
						System.out.println("");
					} else {
						FunknetzServerGui.setStatusText(new Date() + ":\nDie Zeitereignisse wurden aktualisiert\n");
					} //endif
					timeServer_class.setTimeTable(tempTimeTable_ref);
				} else if (readWrite_ref.equals("get")) {
					if (!useGui_class) {
						System.out.println(new Date() + ": Es wurde eine Liste mit Zeitereignissen versandt.");
						System.out.println("");
					} else {
						FunknetzServerGui.setStatusText(new Date() + ":\nEs wurde eine Liste mit Zeitereignissen versandt.");
					} //endif
					ous_class.writeObject(timeServer_class.getTimeTable());
					ous_class.writeObject("done");
				} //endif
			} //endif
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
	} //endmethod connect
						
	/**
	*	Stellt alle Aktivitaeten des Servers ein, 
	*	lediglich das GUI bleibt bestehen.
	**/
	public void stopListening() {
		if (useGui_class) {
			if (isActive) {
				FunknetzServerGui.setStatusText("server started...\nListening on port: " + port_class + " halted.\n");
				FunknetzServerGui.autoScroll();
			} else {
				FunknetzServerGui.setStatusText("Der Server ist bereits angehalten.\n");
				FunknetzServerGui.autoScroll();
			} //endif
		} else {
			System.out.println("server stopped ...\nListening on port: " + port_class + " halted.\n");
		} //endif
		try {
			serverSock_ref.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
		isActive = false;
	} //endmethod stopListening
	
	/**
	*	Uebermittelt das uebergebene Feld, an den Mikroprozessor.
	*	Das uebergebene Feld beinhaltet den Code, fuer die
	*	Steuerung des Mikroprozessors. Mit Hilfe einer Instanz der Klasse
	*	ComConnect, wird dieses an die Funknetz-Box uebertragen.
	*	@param array Der an den Mikroprozessor zu uebertragende Code in int-Form.
	**/
	public static void execute(int[] array) {
		if (useGui_class) {
			String outputString = "";
		
			for (int i = 0; i < array.length; i++) {
				outputString = outputString + (Integer.toHexString(array[i]) + " ");
			} //endfor
			FunknetzServerGui.setStatusText((new Date()).toString() + ": \nBefehl erhalten: " + outputString + "\n\n");
			FunknetzServerGui.autoScroll();
		} else {
			String outputString = "";
			
			for (int i = 0; i < array.length; i++) {
				outputString = outputString + (Integer.toHexString(array[i]) + " ");
			} //endfor
			
			System.out.println((new Date()).toString() + ": \nBefehl erhalten: " + outputString + "\n\n");
		} //endif
		connection_class.transmit(array);
	} //endmethod execute
	
	/**
	*	Gibt die in der ini-Datei gespeicherten Werte zurueck.
	*	Hierzu wird eine Instanz der Klasse IniHelfer genutzt.
	*	@return auswahl der im GUI gesetzte, zuletzt verwendete Port
	*	@see IniHelfer
	**/
	public static String readIni() {
		String auswahl = helfer_class.readIni();
		return auswahl;
	} //endmethod readIni
	
	/**
	*	Schreibt die uebergebenen Werte in die ini-Datei.
	*	Hierzu wird eine Instanz der Klasse IniHelfer genutzt.
	*	@param a der zu speichernde Serial-Port Bezeichner.
	*	@see IniHelfer
	**/
	public static void writeIni(String a) {
		helfer_class.writeIni(a);
	} //endmethod writeIni
	
	/**
	*	Erstellt an Hand der Nummer bzw. des Stellenwertes
	*	des im Client gedrueckten Knopfes den an den
	*	Mikroprozessor zu uebertragenden Code.
	*	@return rr der Code fuer den Mikroprozessor.
	*	@param wert Stellenwert des Knopfes
	**/
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
	
	// --- Innere Klassen
	
	/**
	*	Initialisiert und startet den Server inklusive aller benoetigten
	*	Sockets und Module. Es kuemmert sich auch um die Validierung der
	*	einzelnen Parameter und die damit verbundene Ausgabe von Fehlermeldungen.
	**/
	public class StartListening implements Runnable {
		public void run() {
			if (useGui_class && !(gui_class.getPortTextF().equals(""))) {
				String portText_ref = gui_class.getPortTextF();
				boolean portValid = false;
				try {
					port_class = Integer.parseInt(portText_ref);
					portValid = true;
				} catch (NumberFormatException ex) {
					FunknetzServerGui.setStatusText("Bitte gueltige Portnummer eingeben.\n");
					FunknetzServerGui.autoScroll();
				} //endtry
				if (portValid) {
					try { 
						serverSock_ref = new ServerSocket(port_class);
						FunknetzServerGui.setStatusText("Listening on port: " + port_class + "\n");
						FunknetzServerGui.autoScroll();
						isActive = true;
					} catch (IllegalArgumentException iae) {
						FunknetzServerGui.setStatusText("Ungueltige Portnummer: " + port_class + ".\n");
						FunknetzServerGui.autoScroll();
					} catch (IOException ex) {
						FunknetzServerGui.setStatusText("Der Port " + port_class + " wird bereits benutzt.\n");
						FunknetzServerGui.autoScroll();
						stopListening();
					} //endtry
				} //endif
				
				if (isActive) {
					try {
						Thread tt_ref = new Thread(new SuccessTimer());
						tt_ref.start();
						connection_class = new ComConnect();
						success = true;
						FunknetzServerGui.setStatusText("server started ...\n"); 
					} catch (Exception ex) {
						FunknetzServerGui.setStatusText("FEHLER: Kann " + ComConnect.portIdent_class + " nicht oeffnen.\n");
						FunknetzServerGui.autoScroll();
						stopListening();
					} //endtry
					if (isActive) {
						try {
							Thread con_ref = new Thread(new Connector());
							con_ref.start();
						} catch (Exception ex) {}
					} //endif
				} //endif
			} else if (!useGui_class) {
				System.out.println("\n### Funknetz-Server v0.2 ###");
				System.out.println("=========================================");
				boolean portValid = false;
				try {
					port_class = Integer.parseInt(portTemp_class);
					portValid = true;
				} catch (NumberFormatException ex) {
					System.out.println("FEHLER: Sie haben eine ungueltige Portnummer eingegeben.");
				} //endtry
				
				if (portValid) {
					try { 
						serverSock_ref = new ServerSocket(port_class);
						System.out.println("Listening on port: " + port_class + "\n");
						isActive = true;
					} catch (IOException ex_ref) {
						System.out.println("Der Port " + port_class + " wird bereits benutzt.");
					} catch (IllegalArgumentException iae_ref) {
						System.out.println("Ungueltige Portnummer: " + port_class + ".");
					} //endtry
				} //endif
				
				if (isActive) {
					System.out.println("Initialisiere ComPort ...");
					try {
						connection_class = new ComConnect();
					} catch (Exception ex) {
						System.out.println("\nFEHLER: Der ComPort " + ComConnect.portIdent_class + " konnte nicht geoeffnet werden. Vielleicht existiert er nicht oder wird bereits verwendet.");
						stopListening();
					} //endtry
					if (isActive) {
						try {
							Thread tRun_ref = new Thread(new Connector());
							tRun_ref.start();
							System.out.println("Initialisierung abgeschlossen ...");
							System.out.println("\nserver started ...\n");
						} catch (Exception ex_ref) {}
					} //endif
				} //endif
			} //endif
		} //endmethod run
	} //endclass StartListening
	
	/**
	*	Ein Timer fuer die Ueberpruefung des SerialPort Status.
	*	Der Timer wartet 1,5 Sekunden auf die erfolgreiche
	*	Initialisierung des SerialPorts. Schlaegt diese Fehl,
	*	wird eine Fehlermeldung ausgegeben.
	**/
	public class SuccessTimer implements Runnable {
		public void run() {
			try {
				Thread.sleep(1500);
				if (!success) {
					FunknetzServerGui.setStatusText("Es gab ein Problem mit der Serialport API.");
					FunknetzServerGui.autoScroll();
				} //endif
			} catch (Exception ex) {
				ex.printStackTrace();
			} //endtry
		} //endmethod run
	} //endclass SuccessTimer
	
	/**
	* 	Wartet auf neue Verbindung durch den Client und ruft
	*	bei Erfolg die connect()-Methode fuer die Server<->Client
	*	Unterhaltung auf.
	*	@see #connect()
	**/
	class Connector implements Runnable {
		public void run() {
			while (true) {
				try {
					Socket s_ref = serverSock_ref.accept();
					ois_class = new ObjectInputStream(s_ref.getInputStream());
					ous_class = new ObjectOutputStream(s_ref.getOutputStream());
					connect();
					s_ref.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				} //endtry
			} //endwhile
		} //endmethod run
	} //endclass Connector
	
} //endclass FunknetzServer