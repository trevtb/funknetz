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
	-- 							--
	-- SYSTEM ENVIRONMENT 		--
	-- OS		Linux 2.6.28-14	--	
	-- SOFTWARE 	JDK 1.6.14   		--
	-- 							--
	---------------------------------------------------------------------------------
	---------------------------------------------------------------------------------	*/
	
/**	
*	Stoersender: Serverversion, wird durch Client ferngesteuert.
*	Der Stoersender sendet im 1-Sekunden-Abstand jeweils
*	ein ON und ein OFF Signal an die FunknetzBox.
*	Hierdurch wird der Schalter auf dem jeweiligen Kanal fuer
*	die Dauer des Betriebs gestoert.
*
* 	@version 0.2 von 08.2009
*
* 	@author Tobias Burkard
**/
public class Jammer {
	
	// --- Attribute
	private static boolean isActive_class = false;			// Status des Jammers
	private boolean useGui;							// Ob ein GUI benutzt werden soll, oder nicht
	private int[] onCom_ref;							// ON-Befehl fuer den Mikroprozessor
	private int[] offCom_ref;							// OFF-Befehl fuer den Mikroprozessor
	
	// --- Konstruktoren
	
	/**
	*	erzeugt einen neuen Jammer und erzeugt auf Wunsch ein GUI.
	*	lautet der uebergebene Parameter true, wird ein GUI erzeugt.
	*	@param useGui ob ein GUI benutzt werden soll oder nicht.
	**/
	public Jammer (boolean useGui) {
		this.useGui = useGui;
	} //endconstructor
	
	/**
	*	Standardkonstruktor: erzeugt eine neue Jammer-Instanz ohne GUI.
	**/
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
		
				while (Jammer.isActive_class) {
					System.out.println("== SENDE = 'ON' = ==");
					
					//for (int i = 0; i < 2; i++) {
						FunknetzServer.execute(onCom_ref);
					//} //endfor
					
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						ex.printStackTrace();
					} //endtry
					
					System.out.println("== SENDE = 'OFF' = ==");
					
					//for (int i = 0; i < 2; i++) {
						FunknetzServer.execute(offCom_ref);
					//} //endfor
					
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						ex.printStackTrace();
					} //endtry
				} //endwhile
			} else if (useGui) {
				
				FunknetzServerGui.setStatusText("Jammer gestartet ...\n");
				
				while (Jammer.isActive_class) {
					FunknetzServerGui.setStatusText("== SENDE = 'ON' = ==");
					
					//for (int i = 0; i < 2; i++) {
						FunknetzServer.execute(onCom_ref);
					//} //endfor
					
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						ex.printStackTrace();
					} //endtry
					
					FunknetzServerGui.setStatusText("== SENDE = 'OFF' = ==");
					
					//for (int i = 0; i < 2; i++) {
						FunknetzServer.execute(offCom_ref);
					//} //endfor
					
					try {
						Thread.sleep(1000);
					} catch (Exception ex) {
						ex.printStackTrace();
					} //endtry
				} //endwhile
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
		} else {
			System.out.println("Jammer wird angehalten...");
			System.out.println("=========================================\n");
		} //endif
	} //endmethod stopJam
} //endclass Jammer