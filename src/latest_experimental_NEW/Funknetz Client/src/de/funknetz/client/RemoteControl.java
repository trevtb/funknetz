package de.funknetz.client;

// --- Importe
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: RemoteControl --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Client--
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- SYSTEM ENVIRONMENT 					--
	-- OS		Ubuntu 9.10 (Linux 2.6.31)	--
	-- SOFTWARE 	JDK 1.6.15 				--
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	Stellt das GUI-Interface fuer die Fernsteuerung bereit.
* 	Eine Instanz der Klasse RemoteControl stellt ein
*	GUI-Interface fuer den FunknetzClient, zur direkte
*	Steuerung der Geraete zur Verfuegung.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
class RemoteControl {
	
	// --- Attribute
	
	private JFrame frame_ref;				// Der Frame fuer das Haupt-Client-GUI
	private JComboBox netzwerkWahl_ref;		// Die ComboBox um den Kanal auszuwaehlen
	private int kanal;						// Der aktuelle ausgewaehlte Kanal (INTEGER)
	private int schalter;					// Der zu uebertragende Schalter
	private String befehl_ref;				// Der zu uebergebende Befehl (ON,OFF)
	private static JTextField textRFromServ_class;	// Array fuer die oberen Textfelder (IP,Port,RequestFroServer).
	
	private String[] kNamen_ref;				// Array fuer die Namen der Kanaele
	private Device[][] devices_ref;			// Array fuer die Geraete
	private int selectedChan;				// der aktuell ausgewaehlte Kanal
	
	private JLabel[] labelList_ref;				// Liste der Labels (Settings-GUI)
	
	// -- Konstruktoren
	
	/**	
	*	Standardkonstruktor: initialisiert fuer das Gui benoetigte 
	*	Attribute.
	**/
	public RemoteControl() {
		labelList_ref = new JLabel[3];
		kanal = 0;
	} //endkonstruktor
	
	// --- Methoden
	
	/**
	 * Aktualisiert die Kanal- und Schalternamen aus dem FunknetzClient.
	 */
	private void updateIni() {
		kNamen_ref = FunknetzClient.getKNamen();
		devices_ref = FunknetzClient.getDevices();
		if (devices_ref == null) {
			JOptionPane.showMessageDialog(frame_ref, "Es trat ein Problem mit der Verbindung zum Server auf, versuchen Sie es erneut.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
			devices_ref = new Device[4][3];
			for (int i=0; i<devices_ref.length;i++) {
				for (int j=0; j<devices_ref[i].length; j++) {
					devices_ref[i][j] = new Device("user", "K"+(i+1)+" "+"Schalter "+(j+1));
				} //endfor
			} //endfor
		} //endif
		selectedChan = FunknetzClient.getSelectedChan();
	} //endmethod updateIni
	
	/** 	
	*	Erstellt das komplette Client-GUI, dies ist DIE
	*	zentrale Methode zur Erstellung des GUIs.
	**/
	public void draw() {		
		frame_ref = new JFrame("Fernsteuerung");										// Hier wird der Frame fuer das Haupt-GUI erzeugt
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/fernsteuerung_s.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		frame_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		updateIni();
		
		Box hintergrund_ref = new Box(BoxLayout.X_AXIS);								// Erzeugung des Hintergrund-Panels fuer die Raender (10,10,10,10)
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel hauptPanel_ref = new JPanel();											// Erzeugung von hauptPanel als Fassung fuer teilPanel1 und teilPanel2
		hauptPanel_ref.setLayout(new BorderLayout());
		
		JPanel teilPanel1_ref = new JPanel();												// Hier entsteht der erste Teil von hauptPanel (teilPanel1)
		teilPanel1_ref.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
		teilPanel1_ref.setLayout(new BoxLayout(teilPanel1_ref, BoxLayout.X_AXIS));
		
		
		JLabel labelRequestFromServer_ref = new JLabel("Uebertragungsstatus :  ");
		JTextField textFRequestFromServer_ref = new JTextField(10);
		textRFromServ_class = textFRequestFromServer_ref;
		
		ImageIcon iconRequestFromServer_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/next.gif"));
		JLabel iconLabRequestFromServer_ref = new JLabel(iconRequestFromServer_ref);
		teilPanel1_ref.add(labelRequestFromServer_ref);
		teilPanel1_ref.add(iconLabRequestFromServer_ref);
		teilPanel1_ref.add(Box.createHorizontalStrut(2));
		teilPanel1_ref.add(textFRequestFromServer_ref);
		teilPanel1_ref.add(Box.createHorizontalStrut(85));
		
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
		
		ImageIcon iconButON_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/on.gif"));
		ImageIcon iconButOFF_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/off.gif"));
		for (int i = 0; i < 3; i++) {
			ActionListener[] eventArray_ref = {new ButtonSch1Listener(), new ButtonSch2Listener(), new ButtonSch3Listener()};
			JLabel label_ref = new JLabel(devices_ref[0][i].getName());
			labelList_ref[i] = label_ref;
			JButton button1_ref = new JButton("OFF", iconButOFF_ref);
			button1_ref.setActionCommand("OFF");
			button1_ref.addActionListener(eventArray_ref[i]);
			buttonList_ref.add(button1_ref);
			JButton button2_ref = new JButton("ON", iconButON_ref);
			button2_ref.setActionCommand("ON");
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
		String sPath2Jpg_ref = "de/funknetz/client/resource/fernbedienung.gif";
		java.net.URL url_ref = ClassLoader.getSystemResource( sPath2Jpg_ref );
		Icon bild_ref = new ImageIcon(url_ref);
		JLabel bildle_ref = new JLabel (bild_ref);
		hintergrund_ref.add(bildle_ref);												// Ende des hintergrund-Panels
		
		frame_ref.getContentPane().add(BorderLayout.CENTER, hintergrund_ref);				// Hier wird der Frame eingerichtet und bestueckt
		frame_ref.pack();
		frame_ref.setResizable(false);
		netzwerkWahl_ref.setSelectedIndex(selectedChan);
		frame_ref.setVisible(true);
	} //endmethod draw
	
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
	*	Liefert die zuletzt erhaltene Antwort
	*	des Funknetz-Servers zurueck.
	*	@return zuletzt empfangene Server-Antwort.
	**/
	public String getRqstFromSrv() {
		String rqstFromSrv_ref = textRFromServ_class.getText();
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
			if (MainGui.getPort() != -1 && !MainGui.getIP().equals("")) {
				textRFromServ_class.setText("Versuche: " + MainGui.getIP() + ":" + MainGui.getPort() + " ...");
				FunknetzClient.setIni();
				String status_ref = FunknetzClient.transmit(MainGui.getIP(), MainGui.getPort(), kanal, schalter, getBefehl());
				textRFromServ_class.setText(status_ref);
			} else {
				textRFromServ_class.setText("Ungueltige IP oder ungueltiger Port.");
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
						labelList_ref[i].setText(devices_ref[0][i].getName());
					} //endfor
					kanal = 0;
				} else if (item_ref.equals(kNamen_ref[1])) {
					for (int i = 0; i < 3; i++) {
						labelList_ref[i].setText(devices_ref[1][i].getName());
					} //endfor
					kanal = 1;
				} else if (item_ref.equals(kNamen_ref[2])) {
					for (int i = 0; i < 3; i++) {
						labelList_ref[i].setText(devices_ref[2][i].getName());
					} //endfor
					kanal = 2;
				} else if (item_ref.equals(kNamen_ref[3])) {
					for (int i = 0; i < 3; i++) {
						labelList_ref[i].setText(devices_ref[3][i].getName());
					} //endfor
					kanal = 3;
				}//endif
			} //endif
			FunknetzClient.setSelectedChan(selectedChan);
			FunknetzClient.setIni();
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
			thrower();
		} //endmethod actionPerformed
	} //endclass ButtonSch3Listener
	
} //endclass RemoteControl
