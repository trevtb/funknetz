package de.funknetz.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class FunknetzServer {
	private static int port;
	private String ip_ref;
	private Boolean transmitString_ref;
	private static FunknetzServerGui gui;
	private ServerSocket serverSock;
	private boolean isActive;
	private static boolean useGui = true;
	private static ComConnect connection;
	private static IniHelfer helfer;
	private boolean success;
	private Jammer jamIt_ref;
	
	public static void main (String[] args) {
		FunknetzServer.helfer = new IniHelfer();
		ComConnect.portIdent = helfer.readIni();
		if (args.length == 1 && args[0].equals("--help")) {
			System.out.println("Syntax:\nOhne GUI: 'java -jar FunknetzServer.jar [port] [serialport]\nMit GUI: 'java -jar FunknetzServer.jar'.\nJammer: 'java -jar FunknetzServer.jar [serialport] jam [kanal] [schalter]'."); 
		} else if (args.length == 4 && args[1].equals("jam")) {
			Jammer jammer = new Jammer(true);
			jammer.startJam(args[2], args[3], args[0]);
		} else if (args.length == 0) {
			gui = new FunknetzServerGui();
		} else if (args.length == 2 && !args[0].equals("--help")) {
			ComConnect.portIdent = args[1];
			useGui = false;
			FunknetzServer server = new FunknetzServer();
			try {
				port = Integer.parseInt(args[0]);
				server.startListening();
			} catch (NumberFormatException ex) {
				System.out.println("Sie haben eine ungueltige Portnummer eingegeben.");
			} //endtry
		} else {
				System.out.println("Falsche Syntax. Benutzen Sie: 'java -jar FunknetzServer.jar [port] [serialport]'. z.B. java -jar FunknetzServer.jar 5000 COM1.");
				System.out.println("Falls Sie den Jammer nutzen moechten, verwenden Sie: 'java -jar FunknetzServer.jar [serialport] [kanal] [schalter]' - z.B. 'java -jar FunknetzServer.jar COM1 1 1'"); 
		} //endif
	} //endmethod main
	
	class StartListening implements Runnable {
		public void run() {
			try { 
				serverSock = new ServerSocket(port);
				gui.setStatusText("Listening on port: " + port + "\n");
				gui.autoScroll();
				isActive = true;
			} catch (IllegalArgumentException iae) {
				gui.setStatusText("Ungueltige Portnummer: " + port + ".\n");
				gui.autoScroll();
			} catch (IOException ex) {
				gui.setStatusText("Der Port " + port + " wird bereits benutzt.\n");
				gui.autoScroll();
				stopListening();
			} //endtry
			
			if (isActive) {
				try {
					Thread tt = new Thread(new SuccessTimer());
					tt.start();
					connection = new ComConnect();
					success = true;
					gui.setStatusText("server started ...\n"); 
				} catch (Exception ex) {
					gui.setStatusText("FEHLER: Kann " + ComConnect.portIdent + " nicht oeffnen.\n");
					gui.autoScroll();
					stopListening();
				} //endtry
				if (isActive) {
					try {
						ServerSocket serverSock = new ServerSocket(port);
						while (true) {
							Socket s = serverSock.accept();
							ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
							String befehlsArt_ref = (String) ois.readObject();
							if (befehlsArt_ref.equals("JStart") {
								jamIt_ref = new Jammer();
								String[] kanBef_ref = (String[]) ois.readObject();
								jamIt_ref.startJam(kanBef_ref[0], kanBef_ref[1]);
							} else if (befehlsArt_ref.equals("JStop") {
								jamIt_ref.stopJam();
							} else if (befehlsArt_ref.equals("JStat") {
								transmitString_ref = new Boolean(jamIt_ref.getStatus());
								ip_ref = s.getInetAddress().toString();
								try {
									Thread transmitter = new Thread(new Transmitter);
									transmitter.start();
								} //endtry
							} else if (befehlsArt_ref.equals("TEvent") {
								System.out.println("Zeitevent erhalten!");
							} else if (befehlsArt_ref.equals("REvent") {
								int[] meinArray = (int[]) ois.readObject();
								execute(meinArray);
							} //endif
							
						} //endwhile
					} catch (Exception ex) {}
				} //endif
			} //endif
		} //endmethod run
	} //endclass StartListening
	
	public class SuccessTimer implements Runnable {
		public void run() {
			try {
				Thread.sleep(1500);
				if (!success) {
					gui.setStatusText("Es gab ein Problem mit der Serialport API.");
					gui.autoScroll();
				} //endif
			} catch (Exception ex) {
				ex.printStackTrace();
			} //endtry
		} //endmethod run
	} //endclass SuccessTimer
	
	public void startListening() {
		if (isActive == false) {
			if (useGui) {
				String portText = gui.getPortTextF();
				try {
					port = Integer.parseInt(portText);
				} catch (NumberFormatException ex) {
					gui.setStatusText("Bitte gueltige Portnummer eingeben.\n");
					gui.autoScroll();
					portText = "";
				} //endtry
				
				if (!portText.equals("")) {
					Thread listenToSock = new Thread(new StartListening());
					listenToSock.start();
				} //endif
			} else {
				System.out.println("\n### Funknetz-Server v0.1 ###");
				System.out.println("=========================================");
				try { 
					serverSock = new ServerSocket(port);
					System.out.println("Listening on port: " + port + "\n");
					isActive = true;
				} catch (IOException ex) {
					System.out.println("Der Port " + port + " wird bereits benutzt.");
				} catch (IllegalArgumentException iae) {
					System.out.println("Ungueltige Portnummer: " + port + ".");
				} //endtry
				if (isActive) {
					System.out.println("Initialisiere ComPort ...");
					try {
						connection = new ComConnect();
					} catch (Exception ex) {
						System.out.println("\nFEHLER: Der ComPort " + ComConnect.portIdent + " konnte nicht geoeffnet werden. Vielleicht existiert er nicht oder wird bereits verwendet.");
						stopListening();
					} //endtry
					if (isActive) {
						try {
							System.out.println("Initialisierung abgeschlossen ...");
							System.out.println("\nserver started ...\n");
							while (true) {
								Socket s = serverSock.accept();
								ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
								int[] meinArray = (int[]) ois.readObject();
								execute(meinArray);
							} //endwhile
						} catch (Exception ex) {}
					} //endif
				} //endif
			} //endif
		} else {
			if (useGui) {
				gui.setStatusText("Der Server ist bereits gestartet.\n");
				gui.autoScroll();
			} else {
				System.out.println("Der Server ist bereits gestartet.\n");
			} //endif
		} //endif
	} //endmethod startListening
	
	public void stopListening() {
		if (useGui) {
			if (isActive) {
				gui.setStatusText("server stopped ...\nListening on port: " + port + " halted.\n");
				gui.autoScroll();
			} else {
				gui.setStatusText("Der Server ist bereits angehalten.\n");
				gui.autoScroll();
			} //endif
		} else {
			System.out.println("server stopped ...\nListening on port: " + port + " halted.\n");
		} //endif
		try {
			serverSock.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
		isActive = false;
	} //endmethod stopListening
	
	public int getPort() {
		return port;
	} //endmethod getPort
	
	public void execute(int[] array) {
		if (useGui) {
			String outputString = "";
		
			for (int i = 0; i < array.length; i++) {
				outputString = outputString + (Integer.toHexString(array[i]) + " ");
			} //endfor
			gui.setStatusText((new Date()).toString() + ": \nBefehl erhalten: " + outputString + "\n\n");
			gui.autoScroll();
		} else {
			String outputString = "";
			
			for (int i = 0; i < array.length; i++) {
				outputString = outputString + (Integer.toHexString(array[i]) + " ");
			} //endfor
			
			System.out.println((new Date()).toString() + ": \nBefehl erhalten: " + outputString + "\n\n");
		} //endif
		connection.transmit(array);
	} //endmethod execute
	
	public static String readIni() {
		String auswahl = helfer.readIni();
		return auswahl;
	} //endmethod readIni
	
	public static void writeIni(String a) {
		helfer.writeIni(a);
	} //endmethod writeIni
	
	
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
	
	public FunknetzServer() {
		isActive = false;
		helfer = new IniHelfer();
	} //endkonstruktor FunknetzServer
	
	class Transmitter implements Runnable {
		public void run() {
			try {
				Socket s = new Socket();
				s.bind(null);
				s.connect(new InetSocketAddress(ip_ref, port), 2000);
				ObjectOutputStream ous = new ObjectOutputStream(s.getOutputStream());
				ous.writeObject(transmitString_ref);
				ous.close();
				s.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			} //endtry
		} //endmethod run
	} //endclass Transmitter
} //endclass FunknetzServer