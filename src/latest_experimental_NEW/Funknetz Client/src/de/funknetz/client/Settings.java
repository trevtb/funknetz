/**
 * 
 */
package de.funknetz.client;

// --- Importe
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/*	---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
--| Copyright (c) by Tobias Burkard, 2009	      |--
---------------------------------------------------------------------------------
-- --
-- CLASS: Settings --
-- --
---------------------------------------------------------------------------------
-- --
-- PROJECT: Funknetz Client--
-- --
---------------------------------------------------------------------------------
-- --
-- SYSTEM ENVIRONMENT 					--
-- OS			Ubuntu 9.10 (Linux 2.6.31)	--
-- SOFTWARE 	JDK 1.6.15 				--
-- --
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------	*/

/**	
*	Das Settings-GUI bietet Einstellungsmoeglichkeiten fuer
*	Kanal- und Schalternamen. Diese koennen somit global in der 
*	ganzen Anwendung genutzt werden.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class Settings {
	Device[][] deviceList_ref;
	JFrame frame_ref;
	String[] kNamen_ref;
	JComboBox[][] comboArray_ref;
	JTextField[] kanalTextFArray_ref;
	JTextField[][] schalterTextFArray_ref;
	String ip_ref;
	int port;
	int selectedChan;
	ImageIcon[] iconArray_ref;
	
	/**
	 * Zeichnet das Settings-Gui.
	 */
	public void draw() {
		ImageIcon userIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/user_s.gif"));
		ImageIcon coffeeIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/coffee_ss.gif"));
		iconArray_ref = new ImageIcon[2];
		iconArray_ref[0] = userIcon_ref;
		iconArray_ref[1] = coffeeIcon_ref;
		frame_ref = new JFrame("Einstellungen");	// Erzeugung eines neuen Frames
		frame_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/einstellungen_s.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		updateIni();
		Box hintergrund_ref = new Box(BoxLayout.Y_AXIS);											// Erzeugung eines Hintergrundpanels mit den Raendern (10,10,10,10)
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		frame_ref.getContentPane().add(BorderLayout.CENTER, hintergrund_ref);
		
		JPanel teilPanel1_ref = new JPanel();												// Erzeugung von teilPanel1
		teilPanel1_ref.setLayout(new BorderLayout());
		
		kanalTextFArray_ref = new JTextField[4];											// Erzeugung von teilPanel1Part1 (Teil von zwischenPanel)	
		GridLayout teilPanel1Part1Raster_ref = new GridLayout(5,4);
		teilPanel1Part1Raster_ref.setVgap(1);
		teilPanel1Part1Raster_ref.setHgap(1);
		JPanel teilPanel1Part1_ref = new JPanel(teilPanel1Part1Raster_ref);	
		teilPanel1Part1_ref.setBorder(new EtchedBorder());		
		JLabel kanaeleLabel_ref = new JLabel("Kanaele");
		kanaeleLabel_ref.setHorizontalAlignment(SwingConstants.CENTER);
		teilPanel1Part1_ref.add(kanaeleLabel_ref);
		JLabel schalter1_ref = new JLabel("Schalter 1");
		schalter1_ref.setHorizontalAlignment(SwingConstants.CENTER);
		teilPanel1Part1_ref.add(schalter1_ref);
		JLabel schalter2_ref = new JLabel("Schalter 2");
		schalter2_ref.setHorizontalAlignment(SwingConstants.CENTER);
		teilPanel1Part1_ref.add(schalter2_ref);
		JLabel schalter3_ref = new JLabel("Schalter 3");
		schalter3_ref.setHorizontalAlignment(SwingConstants.CENTER);
		teilPanel1Part1_ref.add(schalter3_ref);
		schalterTextFArray_ref = new JTextField[4][3];
		ImageIcon buttonOkIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/ok.gif"));
		JButton buttonOk_ref = new JButton("Ok", buttonOkIcon_ref);
		buttonOk_ref.addActionListener(new SettingsButtonListener());
		
		comboArray_ref = new JComboBox[4][3];
		for (int i = 0; i < 4; i++) {
			JTextField textF1_ref = new JTextField(10);
			textF1_ref.setText(kNamen_ref[i]);
			textF1_ref.addFocusListener(new SettingsMarkerListener());
			textF1_ref.addActionListener(new SettingsOkListener());
			teilPanel1Part1_ref.add(textF1_ref);
			kanalTextFArray_ref[i] = textF1_ref;
			for (int j = 0; j < 3; j++) {
				Box tempBox_ref = new Box(BoxLayout.X_AXIS);
				JComboBox comboTemp_ref = new JComboBox(iconArray_ref);
				int index = -1;
				if (deviceList_ref[i][j].getType().equals("user")) {
					index = 0;
				} else if (deviceList_ref[i][j].getType().equals("coffee")) {
					index = 1;
				} //endif
				comboTemp_ref.setSelectedIndex(index);
				comboArray_ref[i][j] = comboTemp_ref;
				tempBox_ref.add(comboTemp_ref);
				JTextField textF2_ref = new JTextField(10);
				textF2_ref.setText(deviceList_ref[i][j].getName());
				textF2_ref.addFocusListener(new SettingsMarkerListener());
				textF2_ref.addActionListener(new SettingsOkListener());
				tempBox_ref.add(textF2_ref);
				teilPanel1Part1_ref.add(tempBox_ref);
				schalterTextFArray_ref[i][j] = textF2_ref;
			} //endfor
		} //endfor
	
		Box teilPanel2_ref = new Box(BoxLayout.X_AXIS);
		teilPanel2_ref.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		ImageIcon buttonCancelIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/abbrechen.gif"));
		JButton buttonCancel_ref = new JButton("Abbrechen", buttonCancelIcon_ref);
		buttonCancel_ref.addActionListener(new SettingsButtonListener());
		ImageIcon buttonBackIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/zuruecksetzen.gif"));
		JButton buttonBack_ref = new JButton("Zuruecksetzen", buttonBackIcon_ref);
		buttonBack_ref.addActionListener(new SettingsButtonListener());
		teilPanel2_ref.add(buttonCancel_ref);
		teilPanel2_ref.add(Box.createHorizontalStrut(3));
		teilPanel2_ref.add(buttonBack_ref);
		teilPanel2_ref.add(Box.createHorizontalStrut(3));
		teilPanel2_ref.add(buttonOk_ref);
		
		JLabel beschreibungLabel_ref = new JLabel("Hier koennen die Kanal- und Geraetebezeichner gesetzt werden.");
		JLabel beschreibungLabel2_ref = new JLabel("Durch das Icon vor dem Textfeld, wird der Geraetetyp bestimmt.");
		JLabel beschreibungLabel3_ref = new JLabel("Ein Klick auf 'OK' speichert die Werte.");
		beschreibungLabel_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		beschreibungLabel2_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		beschreibungLabel3_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		Box beschreibungBox_ref = new Box(BoxLayout.Y_AXIS);
		beschreibungBox_ref.setBorder(new TitledBorder("Info"));
		beschreibungBox_ref.add(beschreibungLabel_ref);
		beschreibungBox_ref.add(beschreibungLabel2_ref);
		beschreibungBox_ref.add(beschreibungLabel3_ref);
		
		teilPanel1_ref.add(BorderLayout.CENTER, teilPanel1Part1_ref);
		teilPanel1_ref.add(BorderLayout.SOUTH, beschreibungBox_ref);
		
		hintergrund_ref.add(teilPanel1_ref);
		hintergrund_ref.add(teilPanel2_ref);
		
		frame_ref.pack();
		frame_ref.setResizable(false);
		frame_ref.setVisible(true);
	} //endmethod draw
	
	/**
	 * Aktualisiert die Kanal- und Schalternamen aus dem FunknetzClient.
	 */
	private void updateIni() {
		kNamen_ref = FunknetzClient.getKNamen();
		deviceList_ref = FunknetzClient.getDevices();
		if (deviceList_ref == null) {
			JOptionPane.showMessageDialog(frame_ref, "Es trat ein Problem mit der Verbindung zum Server auf, versuchen Sie es erneut.", "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
			deviceList_ref = new Device[4][3];
			for (int i=0; i<deviceList_ref.length;i++) {
				for (int j=0; j<deviceList_ref[i].length; j++) {
					deviceList_ref[i][j] = new Device("user", "K"+(i+1)+" "+"Schalter "+(j+1));
				} //endfor
			} //endfor
		} //endif
		ip_ref = FunknetzClient.getIP();
		port = FunknetzClient.getPort();
		selectedChan = FunknetzClient.getSelectedChan();
	} //endmethod updateIni
	
	// --- Innere Klassen
	
		// --- Listener
	
	/**
	 * Dient zum automatischen markieren des Inhalts, falls ein 
	 * JTextField im Settings-GUI vom Benutzer angeklickt wird.
	 */
	public class SettingsMarkerListener implements FocusListener{
		public void focusGained (FocusEvent e_ref) {
			((JTextField)e_ref.getSource()).selectAll();
		} //endmethod focusGained
		
		public void focusLost (FocusEvent e_ref) {
		} //endmethod focusLost
	} //endclass SettingsMarkerListener
	
	/** 	
	*	Ueberwacht die Textfelder des Settings-GUIs und speichert
	*	die geaenderten Werte bei Druecken der "ENTER"-Taste.
	**/
	class SettingsOkListener implements ActionListener {
		public void actionPerformed (ActionEvent ev_ref) {
			String tempType_ref = null;
			for (int i = 0; i < 4; i++) {
					kNamen_ref[i] = kanalTextFArray_ref[i].getText();
					for (int j = 0; j < 3; j++) {
						if (comboArray_ref[i][j].getSelectedItem().equals(iconArray_ref[0])) {
							tempType_ref = "user";
						} else if (comboArray_ref[i][j].getSelectedItem().equals(iconArray_ref[1])) {
							tempType_ref = "coffee";
						} //endif
						deviceList_ref[i][j] = new Device(tempType_ref, schalterTextFArray_ref[i][j].getText());
					} //endfor
				} //endfor
				FunknetzClient.setKNamen(kNamen_ref);
				FunknetzClient.setDevices(deviceList_ref);
				FunknetzClient.setIni();
				frame_ref.dispose();
		} //endmethod actionPerformed
	} //endclass SettingsOkListener
	
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
				String tempType_ref = null;
				for (int i = 0; i < 4; i++) {
					kNamen_ref[i] = kanalTextFArray_ref[i].getText();
					for (int j = 0; j < 3; j++) {
						if (comboArray_ref[i][j].getSelectedItem().equals(iconArray_ref[0])) {
							tempType_ref = "user";
						} else if (comboArray_ref[i][j].getSelectedItem().equals(iconArray_ref[1])) {
							tempType_ref = "coffee";
						} //endif
						deviceList_ref[i][j] = new Device(tempType_ref, schalterTextFArray_ref[i][j].getText());
					} //endfor
				} //endfor
				FunknetzClient.setKNamen(kNamen_ref);
				FunknetzClient.setDevices(deviceList_ref);
				FunknetzClient.setIni();
				frame_ref.dispose();
			} else if ((ev_ref.getActionCommand()).equals("Zuruecksetzen")) {
				//FunknetzClient.newIni();
				//updateIni();
				for (int i = 0; i < 4; i++) {
					kanalTextFArray_ref[i].setText("Kanal " + (i+1)); 
					for (int j = 0; j < 3; j++) {
						schalterTextFArray_ref[i][j].setText("K"+(i+1)+" Schalter "+(j+1));
						comboArray_ref[i][j].setSelectedIndex(0);
					} //endfor
				} //endfor
			} else {
				frame_ref.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass SettingsButtonListener
} //endclass Settings
