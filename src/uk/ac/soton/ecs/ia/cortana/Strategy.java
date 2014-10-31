package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.sics.tac.aw.TACAgent;

public class Strategy {

	private List<Position> positions;
	
	public Strategy() {
		this.positions = new ArrayList<Position>();
	}
	
	public void createPositions(Client client) {
		
		
		Auction inflight = client.inFlight;
		Auction outflight = client.outFlight;
		Collection<HotelAuction> hotelList = client.hotels.values();
		
		if(inflight.getPosition() == null) {
			Position flightPosition = new FlightPosition(inflight);
			inflight.setPosition(flightPosition);
			positions.add(flightPosition);
		}
		
		inflight.getPosition().peopleWhoWantMe.add(client);
		
		if(outflight.getPosition() == null) {
			Position flightPosition = new FlightPosition(outflight);
			outflight.setPosition(flightPosition);
			positions.add(flightPosition);
		}
		
		outflight.getPosition().peopleWhoWantMe.add(client);
		
		for(HotelAuction hotelAuction:hotelList) {
			if(hotelAuction.getPosition() == null) {
				Position hotelPosition = new HotelPosition(hotelAuction);
				hotelAuction.setPosition(hotelPosition);
				positions.add(hotelPosition);
			}
			
			hotelAuction.getPosition().peopleWhoWantMe.add(client);
		}
		
	}

	public void sendBids(TACAgent agent) {
		for(Position position:this.positions) {
			position.bidMe(agent);
		}
	}
	
}
