package de.funknetz.client;

// --- Importe
import java.io.*;

/*	---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
--| Copyright (c) by Tobias Burkard, 2009	      |--
---------------------------------------------------------------------------------
-- --
-- CLASS: Device --
-- --
---------------------------------------------------------------------------------
-- --
-- PROJECT: Funknetz Client/Server--
-- --
---------------------------------------------------------------------------------
-- --
-- SYSTEM ENVIRONMENT 						--
-- OS			Ubuntu 9.10 (Linux 2.6.31)	--
-- SOFTWARE 	JDK 1.6.14 					--
-- --
---------------------------------------------------------------------------------
---------------------------------------------------------------------------------	*/

/**	Repraesentiert ein Geraet fuer die Funknetzbox.
*
* 	@version 0.3 von 11.2009
*
* 	@author Tobias Burkard
**/
class Device implements Serializable {
	private static final long serialVersionUID = 1L;
	String type_ref;
	String name_ref;
	String[] deviceInfo_ref;
	
	// --- Konstruktoren
	Device(String type_ref, String name_ref, String[] deviceInfo_ref) {
		this.type_ref = type_ref;
		this.name_ref = name_ref;
		this.deviceInfo_ref = deviceInfo_ref;
	} //endconstructor
	
	Device(String type_ref, String name_ref) {
		this.type_ref = type_ref;
		this.name_ref = name_ref;
		this.deviceInfo_ref = new String[1];
		deviceInfo_ref[0] = "none";
	} //endconstructor
	
	// --- Methoden
	
	// -- Getter und Setter
	String getType() {
		return type_ref;
	} //endmethod getType
	
	void setType(String type_ref) {
		this.type_ref = type_ref;
	} //endmethod setType
	
	String getName() {
		return name_ref;
	} //endclass getName
	
	void setName(String name_ref) {
		this.name_ref = name_ref;
	} //endmethod setName
	
	String[] getDeviceInfo() {
		return deviceInfo_ref;
	} //endmethod getDeviceInfo
	
	void setDeviceInfo(String[] deviceInfo_ref) {
		this.deviceInfo_ref = deviceInfo_ref;
	} //endmethod setDeviceInfo
} //endclass Device
