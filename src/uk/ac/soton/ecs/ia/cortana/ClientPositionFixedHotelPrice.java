package uk.ac.soton.ecs.ia.cortana;

import java.util.List;

public class ClientPositionFixedHotelPrice extends ClientPosition {

	public double pricePerNight;
	
	public ClientPositionFixedHotelPrice(ClientPreference client, FlightAuction inFlight, FlightAuction outFlight, List<HotelAuction> hotels, double pricePerNight) {
		super(client, inFlight, outFlight, hotels);
		this.pricePerNight = pricePerNight;
	}

}
