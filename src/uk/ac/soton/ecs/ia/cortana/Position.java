package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;

import se.sics.tac.aw.Bid;

public abstract class Position {

	protected final Auction auction;
	protected boolean isTheoretical = true;
	
	public List<ClientPosition> peopleWhoWantMe;
	
	private boolean finalised;
	private float actualBid;
	private int quantityBid;
	protected boolean shouldBid;
	
	public Position(Auction auction) {
		this.auction = auction;
		this.peopleWhoWantMe = new ArrayList<ClientPosition>();
		this.finalised = false;
		this.shouldBid = false;
	}
	
	public void bidMe() {
		if(shouldBid && isTheoretical && !isFullySatisfied()) {
			this.isTheoretical=false;
			System.out.println("Bidding for " + getQuantityToBid() + " at " +  this.getActualBidPrice());
			auction.bid(getQuantityToBid(), this.getActualBidPrice());
			this.shouldBid = false;
		}
	}
	
	private int getQuantityToBid() {
		
		if(finalised)
			return this.quantityBid;
		
		if(auction.getBid() == null)
			return this.peopleWhoWantMe.size() - auction.getNumberOwned();
		
		if(this.peopleWhoWantMe.size() - auction.getNumberOwned() < auction.getBid().getQuantity())
			return auction.getBid().getQuantity();
		
		return this.peopleWhoWantMe.size() - auction.getNumberOwned();
	}
	
	public boolean isFullySatisfied(){
		return peopleWhoWantMe.size() <= auction.getNumberOwned();
	}
	
	public boolean isValid() {
		
		//TODO this doesn't work because if we have a good enough bid, but our HQW is less than what we need it still passes
		
		if(isFullySatisfied()) {
//			System.out.println("Auction closed and fully satisfied :)");
			return true;
		}
		// Make sure the auction is actually closed
		else if(auction.isClosed()) {
			System.out.print("Haven't won auction :(");
		}
		
		if(!auction.isClosed() && finalised && getActualBidPrice() >= auction.getAskPrice()) {
//			System.out.println("Auction open and we are bidding enough :)");
			return true;
		}
		else if(finalised) {
			System.out.print("Auction ask " + auction.getAskPrice() + " Our Bid " + getOptimalBidPrice() + " :(");
		}
		
		if(!auction.isClosed() && !isTheoretical && peopleWhoWantMe.size() <= auction.getNumberProbablyOwned() + auction.getNumberOwned()) {
//			System.out.println("Auction open and we placed a bid and we probably/do own enough :)");
			return true;
		}
		else if(!auction.isClosed() && !isTheoretical) {
			System.out.print("We only have " + (auction.getNumberProbablyOwned()+auction.getNumberOwned()) + " of " + peopleWhoWantMe.size() + " :(");
		}
		
		System.out.println("   Not Valid");
		
		return false;	
	}

	private float getActualBidPrice() {
		if(finalised)
			return this.actualBid;
		
		if(auction.getBid() == null)
			return (float) Math.max(getOptimalBidPrice(), auction.getAskPrice());
					
		return (float) Math.max(Math.max(getOptimalBidPrice(), auction.getAskPrice()), auction.getBid().getHighestPrice());
	}
	
	abstract float getOptimalBidPrice();
	
	public float getCost(){
		if(this.isTheoretical){
			//TODO what if there is not only stuff already bought, but also a current bid on the auction
			return auction.agent.getCost(auction.AUCTION_ID) + this.getActualBidPrice()*getQuantityToBid();
		}
		else{
			Bid b = auction.getBid();
			return auction.agent.getCost(auction.AUCTION_ID) + b.getTotalPotentialCost();
		}
	}
	
	public void finalise() {
		this.actualBid = getActualBidPrice();
		this.quantityBid = getQuantityToBid();
		
		this.finalised = true;
		
		System.out.println("Finalised to " + quantityBid + " at " + actualBid);
	}
	
}
