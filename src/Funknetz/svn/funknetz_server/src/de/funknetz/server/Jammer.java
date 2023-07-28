package de.funknetz.server;

/*	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------
	--| Copyright (c) by Tobias Burkard, 2009	      |--
	---------------------------------------------------------------------------------
	-- --
	-- CLASS: Jammer --
	-- --
	---------------------------------------------------------------------------------
	-- --
	-- PROJECT: Funknetz Server--
	-- --
	---------------------------------------------------------------------------------
	-- 	--
	-- SYSTEM ENVIRONMENT 					--
	-- OS		Ubuntu 9.10 (Linux 2.6.31)	--	
	-- SOFTWARE 	JDK 1.6.15   			--
	-- 	--
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Stoersender: Serverversion, wird durch Client ferngesteuert.
*	Der Stoersender sendet im 1-Sekunden-Abstand jeweils
*	ein ON und ein OFF Signal an die FunknetzBox.
*	Hierdurch wird der Schalter auf dem jeweiligen Kanal fuer
*	die Dauer des Betriebs gestoert.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
public class Jammer {
	
	// --- Attribute
	private static boolean isActive_class = false;		// Status des Jammers
	private int[] onCom_ref;							// ON-Befehl fuer den Mikroprozessor
	private int[] offCom_ref;							// OFF-Befehl fuer den Mikroprozessor
	private boolean useGui;								// Ob ein GUI benutzt wird
	
	// --- Konstruktoren
	
	/**
	 * Konstruiert einen neuen Jammer (mit oder ohne GUI).
	 * @param useGui ob ein GUI benutzt wird oder nicht.
	 */
	public Jammer (boolean useGui) {
		this.useGui = useGui;
	} //endconstructor
	
	/**
	 * Standardkonstruktor: erzeugt einen neuen Jammer ohne GUI.
	 */
	public Jammer () {
		this(false);
	} //endconstructor


	
	// --- Methoden
	
	/**
	*	startet den Jammer als eigenstaendigen Prozess.
	*	Hierzu wird eine neue Instanz von StartListening,
	*	einem Runnable-Objekt erzeugt.
	*	@param onCom_ref Code fuer den Mikroprozessor, ON-Befehl. (int Form)
	*	@param offCom_ref Code fuer den Mikroprozessor, OFF-Befehl. (int Form)
	**/
	public void startJam(int[] onCom_ref, int[] offCom_ref) {
		this.onCom_ref = onCom_ref;
		this.offCom_ref = offCom_ref;
		try {
			Thread tt = new Thread(new JamStart());
			tt.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		} //endtry
	} //endmethod startJam
	
	/**
	*	Die eigentliche Jammer Instanz als eigenstaendiger Prozess.
	*	Die Ausgabemeldungen haengen von der Verwendung eines GUIs ab.
	*	Wird ueber die Methode startListening() instantiiert.
	*	@see #startJam(int[] onCom_ref, int[] offCom_ref)
	**/
	class JamStart implements Runnable {
		public void run() {
			Jammer.isActive_class = true;
			
			if (!useGui)  {
				System.out.println("\nJammer gestartet ...");
				System.out.println("=========================================");
				try {
					while (Jammer.isActive_class) {
						System.out.println("== SENDE = 'ON' = ==");
				  		FunknetzServer.execute(onCom_ref);
				  		Thread.sleep(2000);
				  		System.out.println("== SENDE = 'OFF' = ==");
				  		FunknetzServer.execute(offCom_ref);
				 		Thread.sleep(2000);
				  	} //endwhile
				} catch (Exception ex_ref) {
					ex_ref.printStackTrace();
				} //endtry
			} else if (useGui) {
				FunknetzServerGui.setStatusText("Jammer gestartet ...\n");
				try {
					while (Jammer.isActive_class) {
						FunknetzServerGui.setStatusText("== SENDE = 'ON' = ==");
						FunknetzServer.execute(onCom_ref);
						Thread.sleep(2000);
						FunknetzServerGui.setStatusText("== SENDE = 'OFF' = ==");
						FunknetzServer.execute(offCom_ref);					
						Thread.sleep(2000);
					} //endwhile
				} catch (Exception ex_ref) {
					ex_ref.printStackTrace();
				} //endtry
			} //endif
		} //endmethod run
	} //endclass JamStart
	
	/**
	*	Liefert den Status des Jammers zurueck.
	*	Der Rueckgabewert gibt Auskunft darueber,
	*	ob der Jammer momentan aktiv ist oder nicht.
	*	@return Status des Jammers.
	**/
	public boolean getStatus() {
		return isActive_class;
	} //endmethod getStatus
	
	/**
	*	Haelt den Jammer an.
	**/
	public void stopJam() {
		isActive_class = false;
		if (useGui) {
			FunknetzServerGui.setStatusText("Jammer wird angehalten...\n\n");
		} else if (!useGui) {
			System.out.println("Jammer wird angehalten...");
			System.out.println("=========================================\n");
		} //endif
	} //endmethod stopJam
} //endclass Jammer