package de.funknetz.server;

// --- Importe
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: TimeServer --
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
*	Eine Instanz der Klasse TimeServer dient der Zeitsteuerung des Funknetz-Systems.
*	Es wird eine Tabelle mit Zeitereignissen verwaltet, welche auch lokal auf der 
*	Festplatte in einer Datei gespeichert wird. Es koennen neue Eintraege in die Tabelle
*	aufgenommen werden oder alte geloescht werden. Auch die Manipulation von Eintraegen
*	ist moeglich. Stimmt die momentane Uhrzeit und das Datum mit einem der Zeitereignisse
*	in der Tabelle ueberein, wird dieses ausgefuehrt. Auch die Angabe von Intervallen, in denen
*	die einzelnen Ereignisse ausgefuehrt werden sollen ist moeglich.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
public class TimeServer {
	
	// --- Attribute
	private Object[][] timeTable_ref;
	private static int number_class;
	private Timer executionTimer_ref;
	
	// --- Konstruktoren
	
	/**
	*	liest zu Beginn die Tabelle mit Zeitereignissen ein und startet den Timer.
	*	@param useGui ob der TimeServer mit oder ohne GUI gestartet werden soll
	**/
	public TimeServer() {
		executionTimer_ref = new Timer("FNTimer");
		this.read();
		this.scheduleTimer();
	} //endconstructor
	
	// --- Methoden

	/**
	*	setzt die Tabelle mit Zeitereignissen auf das uebergebene Feld und speichert sie.
	*	Zuvor bringt die Methode die Felder noch in das korrekte Format.
	*	Uebergeben wird der Methode ein zweidimensionales String-Feld.
	*	Zum Schreiben wird die Methode write() verwendet. Am ende wird
	*	zudem neu ein neuer Timer mit den geaenderten Werten erzeugt.
	*	@see #scheduleTimer()
	*	@see #write()
	*	@param timeTable_ref Tabelle mit Zeitereignissen in Stringform
	**/
	public void setTimeTable (String[][] timeTable_ref) {
		this.timeTable_ref = new Object[timeTable_ref.length][6];
		int year, month, day, hour, minute = 0;
		String[] date_ref;
		String[] time_ref;
		Calendar c_ref;
		for (int i = 0; i < timeTable_ref.length; i++) {
			c_ref = Calendar.getInstance();
			date_ref = timeTable_ref[i][0].split("\\.");
			day = Integer.parseInt(date_ref[0]);
			month = -1 + Integer.parseInt(date_ref[1]);
			year = Integer.parseInt(date_ref[2]);
			
			time_ref = timeTable_ref[i][1].split(":");
			hour = Integer.parseInt(time_ref[0]);
			minute = Integer.parseInt(time_ref[1]);
			
			c_ref.set(year, month, day, hour, minute, 0);			// Inhalt des ersten Feldes fuer tabelTemp_ref erzeugt
			
			Object[] line_ref = {c_ref, timeTable_ref[i][2], timeTable_ref[i][3], timeTable_ref[i][4], timeTable_ref[i][5], timeTable_ref[i][6]};
			
			this.timeTable_ref[i] = line_ref.clone();
		} //endfor
		write();
		scheduleTimer();
	} //endmethod setTimeTable
	
	/**
	*	liefert die momentan aktuelle Tabelle mit Zeitereignissen.
	*	Hierzu liest die Methode die Tabelle von der Festplatte ein und konvertiert
	*	jedes einzelne Feld in Stringform.
	*	@return aktuelle Tabelle mit Zeitereignissen in Stringform.
	**/
	public String[][] getTimeTable() {
		read();
		String[][] returnVal_ref = new String[1][7];

		Calendar c_ref;
		String datum_ref;
		String zeit_ref;
		String[] zeitTemp_ref;
		String bezeichner_ref;
		String intervall_ref;
		String schalter_ref;
		String kanal_ref;
		String befehl_ref;
		
		int count = 0;
		for (int i = 0; i < timeTable_ref.length; i++) {
			if (timeTable_ref[i] != null) {
				count++;
			} //endif
		} //endfor
		if (count > 0) {
			returnVal_ref = new String[count][7];
			int zaehler = 0;
			for (int i = 0; i < timeTable_ref.length; i++) {
				if (timeTable_ref[i] != null) {
					c_ref = (GregorianCalendar) timeTable_ref[i][0];
					datum_ref = c_ref.get(Calendar.DAY_OF_MONTH) + "." + (c_ref.get(Calendar.MONTH)+1) + "." + c_ref.get(Calendar.YEAR);
					zeit_ref = String.format("%tT", c_ref);
					zeitTemp_ref = zeit_ref.split(":");
					zeit_ref = zeitTemp_ref[0] + ":" + zeitTemp_ref[1];
					
					bezeichner_ref = (String) timeTable_ref[i][1];
					kanal_ref = (String) timeTable_ref[i][2];
					schalter_ref = (String) timeTable_ref[i][3];
					befehl_ref = (String) timeTable_ref[i][4];
					intervall_ref = (String) timeTable_ref[i][5];
					
					String[] line_ref = {datum_ref, zeit_ref, bezeichner_ref, kanal_ref, schalter_ref, befehl_ref, intervall_ref};
					returnVal_ref[zaehler] = line_ref.clone();
					zaehler++;
				} //endif
			} //endfor
		} //endif
		return returnVal_ref;
	} //endmethod getTimeTable
	
	/**
	*	Fuehrt das TimeEvent des am uebergebenen Index in timeTable_ref gespeicherten Eintrags aus.
	*	@param position Index des Zeitereignisses in der Tabelle
	**/
	private void execute (int position) {
		int kanal = Integer.parseInt((String)timeTable_ref[position][2]) - 1;
		int schalter = Integer.parseInt((String)timeTable_ref[position][3]) - 1;
		int befehl;
		if (((String)timeTable_ref[position][4]).equals("ON")) {
			befehl = 1;
		} else {
			befehl = 0;
		} //endif
		int aufrufWert = kanal * 6;
		aufrufWert += schalter *2;
		aufrufWert += befehl;
			
		int[] meinArray_ref = FunknetzServer.makeIt(aufrufWert);
		FunknetzServer.execute(meinArray_ref);
	} //endmethod execute
	
	/**
	*	kuemmert sich um die Dateiausgabe.
	*	Die Methode speichert die aktuell in timeTable_ref gehaltene
	*	Tabelle mit Zeitereignissen in einer Datei namens tevents im
	*	aktuellen Ordner.
	**/
	private void write() {
		try {
			FileOutputStream fos_ref = new FileOutputStream("tevents");
			ObjectOutputStream oos_ref = new ObjectOutputStream(fos_ref);
			oos_ref.writeObject(timeTable_ref);
			oos_ref.close();
		} catch (IOException ex_ref) {
			ex_ref.printStackTrace();
		} //endtry
	} //endmethod write
	
	/**
	*	kuemmert sich um das Einlesen der tevents Datei.
	*	Die tevents Datei im aktuellen Ordner, welche die
	*	Tabelle mit Zeitereignissen fasst wird ausgelesen.
	*	Die Variable timeTable_ref wird auf den ausgelesenen
	*	Inhalt gesetzt. Ist bisher keine Datei vorhanden,
	*	wid ein leeres, 2-dimensionales String-Array erzeugt.
	**/
	private void read() {
		try {
			FileInputStream fis_ref = new FileInputStream("tevents");
			ObjectInputStream ois_ref = new ObjectInputStream(fis_ref);
			timeTable_ref = (Object[][]) ois_ref.readObject();
			ois_ref.close();
		} catch (FileNotFoundException ex_ref) {
			timeTable_ref = new Object[1][7];
		} catch (Exception e_ref) {
			e_ref.printStackTrace();
		} //endtry
	} //endmethod read
	
	/**
	*	Erstellt einen neuen Timer und fuellt ihn mit den Zeitereignissen in timeTable_ref
	**/
	private void scheduleTimer() {
		executionTimer_ref.cancel();
		executionTimer_ref = new Timer("FNTimer");
		number_class = 0;
		Calendar c_ref;
		long period = 0L;
		for (int i = 0; i < timeTable_ref.length; i++) {
			if (timeTable_ref[i] != null) {
				TimeEvent myEvent_ref = new TimeEvent();
				c_ref = (GregorianCalendar) timeTable_ref[i][0];
				if (timeTable_ref[i][5].equals("---")) {
					executionTimer_ref.schedule(myEvent_ref, c_ref.getTime());
				} else if (timeTable_ref[i][5].equals("T")) {
					period = 24L * 60L * 60L * 1000L;
					executionTimer_ref.scheduleAtFixedRate(myEvent_ref, c_ref.getTime(), period);
				} else if (timeTable_ref[i][5].equals("W")) {
					period = 7L * 24L * 60L * 60L * 1000L;
					executionTimer_ref.scheduleAtFixedRate(myEvent_ref, c_ref.getTime(), period);
				} //endif
			} //endif
		} //endfor
	} //endmethod scheduleTimer
	
	// --- Innere Klassen
	
	/**
	*	Ein ZeitEreignis: repraesentiert einen bestimmten Punkt in der Zeit,
	*	zu dem ein bestimmter Befehl an die FunknetzBox geschickt wird.
	*	Der dem Event zugehoerige Befehle wird der timeTable_ref entnommen.
	*	Nach der Ausfuehrung wird der entsprechende Eintrag sowohl aus dem Timer,
	*	als auch aus der timeTable_ref entfernt.
	**/
	class TimeEvent extends TimerTask {
		private int number;
		
		TimeEvent() {
				this.number = number_class;
				number_class++;
		} //endconstructor
		
		public void run() {
			number_class--;
			execute(this.number);
			if (number_class > -1) {
				timeTable_ref[number] = null;
			} //endif
			write();
			this.cancel();
			executionTimer_ref.purge();
		} //endmethod run
	} //endclass EventTimer
	
} //endclass TimeServer