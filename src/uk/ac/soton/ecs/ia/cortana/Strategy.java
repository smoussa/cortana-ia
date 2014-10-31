package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import se.sics.tac.aw.TACAgent;

public class Strategy {

	private List<Position> satisfiedPositions;
	private List<Position> positions;
	
	public Strategy() {
		this.satisfiedPositions = new ArrayList<Position>();
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

	private void updateSatisfiedPositions() {
		Iterator<Position> posIt = positions.iterator();
		
		while(posIt.hasNext()) {
			Position next = posIt.next();
			if(next.isFullySatisfied) {
				posIt.remove();
				satisfiedPositions.add(next);
			}
		}
	}
	
	public boolean isStrategyValid() {

		updateSatisfiedPositions();
		
		for(Position position:this.satisfiedPositions) {
			if(!position.isValid())
				return false;
		}
		
		return true;
		
	}
	
	public boolean isStrategySatisfied() {
		
		updateSatisfiedPositions();
		
		if(this.positions.size() > 0)
			return false;
		
		return true;
	}
	
}
