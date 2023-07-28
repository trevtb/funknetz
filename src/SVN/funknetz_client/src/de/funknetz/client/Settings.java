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
	JFrame frame_ref;
	String[] kNamen_ref;
	String[][] sNamen_ref;
	JTextField[] kanalTextFArray_ref;
	JTextField[][] schalterTextFArray_ref;
	String ip_ref;
	int port;
	int selectedChan;
	
	/**
	 * Zeichnet das Settings-Gui.
	 */
	public void draw() {
		updateIni();
		frame_ref = new JFrame("Einstellungen");	// Erzeugung eines neuen Frames
		frame_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/einstellungen_s.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		
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
		teilPanel1Part1_ref.add(kanaeleLabel_ref);
		JLabel schalter1_ref = new JLabel("Schalter 1");
		teilPanel1Part1_ref.add(schalter1_ref);
		JLabel schalter2_ref = new JLabel("Schalter 2");
		teilPanel1Part1_ref.add(schalter2_ref);
		JLabel schalter3_ref = new JLabel("Schalter 3");
		teilPanel1Part1_ref.add(schalter3_ref);
		schalterTextFArray_ref = new JTextField[4][3];
		ImageIcon buttonOkIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/ok.gif"));
		JButton buttonOk_ref = new JButton("Ok", buttonOkIcon_ref);
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
	
		GridLayout teilPanel2Raster_ref = new GridLayout(1,2);				// Erzeugung von teilPanel2
		teilPanel2Raster_ref.setHgap(10);
		teilPanel2Raster_ref.setVgap(1);
		JPanel teilPanel2_ref = new JPanel(teilPanel2Raster_ref);
		teilPanel2_ref.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		ImageIcon buttonCancelIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/abbrechen.gif"));
		JButton buttonCancel_ref = new JButton("Abbrechen", buttonCancelIcon_ref);
		buttonCancel_ref.addActionListener(new SettingsButtonListener());
		ImageIcon buttonBackIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/zuruecksetzen.gif"));
		JButton buttonBack_ref = new JButton("Zuruecksetzen", buttonBackIcon_ref);
		buttonBack_ref.addActionListener(new SettingsButtonListener());
		teilPanel2_ref.add(buttonCancel_ref);
		teilPanel2_ref.add(buttonBack_ref);
		teilPanel2_ref.add(buttonOk_ref);
		
		JLabel beschreibungLabel_ref = new JLabel("Ein Klick auf 'OK' speichert die Werte.");
		
		teilPanel1_ref.add(BorderLayout.CENTER, teilPanel1Part1_ref);
		teilPanel1_ref.add(BorderLayout.SOUTH, beschreibungLabel_ref);
		
		hintergrund_ref.add(teilPanel1_ref);
		hintergrund_ref.add(teilPanel2_ref);
		
		//frame_ref.setBounds(500,300,550,250);
		frame_ref.pack();
		frame_ref.setVisible(true);
	} //endmethod draw
	
	/**
	 * Aktualisiert die Kanal- und Schalternamen aus dem FunknetzClient.
	 */
	private void updateIni() {
		kNamen_ref = FunknetzClient.getKNamen();
		sNamen_ref = FunknetzClient.getSNamen();
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
			for (int i = 0; i < 4; i++) {
					kNamen_ref[i] = kanalTextFArray_ref[i].getText();
					for (int j = 0; j < 3; j++) {
						sNamen_ref[i][j] = schalterTextFArray_ref[i][j].getText();
					} //endfor
				} //endfor
				FunknetzClient.setKNamen(kNamen_ref);
				FunknetzClient.setSNamen(sNamen_ref);
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
				for (int i = 0; i < 4; i++) {
					kNamen_ref[i] = kanalTextFArray_ref[i].getText();
					for (int j = 0; j < 3; j++) {
						sNamen_ref[i][j] = schalterTextFArray_ref[i][j].getText();
					} //endfor
				} //endfor
				FunknetzClient.setKNamen(kNamen_ref);
				FunknetzClient.setSNamen(sNamen_ref);
				FunknetzClient.setIni();
				frame_ref.dispose();
			} else if ((ev_ref.getActionCommand()).equals("Zuruecksetzen")) {
				FunknetzClient.newIni();
				updateIni();
				for (int i = 0; i < 4; i++) {
					kanalTextFArray_ref[i].setText(kNamen_ref[i]); 
					for (int j = 0; j < 3; j++) {
						schalterTextFArray_ref[i][j].setText(sNamen_ref[i][j]);
					} //endfor
				} //endfor
			} else {
				frame_ref.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass SettingsButtonListener
} //endclass Settings
