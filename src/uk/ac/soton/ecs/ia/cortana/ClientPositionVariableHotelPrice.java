package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Map;

public class ClientPositionVariableHotelPrice extends ClientPosition {

	private Map<HotelAuction, Double> clientHotelPrices;
	
	public ClientPositionVariableHotelPrice(ClientPreference client,
											FlightAuction inFlight, FlightAuction outFlight,
											Map<HotelAuction, Double> clientHotelPrices) {
		super(client, inFlight, outFlight, new ArrayList<>(clientHotelPrices.keySet()));
		this.clientHotelPrices = clientHotelPrices;
	}
	
	public Double getHotelPriceForAuction(HotelAuction hotelAuction) {
		return clientHotelPrices.get(hotelAuction);
	}

}
