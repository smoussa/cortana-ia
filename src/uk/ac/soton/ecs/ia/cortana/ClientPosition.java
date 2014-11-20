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
		
		// Under predicts because fun bonus is not considered!
		
		double ut = CortanaHeuristics.CLIENT_UTILITY;
		
		if (hotels.get(0).AUCTION_TYPE == TacTypeEnum.GOOD_HOTEL)
			ut += client.hotelBonus;
		
//		System.out.println("Ut hotel " + client.hotelBonus);
		
//		System.out.println("Inflight pref " + client.inFlight.getDayNumber() + " Inflight actual " + inFlight.AUCTION_DAY.getDayNumber());
		int inFlightDayError = Math.abs(inFlight.AUCTION_DAY.getDayNumber() - client.inFlight.getDayNumber());
		
//		System.out.println("Outflight pref " + client.outFlight.getDayNumber() + " Outflight actual " + outFlight.AUCTION_DAY.getDayNumber());
		int outFlightDayError = Math.abs(outFlight.AUCTION_DAY.getDayNumber() - client.outFlight.getDayNumber());
		
		ut -= 100 * (inFlightDayError + outFlightDayError);
		
//		System.out.println("Ut inflight error -" + (100 * (inFlightDayError + outFlightDayError)));
		
		return ut;
	}

}
