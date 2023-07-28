package de.funknetz.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;

public class Jammer {
	private JComboBox chanBox;
	private JComboBox switBox;
	private int chan;
	private int swit;
	private boolean useGui;
	private JTextArea statusFeld;
	private JFrame frame3;
	private boolean validation;
	private JScrollPane scroller;
	
	public void startJam(String kanal, String schalter) {
		String[] testStringA = {kanal, schalter};
		validation = true;
		
		Object[] meinArray = getJamCommands((Integer.parseInt(testStringA[0])), (Integer.parseInt(testStringA[1])));
		
		try {
			Socket s = new Socket();
			s.bind(null);
			s.connect(new InetSocketAddress(FunknetzGui.getIP(), FunknetzGui.getPort()), 2000);
			ObjectOutputStream ous = new ObjectOutputStream(s.getOutputStream());
			ous.writeObject("JStart", meinArray);
			ous.close();
			s.close();
			statusFeld.setText(statusFeld.getText() + new Date() + "Jammer gestartet...\n");
			autoScroll();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
	} //endmethod startJam
	
	public void stopJam() {
		try {
			Socket s = new Socket();
			s.bind(null);
			s.connect(new InetSocketAddress(FunknetzGui.getIP(), FunknetzGui.getPort()), 2000);
			ObjectOutputStream ous = new ObjectOutputStream(s.getOutputStream());
			ous.writeObject("JStop");
			ous.close();
			s.close();
			statusFeld.setText(statusFeld.getText() + new Date() + "Jammer angehalten...\n");
			autoScroll();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
	} //endmethod stopJam
		
	public Object[] getJamCommands(int kanal, int schalter) {
		int aufrufWert = (kanal-1)*6;
		aufrufWert += (schalter-1)*2;
		
		int[] switchON = FunknetzClient.makeIt(aufrufWert+1);
		int[] switchOFF = FunknetzClient.makeIt(aufrufWert);
		
		Object[] returnA = {switchON, switchOFF};
		return returnA;
	} //endmethod getJamCommands
	
	public void drawGUI() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel hintergrund = new JPanel();
		hintergrund.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel zwischenPanel = new JPanel();
		JLabel chanL = new JLabel("Kanal: ");
		JLabel switL = new JLabel("Schalter: ");
		
		chanBox = new JComboBox();
		chanBox.addItemListener(new ComboBox1Listener());
		for (int i = 0; i < 4; i++) {
			chanBox.addItem("" + (i+1));
		} //endfor
		switBox = new JComboBox();
		switBox.addItemListener(new ComboBox2Listener());
		for (int i = 0; i < 3; i++) {
			switBox.addItem("" + (i+1));
		} //endfor
		
		zwischenPanel.add(chanL);
		zwischenPanel.add(chanBox);
		zwischenPanel.add(switL);
		zwischenPanel.add(switBox);
		
		statusFeld = new JTextArea(10,20);
		statusFeld.setEditable(false);
		statusFeld.setLineWrap(true);
		statusFeld.setText("Waehlen Sie den Kanal und Port und\nklicken Sie auf 'Start' um den Jammer zu\nstarten.\n");
		scroller = new JScrollPane(statusFeld);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setBorder(BorderFactory.createEtchedBorder());
		
		JButton zurueckButton = new JButton("Zurueck");
		zurueckButton.addActionListener(new ButtonListener());
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ButtonListener());
		JButton abbrButton = new JButton("Abbrechen");
		abbrButton.addActionListener(new ButtonListener());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(zurueckButton);
		buttonPanel.add(abbrButton);
		buttonPanel.add(startButton);
		
		JPanel teilPanel = new JPanel();
		teilPanel.setLayout(new BorderLayout());
		
		teilPanel.add(BorderLayout.NORTH, zwischenPanel);
		teilPanel.add(BorderLayout.CENTER, scroller);
		teilPanel.add(BorderLayout.SOUTH, buttonPanel);
		
		hintergrund.add(teilPanel);
		frame.add(hintergrund);
		frame.pack();
		frame.setVisible(true);
		autoScroll();
		
	} //endmethod drawGUI
	
	public void autoScroll() {
		int max = scroller.getVerticalScrollBar().getMaximum();
		scroller.getVerticalScrollBar().setValue(max);
	} //endmethod autoScroll
	
	class ComboBox1Listener implements ItemListener {
		public void itemStateChanged(ItemEvent ev) {
			chan = chanBox.getSelectedIndex() + 1;
		} //endmethod itemStateChanged
	} //endclass ComboBox1Listener
	
	class ComboBox2Listener implements ItemListener {
		public void itemStateChanged(ItemEvent ev) {
			swit = switBox.getSelectedIndex() + 1;
		} //endmethod itemStateChanged
	} //endclass ComboBox2Listener
	
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (((JButton)e.getSource()).getText().equals("Start")) {
				startJam((chan+""), (swit+""));
			} else if (((JButton)e.getSource()).getText().equals("Abbrechen")) {
				stopJam();
			} else {
				frame.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass ButtonListener
} //endclass Jammer