package de.funknetz.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

class FunknetzServerGui {
	private JButton start;
	private JButton stop;
	private JTextArea status;
	private FunknetzServer server;
	private JFrame frame;
	private JTextField portTextF;
	private JFrame frame2;
	private JTextField textFeld;
	private JScrollPane scroller;
	
	public void draw() {
		server = new FunknetzServer();
		frame = new JFrame();
		frame.setTitle("FN Server v0.1");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenuBar menubar = new JMenuBar();							// Hier wird die Menueleiste erzeugt
		JMenu menu = new JMenu("Datei");
		JMenu menu2 = new JMenu("Extras");
		JMenuItem settings = new JMenuItem("Einstellungen");
		JMenuItem jamit = new JMenuItem("Jammer");
		settings.addActionListener(new SettingsMenuListener());
		jamit.addActionListener(new JamitListener());
		menu.add(settings);
		menu2.add(jamit);
		menubar.add(menu);
		menubar.add(menu2);
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
		portTextF = new JTextField(10);
		portTextF.setText("5000");
		buttonBox.add(portTextF);
		portTextF.requestFocus();
		start = new JButton("Start Server");
		start.addActionListener(new MeinButtonListener());
		stop = new JButton("Stop Server");
		stop.addActionListener(new MeinButtonListener());
		buttonBox.add(start);
		buttonBox.add(stop);
		
		status = new JTextArea(10,22);
		status.setEditable(false);
		status.setLineWrap(true);
		scroller = new JScrollPane(status);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createEtchedBorder());
		
		hintergrund.add(buttonBox);
		hintergrund.add(scroller);
		
		frame.setJMenuBar(menubar);
		frame.getContentPane().add(hintergrund);
		frame.pack();
		frame.setVisible(true);
	} //endmethod draw
	
	public void drawSettings() {
		
		frame2 = new JFrame("Einstellungen");
		frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel hauptPanel = new JPanel();
		hauptPanel.setLayout(new BorderLayout());
		
		JPanel hintergrund = new JPanel();
		hintergrund.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel zwischenPanel = new JPanel();
		TitledBorder title = BorderFactory.createTitledBorder("Serial Port Einstellungen");
		zwischenPanel.setBorder(title);
		
		JLabel infoLabel = new JLabel("Waehlen Sie den Port aus, an den die Funknetz-Box angeschlossen ist.");
		
		JLabel label = new JLabel("Serialport: ");
		textFeld = new JTextField(10);
		textFeld.setText(server.readIni());
		zwischenPanel.add(label);
		zwischenPanel.add(textFeld);
		
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
		
		frame2.getContentPane().add(hintergrund);
		frame2.pack();
		frame2.setVisible(true);
	} //endmethod drawSettings
	
	public void autoScroll() {
		int max = scroller.getVerticalScrollBar().getMaximum();
		scroller.getVerticalScrollBar().setValue(max);
	}
		
	public void setStatusText(String s) {
		status.setText(status.getText() + s);
	} //endmethodsetStatusText
	
	public FunknetzServerGui () {
		draw();
	} //endkonstruktor FunknetzServerGui
	
	public void zeichneNeu() {
		frame.repaint();
	} //endmethod zeichneNeu
	
	public String getPortTextF() {
		return portTextF.getText();
	} //endmethod getPortTextF
	
	class MeinButtonListener implements ActionListener {
		public void actionPerformed (ActionEvent ev) {
			String command = ev.getActionCommand();
			if (command.equals("Start Server")) {
				server.startListening();
			} else if (command.equals("Stop Server")) {
				server.stopListening();
			} else if (command.equals("Abbrechen")) {
				frame2.dispose();
			} else if (command.equals("Ok")) {
				ComConnect.portIdent = textFeld.getText();
				FunknetzServer.writeIni(textFeld.getText());
				frame2.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass MeinButtonListener
	
	class SettingsMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			drawSettings();
		} //endmethod actionPerformed
	} //endclass SettingsMenuListener
	
	class JamitListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			Jammer jam = new Jammer();
			jam.drawGUI();
		} //endmethod actionPerformed
	} //endclass JamitListener
	
} //endclass FunknetzServerGui