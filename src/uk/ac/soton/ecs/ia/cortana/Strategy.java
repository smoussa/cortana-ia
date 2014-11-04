package uk.ac.soton.ecs.ia.cortana;

import java.util.HashMap;
import java.util.Map;

import se.sics.tac.aw.TACAgent;

public class Strategy {

	//private List<Position> satisfiedPositions;
	//private List<Position> positions;
	protected Map<Auction, Position> auctionPositions;
	
	protected AuctionMaster auctionMaster;
	
	public Strategy(AuctionMaster auctionMaster) {
		this.auctionMaster = auctionMaster;
		//this.satisfiedPositions = new ArrayList<Position>();
		//this.positions = new ArrayList<Position>();
		this.auctionPositions = new HashMap<Auction, Position>();
	}
	
	public void sendBids(TACAgent agent) {
		for(Position position:this.auctionPositions.values()) {
			position.bidMe(agent);
		}
	}

//	private void updateSatisfiedPositions() {
//		Iterator<Position> posIt = positions.iterator();
//		
//		while(posIt.hasNext()) {
//			Position next = posIt.next();
//			if(next.isFullySatisfied) {
//				posIt.remove();
//				satisfiedPositions.add(next);
//			}
//		}
//	}
//	
//	public boolean isStrategyValid() {
//
//		updateSatisfiedPositions();
//		
//		for(Position position:this.satisfiedPositions) {
//			if(!position.isValid())
//				return false;
//		}
//		
//		return true;
//		
//	}
//	
//	public boolean isStrategySatisfied() {
//		
//		updateSatisfiedPositions();
//		
//		if(this.positions.size() > 0)
//			return false;
//		
//		return true;
//	}
	
}
