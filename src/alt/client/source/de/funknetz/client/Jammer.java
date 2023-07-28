package de.funknetz.client;

// --- Importe
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: Jammer --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Client--
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- SYSTEM ENVIRONMENT --
	-- OS		Linux 2.6.28-14
	-- SOFTWARE 	JDK 1.6.14 --
	-- --
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Das GUI-Interface fuer den auf dem Server implementierten Stoersender.
* 	Eine Instanz der Klasse IniHelfer dient zur Verwaltung
*	der ini-Datei und stellt Input-/Output-Werkezeuge bzw.
*	Methoden hierfuer bereit.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
public class Jammer {
	
	// --- Attribute
	private JComboBox chanBox_ref;				// ComboBox fuer die Auswahl des Kanals
	private JComboBox switBox_ref;				// ComboBox fuer die Auswahl des Schalters
	private int chan = 1;						// Der ausgewaehlte Kanal
	private int swit = 1;						// Der ausgewaehlte Schalter
	private JTextArea statusFeld_ref;				// TextArea fuer Statusmeldungen des Servers
	private JFrame frame_ref;					// Frame des Jammer-GUIs
	private JScrollPane scroller_ref;				// Scrollpane fuer die TextArea des Jammer-GUIs
	private String command_ref;					// Der Befehl fuer den Server (start, stop)
	private ObjectOutputStream ous_ref;			// Der Ausgabestrom fuer die Serveruebertragung
	private ObjectInputStream ois_ref;				// Der Eingangsstrom fuer die Serveruebertragung
	
	// --- Methoden
	
	/**	
	*	liefert die fuer den Mikroprozessor benoetigten
	*	Codes fuer das An- und Ausschalten zurueck.
	*	@return 
	**/
	private Object[] getJamCommands() {
		int aufrufWert = (chan-1)*6;
		aufrufWert += (swit-1)*2;
		
		int[] switchON_ref = FunknetzClient.makeIt(aufrufWert+1);
		int[] switchOFF_ref = FunknetzClient.makeIt(aufrufWert);
		
		Object[] returnA_ref = {switchON_ref, switchOFF_ref};
		return returnA_ref;
	} //endmethod getJamCommands
	
	/**	
	*	Zeichnet das GUI des Jammers
	**/
	public void draw() {
		frame_ref = new JFrame("Jammer");
		frame_ref.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel hintergrund_ref = new JPanel();
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel zwischenPanel_ref = new JPanel();
		JLabel chanL_ref = new JLabel("Kanal: ");
		JLabel switL_ref = new JLabel("Schalter: ");
		
		chanBox_ref = new JComboBox();
		chanBox_ref.addItemListener(new ComboBox1Listener());
		for (int i = 0; i < 4; i++) {
			chanBox_ref.addItem("" + (i+1));
		} //endfor
		switBox_ref = new JComboBox();
		switBox_ref.addItemListener(new ComboBox2Listener());
		for (int i = 0; i < 3; i++) {
			switBox_ref.addItem("" + (i+1));
		} //endfor
		
		zwischenPanel_ref.add(chanL_ref);
		zwischenPanel_ref.add(chanBox_ref);
		zwischenPanel_ref.add(switL_ref);
		zwischenPanel_ref.add(switBox_ref);
		
		statusFeld_ref = new JTextArea(10,20);
		statusFeld_ref.setEditable(false);
		statusFeld_ref.setLineWrap(true);
		statusFeld_ref.setText("Waehlen Sie den Kanal und Port und\nklicken Sie auf 'Start' um den Jammer zu starten.\n");
		scroller_ref = new JScrollPane(statusFeld_ref);
		scroller_ref.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller_ref.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroller_ref.setBorder(BorderFactory.createEtchedBorder());
		
		JButton zurueckButton_ref = new JButton("Zurueck");
		zurueckButton_ref.addActionListener(new ButtonListener());
		JButton startButton_ref = new JButton("Start");
		startButton_ref.addActionListener(new ButtonListener());
		JButton abbrButton_ref = new JButton("Stopp");
		abbrButton_ref.addActionListener(new ButtonListener());
		JPanel buttonPanel_ref = new JPanel();
		buttonPanel_ref.add(zurueckButton_ref);
		buttonPanel_ref.add(abbrButton_ref);
		buttonPanel_ref.add(startButton_ref);
		
		JPanel teilPanel_ref = new JPanel();
		teilPanel_ref.setLayout(new BorderLayout());
		
		teilPanel_ref.add(BorderLayout.NORTH, zwischenPanel_ref);
		teilPanel_ref.add(BorderLayout.CENTER, scroller_ref);
		teilPanel_ref.add(BorderLayout.SOUTH, buttonPanel_ref);
		
		hintergrund_ref.add(teilPanel_ref);
		frame_ref.add(hintergrund_ref);
		frame_ref.pack();
		frame_ref.setVisible(true);
		autoScroll();
		
	} //endmethod draw
	
	/**	
	*	Scrollt die Scrollbar der Textarea bis ganz nach Unten durch,
	*	springt also zu deren Ende.
	**/
	public void autoScroll() {
		int max = scroller_ref.getVerticalScrollBar().getMaximum();
		scroller_ref.getVerticalScrollBar().setValue(max);
	} //endmethod autoScroll
	
	// --- Innere Klassen
	
		// -- Runnable Objekte
	
	/**	
	*	Ermittel das an den Server zu sendende Kommando
	*	und uebermittel es. Zudem empfaengt es auch eine
	*	Bestaetigung und wertet diese entsprechend aus.
	**/
	class JamRun implements Runnable {
		public void run() {
			
			try {
				Socket s_ref = new Socket();
				s_ref.bind(null);
				s_ref.connect(new InetSocketAddress(FunknetzGui.getIP(), FunknetzGui.getPort()), 2000);
				ous_ref = new ObjectOutputStream(s_ref.getOutputStream());
				ois_ref = new ObjectInputStream(s_ref.getInputStream());
				ous_ref.writeObject("jammer");
				if (command_ref.equals("start")) {
					ous_ref.writeObject("start");
					ous_ref.writeObject(getJamCommands());
				} else if (command_ref.equals("stop")){
					ous_ref.writeObject("stop");
				} //endif
				
				String status_ref = (String) ois_ref.readObject();
				if (status_ref.equals("ok")) {
					if (command_ref.equals("start")) {
						statusFeld_ref.setText(statusFeld_ref.getText() + "Der Jammer wurde gestartet.\n");
						autoScroll();
					} else {
						statusFeld_ref.setText(statusFeld_ref.getText() + "Der Jammer wurde angehalten.\n");
						autoScroll();
					} //endif
				} else if (status_ref.equals("error")) {
					if (command_ref.equals("stop")) {
						statusFeld_ref.setText(statusFeld_ref.getText() + "Der Jammer wurde bereits angehalten\nBitte klicken Sie zuerst auf \"Start\".\n");
						autoScroll();
					} else if (command_ref.equals("start")) {
						statusFeld_ref.setText(statusFeld_ref.getText() + "Der Jammer wurde bereits gestartet.\nBitte klicken Sie zuerst auf \"Stop\".\n");
						autoScroll();
					} //endif
				} //endif
				status_ref = "";
				s_ref.close();
			} catch (Exception ex_ref) {
				statusFeld_ref.setText(statusFeld_ref.getText() + "Es trat ein Problem mit der Verbindung auf.\n");
				autoScroll();
			} //endtry
		} //endmethod run
	} //endclass JamRun
	
		// --- Listener
	
	/**	
	*	Setzt den gewuenschten Kanal auf die momentane Auswahl.
	**/
	class ComboBox1Listener implements ItemListener {
		public void itemStateChanged(ItemEvent ev_ref) {
			chan = chanBox_ref.getSelectedIndex() + 1;
		} //endmethod itemStateChanged
	} //endclass ComboBox1Listener
	
	/**	
	*	Setzt den gewuenschten Kanal auf die momentane Auswahl.
	**/
	class ComboBox2Listener implements ItemListener {
		public void itemStateChanged(ItemEvent ev_ref) {
			swit = switBox_ref.getSelectedIndex() + 1;
		} //endmethod itemStateChanged
	} //endclass ComboBox2Listener
	
	/**	
	*	Wartet auf das Ausloesen eines der Buttons und
	*	startet bzw. stoppt dementsprechend den
	*	Jammer auf dem Funknetz-Server. Hierzu wird eine neue
	*	Instanz des Runnable Objekts JamRun erzeugt.
	*	@see JamRun
	**/
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e_ref) {
			if (((JButton)e_ref.getSource()).getText().equals("Start")) {
				command_ref = "start";
				try {
					Thread t_ref = new Thread(new JamRun());
					t_ref.start();
				} catch (Exception ex_ref) {ex_ref.printStackTrace();}
			} else if ( ( (JButton) e_ref.getSource() ).getText().equals("Stopp")) {
				command_ref = "stop";
				try {
					Thread t_ref = new Thread(new JamRun());
					t_ref.start();
				} catch (Exception ex_ref) {
					ex_ref.printStackTrace();
				} //endtry
			} else {
				frame_ref.dispose();
			} //endif
		} //endmethod actionPerformed
	} //endclass ButtonListener
	
} //endclass Jammer