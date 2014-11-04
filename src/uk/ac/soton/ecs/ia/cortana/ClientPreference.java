package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.DayEnum;

public class ClientPreference {

	public final int CLIENT_ID;
	public DayEnum inFlight, outFlight;
		
	//TODO add bonus info
	public ClientPreference(int clientId, DayEnum inFlight, DayEnum outFlight) {
		this.CLIENT_ID = clientId;
		this.inFlight = inFlight;
		this.outFlight = outFlight;
	}
	
}
