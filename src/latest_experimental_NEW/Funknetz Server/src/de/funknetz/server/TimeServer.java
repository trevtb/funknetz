package de.funknetz.server;

// --- Importe
import java.util.*;
import java.io.*;

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
	-- 	--
	-- SYSTEM ENVIRONMENT 						--
	-- OS			Ubuntu 9.10 (Linux 2.6.31)	--	
	-- SOFTWARE 	JDK 1.6.15   				--
	-- 	--
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
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class TimeServer {
	
	// --- Attribute
	private static Object[][] timeTable_class;
	private Timer executionTimer_ref;
	public static boolean isLocked_class;
	
	// --- Konstruktoren
	
	/**
	*	liest zu Beginn die Tabelle mit Zeitereignissen ein und startet den Timer.
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
		timeTable_class = new Object[timeTable_ref.length][];
		int year, month, day, hour, minute = 0;
		String[] date_ref;
		String[] time_ref;
		GregorianCalendar c_ref;
		String type_ref;
		int count = 0;
		for (int i = 0; i < timeTable_ref.length; i++) {
			c_ref = (GregorianCalendar)Calendar.getInstance();
			date_ref = timeTable_ref[i][0].split("\\.");
			day = Integer.parseInt(date_ref[0]);
			month = -1 + Integer.parseInt(date_ref[1]);
			year = Integer.parseInt(date_ref[2]);
					
			time_ref = timeTable_ref[i][1].split(":");
			hour = Integer.parseInt(time_ref[0]);
			minute = Integer.parseInt(time_ref[1]);
					
			c_ref.set(year, month, day, hour, minute, 0);			// Inhalt des ersten Feldes fuer tabelTemp_ref erzeugt
			type_ref = timeTable_ref[i][7];
			String uid_ref = new java.rmi.server.UID().toString();
				
			Object[] line_ref = {c_ref, timeTable_ref[i][2], timeTable_ref[i][3], timeTable_ref[i][4], timeTable_ref[i][5], timeTable_ref[i][6], type_ref, uid_ref};
					
			timeTable_class[count] = line_ref.clone();
			count++;
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
		String[][] returnVal_ref = new String[1][];

		GregorianCalendar c_ref;
		String datum_ref;
		String zeit_ref;
		String[] zeitTemp_ref;
		String bezeichner_ref;
		String intervall_ref;
		String schalter_ref;
		String kanal_ref;
		String befehl_ref;
		String type_ref;
		
		int count = 0;
		for (int i = 0; i < timeTable_class.length; i++) {
			if (timeTable_class[i] != null) {
				count++;
			} //endif
		} //endfor
		if (count > 0) {
			returnVal_ref = new String[count][8];
			int zaehler = 0;
			for (int i = 0; i < timeTable_class.length; i++) {
				if (timeTable_class[i] != null) {
					c_ref = (GregorianCalendar) timeTable_class[i][0];
					datum_ref = c_ref.get(Calendar.DAY_OF_MONTH) + "." + (c_ref.get(Calendar.MONTH)+1) + "." + c_ref.get(Calendar.YEAR);
					zeit_ref = String.format("%tT", c_ref);
					zeitTemp_ref = zeit_ref.split(":");
					zeit_ref = zeitTemp_ref[0] + ":" + zeitTemp_ref[1];	
					
					bezeichner_ref = (String) timeTable_class[i][1];
					kanal_ref = (String) timeTable_class[i][2];
					schalter_ref = (String) timeTable_class[i][3];
					befehl_ref = (String) timeTable_class[i][4];
					intervall_ref = (String) timeTable_class[i][5];
					type_ref = (String) timeTable_class[i][6];
						
					String[] line_ref = {datum_ref, zeit_ref, bezeichner_ref, kanal_ref, schalter_ref, befehl_ref, intervall_ref, type_ref};
					returnVal_ref[zaehler] = line_ref.clone();
					zaehler++;
				} //endif
			} //endfor
		} //endif
		return returnVal_ref;
	} //endmethod getTimeTable
	
	/**
	*	Fuehrt das TimeEvent des am uebergebenen Index in timeTable_class gespeicherten Eintrags aus.
	*	@param array_ref Referenz auf das dem Event zugehoerige Array in der timeTable_ref.
	**/
	private void execute (Object[] array_ref) {
		int kanal = Integer.parseInt((String)array_ref[2]) - 1;
		int schalter = Integer.parseInt((String)array_ref[3]) - 1;
		int befehl;
		if (((String)array_ref[4]).equals("ON")) {
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
	*	Die Methode speichert die aktuell in timeTable_class gehaltene
	*	Tabelle mit Zeitereignissen in einer Datei namens tevents im
	*	aktuellen Ordner.
	**/
	private void write() {
		try {
			FileOutputStream fos_ref = new FileOutputStream("tevents");
			ObjectOutputStream oos_ref = new ObjectOutputStream(fos_ref);
			oos_ref.writeObject(timeTable_class);
			oos_ref.close();
		} catch (IOException ex_ref) {
			ex_ref.printStackTrace();
		} //endtry
	} //endmethod write
	
	/**
	*	kuemmert sich um das Einlesen der tevents Datei.
	*	Die tevents Datei im aktuellen Ordner, welche die
	*	Tabelle mit Zeitereignissen fasst wird ausgelesen.
	*	Die Variable timeTable_class wird auf den ausgelesenen
	*	Inhalt gesetzt. Ist bisher keine Datei vorhanden,
	*	wid ein leeres, 2-dimensionales String-Array mit der 
	*	Laenge 1 erzeugt.
	**/
	private void read() {
		try {
			FileInputStream fis_ref = new FileInputStream("tevents");
			ObjectInputStream ois_ref = new ObjectInputStream(fis_ref);
			timeTable_class = (Object[][]) ois_ref.readObject();
			ois_ref.close();
			if (timeTable_class != null) {
				if (timeTable_class[0] != null) {
					GregorianCalendar tempCal_ref = (GregorianCalendar)Calendar.getInstance();
					tempCal_ref.setTime(new Date());
					for (Object[] elem_ref : timeTable_class) {
						if (((GregorianCalendar)elem_ref[0]).getTimeInMillis() < tempCal_ref.getTimeInMillis()) {
							GregorianCalendar newDate_ref = ((GregorianCalendar)elem_ref[0]);
							newDate_ref.add(Calendar.DAY_OF_MONTH, 7);
							elem_ref[0] = newDate_ref;
						} //endif
					} //endfor
				} //endif
			} //endif
		} catch (FileNotFoundException ex_ref) {
			timeTable_class = new Object[1][];
			write();
		} catch (Exception e_ref) {
			e_ref.printStackTrace();
		} //endtry
	} //endmethod read
	
	/**
	*	Erstellt einen neuen Timer und fuellt ihn mit den Zeitereignissen in timeTable_class
	**/
	private void scheduleTimer() {
		executionTimer_ref.cancel();
		executionTimer_ref.purge();
		executionTimer_ref = new Timer("FNTimer");
		long period = 0L;
		for (int i = 0; i < timeTable_class.length; i++) {	
			if (timeTable_class[i] != null) {
				GregorianCalendar c_ref = (GregorianCalendar) timeTable_class[i][0];
				c_ref.set(Calendar.SECOND, 0);
				if (timeTable_class[i][5].equals("---")) {
					TimeEvent myEvent_ref = new TimeEvent((String)timeTable_class[i][7]);	
					executionTimer_ref.schedule(myEvent_ref, c_ref.getTime());
				} else if (timeTable_class[i][5].equals("T")) {
					period = 24L * 60L * 60L * 1000L;
					TimeEvent myEvent_ref = new TimeEvent((String)timeTable_class[i][7], "t");	
					executionTimer_ref.scheduleAtFixedRate(myEvent_ref, c_ref.getTime(), period);
				} else if (timeTable_class[i][5].equals("W")) {
					period = 7L * 24L * 60L * 60L * 1000L;
					TimeEvent myEvent_ref = new TimeEvent((String)timeTable_class[i][7], "w");	
					executionTimer_ref.scheduleAtFixedRate(myEvent_ref, c_ref.getTime(), period);
				} else if (timeTable_class[i][5].equals("WE")) {
					CalendarHelper calHelper_ref = new CalendarHelper();
					GregorianCalendar[] dates_ref = calHelper_ref.getWeekend(c_ref);
					period = 7L * 24L * 60L * 60L * 1000L;
					for (int j=0;j<dates_ref.length;j++) {
						TimeEvent myEvent_ref = new TimeEvent((String)timeTable_class[i][7], "we");
						executionTimer_ref.scheduleAtFixedRate(myEvent_ref, dates_ref[j].getTime(), period);
					} //endfor
				} else if (timeTable_class[i][5].equals("WT")) {
					CalendarHelper calHelper_ref = new CalendarHelper();
					GregorianCalendar[] dates_ref = calHelper_ref.getWorkDays(c_ref);
					period = 7L * 24L * 60L * 60L * 1000L;
					for (int j=0;j<dates_ref.length;j++) {
						TimeEvent myEvent_ref = new TimeEvent((String)timeTable_class[i][7], "wt");
						executionTimer_ref.scheduleAtFixedRate(myEvent_ref, dates_ref[j].getTime(), period);
					} //endfor
				} //endif
			} //endif
		} //endfor
	} //endmethod scheduleTimer
	
	// --- Innere Klassen
	
	/**
	*	Ein ZeitEreignis: repraesentiert einen bestimmten Punkt in der Zeit,
	*	zu dem ein bestimmter Befehl an die FunknetzBox geschickt wird.
	*	Der dem Event zugehoerige Befehl wird der timeTable_ref entnommen.
	*	Nach der Ausfuehrung wird der entsprechende Eintrag sowohl aus dem Timer,
	*	als auch aus der timeTable_ref entfernt (== null gesetzt).
	**/
	class TimeEvent extends TimerTask {
		private String uid_ref;
		private String type_ref;
		
		TimeEvent(String uid_ref) {
				this.uid_ref = uid_ref;
				this.type_ref = "once";
		} //endconstructor
		
		TimeEvent(String uid_ref, String type_ref) {
			this.uid_ref = uid_ref;
			this.type_ref = type_ref;
		} //endconstructor
		
		public void run() {
			for (int i = 0; i < timeTable_class.length; i++) {
				if (timeTable_class[i] != null && ((String)timeTable_class[i][7]).equals(uid_ref)) {
					if (type_ref.equals("once")) {
						execute(timeTable_class[i]);
						timeTable_class[i] = null;
						this.cancel();
					} else {
						execute(timeTable_class[i]);
					} //endif
				} //endif
			} //endfor
			write();
			executionTimer_ref.purge();
		} //endmethod run
	} //endclass EventTimer
	
} //endclass TimeServer