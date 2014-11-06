package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.sics.tac.aw.TACAgent;

public class Strategy {

	private static final int VALIDITY_WAIT_TIME = 1;
	private static final int MAX_VALIDITY = 2;

	protected Map<Auction, Position> auctionPositions;
	protected List<ClientPosition> clientPositions;
	
	protected AuctionMaster auctionMaster;
	
	Date lastTimeFailed;
	private int validity;
	
	public Strategy(AuctionMaster auctionMaster) {
		this.auctionMaster = auctionMaster;
		this.auctionPositions = new HashMap<Auction, Position>();
		this.validity = MAX_VALIDITY;
	}
	
	public void sendBids(TACAgent agent) {
		for(Position position:this.auctionPositions.values()) {
			position.bidMe(agent);
		}
	}

	public boolean isStrategyValid() {
		boolean isStrategyValid = true;
		
		List<Integer> invalidPositions = new ArrayList<>();
		
		for(Position position:this.auctionPositions.values()) {
			System.out.println("Aucion " + position.auction.AUCTION_TYPE + " " + position.auction.AUCTION_DAY.getDayNumber());
			if(!position.isValid()) {
				isStrategyValid = false;
				invalidPositions.add(position.auction.AUCTION_ID);
			}
		}
		
		if(!isStrategyValid) {
			decreaseValidity();
		}
		
		if(isStrategyValid)
			validity = MAX_VALIDITY;
		
		if(validity <= 0)
			System.out.println("REPLAN BECAUSE OF " + invalidPositions);
		
		return validity > 0;
		
	}
	
	private void decreaseValidity() {
		Date currentTime = Calendar.getInstance().getTime();
		
		// Only accept a validity failure if the last one was over X seconds ago
		if(lastTimeFailed != null) {
			long diff = currentTime.getTime() - lastTimeFailed.getTime();
	        long diffSeconds = diff / 1000 % 60;
	        
	        if(diffSeconds <= VALIDITY_WAIT_TIME) {
	        	validity--;
				lastTimeFailed = currentTime;
	        }
		}
		else {
			validity--;
			lastTimeFailed = currentTime;
		}
	}

	public boolean isStrategySatisfied() {
			
		boolean flag = true;
		for(Position position:this.auctionPositions.values()) {
			if(!position.isFullySatisfied())
				flag = false;
		}
		
		return flag;
	}
	
	public float getScore(){
		float ut = 0;
		for(ClientPosition cp: clientPositions){
			ut += cp.getUtility();
		}
		float cost = 0;
		for(Position p: auctionPositions.values()){
			cost += p.getCost();
		}
		return ut - cost;	
	}
	
}
