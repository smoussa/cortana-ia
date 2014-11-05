package uk.ac.soton.ecs.ia.cortana;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import se.sics.tac.aw.TACAgent;

public class Strategy {

	private static final int VALIDITY_WAIT_TIME = 1;
	private static final int MAX_VALIDITY = 2;

	protected Map<Auction, Position> auctionPositions;
	
	protected AuctionMaster auctionMaster;
	
	Date lastValidityFailure;
	private int validity;
	
	public Strategy(AuctionMaster auctionMaster) {
		this.auctionMaster = auctionMaster;
		this.auctionPositions = new HashMap<Auction, Position>();
		this.validity = 2;
	}
	
	public void sendBids(TACAgent agent) {
		for(Position position:this.auctionPositions.values()) {
			position.bidMe(agent);
		}
	}

	public boolean isStrategyValid() {
		boolean flag = true;
		
		for(Position position:this.auctionPositions.values()) {
			if(!position.isValid())
				flag = false;
		}
		
		if(!flag) {
			
			Date time = Calendar.getInstance().getTime();
			
			// Only accept a validity failure if the last one was over X seconds ago
			if(lastValidityFailure != null) {
				long diff = time.getTime() - lastValidityFailure.getTime();
		        long diffSeconds = diff / 1000 % 60;
		        
		        if(diffSeconds < VALIDITY_WAIT_TIME)
		        	return flag;
			}
			
			validity--;
			lastValidityFailure = time;
		}
		
		if(flag && validity < MAX_VALIDITY)
			validity = MAX_VALIDITY;
		
		return validity <= 0;
		
	}
	
	public boolean isStrategySatisfied() {
			
		boolean flag = true;
		for(Position position:this.auctionPositions.values()) {
			if(!position.isFullySatisfied())
				flag = false;
		}
		
		return flag;
	}
	
}
