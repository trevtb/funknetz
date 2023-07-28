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
	-- PROJECT: Funknetz Server --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- SYSTEM ENVIRONMENT 						--
	-- OS			Ubuntu 9.10 (Linux 2.6.31)	--	
	-- SOFTWARE 	JDK 1.6.15   				--
	-- 	--
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Stellt das GUI fuer den Server bereit.
*	Der Server wird standardmaessig im GUI Modus gestartet.
*	Hier kann natuerlich auch der Serialport grafisch gesetzt werden.
*
* 	@version 0.3 von 11.2009
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
	private ImageIcon okIcon_ref;
	private ImageIcon abbrIcon_ref;
	
	// --- Konstruktoren
	
	/**
	*	Standardkonstruktor: erstellt eine neue Instanz und ruft die draw()-Methode auf.
	*	@see #draw()
	**/
	public FunknetzServerGui () {
		okIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/server/resource/ok.gif"));
		abbrIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/server/resource/abbrechen.gif"));
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
		frame_ref = new JFrame("FunknetzServer v0.3");
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/server/resource/connect_s.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		frame_ref.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menubar_ref = new JMenuBar();							// Hier wird die Menueleiste erzeugt
		JMenu dateiMenu_ref = new JMenu("Datei");
		JMenu bearbMenu_ref = new JMenu("Bearbeiten");
		JMenu hilfeMenu_ref = new JMenu("Hilfe");
		JMenuItem beenden_ref = new JMenuItem("Beenden");
		beenden_ref.addActionListener(new MenuListener());
		JMenuItem settings_ref = new JMenuItem("Einstellungen");
		settings_ref.addActionListener(new MenuListener());
		JMenuItem info_ref = new JMenuItem("Info");
		info_ref.addActionListener(new MenuListener());
		dateiMenu_ref.add(beenden_ref);
		bearbMenu_ref.add(settings_ref);
		hilfeMenu_ref.add(info_ref);
		menubar_ref.add(dateiMenu_ref);
		menubar_ref.add(bearbMenu_ref);
		menubar_ref.add(hilfeMenu_ref);
		Box hintergrund_ref = new Box(BoxLayout.Y_AXIS);
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		GridLayout raster = new GridLayout(1,2);
		raster.setVgap(5);
		raster.setHgap(5);
		JPanel buttonBox_ref = new JPanel(raster);
		buttonBox_ref.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		Box textBox_ref = new Box(BoxLayout.X_AXIS);
		JLabel portLabel = new JLabel("Port:");
		ImageIcon portIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/server/resource/next.gif"));
		JLabel portIconLab_ref = new JLabel(portIcon_ref);
		textBox_ref.add(portLabel);
		textBox_ref.add(Box.createHorizontalStrut(87));
		textBox_ref.add(portIconLab_ref);
		textBox_ref.add(Box.createHorizontalStrut(3));
		portTextF_ref = new JTextField(10);
		portTextF_ref.setText("5000");
		textBox_ref.add(portTextF_ref);
		portTextF_ref.requestFocus();
		start_ref = new JButton("Start Server", okIcon_ref);
		start_ref.setActionCommand("Start Server");
		start_ref.addActionListener(new MeinButtonListener());
		stop_ref = new JButton("Stop Server", abbrIcon_ref);
		stop_ref.setActionCommand("Stop Server");
		stop_ref.addActionListener(new MeinButtonListener());
		buttonBox_ref.add(start_ref);
		buttonBox_ref.add(stop_ref);
		status_class = new JTextArea(10,22);
		status_class.setEditable(false);
		status_class.setLineWrap(true);
		scroller_class = new JScrollPane(status_class);
		scroller_class.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller_class.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller_class.setBorder(BorderFactory.createEtchedBorder());
		
		hintergrund_ref.add(textBox_ref);
		hintergrund_ref.add(Box.createVerticalStrut(5));
		hintergrund_ref.add(buttonBox_ref);
		hintergrund_ref.add(scroller_class);
		
		frame_ref.setJMenuBar(menubar_ref);
		frame_ref.getContentPane().add(hintergrund_ref);
		frame_ref.pack();
		frame_ref.setResizable(false);
		frame_ref.setVisible(true);
	} //endmethod draw
	
	/**
	*	Zeichnet das Settings-Menue des FunknetzServer GUIs.
	*	Hier kann der Serialport gesetzt werden, standardmaessig
	*	wird der zuletzt benutzte Port geladen.
	**/
	public void drawSettings() {
		
		frame2_ref = new JFrame("Einstellungen");
		ImageIcon settingsIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/server/resource/einstellungen_s.gif"));
		frame2_ref.setIconImage(settingsIcon_ref.getImage());
		frame2_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel hauptPanel_ref = new JPanel();
		hauptPanel_ref.setLayout(new BorderLayout());
		
		JPanel hintergrund_ref = new JPanel();
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel zwischenPanel_ref = new JPanel();
		TitledBorder title_ref = BorderFactory.createTitledBorder("Serial Port Einstellungen");
		zwischenPanel_ref.setBorder(title_ref);
		
		JLabel infoLabel_ref = new JLabel("Waehlen Sie den Port aus, an den die Funknetz-Box angeschlossen ist.");
		
		JLabel label_ref = new JLabel("Serialport: ");
		textFeld_ref = new JTextField(10);
		textFeld_ref.setText(FunknetzServer.readIni());
		ImageIcon settingIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/server/resource/next.gif"));
		JLabel settingIconLab_ref = new JLabel(settingIcon_ref);
		zwischenPanel_ref.add(label_ref);
		zwischenPanel_ref.add(settingIconLab_ref);
		zwischenPanel_ref.add(textFeld_ref);
		
		JPanel southBox_ref = new JPanel();
		southBox_ref.setLayout(new BorderLayout());
		
		JPanel buttonBox_ref = new JPanel();
		JButton okBut_ref = new JButton("Ok", okIcon_ref);
		okBut_ref.setActionCommand("Ok");
		okBut_ref.addActionListener(new MeinButtonListener());
		JButton cancelBut_ref = new JButton("Abbrechen", abbrIcon_ref);
		cancelBut_ref.setActionCommand("Abbrechen");
		cancelBut_ref.addActionListener(new MeinButtonListener());
		buttonBox_ref.add(okBut_ref);
		buttonBox_ref.add(cancelBut_ref);
		
		southBox_ref.add(BorderLayout.CENTER, buttonBox_ref);
		southBox_ref.add(BorderLayout.NORTH, infoLabel_ref);
		
		hauptPanel_ref.add(BorderLayout.CENTER, zwischenPanel_ref);
		hauptPanel_ref.add(BorderLayout.SOUTH, southBox_ref);
		
		hintergrund_ref.add(hauptPanel_ref);
		
		frame2_ref.getContentPane().add(hintergrund_ref);
		frame2_ref.pack();
		frame2_ref.setResizable(false);
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
	class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			if (ev_ref.getActionCommand().equals("Beenden")) {
				System.exit(0);
			} else if (ev_ref.getActionCommand().equals("Einstellungen")) {
				drawSettings();
			} else if (ev_ref.getActionCommand().equals("Info")) {
				ImageIcon icon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/server/resource/connect.gif"));
				JOptionPane.showMessageDialog(frame_ref, "Funknetz Server v0.3\n\n( c ) Tobias Burkard 2009\nhttp://funknetz.sourceforge.net", "Info", JOptionPane.INFORMATION_MESSAGE, icon_ref);
			} //endif
		} //endmethod actionPerformed
	} //endclass SettingsMenuListener
	
} //endclass FunknetzServerGui