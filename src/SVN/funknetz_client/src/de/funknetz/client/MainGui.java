/**
 * 
 */
package de.funknetz.client;

// --- Importe

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*	---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
--| Copyright (c) by Tobias Burkard, 2009	      |--
---------------------------------------------------------------------------------
-- --
-- CLASS: MainGui --
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
*	Das Haupt-GUI-Interface fuer den FunknetzClient.
* 	Von hier aus koennen alle gaengigen Einstellungen vorgenommen
* 	und Module geladen werden.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class MainGui {
	
	// --- Attribute
	JFrame frame_ref;
	Settings settingsGui_ref;
	RemoteControl fernstGui_ref;
	Jammer jammer_ref;
	TimeEvents zeitsteuerung_ref;
	static JTextField ipText_ref;
	static JTextField portText_ref;
	
	// --- Methoden
	public void draw() {
		frame_ref = new JFrame("Funknetz Client v0.3");
		frame_ref.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon frameIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/connect_s.gif"));
		frame_ref.setIconImage(frameIcon_ref.getImage());
		JMenuBar menuBar_ref = new JMenuBar();
		frame_ref.setJMenuBar(menuBar_ref);
		JMenu dateiMen_ref = new JMenu("Datei");
		menuBar_ref.add(dateiMen_ref);
		JMenu bearbeitMen_ref = new JMenu("Bearbeiten");
		menuBar_ref.add(bearbeitMen_ref);
		JMenu moduleMen_ref = new JMenu("Module");
		menuBar_ref.add(moduleMen_ref);
		JMenu hilfeMen_ref = new JMenu("Hilfe");
		menuBar_ref.add(hilfeMen_ref);
		JMenuItem dateiMen_Beend_ref = new JMenuItem("Beenden");
		dateiMen_Beend_ref.addActionListener(new MainListener());
		dateiMen_ref.add(dateiMen_Beend_ref);
		JMenuItem bearbeitMen_Einst_ref = new JMenuItem("Einstellungen");
		bearbeitMen_Einst_ref.addActionListener(new MainListener());
		bearbeitMen_ref.add(bearbeitMen_Einst_ref);
		JMenuItem hilfeMen_Info_ref = new JMenuItem("Info");
		hilfeMen_Info_ref.addActionListener(new MainListener());
		hilfeMen_ref.add(hilfeMen_Info_ref);
		JMenuItem moduleMen_Fernst_ref = new JMenuItem("Fernsteuerung");
		moduleMen_Fernst_ref.addActionListener(new MainListener());
		moduleMen_ref.add(moduleMen_Fernst_ref);
		JMenuItem moduleMen_Jamm_ref = new JMenuItem("Jammer");
		moduleMen_Jamm_ref.addActionListener(new MainListener());
		moduleMen_ref.add(moduleMen_Jamm_ref);
		JMenuItem moduleMen_Zeit_ref = new JMenuItem("Zeitsteuerung");
		moduleMen_Zeit_ref.addActionListener(new MainListener());
		moduleMen_ref.add(moduleMen_Zeit_ref);
		
		Box hintergrund_ref = new Box(BoxLayout.X_AXIS);
		hintergrund_ref.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel shortcutPan_ref = new JPanel();
		GridLayout shortcutPanLay_ref = new GridLayout(2,3);
		shortcutPanLay_ref.setVgap(10);
		shortcutPanLay_ref.setHgap(10);
		shortcutPan_ref.setLayout(shortcutPanLay_ref);
		hintergrund_ref.add(shortcutPan_ref);
		hintergrund_ref.add(Box.createHorizontalStrut(10));
		frame_ref.getContentPane().add(hintergrund_ref);
		
		JPanel fernstPan_ref = new JPanel();
		fernstPan_ref.setLayout(new BoxLayout(fernstPan_ref, BoxLayout.Y_AXIS));
		ImageIcon fernstIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/Fernsteuerung.gif"));
		JButton fernstBut_ref = new JButton(fernstIcon_ref);
		fernstBut_ref.setActionCommand("Fernsteuerung");
		fernstBut_ref.addActionListener(new MainListener());
		fernstBut_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension d_ref = new Dimension(fernstIcon_ref.getIconWidth(), fernstIcon_ref.getIconHeight());
		fernstBut_ref.setPreferredSize(d_ref);
		JLabel fernstLab_ref = new JLabel("Fernsteuerung");
		fernstLab_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		fernstPan_ref.add(fernstBut_ref);
		fernstPan_ref.add(fernstLab_ref);
		shortcutPan_ref.add(fernstPan_ref);
		
		JPanel jammPan_ref = new JPanel();
		jammPan_ref.setLayout(new BoxLayout(jammPan_ref, BoxLayout.Y_AXIS));
		ImageIcon jammIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/Jammer.gif"));
		JButton jammBut_ref = new JButton(jammIcon_ref);
		jammBut_ref.setActionCommand("Jammer");
		jammBut_ref.addActionListener(new MainListener());
		jammBut_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension d2_ref = new Dimension(jammIcon_ref.getIconWidth(), jammIcon_ref.getIconHeight());
		jammBut_ref.setPreferredSize(d2_ref);
		JLabel jammLab_ref = new JLabel("Jammer");
		jammLab_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		jammPan_ref.add(jammBut_ref);
		jammPan_ref.add(jammLab_ref);
		shortcutPan_ref.add(jammPan_ref);
		
		JPanel zeitPan_ref = new JPanel();
		zeitPan_ref.setLayout(new BoxLayout(zeitPan_ref, BoxLayout.Y_AXIS));
		ImageIcon zeitIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/Zeitsteuerung.gif"));
		JButton zeitBut_ref = new JButton(zeitIcon_ref);
		zeitBut_ref.setActionCommand("Zeitsteuerung");
		zeitBut_ref.addActionListener(new MainListener());
		zeitBut_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension d3_ref = new Dimension(zeitIcon_ref.getIconWidth(), zeitIcon_ref.getIconHeight());
		zeitBut_ref.setPreferredSize(d3_ref);
		JLabel zeitLab_ref = new JLabel("Zeitsteuerung");
		zeitLab_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		zeitPan_ref.add(zeitBut_ref);
		zeitPan_ref.add(zeitLab_ref);
		shortcutPan_ref.add(zeitPan_ref);
		
		JPanel einstPan_ref = new JPanel();
		einstPan_ref.setLayout(new BoxLayout(einstPan_ref, BoxLayout.Y_AXIS));
		ImageIcon einstIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/Einstellungen.gif"));
		JButton einstBut_ref = new JButton(einstIcon_ref);
		einstBut_ref.setActionCommand("Einstellungen");
		einstBut_ref.addActionListener(new MainListener());
		einstBut_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension d4_ref = new Dimension(einstIcon_ref.getIconWidth(), einstIcon_ref.getIconHeight());
		einstBut_ref.setPreferredSize(d4_ref);
		JLabel einstLab_ref = new JLabel("Einstellungen");
		einstLab_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		einstPan_ref.add(einstBut_ref);
		einstPan_ref.add(einstLab_ref);
		shortcutPan_ref.add(einstPan_ref);
		
		JPanel infoPan_ref = new JPanel();
		infoPan_ref.setLayout(new BoxLayout(infoPan_ref, BoxLayout.Y_AXIS));
		ImageIcon infoIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/Info.gif"));
		JButton infoBut_ref = new JButton(infoIcon_ref);
		infoBut_ref.setActionCommand("Info");
		infoBut_ref.addActionListener(new MainListener());
		infoBut_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension d5_ref = new Dimension(infoIcon_ref.getIconWidth(), infoIcon_ref.getIconHeight());
		infoBut_ref.setPreferredSize(d5_ref);
		JLabel infoLab_ref = new JLabel("Info");
		infoLab_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoPan_ref.add(infoBut_ref);
		infoPan_ref.add(infoLab_ref);
		shortcutPan_ref.add(infoPan_ref);
		
		JPanel beendPan_ref = new JPanel();
		beendPan_ref.setLayout(new BoxLayout(beendPan_ref, BoxLayout.Y_AXIS));
		ImageIcon beendIcon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/Beenden.gif"));
		JButton beendBut_ref = new JButton(beendIcon_ref);
		beendBut_ref.setActionCommand("Beenden");
		beendBut_ref.addActionListener(new MainListener());
		beendBut_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension d6_ref = new Dimension(beendIcon_ref.getIconWidth(), beendIcon_ref.getIconHeight());
		beendBut_ref.setPreferredSize(d6_ref);
		JLabel beendLab_ref = new JLabel("Beenden");
		beendLab_ref.setAlignmentX(Component.CENTER_ALIGNMENT);
		beendPan_ref.add(beendBut_ref);
		beendPan_ref.add(beendLab_ref);
		shortcutPan_ref.add(beendPan_ref);
		
		Box connectPan_ref = new Box(BoxLayout.Y_AXIS);
		connectPan_ref.setBorder(BorderFactory.createTitledBorder("Verbindung"));
		JLabel ipLab_ref = new JLabel("IP-Adresse:");
		connectPan_ref.add(ipLab_ref);
		ipText_ref = new JTextField(8);
		ipText_ref.setText(FunknetzClient.getIP());
		connectPan_ref.add(ipText_ref);
		JLabel portLab_ref = new JLabel("Port:");
		connectPan_ref.add(portLab_ref);
		portText_ref = new JTextField(8);
		portText_ref.setText(FunknetzClient.getPort()+"");
		connectPan_ref.add(portText_ref);
		connectPan_ref.add(Box.createVerticalStrut(75));
		hintergrund_ref.add(connectPan_ref);
		
		frame_ref.pack();
		frame_ref.setResizable(false);
		frame_ref.setVisible(true);
	} //endmethod draw
	
	public static String getIP() {
		return ipText_ref.getText();
	} //endmethod getIp
	
	public static int getPort() {
		int port = -1;
		try {
			port = Integer.parseInt(portText_ref.getText());
		} catch (NumberFormatException ex_ref) {
			port = -1;
		} //endtry
		if (port < 0 || port > 65535) {
			port = -1;
		} //endif
		return port;
	} //endmethod getPort
	
	// --- Innere Klassen
	class MainListener implements ActionListener {
		public void actionPerformed(ActionEvent ev_ref) {
			FunknetzClient.setIP(getIP());
			FunknetzClient.setPort(getPort());
			FunknetzClient.setIni();
			if (ev_ref.getActionCommand().equals("Beenden")) {
				System.exit(0);
			} else if (ev_ref.getActionCommand().equals("Info")) {
				ImageIcon icon_ref = new ImageIcon(ClassLoader.getSystemResource("de/funknetz/client/resource/connect.gif"));
				JOptionPane.showMessageDialog(frame_ref, "Funknetz Client v0.3\n\n( c ) Tobias Burkard 2009\nhttp://funknetz.sourceforge.net", "Info", JOptionPane.INFORMATION_MESSAGE, icon_ref);
			} else if (ev_ref.getActionCommand().equals("Einstellungen")) {
				settingsGui_ref = new Settings();
				settingsGui_ref.draw();
			} else if (ev_ref.getActionCommand().equals("Fernsteuerung")) {
				fernstGui_ref = new RemoteControl();
				fernstGui_ref.draw();
			} else if (ev_ref.getActionCommand().equals("Jammer")) {
				jammer_ref = new Jammer();
				jammer_ref.draw();
			} else if (ev_ref.getActionCommand().equals("Zeitsteuerung")) {
				zeitsteuerung_ref = new TimeEvents();
				zeitsteuerung_ref.draw();
			} //endif
		} //endmethod actionPerformed
	} //endclass MainListener

} //endclass MainGui
