package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;

public class ClientPositionVariableHotelPrice extends ClientPosition {

	// Prices per hotel auction
	public Map<HotelAuction, Double> clientHotelPrices;
	
	public ClientPositionVariableHotelPrice(ClientPreference client,
											FlightAuction inFlight, FlightAuction outFlight,
											Map<HotelAuction, Double> clientHotelPrices,
											List<EntertainmentAuction> eAuctions) {
		super(client, inFlight, outFlight, new ArrayList<>(clientHotelPrices.keySet()), eAuctions);
		this.clientHotelPrices = clientHotelPrices;
	}
	
	public Double getHotelPriceForAuction(HotelAuction hotelAuction) {
		return clientHotelPrices.get(hotelAuction);
	}

}
