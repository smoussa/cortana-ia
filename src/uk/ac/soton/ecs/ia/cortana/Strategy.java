package uk.ac.soton.ecs.ia.cortana;

import java.util.HashMap;
import java.util.Map;

import se.sics.tac.aw.TACAgent;

public class Strategy {

	protected Map<Auction, Position> auctionPositions;
	
	protected AuctionMaster auctionMaster;
	
	public Strategy(AuctionMaster auctionMaster) {
		this.auctionMaster = auctionMaster;
		this.auctionPositions = new HashMap<Auction, Position>();
	}
	
	public void sendBids(TACAgent agent) {
		for(Position position:this.auctionPositions.values()) {
			position.bidMe(agent);
		}
	}

	public boolean isStrategyValid() {
		
		for(Position position:this.auctionPositions.values()) {
			if(!position.isValid())
				return false;
		}
		
		return true;
		
	}
	
	public boolean isStrategySatisfied() {
				
		for(Position position:this.auctionPositions.values()) {
			if(!position.isFullySatisfied())
				return false;
		}
		
		return true;
	}
	
}
