package se.sics.tac.aw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

	public final int CLIENT_ID;
	
	public FlightAuction inFlight, outFlight;
	
	public Map<Day, HotelAuction> hotels;
	
	public Client(int clientId, FlightAuction inFlight, FlightAuction outFlight, List<HotelAuction> hotels) {
		this.CLIENT_ID = clientId;
		this.inFlight = inFlight;
		this.outFlight = outFlight;
		this.hotels = new HashMap<>();
		
		for(HotelAuction hotel:hotels) {
			this.hotels.put(hotel.AUCTION_DAY, hotel);
		}
	}

	public double getHotelPrice() {
		return (CortanaHeuristics.CLIENT_UTILITY - inFlight.getAskingPrice() - outFlight.getAskingPrice() - CortanaHeuristics.ATTEMPTED_PROFIT_PER_CLIENT) / hotels.values().size();
	}
	
}
