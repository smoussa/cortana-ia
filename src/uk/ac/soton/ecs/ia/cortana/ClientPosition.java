package uk.ac.soton.ecs.ia.cortana;

import java.util.List;

import se.sics.tac.aw.TacTypeEnum;

public class ClientPosition {
	
	public ClientPreference client;
	public FlightAuction inFlight, outFlight;
	public List<HotelAuction> hotels;

	public ClientPosition(
			ClientPreference client,
			FlightAuction inFlight,
			FlightAuction outFlight,
			List<HotelAuction> hotels){
		this.inFlight = inFlight;
		this.outFlight = outFlight;
		this.hotels = hotels;
		this.client = client;
	}
	
	public double getUtility(){
		
		double ut = CortanaHeuristics.CLIENT_UTILITY;
		
		if (hotels.get(0).AUCTION_TYPE == TacTypeEnum.GOOD_HOTEL)
			ut =+ client.hotelBonus;
		
		int inFlightDayError = Math.abs(inFlight.AUCTION_DAY.getDayNumber() - client.inFlight.getDayNumber());
		int outFlightDayError = Math.abs(outFlight.AUCTION_DAY.getDayNumber() - client.outFlight.getDayNumber());
		
		ut -= 100 * (inFlightDayError + outFlightDayError);
		return ut;
	}

}
