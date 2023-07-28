package de.funknetz.client;

// --- Importe
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.*;


/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: TimeEvents --
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
	
/**	Stellt das GUI-Interface fuer den TimeServer bereit.
* 	Eine Instanz der Klasse TimeEvents stellt ein
*	GUI-Interface fuer den TimeServer zur Verfuegung.
*	Es koennen individuelle Zeitereignisse gesetzt werden.
*	Zu beginn wird die aktuelle Tabelle mit Ereignissen vom Server abgefragt.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class TimeEvents {
	
	// --- Attribute
	private JFrame frame_ref;					// Der Frame, welcher das GUI fasst.
	private String[][] felderMatrix_ref;				// der Inhalt der Tabelle in String-Form.
	private DefaultTableModel tableMod_ref;			// die Tabelle, welche die Zeitereignisse haelt.
	private JTable table_ref;						// Die Tabelle im GUI
	private ObjectOutputStream oos_ref;			// Ausgabestrom fuer die Serververbindung
	private ObjectInputStream ois_ref;				// Eingabestrom fuer die Serververbindung
	private String commandType_ref;				// Serverbefehl (set/get)
	private JComboBox tagBox_ref;				// Fasst den zu setzenden Tag
	private JComboBox monatBox_ref;				// Fasst den zu setzenden Monat
	private JComboBox jahrBox_ref;				// Fasst das zu setzende Jahr
	private JComboBox hourBox_ref;				// Fasst die zu setzende Stunde
	private JComboBox minuteBox_ref;				// Fasst die zu setzenden Minuten
	private JTextField nameF_ref;					// Fasst den zu setzenden Namen/Bezeichner
	private JComboBox kanalBox_ref;				// Fasst den zu setzenden Kanal
	private JComboBox schalterBox_ref;			// Fasst den zu setzenden Schalter
	private JComboBox befehlBox_ref;				// Fasst den zu setzenden Befehl
	private JCheckBox dailyC_ref;					// Taegliches Intervall
	private JCheckBox weeklyC_ref;				// Woechentliches Intervall
	private JScrollPane scroller_ref;				// ScrollPane, welche die Tabelle fasst
	private static JTextField timeF_class;				// Serveruhr, wird zu beginn synchronisiert
	private static Calendar serverTime_class;		// Die aktuelle Serverzeit
	private static java.util.Timer serverTTimer_class;			// Der Timer fuer die Serverzeit
	private final String[] TABLENAMES = {"Datum", "Uhrzeit", "Name", "Kanal", "Schalter", "Befehl", "Intervall"};
	
	
	// --- Konstruktoren
	
	/**
	*	Standardkonstruktor: liest die aktuelle Ereignis-Tabelle vom Server ein.
	*	@see ServerConnector
	**/
	public TimeEvents() {
		timeF_class = new JTextField(7);
		serverTime_class = Calendar.getInstance();
		commandType_ref = "get";
		try { 
			Thread t_ref = new Thread(new ServerConnector());
			t_ref.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
		initializeServerTimer();
	} //endconstructor
	
	// --- Methoden
	
	/**
	*	Zeichnet das GUI-Interface.
	**/
	public void draw() {
		frame_ref = new JFrame("Zeitereignisse");
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/zeitsteuerung_s.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		frame_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Box hintergrund_ref = new Box(BoxLayout.Y_AXIS);
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		table_ref = new JTable();
		tableMod_ref = (DefaultTableModel) new MyTableModel(felderMatrix_ref, TABLENAMES);
		table_ref.setModel(tableMod_ref);
		((DefaultTableCellRenderer)table_ref.getDefaultRenderer(table_ref.getColumnClass(0))).setHorizontalAlignment(SwingConstants.CENTER);
		scroller_ref = new JScrollPane(table_ref);
		scroller_ref.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller_ref.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		TitledBorder border_ref = new TitledBorder("Zeit-Ereignisse");
		scroller_ref.setBorder(border_ref);
		
		Box buttonBox1_ref = new Box (BoxLayout.X_AXIS);
		ImageIcon einlesenBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/einlesen.gif"));
		JButton einlesenB_ref = new JButton("Einlesen", einlesenBIcon_ref);
		einlesenB_ref.setActionCommand("Einlesen");
		einlesenB_ref.addActionListener(new ButtonBox1Listener());
		ImageIcon bearbeitenBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/edit.gif"));
		JButton bearbeitenB_ref = new JButton("Bearbeiten", bearbeitenBIcon_ref);
		bearbeitenB_ref.setActionCommand("Bearbeiten");
		bearbeitenB_ref.addActionListener(new ButtonBox1Listener());
		ImageIcon loeschenBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/delete.gif"));
		JButton loeschenB_ref = new JButton("Loeschen", loeschenBIcon_ref);
		loeschenB_ref.setActionCommand("Loeschen");
		loeschenB_ref.addActionListener(new ButtonBox1Listener());
		Box timeBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon timeIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/next.gif"));
		JLabel timeIconLab_ref = new JLabel(timeIcon_ref);
		JPanel timePan_ref = new JPanel();
		JLabel timeL_ref = new JLabel("Serverzeit:");
		timePan_ref.add(timeL_ref);
		timePan_ref.add(Box.createHorizontalStrut(5));
		timePan_ref.add(timeIconLab_ref);
		timePan_ref.add(Box.createHorizontalStrut(1));
		timePan_ref.add(timeF_class);
		timeBox_ref.add(Box.createHorizontalGlue());
		timeBox_ref.add(timePan_ref);
		buttonBox1_ref.add(einlesenB_ref);
		buttonBox1_ref.add(Box.createHorizontalStrut(5));
		buttonBox1_ref.add(bearbeitenB_ref);
		buttonBox1_ref.add(Box.createHorizontalStrut(5));
		buttonBox1_ref.add(loeschenB_ref);
		buttonBox1_ref.add(Box.createHorizontalGlue());
		buttonBox1_ref.add(timeBox_ref);
		
		Box creatorPanMain_ref = new Box(BoxLayout.Y_AXIS);
		TitledBorder creatorB_ref = new TitledBorder("Ereignis");
		creatorPanMain_ref.setBorder(creatorB_ref);
		
		JPanel creatorPan_ref = new JPanel();
		creatorPan_ref.setLayout(new BoxLayout (creatorPan_ref, BoxLayout.X_AXIS));
		
		Box creatorPan_tag_ref = new Box(BoxLayout.Y_AXIS);
		JLabel tagL_ref = new JLabel("Tag");
		Box creatorPan_tag_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_tag_L_ref.add(tagL_ref);
		creatorPan_tag_L_ref.add(Box.createHorizontalGlue());
		tagBox_ref = new JComboBox();
		for (int i = 1; i < 32; i++) {
			tagBox_ref.addItem(i + "");
		} //endfor
		creatorPan_tag_ref.add(creatorPan_tag_L_ref);
		creatorPan_tag_ref.add(tagBox_ref);
		
		Box creatorPan_monat_ref = new Box(BoxLayout.Y_AXIS);
		JLabel monatL_ref = new JLabel("Monat");
		Box creatorPan_monat_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_monat_L_ref.add(monatL_ref);
		creatorPan_monat_L_ref.add(Box.createHorizontalGlue());
		monatBox_ref = new JComboBox();
		for (int i = 1; i < 13; i++) {
			monatBox_ref.addItem(i + "");
		} //endfor
		creatorPan_monat_ref.add(creatorPan_monat_L_ref);
		creatorPan_monat_ref.add(monatBox_ref);
		
		Box creatorPan_jahr_ref = new Box(BoxLayout.Y_AXIS);
		JLabel jahrL_ref = new JLabel("Jahr");
		Box creatorPan_jahr_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_jahr_L_ref.add(jahrL_ref);
		creatorPan_jahr_L_ref.add(Box.createHorizontalGlue());
		jahrBox_ref = new JComboBox();
		for (int i = 2009; i < 2101; i++) {
			jahrBox_ref.addItem(i + "");
		} //endfor
		creatorPan_jahr_ref.add(creatorPan_jahr_L_ref);
		creatorPan_jahr_ref.add(jahrBox_ref);
		
		Box creatorPan_uhrzeit_ref = new Box(BoxLayout.Y_AXIS);
		JLabel uhrzeitL_ref = new JLabel("Uhrzeit");
		Box creatorPan_uhrzeit_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_uhrzeit_L_ref.add(uhrzeitL_ref);
		creatorPan_uhrzeit_L_ref.add(Box.createHorizontalGlue());
		Box creatorPan_uhrzeitBox_ref = new Box(BoxLayout.X_AXIS);
		hourBox_ref = new JComboBox();
		for (int i = 0; i < 24; i++) {
			if (i < 10) {
				hourBox_ref.addItem("0"+i);
			} else {
				hourBox_ref.addItem(i + "");
			} //endif
		} //endfor
		minuteBox_ref = new JComboBox();
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				minuteBox_ref.addItem("0"+i);
			} else {
				minuteBox_ref.addItem(i+"");
			} //endif
		} //endfor
		JLabel seperatorL_ref = new JLabel(":");
		creatorPan_uhrzeitBox_ref.add(hourBox_ref);
		creatorPan_uhrzeitBox_ref.add(Box.createHorizontalStrut(1));
		creatorPan_uhrzeitBox_ref.add(seperatorL_ref);
		creatorPan_uhrzeitBox_ref.add(Box.createHorizontalStrut(1));
		creatorPan_uhrzeitBox_ref.add(minuteBox_ref);
		creatorPan_uhrzeit_ref.add(creatorPan_uhrzeit_L_ref);
		creatorPan_uhrzeit_ref.add(creatorPan_uhrzeitBox_ref);
		
		Box creatorPan_name_ref = new Box(BoxLayout.Y_AXIS);
		JLabel nameL_ref = new JLabel("Name");
		nameF_ref = new JTextField("-----", 10);
		nameF_ref.addFocusListener(new NameMarkerListener());
		nameF_ref.setHorizontalAlignment(JTextField.CENTER);
		creatorPan_name_ref.add(nameL_ref);
		creatorPan_name_ref.add(nameF_ref);
		
		Box creatorPan_kanal_ref = new Box(BoxLayout.Y_AXIS);
		JLabel kanalL_ref = new JLabel("Kanal");
		Box creatorPan_kanal_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_kanal_L_ref.add(kanalL_ref);
		creatorPan_kanal_L_ref.add(Box.createHorizontalGlue());
		kanalBox_ref = new JComboBox();
		for (int i = 1; i < 5; i++) {
			kanalBox_ref.addItem(i + "");
		} //endfor
		creatorPan_kanal_ref.add(creatorPan_kanal_L_ref);
		creatorPan_kanal_ref.add(kanalBox_ref);
		
		Box creatorPan_schalter_ref = new Box(BoxLayout.Y_AXIS);
		JLabel schalterL_ref = new JLabel("Schalter");
		Box creatorPan_schalter_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_schalter_L_ref.add(schalterL_ref);
		creatorPan_schalter_L_ref.add(Box.createHorizontalGlue());
		schalterBox_ref = new JComboBox();
		for (int i = 1; i < 4; i++) {
			schalterBox_ref.addItem(i + "");
		} //endfor
		creatorPan_schalter_ref.add(creatorPan_schalter_L_ref);
		creatorPan_schalter_ref.add(schalterBox_ref);
		
		Box creatorPan_befehl_ref = new Box(BoxLayout.Y_AXIS);
		JLabel befehlL_ref = new JLabel("Befehl");
		Box creatorPan_befehl_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_befehl_L_ref.add(befehlL_ref);
		creatorPan_befehl_L_ref.add(Box.createHorizontalGlue());
		befehlBox_ref = new JComboBox();
		befehlBox_ref.addItem("ON");
		befehlBox_ref.addItem("OFF");
		creatorPan_befehl_ref.add(creatorPan_befehl_L_ref);
		creatorPan_befehl_ref.add(befehlBox_ref);
		
		Box creatorPan_intervall_ref = new Box(BoxLayout.Y_AXIS);
		JLabel intervallL_ref = new JLabel("Intervall");
		Box creatorPan_intervall_L_ref = new Box(BoxLayout.X_AXIS);
		creatorPan_intervall_L_ref.add(intervallL_ref);
		creatorPan_intervall_L_ref.add(Box.createHorizontalGlue());
		Box creatorPan_intervall_cB_ref = new Box(BoxLayout.X_AXIS);
		dailyC_ref = new JCheckBox("T");
		dailyC_ref.addActionListener(new IntervallListener());
		weeklyC_ref = new JCheckBox("W");
		weeklyC_ref.addActionListener(new IntervallListener());
		creatorPan_intervall_cB_ref.add(dailyC_ref);
		creatorPan_intervall_cB_ref.add(weeklyC_ref);
		creatorPan_intervall_ref.add(creatorPan_intervall_L_ref);
		creatorPan_intervall_ref.add(creatorPan_intervall_cB_ref);
		
		creatorPan_ref.add(creatorPan_tag_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_monat_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_jahr_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_uhrzeit_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_name_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_kanal_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_schalter_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_befehl_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		creatorPan_ref.add(creatorPan_intervall_ref);
		creatorPan_ref.add(Box.createHorizontalStrut(10));
		
		Box creatorPan_buttonBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon jetztBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/jetzt.gif"));
		JButton cpJETZTbutton_ref = new JButton("Jetzt!", jetztBIcon_ref);
		cpJETZTbutton_ref.setActionCommand("Jetzt!");
		cpJETZTbutton_ref.addActionListener(new CreatorButtonListener());
		ImageIcon zuruecksBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/zuruecksetzen.gif"));
		JButton cpZURUECKSETZENbutton_ref = new JButton("Zuruecksetzen", zuruecksBIcon_ref);
		cpZURUECKSETZENbutton_ref.setActionCommand("Zuruecksetzen");
		cpZURUECKSETZENbutton_ref.addActionListener(new CreatorButtonListener());
		ImageIcon neuBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/add.gif"));
		JButton cpNEUbutton_ref = new JButton("Neu", neuBIcon_ref);
		cpNEUbutton_ref.setActionCommand("Neu");
		cpNEUbutton_ref.addActionListener(new CreatorButtonListener());
		ImageIcon saveBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/save.gif"));
		JButton cpSAVEbutton_ref = new JButton("Speichern", saveBIcon_ref);
		cpSAVEbutton_ref.setActionCommand("Speichern");
		cpSAVEbutton_ref.addActionListener(new CreatorButtonListener());
		creatorPan_buttonBox_ref.add(cpJETZTbutton_ref);
		creatorPan_buttonBox_ref.add(Box.createHorizontalGlue());
		creatorPan_buttonBox_ref.add(cpZURUECKSETZENbutton_ref);
		creatorPan_buttonBox_ref.add(Box.createHorizontalStrut(5));
		creatorPan_buttonBox_ref.add(cpNEUbutton_ref);
		creatorPan_buttonBox_ref.add(Box.createHorizontalStrut(5));
		creatorPan_buttonBox_ref.add(cpSAVEbutton_ref);
		
		creatorPanMain_ref.add(creatorPan_ref);
		creatorPanMain_ref.add(Box.createVerticalStrut(10));
		creatorPanMain_ref.add(creatorPan_buttonBox_ref);
		
		Box buttonBox2_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon closeBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/zurueck.gif"));
		JButton closeB_ref = new JButton("Zurueck", closeBIcon_ref);
		closeB_ref.setActionCommand("Zurueck");
		closeB_ref.addActionListener(new ButtonBox2Listener());
		ImageIcon takeoBIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/ok.gif"));
		JButton takeoB_ref = new JButton("Uebernehmen", takeoBIcon_ref);
		takeoB_ref.setActionCommand("Uebernehmen");
		takeoB_ref.addActionListener(new ButtonBox2Listener());
		buttonBox2_ref.add(closeB_ref);
		buttonBox2_ref.add(Box.createHorizontalGlue());
		buttonBox2_ref.add(takeoB_ref);
		
		hintergrund_ref.add(scroller_ref);
		hintergrund_ref.add(Box.createVerticalStrut(2));
		hintergrund_ref.add(buttonBox1_ref);
		hintergrund_ref.add(Box.createVerticalStrut(5));
		hintergrund_ref.add(creatorPanMain_ref);
		hintergrund_ref.add(Box.createVerticalStrut(20));
		hintergrund_ref.add(buttonBox2_ref);
		
		frame_ref.getContentPane().add(hintergrund_ref);
		frame_ref.pack();
		frame_ref.setVisible(true);
	} //endmethod draw
	
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
			} catch (Exception ex_ref) {
				ex_ref.printStackTrace();
				JOptionPane.showMessageDialog(frame_ref, "Es trat ein Problem mit der Verbindung zum Server auf, versuchen Sie es erneut.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
			} //endtry
		} else if (commandType_ref.equals("set")) {
			try {
				oos_ref.writeObject("set");
				oos_ref.writeObject(felderMatrix_ref);
				oos_ref.writeObject("done");
				JOptionPane.showMessageDialog(frame_ref, "Die Tabelle wurde erfolgreich uebertragen.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(frame_ref, "Es trat ein Problem mit der Verbindung zum Server auf, versuchen Sie es erneut.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
			} //endtry
		} //endif
	} //endmethod connect
	
	/**
	*	Erstellt den Timer fuer die Serverzeianzeige.
	**/
	private void initializeServerTimer() {
		if (serverTTimer_class == null) {
			serverTTimer_class = new java.util.Timer();
			serverTTimer_class.scheduleAtFixedRate(new MyServerTimer(), serverTime_class.getTime(), 1000L);
		} //endif
	} //endmethod initializeServerTimer
		
	
	// --- Innere Klassen
	
	/**
	*	Das Timerobjekt fuer die Serverzeitabfrage.
	**/
	class MyServerTimer extends TimerTask {
		public void run() {
			serverTime_class.add(Calendar.SECOND, 1);
			timeF_class.setText(String.format("%tT", serverTime_class));
		} //endmethod run
	} //endclass EventTimer
	
	/**
	*	Das TableModel fuer die Tabelle mit Zeitereignissen.
	**/
	class MyTableModel extends DefaultTableModel {

		/**
		 * 		Die Eindeutige Klassen-ID: zufaellig generierter Wert.
		 */
		private static final long serialVersionUID = 100330981635722317L;

		/**
		*	Uebergibt die uebergebenen Werte an den Konstruktor der Superklasse.
		*	@param werte_ref die zu setzenden Daten fuer die Tabelle
		*	@param bezeichner_ref die Bezeichner fuer die einzelnen Spalten der Tabelle
		**/
		public MyTableModel(Object[][] werte_ref, Object[] bezeichner_ref) {
			super(werte_ref, bezeichner_ref);
		} //endconstructor
		
		/**
		*	Verhindert das Editieren der Zellen.
		**/
		public boolean isCellEditable(int row, int column) {
			return false;
		} //endmethod isCellEditable
	} //endclass MyTableModel

		// --- Runnable Objekte
	
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
				serverTime_class = tempC_ref;
				connect();
				ois_ref.close();
				oos_ref.close();
				s_ref.close();
				if (commandType_ref.equals("get") && felderMatrix_ref != null) {
					tableMod_ref.setDataVector(felderMatrix_ref, TABLENAMES);
					tableMod_ref.fireTableDataChanged();
				} else if (commandType_ref.equals("get") && felderMatrix_ref == null) {
					JOptionPane.showMessageDialog(frame_ref, "Dem Server liegen aktuell keine Zeitereignisse vor.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
					tableMod_ref.setRowCount(0);
					tableMod_ref.fireTableDataChanged();
				} //endif
			} catch (Exception ex_ref) {
				ex_ref.printStackTrace();
				JOptionPane.showMessageDialog(frame_ref, "Es trat ein Problem mit der Verbindung zum Server auf, versuchen Sie es erneut.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
			} //endtry
		} //endmethod run
	} //endclass ServerConnector
	
		// --- Listener 
	
	/**
	*	Verarbeitet die oberen JButtons.
	*	Dieser Listener verwaltet die Buttons "Einlesen", "Bearbeiten" und "Loeschen".
	*	Bei einem klick auf "Einlesen" wird die Tabelle vom Server abgefragt.
	*	Bei einem klick auf "Bearbeiten" wird der aktuelle Eintrag als Ereignis zum bearbeiten eingelesen.
	*	Bei einem klick auf "Loeschen" wird der aktuelle Eintrag aus der Tabelle geloescht.
	**/
	class ButtonBox1Listener implements ActionListener {
		public void actionPerformed(ActionEvent e_ref) {
			if (e_ref.getActionCommand().equals("Einlesen")) {
				commandType_ref = "get";
				try { 
					Thread t_ref = new Thread(new ServerConnector());
					t_ref.start();
				} catch (Exception ex) {
					ex.printStackTrace();
				} //endtry
			} else if (e_ref.getActionCommand().equals("Bearbeiten")) {
					String[] selectedItem_ref = null;
					int index = table_ref.getSelectedRow();
					if (index > -1) {
						selectedItem_ref = felderMatrix_ref[index];
						String[] date_ref = selectedItem_ref[0].split("\\.");
						tagBox_ref.setSelectedIndex(Integer.parseInt(date_ref[0])-1);
						monatBox_ref.setSelectedIndex(Integer.parseInt(date_ref[1])-1);
						jahrBox_ref.setSelectedIndex(Integer.parseInt(date_ref[2]) - 2009);
						String[] time_ref = selectedItem_ref[1].split(":");
						hourBox_ref.setSelectedIndex(Integer.parseInt(time_ref[0]));
						minuteBox_ref.setSelectedIndex(Integer.parseInt(time_ref[1]));
						nameF_ref.setText(selectedItem_ref[2]);
						kanalBox_ref.setSelectedIndex(Integer.parseInt(selectedItem_ref[3]) - 1);
						schalterBox_ref.setSelectedIndex(Integer.parseInt(selectedItem_ref[4]) - 1);
						if (selectedItem_ref[5].equals("ON")) {
							befehlBox_ref.setSelectedIndex(0);
						} else if (selectedItem_ref[5].equals("OFF")) {
							befehlBox_ref.setSelectedIndex(1);
						} //endif
						if (selectedItem_ref[6].equals("T")) {
							dailyC_ref.setSelected(true);
							weeklyC_ref.setSelected(false);
						} else if (selectedItem_ref[6].equals("W")) {
							dailyC_ref.setSelected(false);
							weeklyC_ref.setSelected(true);
						} //endif
					} else if (index == -1) {
						JOptionPane.showMessageDialog(frame_ref, "Sie muessen einen Eintrag auswaehlen,\nbevor Sie diesen bearbeiten koennen.", "Hinweis", JOptionPane.WARNING_MESSAGE);
					} //endif
				} else if (e_ref.getActionCommand().equals("Loeschen")) {
					int index = table_ref.getSelectedRow();
					if (index > -1 && felderMatrix_ref[0] != null) {
						String[][] tableTemp_ref = new String[felderMatrix_ref.length - 1][];
						felderMatrix_ref[index][0] = null;
						int counter = 0;
						for (int i = 0; i < felderMatrix_ref.length; i++) {
							if (felderMatrix_ref[i][0] != null && counter < tableTemp_ref.length) {
								tableTemp_ref[counter] = felderMatrix_ref[i];
								counter++;
							} //endif
						} //endfor
						felderMatrix_ref = tableTemp_ref;
						tableMod_ref.setDataVector(felderMatrix_ref, TABLENAMES);
						tableMod_ref.fireTableDataChanged();
					} else if (index == -1) {
						JOptionPane.showMessageDialog(frame_ref, "Sie muessen einen Eintrag auswaehlen,\nbevor Sie diesen loeschen koennen.", "Hinweis", JOptionPane.WARNING_MESSAGE);
					} //endif
				} //endif
		} //endmethod actionPerformed
	} //endclass ButtonBox1Listener
	
	/**
	*	Verhindert das setzen mehrere Intervall-Checkboxen auf einmal.
	*	Durch click auf einen Checkbox wird der Listener aktiviert und 
	*	deselektiert dann die andere der beiden Checkboxen.
	**/
	class IntervallListener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			String command_ref = ev_ref.getActionCommand();
			if (command_ref.equals("T")) {
				weeklyC_ref.setSelected(false);
			} else if (command_ref.equals("W")) {
				dailyC_ref.setSelected(false);
			} //endif
		} //endmethod actionPerformed
	} //endclass IntervallListener
	
	/**
	*	Kuemmert sich um die Buttons im Ereignisrahmen ("Neu" und "Speichern".
	*	"Neu" speichert das aktuelle Ereignis als neuen Eintrag in der Tabelle ab.
	*	"Speichern" ueberschreibt den aktuell in der Tabelle markierten Eintrag.
	**/
	class CreatorButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent e_ref) {
			if (e_ref.getActionCommand().equals("Jetzt!")) {
				Calendar c_ref = Calendar.getInstance();
				tagBox_ref.setSelectedIndex((c_ref.get(Calendar.DAY_OF_MONTH))-1);
				monatBox_ref.setSelectedIndex(c_ref.get(Calendar.MONTH));
				int year_ref = c_ref.get(Calendar.YEAR);
				if (year_ref >= 2009 && year_ref <= 2100) {
					year_ref = year_ref - 2009;
				} else {
					year_ref = 0;
				} //endif
				jahrBox_ref.setSelectedIndex(year_ref);
				String[] zeit_ref = (String.format("%tT", c_ref)).split(":");
				hourBox_ref.setSelectedIndex(Integer.parseInt(zeit_ref[0]));
				minuteBox_ref.setSelectedIndex(Integer.parseInt(zeit_ref[1]));
			} else if (e_ref.getActionCommand().equals("Zuruecksetzen")) {
				tagBox_ref.setSelectedIndex(0);
				monatBox_ref.setSelectedIndex(0);
				jahrBox_ref.setSelectedIndex(0);
				minuteBox_ref.setSelectedIndex(0);
				hourBox_ref.setSelectedIndex(0);
				nameF_ref.setText("---");
				kanalBox_ref.setSelectedIndex(0);
				schalterBox_ref.setSelectedIndex(0);
				befehlBox_ref.setSelectedIndex(0);
				dailyC_ref.setSelected(false);
				weeklyC_ref.setSelected(false);
			} else if (e_ref.getActionCommand().equals("Neu")) {
				String[] newEvent_ref = new String[7];
				newEvent_ref[0] = (String) tagBox_ref.getSelectedItem() + "." + (String) monatBox_ref.getSelectedItem() + "." + (String) jahrBox_ref.getSelectedItem();
				newEvent_ref[1] = (String) hourBox_ref.getSelectedItem() + ":" + (String) minuteBox_ref.getSelectedItem();
				newEvent_ref[2] = nameF_ref.getText();
				newEvent_ref[3] = (String) kanalBox_ref.getSelectedItem();
				newEvent_ref[4] = (String) schalterBox_ref.getSelectedItem();
				newEvent_ref[5] = (String) befehlBox_ref.getSelectedItem();
				if (dailyC_ref.isSelected() && !weeklyC_ref.isSelected()) {
					newEvent_ref[6] = "T";
				} else if (!dailyC_ref.isSelected() && weeklyC_ref.isSelected()) {
					newEvent_ref[6] = "W";
				} else if (!dailyC_ref.isSelected() && !weeklyC_ref.isSelected()) {
					newEvent_ref[6] = "---";
				} //endif
				String[][] tableTemp_ref;
				if (tableMod_ref.getRowCount() != 0) {
					tableTemp_ref = new String[felderMatrix_ref.length + 1][];
					for (int i = 0; i < felderMatrix_ref.length; i++) {
						tableTemp_ref[i] = felderMatrix_ref[i].clone();
					} //endfor
				} else {
					tableTemp_ref = new String[1][];
				} //endif
				tableTemp_ref[tableTemp_ref.length - 1] = newEvent_ref.clone();
				felderMatrix_ref = tableTemp_ref;
				tableMod_ref.setDataVector(felderMatrix_ref, TABLENAMES);
				tableMod_ref.fireTableDataChanged();
			} else if (e_ref.getActionCommand().equals("Speichern")) {
				int index = table_ref.getSelectedRow();
				if (index == -1) {
					JOptionPane.showMessageDialog(frame_ref, "Sie muessen zuerst einen Eintrag markieren,\nbevor Sie diesen mit ihrem neuen Ereignis\nueberschreiben koennen.", "Fehler", JOptionPane.ERROR_MESSAGE);
				} else if (index > -1) {
					String[] newEvent_ref = new String[7];
					newEvent_ref[0] = (String) tagBox_ref.getSelectedItem() + "." + (String) monatBox_ref.getSelectedItem() + "." + (String) jahrBox_ref.getSelectedItem();
					newEvent_ref[1] = (String) hourBox_ref.getSelectedItem() + ":" + (String) minuteBox_ref.getSelectedItem();
					newEvent_ref[2] = nameF_ref.getText();
					newEvent_ref[3] = (String) kanalBox_ref.getSelectedItem();
					newEvent_ref[4] = (String) schalterBox_ref.getSelectedItem();
					newEvent_ref[5] = (String) befehlBox_ref.getSelectedItem();
					if (dailyC_ref.isSelected() && !weeklyC_ref.isSelected()) {
						newEvent_ref[6] = "T";
					} else if (!dailyC_ref.isSelected() && weeklyC_ref.isSelected()) {
						newEvent_ref[6] = "W";
					} else if (!dailyC_ref.isSelected() && !weeklyC_ref.isSelected()) {
						newEvent_ref[6] = "---";
					} //endif
					felderMatrix_ref[index] = newEvent_ref.clone();
					tableMod_ref.setDataVector(felderMatrix_ref, TABLENAMES);
					tableMod_ref.fireTableDataChanged();
				} //endif
			} //endif
		} //endmethod actionPerformed
	} //endclass CreatorButtonListener
	
	/**
	*	Kuemmert sich um die unteren beiden Buttons: "Zurueck" und "Uebernehmen".
	*	Bei einem Klick auf "Zurueck" wird das Zeitereignis-Fenster geschlossen.
	*	Bei einem Klick auf "Uebernehmen" werden die Zeitereignisse an den Server uebergeben.
	**/
	class ButtonBox2Listener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			if (ev_ref.getActionCommand().equals("Zurueck")) {
				frame_ref.dispose();
			} else if (ev_ref.getActionCommand().equals("Uebernehmen")) {
				//int isValid = 1;
				//for (int i = 0; i < tableMod_ref.getDataVector().size(); i++) {
				//	if (tableMod_ref.getValueAt(i,0) == null) {
				//		isValid = 0;
				//	} //endif
				//} //endfor
				//if (tableMod_ref.getDataVector().size() > 0 && isValid == 1) {
					commandType_ref = "set";
					try {
						Thread t_ref = new Thread(new ServerConnector());
						t_ref.start();
					} catch (Exception ex_ref) {
						JOptionPane.showMessageDialog(frame_ref, "Verbindungsfehler,\nbitte versuchen Sie es erneut.", "Fehler", JOptionPane.ERROR_MESSAGE);
					} //endtry
				//} else {
				//	JOptionPane.showMessageDialog(frame_ref, "Sie haben keinen Eintrag erstellt,\noder einen leeren Eintrag in der Liste.", "Fehler", JOptionPane.ERROR_MESSAGE);
				//} //endif
			} //endif
		} //endmethod actionPerformed
	} //endclass ButtonBox2Listener
	
	/**
	*	Markiert den Inhalt des Bezeichner-Textfeldes, falls dieses ausgewaehlt wird.
	**/
	public class NameMarkerListener implements FocusListener{
		public void focusGained (FocusEvent e_ref) {
			((JTextField)e_ref.getSource()).selectAll();
		} //endmethod focusGained
		
		public void focusLost (FocusEvent e_ref) {
		} //endmethod focusLost
	} //endclass NameMarkerListener
	
} //endclass TimeEvents