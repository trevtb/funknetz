package de.funknetz.client;

// --- Importe
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;

/*	---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
--| Copyright (c) by Tobias Burkard, 2009	      |--
---------------------------------------------------------------------------------
-- --
-- CLASS: Coffee --
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
*	Dies ist das Kaffee-Modul, ein spezielles Interface zum
*	Kaffee kochen. Es bietet die Moeglichkeit zum zeitgesteuerten
*	oder sofortigen Kaffee kochen und schaltet die Kaffeemaschine
*	nach Abschluss des Kochvorgangs wieder aus. Die Klasse wird vom
*	FunknetzClient instantiiert und anschließend auf ihr die draw()-
*	Methode aufgerufen.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class Coffee {
	// --- Attribute
	private JFrame frame_ref;					// Der Hauptframe
	private JFrame chooserFrame_ref;			// Frame fuer das Kaffeemaschinen-Auswahlmenue
	private Object[][] chooserList_ref;			// Die erzeugte Tabelle im Kaffeemaschinen-Auswahlmenue.
	private int[] selectedMachine_ref;			// Kanal und Schalter der zu verwendenden Kaffeemaschine
	private JTextField kanalText_ref;			// Textfeld fuer den Kanal der zu verwendenden Kaffeemaschine
	private JTextField schalterText_ref;		// Textfeld fuer den Schalter der zu verwendenden Kaffeemaschine
	private String status_ref;					// Status der Serveruebertragung
	private TimeEventHelper timeHelper_ref;		// Helfer-Objekt fuer die Serveruebertragung
	private String[][] timeEvents_ref;			// Liste mit Zeitereignissen
	private JComboBox zeitWComb1_ref;			// Stunden (Wochenende)
	private JComboBox zeitWComb2_ref;			// Minuten (Wochenende)
	private JComboBox zeitTComb1_ref;			// Stunden (Werktags)
	private JComboBox zeitTComb2_ref;			// Minuten (Werktags)
	private JComboBox tassenWComb1_ref;			// Anzahl Tassen (Wochenende)
	private JComboBox tassenTComb1_ref;			// Anzahl Tassen (Werktags)
	private JComboBox[] tassenDur_ref;			// Fasst die JComboBoxes fuer die Dauer der einzelnen Kochvorgaenge
	private String[][] coffeeEventArray_ref;	// Liste mit Zeitereignissen fuer die Kaffeemaschine
	
	// --- Konstruktoren
	public Coffee() {
		tassenDur_ref = new JComboBox[4];
		timeHelper_ref = new TimeEventHelper();
		updateTimeEvents();
	} //endconstructor
	
	// --- Methoden
	/**
	 * Aktualisiert das GUI mit Hilfe der Werte aus timeEvents_ref.
	 */
	private void updateGui() {
		if (timeEvents_ref != null) {
			for (int i = 0; i < timeEvents_ref.length; i++) {
				if (timeEvents_ref[i][7].equals("coffee") && timeEvents_ref[i][5].equals("ON")) {
					if (timeEvents_ref[i][6].equals("WT")) {
						String[] tempTime_ref = timeEvents_ref[i][1].split(":");
						System.out.println("Taeglich: " + tempTime_ref[0]+":"+tempTime_ref[1]);
						zeitTComb1_ref.setSelectedIndex(Integer.parseInt(tempTime_ref[0]));
						zeitTComb2_ref.setSelectedIndex(Integer.parseInt(tempTime_ref[1]));
					} else if (timeEvents_ref[i][6].equals("WE")) {
						String[] tempTime_ref = timeEvents_ref[i][1].split(":");
						System.out.println("Taeglich: " + tempTime_ref[0]+":"+tempTime_ref[1]);
						zeitWComb1_ref.setSelectedIndex(Integer.parseInt(tempTime_ref[0]));
						zeitTComb2_ref.setSelectedIndex(Integer.parseInt(tempTime_ref[1]));
					} //endif
				} //endif
			} //endfor
		} //endif
		
		Device[][] deviceList_ref = FunknetzClient.getDevices();
		if (deviceList_ref == null) {
			deviceList_ref = new Device[4][3];
			for (int i=0; i<deviceList_ref.length;i++) {
				for (int j=0; j<deviceList_ref[i].length; j++) {
					deviceList_ref[i][j] = new Device("user", "K"+(i+1)+" "+"Schalter "+(j+1));
				} //endfor
			} //endfor
		} //endif
		System.out.println("");
		for (int elem : selectedMachine_ref) {
			System.out.print(elem + " ");
		} //endfor
		String[] devInfo_ref = deviceList_ref[selectedMachine_ref[0]][selectedMachine_ref[1]].getDeviceInfo();
		if (!(devInfo_ref[0].equals("none"))) {
			for (int i=0; i<tassenDur_ref.length;i++) {
				tassenDur_ref[i].setSelectedIndex((Integer.parseInt(devInfo_ref[i]))/2 -1);
			} //endfor
		} //endif
	} //endmethod updateTimeEvents
	
	/**
	 * Aktualisiert die Liste mit Zeitereignissen vom Server.
	 */
	private void updateTimeEvents() {
		Object[] tempA_ref = timeHelper_ref.getTimeEvents();
		status_ref = (String)tempA_ref[0];
		timeEvents_ref = (String[][])tempA_ref[2];
		
		if (status_ref.equals("connection_error")) {
			JOptionPane.showMessageDialog(frame_ref, "Es trat ein Problem mit der Verbindung zum Server auf, versuchen Sie es erneut.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
		} //endif
	} //endmethod updateTimeEvents
	
	/**
	 * Erzeugt aus den im GUI gesetzten Einstellungen das Array mit
	 * Zeitereignissen fuer die Kaffeemaschine.
	 */
	private void createSendCoffeeEvents() {
		coffeeEventArray_ref = new String[4][8];
		Calendar tempCalT_ref = Calendar.getInstance();
		tempCalT_ref.set(Calendar.HOUR_OF_DAY,(Integer.parseInt((String)zeitTComb1_ref.getSelectedItem())));
		tempCalT_ref.set(Calendar.MINUTE,(Integer.parseInt((String)zeitTComb2_ref.getSelectedItem())));
		Calendar tempCalW_ref = Calendar.getInstance();
		tempCalW_ref.set(Calendar.HOUR_OF_DAY,(Integer.parseInt((String)zeitWComb1_ref.getSelectedItem())));
		tempCalW_ref.set(Calendar.MINUTE,(Integer.parseInt((String)zeitWComb2_ref.getSelectedItem())));
		
		int durW_ref = 0;
		int durT_ref = 0;
		int tasW_ref = Integer.parseInt((String)tassenWComb1_ref.getSelectedItem());
		int tasT_ref = Integer.parseInt((String)tassenTComb1_ref.getSelectedItem());
		
		if (tasW_ref == 2) {
			durW_ref = Integer.parseInt((String)tassenDur_ref[0].getSelectedItem());
		} else if (tasW_ref == 4) {
			durW_ref = Integer.parseInt((String)tassenDur_ref[1].getSelectedItem());
		} else if (tasW_ref == 6) {
			durW_ref = Integer.parseInt((String)tassenDur_ref[2].getSelectedItem());
		} else if (tasW_ref == 8) {
			durW_ref = Integer.parseInt((String)tassenDur_ref[3].getSelectedItem());
		} //endif	
		
		if (tasT_ref == 2) {
			durT_ref = Integer.parseInt((String)tassenDur_ref[0].getSelectedItem());
		} else if (tasW_ref == 4) {
			durT_ref = Integer.parseInt((String)tassenDur_ref[1].getSelectedItem());
		} else if (tasW_ref == 6) {
			durT_ref = Integer.parseInt((String)tassenDur_ref[2].getSelectedItem());
		} else if (tasW_ref == 8) {
			durT_ref = Integer.parseInt((String)tassenDur_ref[3].getSelectedItem());
		} //endif	
		
		String[] tempA_ref = new String[8];
		String[] tempB_ref = new String[8];
		tempA_ref[0] = tempCalT_ref.get(Calendar.DAY_OF_MONTH) + "." + (tempCalT_ref.get(Calendar.MONTH)+1) + "." + tempCalT_ref.get(Calendar.YEAR);
		tempB_ref[0] = tempCalT_ref.get(Calendar.DAY_OF_MONTH) + "." + (tempCalW_ref.get(Calendar.MONTH)+1) + "." + tempCalW_ref.get(Calendar.YEAR);
		tempA_ref[1] = tempCalT_ref.get(Calendar.HOUR_OF_DAY) + ":" + tempCalT_ref.get(Calendar.MINUTE);
		tempCalT_ref.add(Calendar.MINUTE, durT_ref);
		tempB_ref[1] = tempCalT_ref.get(Calendar.HOUR_OF_DAY) + ":" + tempCalT_ref.get(Calendar.MINUTE);
		tempA_ref[2] = "Kaffee";
		tempB_ref[2] = "Kaffee";
		tempA_ref[3] = selectedMachine_ref[0]+"";
		tempB_ref[3] = selectedMachine_ref[0]+"";
		tempA_ref[4] = selectedMachine_ref[1]+"";
		tempB_ref[4] = selectedMachine_ref[1]+"";
		tempA_ref[5] = "ON";
		tempB_ref[5] = "OFF";
		tempA_ref[6] = "WT";
		tempB_ref[6] = "WT";
		tempA_ref[7] = "coffee";
		tempB_ref[7] = "coffee";
		coffeeEventArray_ref[0] = tempA_ref;
		coffeeEventArray_ref[1] = tempB_ref;

		String[] tempC_ref = new String[8];
		String[] tempD_ref = new String[8];
		tempC_ref[0] = tempCalW_ref.get(Calendar.DAY_OF_MONTH) + "." + (tempCalW_ref.get(Calendar.MONTH)+1) + "." + tempCalW_ref.get(Calendar.YEAR);
		tempD_ref[0] = tempCalW_ref.get(Calendar.DAY_OF_MONTH) + "." + (tempCalW_ref.get(Calendar.MONTH)+1) + "." + tempCalW_ref.get(Calendar.YEAR);
		tempC_ref[1] = tempCalW_ref.get(Calendar.HOUR_OF_DAY) + ":" + tempCalW_ref.get(Calendar.MINUTE);
		tempCalW_ref.add(Calendar.MINUTE, durW_ref);
		tempD_ref[1] = tempCalW_ref.get(Calendar.HOUR_OF_DAY) + ":" + tempCalW_ref.get(Calendar.MINUTE);
		tempC_ref[2] = "Kaffee";
		tempD_ref[2] = "Kaffee";
		tempC_ref[3] = selectedMachine_ref[0]+"";
		tempD_ref[3] = selectedMachine_ref[0]+"";
		tempC_ref[4] = selectedMachine_ref[1]+"";
		tempD_ref[4] = selectedMachine_ref[1]+"";
		tempC_ref[5] = "ON";
		tempD_ref[5] = "OFF";
		tempC_ref[6] = "WE";
		tempD_ref[6] = "WE";
		tempC_ref[7] = "coffee";
		tempD_ref[7] = "coffee";
		coffeeEventArray_ref[2] = tempC_ref;
		coffeeEventArray_ref[3] = tempD_ref;
		
		String[][] eventListNEW_ref;
		int cou = 0;
		if (timeEvents_ref != null) {
			for (int i = 0; i<timeEvents_ref.length;i++) {
				if (timeEvents_ref[i][7].equals("coffee")) {
					cou++;
				} //endif
			} //endfor
			eventListNEW_ref = new String[timeEvents_ref.length - cou + coffeeEventArray_ref.length][8];
		
			cou = 0;
			for (int i = 0; i < timeEvents_ref.length; i++) {
				if (!(timeEvents_ref[i][7].equals("coffee")) && timeEvents_ref[i][7] != null) {
					eventListNEW_ref[cou] = timeEvents_ref[i].clone();
					cou++;
				} //endif
			} //endfor
			for (int i = 0; i<coffeeEventArray_ref.length; i++) {
				eventListNEW_ref[cou] = coffeeEventArray_ref[i].clone();
				cou++;
			} //endfor
		} else {
			eventListNEW_ref = coffeeEventArray_ref.clone();
		} //endif
		
		status_ref = timeHelper_ref.setTimeEvents(eventListNEW_ref);
		if (status_ref.equals("connection_error")) {
			JOptionPane.showMessageDialog(frame_ref, "Es trat ein Problem mit der Verbindung zum Server auf, versuchen Sie es erneut.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
		} else if (status_ref.equals("success")) {
			JOptionPane.showMessageDialog(frame_ref, "Die Einstellungen wurden uebertragen.", "Hinweis", JOptionPane.INFORMATION_MESSAGE);
		} //endif
		
		for (int i = 0; i < eventListNEW_ref.length; i++) {
			System.out.println();
			for (int j = 0; j < eventListNEW_ref[i].length; j++) {
				System.out.print(eventListNEW_ref[i][j] + " ");
			} //endfor
		} //endfor
	} //endmethod createCoffeeEvents
	
	/**
	 * Zeichnet das GUI-Interface des Kaffee-Moduls.
	 * Die einzelnen Tabs des GUIs werden in eigenen Methoden
	 * gezeichnet.
	 * @see #getSettingsPanel()
	 * @see #getStatusPanel()
	 */
	public void draw() {
		while (selectedMachine_ref == null) {
			selectedMachine_ref = FunknetzClient.readCoffeeSettings();
		} //endif
		frame_ref = new JFrame("Kaffee");
		frame_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/coffee_s.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		JPanel hintergrund_ref = new JPanel(new BorderLayout());
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		frame_ref.getContentPane().add(hintergrund_ref);
		
		JTabbedPane pane_ref = new JTabbedPane();
		hintergrund_ref.add(BorderLayout.CENTER, pane_ref);
		
		ImageIcon statusIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/status_t.gif"));
		pane_ref.addTab("Status", statusIcon_ref, getStatusPanel(), "Statusinformationen und Kaffee-Schnellzugriff.");
		
		ImageIcon settingsIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/settings_t.gif"));
		pane_ref.addTab("Einstellungen", settingsIcon_ref, getSettingsPanel(), "Konfigurieren Sie das Kaffeemodul.");
		
		updateGui();
		frame_ref.pack();
		frame_ref.setResizable(false);
		frame_ref.setVisible(true);
	} //endmethod draw
	
	/**
	 * Zeichnet das Status-Tab und liefert es als JPanel zurueck.
	 * @return Das Status-Tab.
	 */
	private JPanel getStatusPanel() {
		JPanel panel_ref = new JPanel();
		panel_ref.setLayout(new BorderLayout());
		
		Box coffeeBox_ref = new Box(BoxLayout.X_AXIS);
		coffeeBox_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_ref.add(BorderLayout.CENTER, coffeeBox_ref);
		
		Box leftPan_ref = new Box(BoxLayout.Y_AXIS);
		JPanel fillstatBox_ref = new JPanel();
		leftPan_ref.add(fillstatBox_ref);
		fillstatBox_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel fillstatLab_ref = new JLabel("Fuellstatus:  ");
		ImageIcon fillstatIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/on.gif"));
		JLabel fillstatImage_ref = new JLabel(fillstatIcon_ref);
		fillstatBox_ref.add(fillstatLab_ref);
		fillstatBox_ref.add(fillstatImage_ref);
		Box sofortBox_ref = new Box(BoxLayout.Y_AXIS);
		leftPan_ref.add(sofortBox_ref);
		sofortBox_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		coffeeBox_ref.add(leftPan_ref);
		sofortBox_ref.setBorder(new TitledBorder("Sofort kochen"));
		Box sofortOptionBox_ref = new Box(BoxLayout.X_AXIS);
		sofortOptionBox_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		sofortBox_ref.add(sofortOptionBox_ref);
		for (int i = 2; i < 10; i = i+2) {
			JLabel tempLab_ref = new JLabel(i+"");
			JCheckBox tempBox_ref = new JCheckBox();
			sofortOptionBox_ref.add(tempLab_ref);
			sofortOptionBox_ref.add(tempBox_ref);
			sofortOptionBox_ref.add(Box.createHorizontalStrut(3));
		} //endfor
		ImageIcon sofortIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/jetzt.gif"));
		JButton sofortButton_ref = new JButton("Sofort!", sofortIcon_ref);
		sofortButton_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		sofortBox_ref.add(sofortButton_ref);
		
		Box imageBox_ref = new Box(BoxLayout.Y_AXIS);
		coffeeBox_ref.add(imageBox_ref);
		ImageIcon coffeeImage_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/cup.gif"));
		JLabel coffeecup_ref = new JLabel(coffeeImage_ref);
		imageBox_ref.add(coffeecup_ref);
		coffeecup_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		JProgressBar progressBar_ref = new JProgressBar(0,100);
		progressBar_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		imageBox_ref.add(progressBar_ref);
		progressBar_ref.setValue(100);
		progressBar_ref.setStringPainted(true);
		
		Box statusBox_ref = new Box(BoxLayout.Y_AXIS);
		statusBox_ref.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_ref.add(BorderLayout.SOUTH, statusBox_ref);
		
		JPanel timeleftBox_ref = new JPanel();
		timeleftBox_ref.setAlignmentX(Component.LEFT_ALIGNMENT);
		statusBox_ref.add(timeleftBox_ref);
		JLabel timeleftLab_ref = new JLabel("Verbleibende Zeit:  ");
		ImageIcon timeleftIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/next.gif"));
		JLabel timeleftImage_ref = new JLabel(timeleftIcon_ref);
		JTextField timeleftText_ref = new JTextField(4);
		timeleftText_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		timeleftBox_ref.add(timeleftLab_ref);
		timeleftBox_ref.add(timeleftImage_ref);
		timeleftBox_ref.add(timeleftText_ref);
		
		return panel_ref;
	} //endmethod getStatusPanel
	
	/**
	 * Zeichnet das Einstellungs-Tab und liefert es als JPanel zurueck.
	 * @return Das Einstellungen-Tab.
	 */
	private Box getSettingsPanel() {
		Box panel_ref = new Box(BoxLayout.Y_AXIS);
		
		Box geraetePortBox_ref = new Box(BoxLayout.X_AXIS);
		geraetePortBox_ref.setAlignmentX(Component.LEFT_ALIGNMENT);
		geraetePortBox_ref.setBorder(new TitledBorder("Geraete Port"));
		panel_ref.add(geraetePortBox_ref);
		JLabel kanalLab_ref = new JLabel("Kanal:  ");
		kanalText_ref = new JTextField(2);
		if (selectedMachine_ref[0] != -1) {
			kanalText_ref.setText("  "+selectedMachine_ref[1]+"");
		} else {
			kanalText_ref.setText(" --");
		} //endif
		kanalText_ref.setEditable(false);
		JLabel schalterLab_ref = new JLabel("Schalter:  ");
		schalterText_ref = new JTextField(2);
		if (selectedMachine_ref[1] != -1) {
			schalterText_ref.setText("  "+selectedMachine_ref[0]+"");
		} else {
			schalterText_ref.setText(" --");
		} //endif
		schalterText_ref.setEditable(false);
		ImageIcon geraeteButIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/add.gif"));
		JButton geraeteBut_ref = new JButton("Auswaehlen", geraeteButIcon_ref);
		geraeteBut_ref.setActionCommand("Auswaehlen");
		geraeteBut_ref.addActionListener(new ButtonListener());
		geraetePortBox_ref.add(kanalLab_ref);
		geraetePortBox_ref.add(kanalText_ref);
		geraetePortBox_ref.add(Box.createHorizontalStrut(5));
		geraetePortBox_ref.add(schalterLab_ref);
		geraetePortBox_ref.add(schalterText_ref);
		geraetePortBox_ref.add(Box.createHorizontalStrut(70));
		geraetePortBox_ref.add(geraeteBut_ref);
		
		Box zeitBox_ref = new Box(BoxLayout.X_AXIS);
		zeitBox_ref.setAlignmentX(Component.LEFT_ALIGNMENT);
		zeitBox_ref.setBorder(new TitledBorder("Zeiteinstellungen"));
		panel_ref.add(zeitBox_ref);
		Box zeitSetBox_ref = new Box(BoxLayout.Y_AXIS);
		zeitSetBox_ref.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		zeitBox_ref.add(zeitSetBox_ref);
		
		Box aktBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon aktBoxIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/next.gif"));
		JLabel aktBoxImage_ref = new JLabel(aktBoxIcon_ref);
		aktBox_ref.add(aktBoxImage_ref);
		aktBox_ref.add(Box.createHorizontalStrut(2));
		JLabel aktLab_ref = new JLabel("Aktivieren?  ");
		aktLab_ref.setForeground(Color.ORANGE);
		JCheckBox aktCheck_ref = new JCheckBox();
		aktBox_ref.add(aktLab_ref);
		aktBox_ref.add(aktCheck_ref);
		zeitSetBox_ref.add(aktBox_ref);
		zeitSetBox_ref.add(Box.createVerticalStrut(5));
		
		Box weekendBox_ref = new Box(BoxLayout.Y_AXIS);
		weekendBox_ref.setBorder(new TitledBorder("Wochenende"));
		Box zeitWBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon zeitWIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/zeitsteuerung_s.gif"));
		JLabel zeitWImage_ref = new JLabel(zeitWIcon_ref);
		zeitWBox_ref.add(zeitWImage_ref);
		JLabel zeitWLab1_ref = new JLabel("Zeit:  ");
		zeitWComb1_ref = new JComboBox();
		for (int i = 0; i < 24; i++) {
			if (i < 10) {
				zeitWComb1_ref.addItem("0"+i);
			} else {
				zeitWComb1_ref.addItem(i + "");
			} //endif
		} //endfor
		JLabel zeitWLab2_ref = new JLabel(" : ");
		zeitWComb2_ref = new JComboBox();
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				zeitWComb2_ref.addItem("0"+i);
			} else {
				zeitWComb2_ref.addItem(i+"");
			} //endif
		} //endfor
		zeitWBox_ref.add(zeitWLab1_ref);
		zeitWBox_ref.add(Box.createHorizontalStrut(5));
		zeitWBox_ref.add(zeitWComb1_ref);
		zeitWBox_ref.add(zeitWLab2_ref);
		zeitWBox_ref.add(zeitWComb2_ref);
		Box tassenWBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon tassenWIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/coffee_s.gif"));
		JLabel tassenWImage_ref = new JLabel(tassenWIcon_ref);
		tassenWBox_ref.add(tassenWImage_ref);
		JLabel tassenWLab1_ref = new JLabel("Tassen:  ");
		tassenWComb1_ref = new JComboBox();
		for (int i=2; i<9; i=i+2) {
			tassenWComb1_ref.addItem(i+"");
		} //endfor
		tassenWBox_ref.add(tassenWLab1_ref);
		tassenWBox_ref.add(tassenWComb1_ref);
		tassenWBox_ref.add(Box.createHorizontalStrut(40));
		weekendBox_ref.add(zeitWBox_ref);
		weekendBox_ref.add(Box.createVerticalStrut(5));
		weekendBox_ref.add(tassenWBox_ref);
		zeitSetBox_ref.add(weekendBox_ref);
		zeitSetBox_ref.add(Box.createVerticalStrut(5));
		
		Box tagesBox_ref = new Box(BoxLayout.Y_AXIS);
		tagesBox_ref.setBorder(new TitledBorder("Werktags"));
		Box zeitTBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon zeitTIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/zeitsteuerung_s.gif"));
		JLabel zeitTImage_ref = new JLabel(zeitTIcon_ref);
		zeitTBox_ref.add(zeitTImage_ref);
		JLabel zeitTLab1_ref = new JLabel("Zeit:  ");
		zeitTComb1_ref = new JComboBox();
		for (int i = 0; i < 24; i++) {
			if (i < 10) {
				zeitTComb1_ref.addItem("0"+i);
			} else {
				zeitTComb1_ref.addItem(i + "");
			} //endif
		} //endfor
		JLabel zeitTLab2_ref = new JLabel(" : ");
		zeitTComb2_ref = new JComboBox();
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				zeitTComb2_ref.addItem("0"+i);
			} else {
				zeitTComb2_ref.addItem(i+"");
			} //endif
		} //endfor
		zeitTBox_ref.add(zeitTLab1_ref);
		zeitTBox_ref.add(zeitTComb1_ref);
		zeitTBox_ref.add(zeitTLab2_ref);
		zeitTBox_ref.add(zeitTComb2_ref);
		Box tassenTBox_ref = new Box(BoxLayout.X_AXIS);
		ImageIcon tassenTIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/coffee_s.gif"));
		JLabel tassenTImage_ref = new JLabel(tassenTIcon_ref);
		tassenTBox_ref.add(tassenTImage_ref);
		JLabel tassenTLab1_ref = new JLabel("Tassen:  ");
		tassenTComb1_ref = new JComboBox();
		for (int i=2; i<9; i=i+2) {
			tassenTComb1_ref.addItem(i+"");
		} //endfor
		tassenTBox_ref.add(tassenTLab1_ref);
		tassenTBox_ref.add(tassenTComb1_ref);
		tassenTBox_ref.add(Box.createHorizontalStrut(40));
		tagesBox_ref.add(zeitTBox_ref);
		tagesBox_ref.add(Box.createVerticalStrut(3));
		tagesBox_ref.add(tassenTBox_ref);
		zeitSetBox_ref.add(tagesBox_ref);
	
		Box durationBox_ref = new Box(BoxLayout.Y_AXIS);
		durationBox_ref.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		durationBox_ref.setBorder(new TitledBorder("Dauer"));
		zeitBox_ref.add(durationBox_ref);
		for (int i = 2; i < 10; i = i+2) {
			Box tempBox_ref = new Box(BoxLayout.X_AXIS);
			tempBox_ref.add(new JLabel(i+" Tassen:  "));
			tassenDur_ref[(i/2)-1] = new JComboBox();
			for (int j = 0; j < 60; j++) {
				if (j < 10) {
					tassenDur_ref[(i/2)-1].addItem("0"+j);
				} else {
					tassenDur_ref[(i/2)-1].addItem(j+"");
				} //endif
			} //endfor
			tempBox_ref.add(tassenDur_ref[(i/2)-1]);
			tempBox_ref.add(new JLabel("  Minuten"));
			durationBox_ref.add(tempBox_ref);
			durationBox_ref.add(Box.createVerticalStrut(15));
		} //endfor
		
		ImageIcon uebernButIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/ok.gif"));
		JButton uebernBut_ref = new JButton("Uebernehmen", uebernButIcon_ref);
		uebernBut_ref.setActionCommand("Uebernehmen");
		uebernBut_ref.addActionListener(new ButtonListener());
		uebernBut_ref.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_ref.add(uebernBut_ref);
		
		return panel_ref;
	} //endmethod getSettingsPanel
	
	/**
	 * Generiert das Auswahlmenue fuer die Kaffeemaschine, die verwendet werden soll.
	 * @return den Frame, welcher das Auswahlmenue haelt.
	 */
	private JFrame chooseDevice() {
		JFrame frameC_ref = new JFrame("Auswahlmenue");
		ImageIcon frameCIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/add.gif"));
		frameC_ref.setIconImage(frameCIcon_ref.getImage());
		frameC_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Box hintergrund_ref = new Box(BoxLayout.Y_AXIS);
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		Box contentPan_ref = new Box(BoxLayout.X_AXIS);
		contentPan_ref.setBorder(new TitledBorder("Kaffeemaschinen-Auswahlmenue"));
		hintergrund_ref.add(contentPan_ref);
		frameC_ref.getContentPane().add(hintergrund_ref);
		
		Device[][] device_ref = FunknetzClient.getDevices();
		Box chooserBox_ref = new Box(BoxLayout.Y_AXIS);
		contentPan_ref.add(chooserBox_ref);
		contentPan_ref.add(Box.createHorizontalStrut(3));
		int counter = 0;
		for (int i = 0; i<4; i++) {
			for (int j = 0; j<3; j++) {
				if (device_ref[i][j].getType().equals("coffee")) {
					counter++;
				} //endif
			} //endfor
		} //endfor
		String[] tempNameA_ref = new String[counter];
		chooserList_ref = new Object[counter][4];
		int count = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				if (device_ref[i][j].getType().equals("coffee")) {
					Box tempBox_ref = new Box(BoxLayout.X_AXIS);
					tempBox_ref.setAlignmentX(Component.LEFT_ALIGNMENT);
					chooserBox_ref.add(tempBox_ref);
					chooserBox_ref.add(Box.createVerticalStrut(3));
					JRadioButton but_ref = new JRadioButton();
					if ((selectedMachine_ref[0]-1) == i && ((selectedMachine_ref[1]-1) == j)) {
						but_ref.setSelected(true);
					} //endif
					but_ref.addActionListener(new RadioButListener());
					tempBox_ref.add(but_ref);
					chooserList_ref[count][0] = but_ref;
					chooserList_ref[count][1] = (i+1)+"";
					chooserList_ref[count][2] = (j+1)+"";
					chooserList_ref[count][3] = device_ref[i][j].getName();
					tempBox_ref.add(Box.createHorizontalStrut(2));
					JLabel chanLab_ref = new JLabel("Kanal: ");
					JLabel switLab_ref = new JLabel("Schalter: ");
					JTextField tempF1_ref = new JTextField(2);
					tempF1_ref.setText((i+1)+"");
					tempF1_ref.setEditable(false);
					JTextField tempF2_ref = new JTextField(2);
					tempF2_ref.setText((j+1)+"");
					tempF2_ref.setEditable(false);
					tempBox_ref.add(chanLab_ref);
					tempBox_ref.add(tempF1_ref);
					tempBox_ref.add(Box.createHorizontalStrut(3));
					tempBox_ref.add(switLab_ref);
					tempBox_ref.add(tempF2_ref);
					tempNameA_ref[count] = device_ref[i][j].getName();
					count++;
				} //endif
			} //endfor
		} //endfor
		Box nameBox_ref = new Box(BoxLayout.Y_AXIS);
		for (int i = 0; i < tempNameA_ref.length; i++) {
			JTextField nameF_ref = new JTextField(7);
			nameF_ref.setText(tempNameA_ref[i]);
			nameF_ref.setEditable(false);
			nameBox_ref.add(nameF_ref);
			nameBox_ref.add(Box.createVerticalStrut(3));
		} //endfor
		contentPan_ref.add(nameBox_ref);
		
		JLabel infoLab1_ref = new JLabel("Es muss im Einstellungs-Modul zuvor mindestens");
		infoLab1_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel infoLab2_ref = new JLabel("eine Kaffeemaschine ausgewaehlt werden.");
		infoLab2_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		hintergrund_ref.add(infoLab1_ref);
		hintergrund_ref.add(infoLab2_ref);
		hintergrund_ref.add(Box.createVerticalStrut(5));
		Box buttonBox_ref = new Box(BoxLayout.X_AXIS);
		buttonBox_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		ImageIcon abbrButIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/abbrechen.gif"));
		JButton abbrBut_ref = new JButton("Abbrechen", abbrButIcon_ref);
		abbrBut_ref.addActionListener(new ChooserListener());
		ImageIcon okButIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/ok.gif"));
		JButton okBut_ref = new JButton("Ok", okButIcon_ref);
		okBut_ref.addActionListener(new ChooserListener());
		buttonBox_ref.add(abbrBut_ref);
		buttonBox_ref.add(Box.createHorizontalStrut(5));
		buttonBox_ref.add(okBut_ref);
		hintergrund_ref.add(buttonBox_ref);
		frameC_ref.pack();
		frameC_ref.setResizable(false);
		return frameC_ref;
	} //endmethod chooseDevice
	
	// --- Innere Klassen
	
	// --- Listener
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			if (ev_ref.getActionCommand().equals("Auswaehlen")) {
				chooserFrame_ref = chooseDevice();
				chooserFrame_ref.setVisible(true);
			} else if (ev_ref.getActionCommand().equals("Uebernehmen")) {
				updateTimeEvents();
				createSendCoffeeEvents();
				Device[][] dev_ref = FunknetzClient.getDevices();
				String[] devInfo_ref = new String[4];
				for (int i=0;i<tassenDur_ref.length;i++) {
					devInfo_ref[i] = ((tassenDur_ref[i].getSelectedIndex() + 1)*2)+"";
				} //endfor
				dev_ref[selectedMachine_ref[0]][selectedMachine_ref[1]].setDeviceInfo(devInfo_ref);
				FunknetzClient.setDevices(dev_ref);
			} //endif
		} //endmethod actionPerformed
	} //endmethod ButtonListener
	
	class ChooserListener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			if (ev_ref.getActionCommand().equals("Abbrechen")) {
				chooserFrame_ref.dispose();
			} else if (ev_ref.getActionCommand().equals("Ok")) {
				int index = -1;
				for (int i = 0; i<chooserList_ref.length; i++) {
					if (((JRadioButton)chooserList_ref[i][0]).isSelected()) {
						index = i;
					} //endif
				} //endfor
				if (index > -1) {
					selectedMachine_ref[0] = Integer.parseInt((String)chooserList_ref[index][1]);
					selectedMachine_ref[1] = Integer.parseInt((String)chooserList_ref[index][2]);
					kanalText_ref.setText(" "+(String)chooserList_ref[index][1]);
					schalterText_ref.setText(" "+(String)chooserList_ref[index][2]);
				} else {
					selectedMachine_ref[0] = -1;
					selectedMachine_ref[1] = -1;
					kanalText_ref.setText(" --");
					schalterText_ref.setText(" --");
				} //endif
				FunknetzClient.writeCoffeeSettings(selectedMachine_ref);
				chooserFrame_ref.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass ChooserListener
	
	class RadioButListener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			int index = -1;
			for (int i = 0; i<chooserList_ref.length; i++) {
				if (((JRadioButton)chooserList_ref[i][0]).isSelected()) {
					index = i;
				} //endif
			} //endfor
			for (int i = 0; i<chooserList_ref.length; i++) {
				((JRadioButton)chooserList_ref[i][0]).setSelected(false);
			} //endfor
			((JRadioButton)chooserList_ref[index][0]).setSelected(true);
		} //endmethod actionPerformed
	} //endclass RadioButListener
} //endclass Coffee
