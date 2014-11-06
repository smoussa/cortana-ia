package uk.ac.soton.ecs.ia.cortana;

import java.util.List;

import se.sics.tac.aw.TacTypeEnum;

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
	
	public double getUtility(){
		double ut = 1000;
		
		if (hotels.get(0).AUCTION_TYPE==TacTypeEnum.GOOD_HOTEL)
			ut =+ client.hotelBonus;
		
		ut -= 100*(Math.abs(inFlight.AUCTION_DAY.getDayNumber()-client.inFlight.getDayNumber())+Math.abs(outFlight.AUCTION_DAY.getDayNumber()-client.outFlight.getDayNumber()));
		
		return ut;
	}

}
