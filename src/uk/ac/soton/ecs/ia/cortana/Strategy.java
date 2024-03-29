package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Strategy {

	// Constants for the 'Validity Health Bar'
	private static final int VALIDITY_WAIT_TIME = 1;
	private static final int MAX_VALIDITY = 5;

	public Map<Auction, Position> auctionPositions;
	public List<ClientPosition> clientPositions;
	
	protected AuctionMaster auctionMaster;
	
	protected Strategy oldStrategy;
	
	// Validity variables
	private Date lastTimeFailed;
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

	/*
	 * We check that a strategy is valid by making sure our positions own what they should and are bidding correctly.
	 * Since there are lags in the server e.g a hotel auction closes where we have won tickets but our owned isn't updated
	 * we have a health bar which has to decrease to 0 before we accept that we are actually invalid.
	 */
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
		
		// If a strategy is valid then max health bar full
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
			ut += cp.getTotalUtility();
			System.out.println("Utility " + cp.getTotalUtility());
		}
		System.out.println("Total Utility " + ut);
		float cost = 0;
		for(Position p: auctionPositions.values()){
			cost += p.getCost();
//			System.out.println("Auction " + p.auction.AUCTION_TYPE + " day " + p.auction.AUCTION_DAY + " costs " + p.getCost());
		}
//		System.out.println("Total Cost " + cost);
		System.out.println("Score " + (ut - cost));
		return ut - cost;	
	}

	public Position getPosition(Auction auction) {
		return this.auctionPositions.get(auction);
	}
	
	public List<ClientPosition> getAllClientPositions() {
		return this.clientPositions;
	}
	
	public ClientPosition getClientPosition(int index) {
		return this.clientPositions.get(index);
	}
	
	public int getClientPositionCount(){
		return this.clientPositions.size();
	}

	protected abstract void createPositions();

	protected abstract void createClientPositions();
	
	
}
