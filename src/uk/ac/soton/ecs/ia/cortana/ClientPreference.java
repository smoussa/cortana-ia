package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.DayEnum;

public class ClientPreference {

	public final int CLIENT_ID;
	public DayEnum inFlight, outFlight;
	public int hotelBonus, e1Bonus, e2Bonus, e3Bonus;
		
	public ClientPreference(
			int clientId,
			DayEnum inFlight,
			DayEnum outFlight,
			int hotelBonus,
			int e1Bonus,
			int e2Bonus,
			int e3Bonus) {
		this.CLIENT_ID = clientId;
		this.inFlight = inFlight;
		this.outFlight = outFlight;
		this.hotelBonus = hotelBonus;
		this.e1Bonus = e1Bonus;
		this.e2Bonus = e2Bonus;
		this.e3Bonus = e3Bonus;
	}
	
}
