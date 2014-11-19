package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Strategy {

	private static final int VALIDITY_WAIT_TIME = 1;
	private static final int MAX_VALIDITY = 5;

	protected Map<Auction, Position> auctionPositions;
	protected List<ClientPosition> clientPositions;
	
	protected AuctionMaster auctionMaster;
	
	private Strategy oldStrategy;
	
	Date lastTimeFailed;
	private int validity;
	
	// Updated Strategy
	public Strategy(Strategy oldStrategy) {
		this.auctionMaster = oldStrategy.auctionMaster;
		this.oldStrategy = oldStrategy;
		this.auctionPositions = new HashMap<Auction, Position>();
		this.clientPositions = new ArrayList<ClientPosition>();
		this.validity = MAX_VALIDITY;
		this.createClientPositions();
		this.createPositions();
		this.finalizePositions();
	}

	// Initial Strategy
	public Strategy(AuctionMaster auctionMaster) {
		this.auctionMaster = auctionMaster;
		this.auctionPositions = new HashMap<Auction, Position>();
		this.clientPositions = new ArrayList<ClientPosition>();
		this.validity = MAX_VALIDITY;
		this.createClientPositions();
		this.createPositions();
		this.finalizePositions();
	}
	
	private void finalizePositions() {
		for (Position p:auctionPositions.values()){
			p.finalise();
		}
	}
	
	public void sendBids() {
		for(Position position:this.auctionPositions.values()) {
			position.bidMe();
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
	        
	        if(diffSeconds >= VALIDITY_WAIT_TIME) {
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

	public Position getPosition(Auction auction) {
		return this.auctionPositions.get(auction);
	}

	protected abstract void createPositions();

	protected abstract void createClientPositions();
	
	
}
