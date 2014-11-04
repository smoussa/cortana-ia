package uk.ac.soton.ecs.ia.cortana;

import java.util.List;

public class ClientPosition {
	
	public ClientPreference client;
	
	public FlightAuction inFlight, outFlight;
	public List<HotelAuction> hotels;

	public ClientPosition(ClientPreference client, FlightAuction inFlight, FlightAuction outFlight, List<HotelAuction> hotels){
		this.inFlight = inFlight;
		this.outFlight = outFlight;
		this.hotels = hotels;
		this.client = client;
	}

}
