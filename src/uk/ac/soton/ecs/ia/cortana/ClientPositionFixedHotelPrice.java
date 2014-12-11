package uk.ac.soton.ecs.ia.cortana;

import java.util.List;

// Not used in our bot
public class ClientPositionFixedHotelPrice extends ClientPosition {

	public double pricePerNight;
	
	public ClientPositionFixedHotelPrice(ClientPreference client, FlightAuction inFlight, FlightAuction outFlight, List<HotelAuction> hotels, double pricePerNight) {
		super(client, inFlight, outFlight, hotels, null);
		this.pricePerNight = pricePerNight;
	}

}
