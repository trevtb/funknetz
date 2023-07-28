package de.funknetz.server;

// --- Importe
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: FunknetzServerGui --
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
*	Stellt das GUI fuer den Server bereit.
*	Der Server wird standardmaessig im GUI Modus gestartet.
*	Hier kann natuerlich auch der Serialport grafisch gesetzt werden.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
class FunknetzServerGui {
	
	// --- Attribute
	private JButton start_ref;						// Start-Button des Server-GUI
	private JButton stop_ref;						// Stop-Button des Server-GUI
	private static JTextArea status_class;			// TextArea des Server-GUI
	private FunknetzServer server_ref;				// Referenz auf den eigentlichen Server
	private JFrame frame_ref;					// Frame des Server-GUI
	private JTextField portTextF_ref;				// TextFeld fuer den TCP/IP-Port im Server-GUI
	private JFrame frame2_ref;					// Frame fuer das Settings-GUI
	private JTextField textFeld_ref;				// TextFeld fuer den Serialportbezeichner im Settings-GUI
	private static JScrollPane scroller_class;			// ScrollPane fuer die TextArea des Server-GUI
	
	// --- Konstruktoren
	
	/**
	*	Standardkonstruktor: erstellt eine neue Instanz und ruft die draw()-Methode auf.
	*	@see #draw()
	**/
	public FunknetzServerGui () {
		draw();
	} //endkonstruktor FunknetzServerGui
	
	// --- Methoden
	
	/**
	*	Zeichnet das GUI Interface.
	*	Die Methode besorgt sich zunaechst eine Referenz auf ein 
	*	neues FunknetzServer Objekt und zeichnet anschliessend
	*	das GUI.
	*	@see FunknetzServer
	**/
	public void draw() {
		server_ref = new FunknetzServer();
		frame_ref = new JFrame();
		frame_ref.setTitle("FN Server v0.2");
		frame_ref.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menubar = new JMenuBar();							// Hier wird die Menueleiste erzeugt
		JMenu menu = new JMenu("Datei");
		JMenuItem settings = new JMenuItem("Einstellungen");
		settings.addActionListener(new SettingsMenuListener());
		menu.add(settings);
		menubar.add(menu);
		JPanel hintergrund = new JPanel();
		hintergrund.setLayout(new BoxLayout(hintergrund, BoxLayout.Y_AXIS));
		hintergrund.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		GridLayout raster = new GridLayout(2,2);
		raster.setVgap(5);
		raster.setHgap(5);
		JPanel buttonBox = new JPanel(raster);
		buttonBox.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		JLabel portLabel = new JLabel("Port:");
		buttonBox.add(portLabel);
		portTextF_ref = new JTextField(10);
		portTextF_ref.setText("5000");
		buttonBox.add(portTextF_ref);
		portTextF_ref.requestFocus();
		start_ref = new JButton("Start Server");
		start_ref.addActionListener(new MeinButtonListener());
		stop_ref = new JButton("Stop Server");
		stop_ref.addActionListener(new MeinButtonListener());
		buttonBox.add(start_ref);
		buttonBox.add(stop_ref);
		
		status_class = new JTextArea(10,22);
		status_class.setEditable(false);
		status_class.setLineWrap(true);
		scroller_class = new JScrollPane(status_class);
		scroller_class.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller_class.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller_class.setBorder(BorderFactory.createEtchedBorder());
		
		hintergrund.add(buttonBox);
		hintergrund.add(scroller_class);
		
		frame_ref.setJMenuBar(menubar);
		frame_ref.getContentPane().add(hintergrund);
		frame_ref.pack();
		frame_ref.setVisible(true);
	} //endmethod draw
	
	/**
	*	Zeichnet das Settings-Menue des FunknetzServer GUIs.
	*	Hier kann der Serialport gesetzt werden, standardmaessig
	*	wird der zuletzt benutzte Port geladen.
	**/
	public void drawSettings() {
		
		frame2_ref = new JFrame("Einstellungen");
		frame2_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel hauptPanel = new JPanel();
		hauptPanel.setLayout(new BorderLayout());
		
		JPanel hintergrund = new JPanel();
		hintergrund.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel zwischenPanel = new JPanel();
		TitledBorder title = BorderFactory.createTitledBorder("Serial Port Einstellungen");
		zwischenPanel.setBorder(title);
		
		JLabel infoLabel = new JLabel("Waehlen Sie den Port aus, an den die Funknetz-Box angeschlossen ist.");
		
		JLabel label = new JLabel("Serialport: ");
		textFeld_ref = new JTextField(10);
		textFeld_ref.setText(FunknetzServer.readIni());
		zwischenPanel.add(label);
		zwischenPanel.add(textFeld_ref);
		
		JPanel southBox = new JPanel();
		southBox.setLayout(new BorderLayout());
		
		JPanel buttonBox = new JPanel();
		JButton okBut = new JButton("Ok");
		okBut.addActionListener(new MeinButtonListener());
		JButton cancelBut = new JButton("Abbrechen");
		cancelBut.addActionListener(new MeinButtonListener());
		buttonBox.add(okBut);
		buttonBox.add(cancelBut);
		
		southBox.add(BorderLayout.CENTER, buttonBox);
		southBox.add(BorderLayout.NORTH, infoLabel);
		
		hauptPanel.add(BorderLayout.CENTER, zwischenPanel);
		hauptPanel.add(BorderLayout.SOUTH, southBox);
		
		hintergrund.add(hauptPanel);
		
		frame2_ref.getContentPane().add(hintergrund);
		frame2_ref.pack();
		frame2_ref.setVisible(true);
	} //endmethod drawSettings
	
	/** 
	*	Springt bei Aufruf zum Ende der TextArea.
	*	Die JScrollBar scroller_class fasst die JTextArea.
	*	Die Methode setzt die ScrollBar auf die maximale
	*	untere Position.
	**/
	public static void autoScroll() {
		int max = scroller_class.getVerticalScrollBar().getMaximum();
		scroller_class.getVerticalScrollBar().setValue(max);
	}
	
	/**
	*	Erweitert den Text der TextArea auf den uebergebenen String.
	*	Es sollte unbedingt ein Newline-Zeichen an das Ende des
	*	zu setzenden Strings gehaengt werden.
	*	@param s String, um den die TextArea zu erweitern ist
	**/
	public static void setStatusText(String s) {
		status_class.setText(status_class.getText() + s);
		autoScroll();
	} //endmethodsetStatusText
	
	/**
	*	Zeichnet den Frame des Server GUIs erneut.
	**/
	public void zeichneNeu() {
		frame_ref.repaint();
	} //endmethod zeichneNeu
	
	/**
	*	Liefert den vom Benutzer gesetzten Port zurueck.
	**/
	public String getPortTextF() {
		return portTextF_ref.getText();
	} //endmethod getPortTextF
	
	// --- Innere Klassen
	
	/**
	*	Lauscht auf die Buttons des Server- und Settings-GUI.
	*	Ruft fuer das Server-GUI entweder die Methode 
	*	startListening() oder stopListening() auf bzw.
	*	schliesst den Frame.
	*	Der OK-Button des Settings-GUI setzt den Serialportbezeichner
	*	in der Klasse ComConnect (portIdent_class) und speicher
	*	die Aenderung in der ini-Datei.
	**/
	class MeinButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			String command = ev.getActionCommand();
			if (command.equals("Start Server")) {
				server_ref.startListening();
			} else if (command.equals("Stop Server")) {
				server_ref.stopListening();
			} else if (command.equals("Abbrechen")) {
				frame2_ref.dispose();
			} else if (command.equals("Ok")) {
				ComConnect.portIdent_class = textFeld_ref.getText();
				FunknetzServer.writeIni(textFeld_ref.getText());
				frame2_ref.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass MeinButtonListener
	
	/**
	*	Zeichnet das Einstellungsmenue.
	*	@see #drawSettings()
	**/
	class SettingsMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			drawSettings();
		} //endmethod actionPerformed
	} //endclass SettingsMenuListener
	
} //endclass FunknetzServerGui