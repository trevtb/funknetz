package de.funknetz.client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class FunknetzGui {
	
	private IniHelfer helfer;
	private FunknetzClient client;
	
	private JFrame frame;				// Der Frame fuer das Haupt-Client-GUI
	private JComboBox netzwerkWahl;		// Die ComboBox um den Kanal auszuwaehlen
	private int kanal;					// Der aktuelle ausgewaehlte Kanal (INTEGER)
	private int schalter;				// Der zu uebertragende Schalter
	private String befehl;				// Der zu uebergebende Befehl (ON,OFF)
	private static JTextField[] textFArray;		// Array fuer die oberen Textfelder (IP,Port,RequestFroServer).
	
	// Werte direkt ausgelesen, aus dem uebergebenen ArrayList<Object> vom IniHelfer
	private String[] kNamen;			// Array fuer die Namen der Kanaele
	private String[][] sNamen;			// Array fuer die Namen der Schalter (2-Dimensional)
	private String ip;					// Wer fuer die IP
	private int port;					// Wert fuer den Port
	private int selectedChan;			// der aktuell ausgewaehlte Kanal
	
	private JTextField textFPort;			// Textfeld fuer den Port
	private JTextField textFIp;			// Textfeld fuer die IP
	
	
	private JFrame frame2;				// Der Frame fuer das Settings-GUI
	private JLabel[] labelList;				// Liste der Labels (Settings-GUI)
	private JTextField[][] schalterTextFArray;	// Namen der Schalter (Settings-Gui)
	private JTextField[] kanalTextFArray;	// Namen der Kanaele (Settings-Gui)
	private JButton buttonOk;			// OK-Knopf im Settings-GUI
	
	private void updateIni() {
		ArrayList<Object> iniArrayList = helfer.readIni();
		if (iniArrayList == null) {
			iniArrayList = helfer.readIni();
		} //endif
		kNamen = (String[]) iniArrayList.get(0);
		sNamen = (String[][]) iniArrayList.get(1);
		ip = (String) iniArrayList.get(2);
		port = ((Integer) iniArrayList.get(3)).intValue();
		selectedChan = ((Integer) iniArrayList.get(4)).intValue();
	} //endmethod updateIni
	
	private void updateText() {
		for (int i = 0; i < 3; i++) {
				labelList[i].setText(sNamen[kanal][i]);
		} //endfor
		netzwerkWahl.removeAllItems();
		for (int i = 0; i < 4; i++) {
			netzwerkWahl.addItem(kNamen[i]);
		} //endfor
	} //endmethod updateText
	
	// G U I	C O D E	F U E R	D A S	H A U P T F E N S T E R
	private void draw() {
		updateIni();
		
		frame = new JFrame("FN Client v0.1");						// Hier wird der Frame fuer das Haupt-GUI erzeugt
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menubar = new JMenuBar();							// Hier wird die Menueleiste erzeugt
		JMenu menu = new JMenu("Datei");
		JMenu menu2 = new JMenu("Extras");
		JMenuItem settings = new JMenuItem("Einstellungen");
		settings.addActionListener(new SettingsMenuListener());
		menu.add(settings);
		JMenuItem jammer = new JMenuItem("Jammer");
		jammer.addActionListener(new JammerMenuListener());
		menu2.add(jammer);
		menubar.add(menu);
		menubar.add(menu2);
		
		Box hintergrund = new Box(BoxLayout.X_AXIS);					// Erzeugung des Hintergrund-Panels fuer die Raender (10,10,10,10)
		hintergrund.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel hauptPanel = new JPanel();									// Erzeugung von hauptPanel als Fassung fuer teilPanel1 und teilPanel2
		hauptPanel.setLayout(new BorderLayout());
		
		JPanel teilPanel1 = new JPanel();										// Hier entsteht der erste Teil von hauptPanel (teilPanel1)
		teilPanel1.setBorder(BorderFactory.createEmptyBorder(0,0,10,10));
		teilPanel1.setLayout(new BoxLayout(teilPanel1, BoxLayout.X_AXIS));
		
		GridLayout teilPanel1Part1Raster = new GridLayout(2,1);						// Hier entsteht der erste Teil von teilPanel1 (teilPanel1Part1)
		teilPanel1Part1Raster.setVgap(1);
		teilPanel1Part1Raster.setHgap(1);
		JPanel teilPanel1Part1 = new JPanel(teilPanel1Part1Raster);
		teilPanel1Part1.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		JLabel labelIp = new JLabel("IP :");
		JLabel labelRequestFromServer = new JLabel("Request from Server :");
		teilPanel1Part1.add(labelIp);
		teilPanel1Part1.add(labelRequestFromServer);
		
		JPanel teilPanel1Part2 = new JPanel();										// Hier entsteht der zweite Teil von teilPanel1 (teilPanel1Part2)
		teilPanel1Part2.setLayout(new BoxLayout(teilPanel1Part2, BoxLayout.Y_AXIS));
		JLabel labelPort = new JLabel("Port :");
		JTextField textFRequestFromServer = new JTextField(10);
		textFArray[2] = textFRequestFromServer;
		textFIp = new JTextField(10);
		textFIp.setText(ip);
		textFArray[0] = textFIp;
		textFPort = new JTextField(5);
		textFPort.setText("" + port);
		textFArray[1] = textFPort;
		JPanel teilPanel1Part2Teil1 = new JPanel();										// Hier entsteht nochmals ein Unter-Panel von teilPanel1Part2
		teilPanel1Part2Teil1.add(textFIp);
		teilPanel1Part2Teil1.add(labelPort);
		teilPanel1Part2Teil1.add(textFPort);
		teilPanel1Part2.add(teilPanel1Part2Teil1);
		teilPanel1Part2.add(textFRequestFromServer);	
		
		teilPanel1.add(teilPanel1Part1);
		teilPanel1.add(teilPanel1Part2);
		
		JPanel teilPanel2 = new JPanel();										// Hier beginnt der zweite Teil des hauptPanels (teilPanel2)
		teilPanel2.setLayout(new BorderLayout());
		teilPanel2.setBorder(BorderFactory.createEmptyBorder(10,0,10,10));
		
		JPanel teilPanel2Part1 = new JPanel();										// Hier entsteht Part1 von teilPanel2 (teilPanel2Part1)
		TitledBorder title = BorderFactory.createTitledBorder("Funknetz");
		teilPanel2Part1.setBorder(title);
		teilPanel2Part1.setLayout(new BorderLayout());
		netzwerkWahl = new JComboBox();
		
		for (int i = 0; i < 4; i++) {
			netzwerkWahl.addItem(kNamen[i]);
		} //endfor
		
		netzwerkWahl.addItemListener(new TeilPanel2Listener());
		teilPanel2Part1.add(BorderLayout.NORTH, netzwerkWahl);
		
		GridLayout buttonListRaster = new GridLayout(3,4);								// Erzeugung eines Unter-Panels von teilPanel2Part1 (buttonList),
		buttonListRaster.setVgap(7);													// welches die einzelnen ON/OFF Knoepfe fasst
		buttonListRaster.setHgap(7);
		JPanel buttonList = new JPanel(buttonListRaster);									
		buttonList.setBorder(BorderFactory.createEmptyBorder(15,50,15,0));
		
		for (int i = 0; i < 3; i++) {
			ActionListener[] eventArray = {new ButtonSch1Listener(), new ButtonSch2Listener(), new ButtonSch3Listener()};
			JLabel label = new JLabel(sNamen[0][i]);
			labelList[i] = label;
			JButton button1 = new JButton("OFF");
			button1.addActionListener(eventArray[i]);
			buttonList.add(button1);
			JButton button2 = new JButton("ON");
			button2.addActionListener(eventArray[i]);
			buttonList.add(button2);
		} //endfor
		
		GridLayout teilPanel2Part1Teil1Raster = new GridLayout(3,1);							// Ein weiteres Unter-panel von teilPanel2Part1 (teilPanel2Part1Teil1).
		teilPanel2Part1Teil1Raster.setVgap(1);											// Es fasst die Labels der einzelnen Schalter (aus labelList)
		teilPanel2Part1Teil1Raster.setHgap(1);			
		JPanel teilPanel2Part1Teil1 = new JPanel(teilPanel2Part1Teil1Raster);
		teilPanel2Part1Teil1.setBorder(BorderFactory.createEmptyBorder(0,0,0,100));
		
		for (int i = 0; i < 3; i++) {
			teilPanel2Part1Teil1.add(labelList[i]);
		} //endfor
		
		teilPanel2Part1.add(BorderLayout.WEST, teilPanel2Part1Teil1);			
		
		teilPanel2Part1.add(BorderLayout.CENTER, buttonList);	
		
		JLabel labelInfo = new JLabel("Waehle den Kanal und klicke danach auf die Buttons.");
		teilPanel2.add(BorderLayout.SOUTH, labelInfo);
		teilPanel2.add(BorderLayout.EAST, teilPanel2Part1);
	
		hauptPanel.add(BorderLayout.NORTH, teilPanel1);
		hauptPanel.add(BorderLayout.CENTER, teilPanel2);
		
		hintergrund.add(hauptPanel);
		String sPath2Jpg = "de/funknetz/client/resource/fernbedienung.jpg";
		java.net.URL url = ClassLoader.getSystemResource( sPath2Jpg );
		Icon bild = new ImageIcon(url);
		JLabel bildle = new JLabel (bild);
		hintergrund.add(bildle);										// Ende des hintergrund-Panels
		
		frame.getContentPane().add(BorderLayout.CENTER, hintergrund);	// Hier wird der Frame eingerichtet und bestueckt
		frame.setJMenuBar(menubar);
		frame.pack();
		netzwerkWahl.setSelectedIndex(selectedChan);
		frame.setVisible(true);
	} //endmethod draw
	
	
	// G U I	C O D E	F U E R	D A S	S E T T I N G S - G U I
	private void drawSettings() {
		frame2 = new JFrame("Einstellungen");								// Erzeugung eines neuen Frames
		
		JPanel hintergrund = new JPanel();										// Erzeugung eines Hintergrundpanels mit den Raendern (10,10,10,10)
		hintergrund.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		frame2.getContentPane().add(BorderLayout.CENTER, hintergrund);
		
		JPanel teilPanel1 = new JPanel();											// Erzeugung von teilPanel1
		teilPanel1.setLayout(new BorderLayout());
		
		kanalTextFArray = new JTextField[4];											// Erzeugung von teilPanel1Part1 (Teil von zwischenPanel)	
		GridLayout teilPanel1Part1Raster = new GridLayout(5,4);
		teilPanel1Part1Raster.setVgap(1);
		teilPanel1Part1Raster.setHgap(1);
		JPanel teilPanel1Part1 = new JPanel(teilPanel1Part1Raster);	
		teilPanel1Part1.setBorder(new EtchedBorder());		
		JLabel kanaeleLabel = new JLabel("Kanaele");
		teilPanel1Part1.add(kanaeleLabel);
		JLabel schalter1 = new JLabel("Schalter 1");
		teilPanel1Part1.add(schalter1);
		JLabel schalter2 = new JLabel("Schalter 2");
		teilPanel1Part1.add(schalter2);
		JLabel schalter3 = new JLabel("Schalter 3");
		teilPanel1Part1.add(schalter3);
		schalterTextFArray = new JTextField[4][3];
		buttonOk = new JButton("Ok");
		buttonOk.addActionListener(new SettingsButtonListener());
		
		for (int i = 0; i < 4; i++) {
			JTextField textF1 = new JTextField(10);
			textF1.setText(kNamen[i]);
			textF1.addFocusListener(new SettingsMarkerListener());
			textF1.addActionListener(new SettingsOkListener());
			teilPanel1Part1.add(textF1);
			kanalTextFArray[i] = textF1;
			for (int j = 0; j < 3; j++) {
				JTextField textF2 = new JTextField(10);
				textF2.setText(sNamen[i][j]);
				textF2.addFocusListener(new SettingsMarkerListener());
				textF2.addActionListener(new SettingsOkListener());
				teilPanel1Part1.add(textF2);
				schalterTextFArray[i][j] = textF2;
			} //endfor
		} //endfor
	
		GridLayout teilPanel2Raster = new GridLayout(1,2);							// Erzeugung von teilPanel2
		teilPanel2Raster.setHgap(10);
		teilPanel2Raster.setVgap(1);
		JPanel teilPanel2 = new JPanel(teilPanel2Raster);
		teilPanel2.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		JButton buttonCancel = new JButton("Abbrechen");
		buttonCancel.addActionListener(new SettingsButtonListener());
		JButton buttonBack = new JButton("Zuruecksetzen");
		buttonBack.addActionListener(new SettingsButtonListener());
		teilPanel2.add(buttonCancel);
		teilPanel2.add(buttonBack);
		teilPanel2.add(buttonOk);
		
		JLabel beschreibungLabel = new JLabel("Ein Klick auf 'OK' speichert die Werte.");
		
		teilPanel1.add(BorderLayout.CENTER, teilPanel1Part1);
		teilPanel1.add(BorderLayout.SOUTH, beschreibungLabel);
		
		hintergrund.add(BorderLayout.CENTER, teilPanel1);
		hintergrund.add(BorderLayout.SOUTH, teilPanel2);
		
		frame2.setBounds(500,300,550,250);
		frame2.setVisible(true);
	} //endmethod drawSettings
	
	public static String getIP() {
		String ip = textFArray[0].getText();
		return ip;
	} //endmethod getIP
	
	public static int getPort() {
		int port = -1;
		try {
			port = Integer.parseInt(textFArray[1].getText());
		} catch (NumberFormatException ex) {
			port = -1;
		} //endtry
		if (port < 0 || port > 65535) {
			port = -1;
		} //endif
		return port;
	} //endmethod getPort
	
	public String getRqstFromSrv() {
		String rqstFromSrv = textFArray[2].getText();
		return rqstFromSrv;
	} //endmethod getRqstFromSrv
	
	public int getBefehl() {
		int order = -1;
		if (befehl.equals("ON")) {
			order = 1;
		} else if (befehl.equals("OFF")) {
			order = 0;
		} //endif
		return order;
	} //endmethod getBefehl
	
	// ENDE der Getter
	
	// Methode, welche die Informationen an den Server uebergeben soll
	public void thrower() {
		Thread n = new Thread(new ClientRun());
		n.start();
	} //endmethod thrower
	// ENDE der An-Server-Uebergeben Methode
	
	public class ClientRun implements Runnable {
		public void run() {
			if (getPort() != -1 && !getIP().equals("")) {
				textFArray[2].setText("Versuche: " + getIP() + ":" + getPort() + " ...");
				String status = FunknetzClient.transmit(getIP(), getPort(), kanal, schalter, getBefehl());
				textFArray[2].setText(status);
			} else {
				textFArray[2].setText("Ungueltige IP oder ungueltiger Port.");
			} //endif
		} //endmethod run
	} //endclass ClientRun
	
	// LISTENER
		// ITEM LISTENER
	
	class TeilPanel2Listener implements ItemListener {
		public void itemStateChanged (ItemEvent ev) {
			String item = (String) netzwerkWahl.getSelectedItem();
			selectedChan = netzwerkWahl.getSelectedIndex();
			if (item != null) {
				if (item.equals(kNamen[0])) {
					for (int i = 0; i < 3; i++) {
						labelList[i].setText(sNamen[0][i]);
					} //endfor
					kanal = 0;
				} else if (item.equals(kNamen[1])) {
					for (int i = 0; i < 3; i++) {
						labelList[i].setText(sNamen[1][i]);
					} //endfor
					kanal = 1;
				} else if (item.equals(kNamen[2])) {
					for (int i = 0; i < 3; i++) {
						labelList[i].setText(sNamen[2][i]);
					} //endfor
					kanal = 2;
				} else if (item.equals(kNamen[3])) {
					for (int i = 0; i < 3; i++) {
						labelList[i].setText(sNamen[3][i]);
					} //endfor
					kanal = 3;
				}//endif
			} //endif
			FunknetzClient.setIni(kNamen, sNamen, ip, port, selectedChan);
		} //endmethod itemStateChanged
	} //endclass tPanel2Listener
		
		// ENDE DER ITEM LISTENER
		
		// BUTTON LISTENER
	
	class ButtonSch1Listener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			schalter = 0;
			befehl = ev.getActionCommand();
			if (!ip.equals(textFIp.getText()) || port != Integer.parseInt(textFPort.getText())) {
				port = getPort();
				ip = getIP();
				FunknetzClient.setIni(kNamen, sNamen, ip, port, selectedChan);
			} //endif
			thrower();
		} //endmethod actionPerformed
	} //endclass ButtonSch1Listener
	
	class ButtonSch2Listener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			schalter = 1;
			befehl = ev.getActionCommand();
			if (!ip.equals(textFIp.getText()) && port != Integer.parseInt(textFPort.getText())) {
				port = getPort();
				ip = getIP();
				FunknetzClient.setIni(kNamen, sNamen, ip, port, selectedChan);
			} //endif
			thrower();
		} //endmethod actionPerformed
	} //endclass ButtonSch2Listener
	
	class ButtonSch3Listener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			schalter = 2;
			befehl = ev.getActionCommand();
			if (!ip.equals(textFIp.getText()) && port != Integer.parseInt(textFPort.getText())) {
				port = getPort();
				ip = getIP();
				FunknetzClient.setIni(kNamen, sNamen, ip, port, selectedChan);
			} //endif
			thrower();
		} //endmethod actionPerformed
	} //endclass ButtonSch3Listener
	
	class SettingsMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			drawSettings();
		} //endmethod actionPerformed
	} //endclass SettingsMenuListener
	
	class JammerMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JammerGUI jamIt_ref = new JammerGUI();
			jamIt_ref.drawGUI();
		} //endmethod actionPerformed
	} //endclass JammerMenuListener
	
	class SettingsButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			if ((ev.getActionCommand()).equals("Ok")) {
				for (int i = 0; i < 4; i++) {
					kNamen[i] = kanalTextFArray[i].getText();
					for (int j = 0; j < 3; j++) {
						sNamen[i][j] = schalterTextFArray[i][j].getText();
					} //endfor
				} //endfor
				FunknetzClient.setIni(kNamen, sNamen, ip, port, selectedChan);
				updateText();
				frame.repaint();
				frame2.dispose();
			} else if ((ev.getActionCommand()).equals("Zuruecksetzen")) {
				FunknetzClient.newIni();
				updateIni();
				for (int i = 0; i < 4; i++) {
					kanalTextFArray[i].setText(kNamen[i]); 
					for (int j = 0; j < 3; j++) {
						schalterTextFArray[i][j].setText(sNamen[i][j]);
					} //endfor
				} //endfor
				updateText();
				frame.repaint();
			} else {
				frame2.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass SettingsButtonListener
	
	public class SettingsMarkerListener implements FocusListener{
		public void focusGained (FocusEvent e) {
			((JTextField)e.getSource()).selectAll();
		} //endmethod focusGained
		
		public void focusLost (FocusEvent e) {
		} //endmethod focusLost
		
	} //endclass SettingsMarkerListener
	
	public class SettingsOkListener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			for (int i = 0; i < 4; i++) {
					kNamen[i] = kanalTextFArray[i].getText();
					for (int j = 0; j < 3; j++) {
						sNamen[i][j] = schalterTextFArray[i][j].getText();
					} //endfor
				} //endfor
				FunknetzClient.setIni(kNamen, sNamen, ip, port, selectedChan);
				updateText();
				frame.repaint();
				frame2.dispose();
		} //endmethod actionPerformed
	} //endclass SettingsOkListener
			
		
		// ENDE BUTTON LISTENER
	//ENDE DER LISTENER
	
	public FunknetzGui() {
		helfer = FunknetzClient.getIniHelfer();
		labelList = new JLabel[3];
		kanal = 0;
		textFArray = new JTextField[3];
		draw();
	} //endkonstruktor
	
} //endclass FunknetzGui
