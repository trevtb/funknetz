package de.funknetz.server;

// --- Importe
import java.util.*;

/*---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
--| Copyright (c) by Tobias Burkard, 2009	      |--
---------------------------------------------------------------------------------
-- --
-- CLASS: CalendarHelper --
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
*	Eine Instanz der Klasse CalendarHelper dient als Hilfswerkzeug zur Zeitberechnung
*	im Gregorianischen Kalender.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class CalendarHelper {
	
	// --- Methoden
	/**
	 * Die Methode errechnet mittels eines Algorithmus zur Wochentagsbestimmung
	 * im Gregorianischen Kalender den dem Datum zugehoerigen Wochentag
	 * und liefert diesen als int-Wert zurueck.
	 * Mo=1 ; Di=2; Mi=3; Do=4; Fr=5; Sa=6; So=7
	 * @param year das Jahr
	 * @param month der Monat
	 * @param day der Tag
	 * @return der zugehoerige Wochentag.
	 */
	private int getWochentag(int year, int month, int day) {
		int tagesziffer;
		int[] monatsziffern = {0,3,3,6,1,4,6,2,5,0,3,5};
		int jahresziffer;
		int jahrhundertziffer;
		boolean isSchaltjahr;
		int wochentag;
		GregorianCalendar calendar_ref = (GregorianCalendar) Calendar.getInstance();
		calendar_ref.set(year, (month-1), day);
		tagesziffer = (calendar_ref.get(Calendar.DAY_OF_MONTH)) % 7;
		jahresziffer = (calendar_ref.get(Calendar.YEAR)) % 100;
		jahresziffer = (jahresziffer + (jahresziffer / 4)) % 7;
		jahrhundertziffer = calendar_ref.get(Calendar.YEAR) - ((calendar_ref.get(Calendar.YEAR)) % 100);
		jahrhundertziffer = jahrhundertziffer / 100;
		jahrhundertziffer = (3 - (jahrhundertziffer % 4)) * 2;
		if (((calendar_ref.get(Calendar.YEAR)) % 4) == 0) {
			isSchaltjahr = true;
		} else {
			isSchaltjahr = false;
		} //endif
		wochentag = tagesziffer + monatsziffern[calendar_ref.get(Calendar.MONTH)] + jahresziffer + jahrhundertziffer;
		if (isSchaltjahr && ((calendar_ref.get(Calendar.MONTH)) < 2)) {
			wochentag = wochentag - 1;
		} //endif
		wochentag = wochentag % 7;
		
		return wochentag;	
	} //endmethod start	
	
	/**
	 * Ruft die Methode getWochentag auf, um den Wochentag zu bestimmen und
	 * legt dann ein GregorianCalendar-Array an, welches den folgenden Samstag und Sonntag
	 * beinhaltet bzw. auch den aktuellen, falls die Zeit des der Methode 
	 * übergebenen GregorianCalendar ein Sams- bzw. Sonntag ist und einen späteren Zeitpunkt als -JETZT- darstellt.
	 * @param c_ref das Datum des Tages, dessen folgendes Wochenende bestimmt werden soll.
	 * @return Array mit dem folgenden Wochenende bzw. auch dem aktuellen, falls der uebergebene Zeitpunkt des Tages in der Zukunft liegt.
	 * 
	 * @see #getWochentag(int, int, int)
	 */
	public GregorianCalendar[] getWeekend(GregorianCalendar c_ref) {
		int year = c_ref.get(Calendar.YEAR);
		int month = c_ref.get(Calendar.MONTH);
		int day = c_ref.get(Calendar.DAY_OF_MONTH);
		int hour = c_ref.get(Calendar.HOUR_OF_DAY);
		int minute = c_ref.get(Calendar.MINUTE);
		GregorianCalendar[] dateArray_ref = new GregorianCalendar[2];
		dateArray_ref[0] = (GregorianCalendar) Calendar.getInstance();
		dateArray_ref[1] = (GregorianCalendar) Calendar.getInstance();
		int wochentag = getWochentag(year, (month+1), day);
		for (GregorianCalendar cal_ref : dateArray_ref) {
			cal_ref.set(year, month, day, hour, minute, 0);
		} //endfor
		dateArray_ref[0].add(Calendar.DAY_OF_MONTH, (6 - wochentag));
		dateArray_ref[1].add(Calendar.DAY_OF_MONTH, (7 - wochentag));
		GregorianCalendar compareCal_ref = (GregorianCalendar) Calendar.getInstance();
		compareCal_ref.setTime(new Date());
		for (int i=0; i<dateArray_ref.length; i++) {
			if (dateArray_ref[i].getTimeInMillis() <= compareCal_ref.getTimeInMillis()) {
				dateArray_ref[i].add(Calendar.DAY_OF_MONTH, 7);
			} //endif	
		} //endfor
		return dateArray_ref;
	} //endmethod getWeekend
	
	/**
	 * Ruft die Methode getWochentag auf, um den Wochentag zu bestimmen und
	 * legt dann ein GregorianCalendar-Array an, welches alle folgenden
	 * Werktage bzw. auch den aktuellen Werktag, falls die Zeit des der Methode 
	 * übergebenen GregorianCalendar einen späteren Zeitpunkt als -JETZT- darstellt,
	 * beinhaltet. Es handelt sich dabei NUR um Werktage, das Array beinhaltet keine
	 * Wochenenden.
	 * @param c_ref das Datum des Tages, dessen folgende Werktage bestimmt werden sollen.
	 * @return Array mit den folgenden Werktagen bzw. auch dem aktuellen, falls der uebergebene Zeitpunkt des Tages in der Zukunft liegt.
	 * 
	 * @see #getWochentag(int, int, int)
	 */
	public GregorianCalendar[] getWorkDays(GregorianCalendar c_ref) {
		int year = c_ref.get(Calendar.YEAR);
		int month = c_ref.get(Calendar.MONTH);
		int day = c_ref.get(Calendar.DAY_OF_MONTH);
		int hour = c_ref.get(Calendar.HOUR_OF_DAY);
		int minute = c_ref.get(Calendar.MINUTE);
		GregorianCalendar[] dateArray_ref = new GregorianCalendar[5];
		for (int i=0; i<dateArray_ref.length;i++) {
			dateArray_ref[i] = (GregorianCalendar) Calendar.getInstance();
		} //endfor	
		int wochentag = getWochentag(year, (month+1), day);
		for (GregorianCalendar cal_ref : dateArray_ref) {
			cal_ref.set(year, month, day, hour, minute, 0);
		} //endfor	
		for (int i=0; i<dateArray_ref.length;i++) {
			dateArray_ref[i].add(Calendar.DAY_OF_MONTH,((i+1) - wochentag));
			GregorianCalendar compareCal_ref = (GregorianCalendar) Calendar.getInstance();
			compareCal_ref.setTime(new Date());
			if (dateArray_ref[i].getTimeInMillis() <= compareCal_ref.getTimeInMillis()) {
				dateArray_ref[i].add(Calendar.DAY_OF_MONTH, 7);
			} //endif	
		} //endfor	
		return dateArray_ref;
	} //endmethod getWorkDays	

} //endclass CalendarHelper
