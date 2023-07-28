package de.funknetz.client;

// --- Importe
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: FunknetzGui --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Client--
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- SYSTEM ENVIRONMENT --
	-- OS		Linux 2.6.28-14
	-- SOFTWARE 	JDK 1.6.14 --
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	Stellt das GUI-Interface fuer den Client bereit.
* 	Eine Instanz der Klasse FunknetzGui stellt ein
*	GUI-Interface fuer den FunknetzClient zur Verfuegung.
*	Somit kann der Funknetz-Server bequem ferngesteuert werden.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
class FunknetzGui {
	
	// --- Attribute
	private IniHelfer helfer_ref;
	
	private JFrame frame_ref;				// Der Frame fuer das Haupt-Client-GUI
	private JComboBox netzwerkWahl_ref;		// Die ComboBox um den Kanal auszuwaehlen
	private int kanal;						// Der aktuelle ausgewaehlte Kanal (INTEGER)
	private int schalter;					// Der zu uebertragende Schalter
	private String befehl_ref;				// Der zu uebergebende Befehl (ON,OFF)
	private static JTextField[] textFArray_class;	// Array fuer die oberen Textfelder (IP,Port,RequestFroServer).
	
	private String[] kNamen_ref;				// Array fuer die Namen der Kanaele
	private String[][] sNamen_ref;			// Array fuer die Namen der Schalter (2-Dimensional)
	private String ip_ref;					// Wer fuer die IP
	private int port;						// Wert fuer den Port
	private int selectedChan;				// der aktuell ausgewaehlte Kanal
	
	private JTextField textFPort_ref;			// Textfeld fuer den Port
	private JTextField textFIp_ref;				// Textfeld fuer die IP
	
	
	private JFrame frame2_ref;				// Der Frame fuer das Settings-GUI
	private JLabel[] labelList_ref;				// Liste der Labels (Settings-GUI)
	private JTextField[][] schalterTextFArray_ref;	// Namen der Schalter (Settings-Gui)
	private JTextField[] kanalTextFArray_ref;		// Namen der Kanaele (Settings-Gui)
	private JButton buttonOk_ref;				// OK-Knopf im Settings-GUI
	
	// -- Konstruktoren
	
	/**	
	*	Standardkonstruktor: erstellt ein neues IniHelfer Objekt,
	*	initialisiert fuer den Start benoetigte Felder und ruft zum
	*	Schluss die draw()-Methode auf, damit das GUI gleich beim
	*	Erzeugen einer neuen GUI-Instanz gezeichnet wird.
	**/
	public FunknetzGui() {
		helfer_ref = FunknetzClient.getIniHelfer();
		labelList_ref = new JLabel[3];
		kanal = 0;
		textFArray_class = new JTextField[3];
	} //endkonstruktor
	
	// --- Methoden
	
	/** 	
	*	Liest die zuletzt verwendeten Werte und gesetzten Bezeichner
	*	mit Hilfe einer Instanz des IniHelfers aus der ini-Datei ein
	*	und uebernimmt diese im GUI-Interface.
	*	@see IniHelfer
	**/
	private void updateIni() {
		ArrayList<Object> iniArrayList_ref = helfer_ref.readIni();
		if (iniArrayList_ref == null) {
			iniArrayList_ref = helfer_ref.readIni();
		} //endif
		kNamen_ref = (String[]) iniArrayList_ref.get(0);
		sNamen_ref = (String[][]) iniArrayList_ref.get(1);
		ip_ref = (String) iniArrayList_ref.get(2);
		port = ((Integer) iniArrayList_ref.get(3)).intValue();
		selectedChan = ((Integer) iniArrayList_ref.get(4)).intValue();
	} //endmethod updateIni
	
	/** 	
	*	Baut Teile des GUIs neu auf, um die geaenderten
	*	Bezeichner sofort sichtbar zu machen.
	**/
	private void updateText() {
		for (int i = 0; i < 3; i++) {
				labelList_ref[i].setText(sNamen_ref[kanal][i]);
		} //endfor
		netzwerkWahl_ref.removeAllItems();
		for (int i = 0; i < 4; i++) {
			netzwerkWahl_ref.addItem(kNamen_ref[i]);
		} //endfor
	} //endmethod updateText
	
	/** 	
	*	Erstellt das komplette Client-GUI, dies ist DIE
	*	zentrale Methode zur Erstellung des GUIs.
	**/
	public void draw() {
		updateIni();
		
		frame_ref = new JFrame("FN Client v0.1");										// Hier wird der Frame fuer das Haupt-GUI erzeugt
		frame_ref.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menubar_ref = new JMenuBar();										// Hier wird die Menueleiste erzeugt
		
		JMenu dateiM_ref = new JMenu("Datei");
		JMenuItem settings_ref = new JMenuItem("Einstellungen");
		settings_ref.addActionListener(new SettingsMenuListener());
		dateiM_ref.add(settings_ref);
		
		JMenu extrasM_ref = new JMenu("Extras");
		JMenuItem jammer_ref = new JMenuItem("Jammer");
		JMenuItem timeEvents_ref = new JMenuItem("Zeitereignisse");
		timeEvents_ref.addActionListener(new TimeEventListener());
		jammer_ref.addActionListener(new JammerMenuListener());
		extrasM_ref.add(jammer_ref);
		extrasM_ref.add(timeEvents_ref);
		
		menubar_ref.add(dateiM_ref);
		menubar_ref.add(extrasM_ref);
		
		Box hintergrund_ref = new Box(BoxLayout.X_AXIS);								// Erzeugung des Hintergrund-Panels fuer die Raender (10,10,10,10)
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel hauptPanel_ref = new JPanel();											// Erzeugung von hauptPanel als Fassung fuer teilPanel1 und teilPanel2
		hauptPanel_ref.setLayout(new BorderLayout());
		
		JPanel teilPanel1_ref = new JPanel();												// Hier entsteht der erste Teil von hauptPanel (teilPanel1)
		teilPanel1_ref.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
		teilPanel1_ref.setLayout(new BoxLayout(teilPanel1_ref, BoxLayout.X_AXIS));
		
		GridLayout teilPanel1Part1Raster_ref = new GridLayout(2,1);							// Hier entsteht der erste Teil von teilPanel1 (teilPanel1Part1)
		teilPanel1Part1Raster_ref.setVgap(1);
		teilPanel1Part1Raster_ref.setHgap(1);
		JPanel teilPanel1Part1_ref = new JPanel(teilPanel1Part1Raster_ref);
		teilPanel1Part1_ref.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		JLabel labelIp_ref = new JLabel("IP :");
		JLabel labelRequestFromServer_ref = new JLabel("Request from Server :");
		teilPanel1Part1_ref.add(labelIp_ref);
		teilPanel1Part1_ref.add(labelRequestFromServer_ref);
		
		JPanel teilPanel1Part2_ref = new JPanel();											// Hier entsteht der zweite Teil von teilPanel1 (teilPanel1Part2)
		teilPanel1Part2_ref.setLayout(new BoxLayout(teilPanel1Part2_ref, BoxLayout.Y_AXIS));
		JLabel labelPort_ref = new JLabel("Port :");
		JTextField textFRequestFromServer_ref = new JTextField(10);
		textFArray_class[2] = textFRequestFromServer_ref;
		textFIp_ref = new JTextField(10);
		textFIp_ref.setText(ip_ref);
		textFArray_class[0] = textFIp_ref;
		textFPort_ref = new JTextField(5);
		textFPort_ref.setText("" + port);
		textFArray_class[1] = textFPort_ref;
		JPanel teilPanel1Part2Teil1_ref = new JPanel();										// Hier entsteht nochmals ein Unter-Panel von teilPanel1Part2
		teilPanel1Part2Teil1_ref.add(textFIp_ref);
		teilPanel1Part2Teil1_ref.add(labelPort_ref);
		teilPanel1Part2Teil1_ref.add(textFPort_ref);
		teilPanel1Part2_ref.add(teilPanel1Part2Teil1_ref);
		teilPanel1Part2_ref.add(textFRequestFromServer_ref);	
		
		teilPanel1_ref.add(teilPanel1Part1_ref);
		teilPanel1_ref.add(teilPanel1Part2_ref);
		
		JPanel teilPanel2_ref = new JPanel();												// Hier beginnt der zweite Teil des hauptPanels (teilPanel2)
		teilPanel2_ref.setLayout(new BorderLayout());
		teilPanel2_ref.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
		
		JPanel teilPanel2Part1_ref = new JPanel();											// Hier entsteht Part1 von teilPanel2 (teilPanel2Part1)
		TitledBorder title_ref = BorderFactory.createTitledBorder("Funknetz");
		teilPanel2Part1_ref.setBorder(title_ref);
		teilPanel2Part1_ref.setLayout(new BorderLayout());
		netzwerkWahl_ref = new JComboBox();
		
		for (int i = 0; i < 4; i++) {
			netzwerkWahl_ref.addItem(kNamen_ref[i]);
		} //endfor
		
		netzwerkWahl_ref.addItemListener(new TeilPanel2Listener());
		teilPanel2Part1_ref.add(BorderLayout.NORTH, netzwerkWahl_ref);
		
		GridLayout buttonListRaster_ref = new GridLayout(3,4);								// Erzeugung eines Unter-Panels von teilPanel2Part1 (buttonList),
		buttonListRaster_ref.setVgap(7);												// welches die einzelnen ON/OFF Knoepfe fasst
		buttonListRaster_ref.setHgap(7);
		JPanel buttonList_ref = new JPanel(buttonListRaster_ref);									
		buttonList_ref.setBorder(BorderFactory.createEmptyBorder(15,50,15,0));
		
		for (int i = 0; i < 3; i++) {
			ActionListener[] eventArray_ref = {new ButtonSch1Listener(), new ButtonSch2Listener(), new ButtonSch3Listener()};
			JLabel label_ref = new JLabel(sNamen_ref[0][i]);
			labelList_ref[i] = label_ref;
			JButton button1_ref = new JButton("OFF");
			button1_ref.addActionListener(eventArray_ref[i]);
			buttonList_ref.add(button1_ref);
			JButton button2_ref = new JButton("ON");
			button2_ref.addActionListener(eventArray_ref[i]);
			buttonList_ref.add(button2_ref);
		} //endfor
		
		GridLayout teilPanel2Part1Teil1Raster_ref = new GridLayout(3,1);						// Ein weiteres Unter-panel von teilPanel2Part1 (teilPanel2Part1Teil1).
		teilPanel2Part1Teil1Raster_ref.setVgap(1);										// Es fasst die Labels der einzelnen Schalter (aus labelList)
		teilPanel2Part1Teil1Raster_ref.setHgap(1);			
		JPanel teilPanel2Part1Teil1_ref = new JPanel(teilPanel2Part1Teil1Raster_ref);
		teilPanel2Part1Teil1_ref.setBorder(BorderFactory.createEmptyBorder(0,0,0,100));
		
		for (int i = 0; i < 3; i++) {
			teilPanel2Part1Teil1_ref.add(labelList_ref[i]);
		} //endfor
		
		teilPanel2Part1_ref.add(BorderLayout.WEST, teilPanel2Part1Teil1_ref);			
		
		teilPanel2Part1_ref.add(BorderLayout.CENTER, buttonList_ref);	
		
		JLabel labelInfo_ref = new JLabel("Waehle den Kanal und klicke danach auf die Buttons.");
		teilPanel2_ref.add(BorderLayout.SOUTH, labelInfo_ref);
		teilPanel2_ref.add(BorderLayout.EAST, teilPanel2Part1_ref);
	
		hauptPanel_ref.add(BorderLayout.NORTH, teilPanel1_ref);
		hauptPanel_ref.add(BorderLayout.CENTER, teilPanel2_ref);
		
		hintergrund_ref.add(hauptPanel_ref);
		String sPath2Jpg_ref = "de/funknetz/client/resource/fernbedienung.jpg";
		java.net.URL url_ref = ClassLoader.getSystemResource( sPath2Jpg_ref );
		Icon bild_ref = new ImageIcon(url_ref);
		JLabel bildle_ref = new JLabel (bild_ref);
		hintergrund_ref.add(bildle_ref);												// Ende des hintergrund-Panels
		
		frame_ref.getContentPane().add(BorderLayout.CENTER, hintergrund_ref);				// Hier wird der Frame eingerichtet und bestueckt
		frame_ref.setJMenuBar(menubar_ref);
		frame_ref.pack();
		netzwerkWahl_ref.setSelectedIndex(selectedChan);
		frame_ref.setVisible(true);
	} //endmethod draw
	
	
	/** 	
	*	Erstellt das komplette Settings-GUI, in welchem
	*	die Bezeichnungen fuer die einzelnen Schalter und
	*	Kanaele gesetzt werden koennen.
	**/
	private void drawSettings() {
		frame2_ref = new JFrame("Einstellungen");										// Erzeugung eines neuen Frames
		
		JPanel hintergrund_ref = new JPanel();											// Erzeugung eines Hintergrundpanels mit den Raendern (10,10,10,10)
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		frame2_ref.getContentPane().add(BorderLayout.CENTER, hintergrund_ref);
		
		JPanel teilPanel1_ref = new JPanel();												// Erzeugung von teilPanel1
		teilPanel1_ref.setLayout(new BorderLayout());
		
		kanalTextFArray_ref = new JTextField[4];											// Erzeugung von teilPanel1Part1 (Teil von zwischenPanel)	
		GridLayout teilPanel1Part1Raster_ref = new GridLayout(5,4);
		teilPanel1Part1Raster_ref.setVgap(1);
		teilPanel1Part1Raster_ref.setHgap(1);
		JPanel teilPanel1Part1_ref = new JPanel(teilPanel1Part1Raster_ref);	
		teilPanel1Part1_ref.setBorder(new EtchedBorder());		
		JLabel kanaeleLabel_ref = new JLabel("Kanaele");
		teilPanel1Part1_ref.add(kanaeleLabel_ref);
		JLabel schalter1_ref = new JLabel("Schalter 1");
		teilPanel1Part1_ref.add(schalter1_ref);
		JLabel schalter2_ref = new JLabel("Schalter 2");
		teilPanel1Part1_ref.add(schalter2_ref);
		JLabel schalter3_ref = new JLabel("Schalter 3");
		teilPanel1Part1_ref.add(schalter3_ref);
		schalterTextFArray_ref = new JTextField[4][3];
		buttonOk_ref = new JButton("Ok");
		buttonOk_ref.addActionListener(new SettingsButtonListener());
		
		for (int i = 0; i < 4; i++) {
			JTextField textF1_ref = new JTextField(10);
			textF1_ref.setText(kNamen_ref[i]);
			textF1_ref.addFocusListener(new SettingsMarkerListener());
			textF1_ref.addActionListener(new SettingsOkListener());
			teilPanel1Part1_ref.add(textF1_ref);
			kanalTextFArray_ref[i] = textF1_ref;
			for (int j = 0; j < 3; j++) {
				JTextField textF2_ref = new JTextField(10);
				textF2_ref.setText(sNamen_ref[i][j]);
				textF2_ref.addFocusListener(new SettingsMarkerListener());
				textF2_ref.addActionListener(new SettingsOkListener());
				teilPanel1Part1_ref.add(textF2_ref);
				schalterTextFArray_ref[i][j] = textF2_ref;
			} //endfor
		} //endfor
	
		GridLayout teilPanel2Raster_ref = new GridLayout(1,2);								// Erzeugung von teilPanel2
		teilPanel2Raster_ref.setHgap(10);
		teilPanel2Raster_ref.setVgap(1);
		JPanel teilPanel2_ref = new JPanel(teilPanel2Raster_ref);
		teilPanel2_ref.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		JButton buttonCancel_ref = new JButton("Abbrechen");
		buttonCancel_ref.addActionListener(new SettingsButtonListener());
		JButton buttonBack_ref = new JButton("Zuruecksetzen");
		buttonBack_ref.addActionListener(new SettingsButtonListener());
		teilPanel2_ref.add(buttonCancel_ref);
		teilPanel2_ref.add(buttonBack_ref);
		teilPanel2_ref.add(buttonOk_ref);
		
		JLabel beschreibungLabel_ref = new JLabel("Ein Klick auf 'OK' speichert die Werte.");
		
		teilPanel1_ref.add(BorderLayout.CENTER, teilPanel1Part1_ref);
		teilPanel1_ref.add(BorderLayout.SOUTH, beschreibungLabel_ref);
		
		hintergrund_ref.add(BorderLayout.CENTER, teilPanel1_ref);
		hintergrund_ref.add(BorderLayout.SOUTH, teilPanel2_ref);
		
		frame2_ref.setBounds(500,300,550,250);
		frame2_ref.setVisible(true);
	} //endmethod drawSettings
	
	/** 	
	*	Erstellt einen neuen Prozess (ClientRun), welcher dann
	*	im Hintergrund die Informationen an den Server
	*	uebertraegt.
	*	@see ClientRun
	**/
	public void thrower() {
		Thread n_ref = new Thread(new ClientRun());
		n_ref.start();
	} //endmethod thrower
	
	// --- Getter
	
	/**	
	*	Liefert die aktuelle im GUI verwendete IP zurueck.
	*	@return die aktuelle im GUI verwendete IP-Adresse
	**/
	public static String getIP() {
		String ip_ref = textFArray_class[0].getText();
		return ip_ref;
	} //endmethod getIP
	
	/** 	
	*	Liefert den aktuell im GUI verwendeten Port zurueck
	*	und ueberprueft diesen zudem auf Gueltigkeit.
	* 	@return den aktuell im GUI verwendeten Port
	**/
	public static int getPort() {
		int port = -1;
		try {
			port = Integer.parseInt(textFArray_class[1].getText());
		} catch (NumberFormatException ex_ref) {
			port = -1;
		} //endtry
		if (port < 0 || port > 65535) {
			port = -1;
		} //endif
		return port;
	} //endmethod getPort
	
	/**	
	*	Liefert die zuletzt erhaltene Antwort
	*	des Funknetz-Servers zurueck.
	*	@return zuletzt empfangene Server-Antwort.
	**/
	public String getRqstFromSrv() {
		String rqstFromSrv_ref = textFArray_class[2].getText();
		return rqstFromSrv_ref;
	} //endmethod getRqstFromSrv
	
	/**	
	*	Liefert den auszufuehrenden Befehl bzw. 
	*	zuletzt geklickten Button (On/Off) zurueck.
	*	@return auszufuehrender Befehl bzw. zuletzt geklickter Button (On/Off)
	**/
	public int getBefehl() {
		int order = -1;
		if (befehl_ref.equals("ON")) {
			order = 1;
		} else if (befehl_ref.equals("OFF")) {
			order = 0;
		} //endif
		return order;
	} //endmethod getBefehl
	
	// --- Innere Klassen
	
		// --- Runnable Objekte
	
	/** 	
	*	Runnable-Objekt zur Sammlung und Uebertragung
	*	der Daten an den Funknetz-Server.
	**/
	public class ClientRun implements Runnable {
		public void run() {
			if (getPort() != -1 && !getIP().equals("")) {
				textFArray_class[2].setText("Versuche: " + getIP() + ":" + getPort() + " ...");
				String status_ref = FunknetzClient.transmit(getIP(), getPort(), kanal, schalter, getBefehl());
				textFArray_class[2].setText(status_ref);
			} else {
				textFArray_class[2].setText("Ungueltige IP oder ungueltiger Port.");
			} //endif
		} //endmethod run
	} //endclass ClientRun
	
		// --- Listener
	
	/** 	
	*	Setzt den aktuell ausgewaehlten Kanal und Schalter
	*	und schreibt auch eine neue ini-Datei, falls der Benutzer
	*	eine neue Auswahl trifft.
	**/
	class TeilPanel2Listener implements ItemListener {
		public void itemStateChanged (ItemEvent ev_ref) {
			String item_ref = (String) netzwerkWahl_ref.getSelectedItem();
			selectedChan = netzwerkWahl_ref.getSelectedIndex();
			if (item_ref != null) {
				if (item_ref.equals(kNamen_ref[0])) {
					for (int i = 0; i < 3; i++) {
						labelList_ref[i].setText(sNamen_ref[0][i]);
					} //endfor
					kanal = 0;
				} else if (item_ref.equals(kNamen_ref[1])) {
					for (int i = 0; i < 3; i++) {
						labelList_ref[i].setText(sNamen_ref[1][i]);
					} //endfor
					kanal = 1;
				} else if (item_ref.equals(kNamen_ref[2])) {
					for (int i = 0; i < 3; i++) {
						labelList_ref[i].setText(sNamen_ref[2][i]);
					} //endfor
					kanal = 2;
				} else if (item_ref.equals(kNamen_ref[3])) {
					for (int i = 0; i < 3; i++) {
						labelList_ref[i].setText(sNamen_ref[3][i]);
					} //endfor
					kanal = 3;
				}//endif
			} //endif
			FunknetzClient.setIni(kNamen_ref, sNamen_ref, ip_ref, port, selectedChan);
		} //endmethod itemStateChanged
	} //endclass tPanel2Listener
	
	/** 	
	*	Lauscht auf Betaetigung von Buttons der Schaltergruppe eins
	*	und schreibt eine neue ini-Datei bei Betaetigung.
	**/	
	class ButtonSch1Listener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			schalter = 0;
			befehl_ref = ev_ref.getActionCommand();
			if (!ip_ref.equals(textFIp_ref.getText()) || port != Integer.parseInt(textFPort_ref.getText())) {
				port = getPort();
				ip_ref = getIP();
				FunknetzClient.setIni(kNamen_ref, sNamen_ref, ip_ref, port, selectedChan);
			} //endif
			thrower();
		} //endmethod actionPerformed
	} //endclass ButtonSch1Listener
	
	/** 	
	*	Lauscht auf Betaetigung von Buttons der Schaltergruppe zwei
	*	und schreibt eine neue ini-Datei bei Betaetigung.
	**/
	class ButtonSch2Listener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			schalter = 1;
			befehl_ref = ev_ref.getActionCommand();
			if (!ip_ref.equals(textFIp_ref.getText()) && port != Integer.parseInt(textFPort_ref.getText())) {
				port = getPort();
				ip_ref = getIP();
				FunknetzClient.setIni(kNamen_ref, sNamen_ref, ip_ref, port, selectedChan);
			} //endif
			thrower();
		} //endmethod actionPerformed
	} //endclass ButtonSch2Listener
	
	/** 	
	*	Lauscht auf Betaetigung von Buttons der Schaltergruppe drei
	*	und schreibt eine neue ini-Datei bei Betaetigung.
	**/
	class ButtonSch3Listener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			schalter = 2;
			befehl_ref = ev_ref.getActionCommand();
			if (!ip_ref.equals(textFIp_ref.getText()) && port != Integer.parseInt(textFPort_ref.getText())) {
				port = getPort();
				ip_ref = getIP();
				FunknetzClient.setIni(kNamen_ref, sNamen_ref, ip_ref, port, selectedChan);
			} //endif
			thrower();
		} //endmethod actionPerformed
	} //endclass ButtonSch3Listener
	
	/** 
	*	zeichnet bei Aufruf das Settings-GUI
	**/
	class SettingsMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			drawSettings();
		} //endmethod actionPerformed
	} //endclass SettingsMenuListener
	
	/** 	
	*	erstellt bei Aufruf eine neue Jammer-Instanz
	*	und zeichnet dessen GUI.
	*	@see Jammer
	**/
	class JammerMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			Jammer jam_ref = new Jammer();
			jam_ref.draw();
		} //endmethod actionPerformed
	} //endclass JammerMenuListener
	
	class TimeEventListener implements ActionListener {
		public void actionPerformed(ActionEvent e_ref) {
			TimeEvents timeE_ref = new TimeEvents();
			timeE_ref.draw();
		} //endmethod actionPerformed
	} //endclass TimeEventListener
	
	/** 	
	*	dieser Listener ist zustaendig fuer das Settings-GUI und
	*	ueberwacht dessen Buttons. Abhaengig vom geklickten
	*	Button wird das Settings-GUI entweder geschlossen, 
	*	die Bezeichner fuer Schalter und Kanele auf Standardwerte gesetzt,
	* 	oder aber die geaenderten Werte uebernommen.
	*	Bei Aenderung oder Zuruecksetzen wird auch die ini-Datei neu geschrieben.
	**/
	class SettingsButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			if ((ev_ref.getActionCommand()).equals("Ok")) {
				for (int i = 0; i < 4; i++) {
					kNamen_ref[i] = kanalTextFArray_ref[i].getText();
					for (int j = 0; j < 3; j++) {
						sNamen_ref[i][j] = schalterTextFArray_ref[i][j].getText();
					} //endfor
				} //endfor
				FunknetzClient.setIni(kNamen_ref, sNamen_ref, ip_ref, port, selectedChan);
				updateText();
				frame_ref.repaint();
				frame2_ref.dispose();
			} else if ((ev_ref.getActionCommand()).equals("Zuruecksetzen")) {
				FunknetzClient.newIni();
				updateIni();
				for (int i = 0; i < 4; i++) {
					kanalTextFArray_ref[i].setText(kNamen_ref[i]); 
					for (int j = 0; j < 3; j++) {
						schalterTextFArray_ref[i][j].setText(sNamen_ref[i][j]);
					} //endfor
				} //endfor
				updateText();
				frame_ref.repaint();
			} else {
				frame2_ref.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass SettingsButtonListener
	
	/** 	
	*	ueberwacht die Textfelder im Settings-GUI und markiert
	*	den gesamten Feldinhalt bei Auswahl des Feldes.
	**/
	public class SettingsMarkerListener implements FocusListener{
		public void focusGained (FocusEvent e_ref) {
			((JTextField)e_ref.getSource()).selectAll();
		} //endmethod focusGained
		
		public void focusLost (FocusEvent e_ref) {
		} //endmethod focusLost
	} //endclass SettingsMarkerListener
	
	/** 	
	*	ueberwacht die Textfelder des Settings-GUIs und speichert
	*	die geaenderten Werte bei Druecken der "ENTER"-Taste.
	**/
	class SettingsOkListener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			for (int i = 0; i < 4; i++) {
					kNamen_ref[i] = kanalTextFArray_ref[i].getText();
					for (int j = 0; j < 3; j++) {
						sNamen_ref[i][j] = schalterTextFArray_ref[i][j].getText();
					} //endfor
				} //endfor
				FunknetzClient.setIni(kNamen_ref, sNamen_ref, ip_ref, port, selectedChan);
				updateText();
				frame_ref.repaint();
				frame2_ref.dispose();
		} //endmethod actionPerformed
	} //endclass SettingsOkListener
	
} //endclass FunknetzGui
