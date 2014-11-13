package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.TACAgent;

public abstract class Position {

	protected final Auction auction;
	protected boolean isTheoretical = true;
	
	public List<ClientPosition> peopleWhoWantMe;
	
	private boolean finalised;
	private float actualBid;
	private int quantityBid;
	
	public Position(Auction auction) {
		this.auction = auction;
		this.peopleWhoWantMe = new ArrayList<ClientPosition>();
		this.finalised = false;
	}
	
	public void bidMe(TACAgent agent) {
		if(!this.isFullySatisfied()) {
			this.isTheoretical=false;
			//TODO take into account stuff we already own for that auction
			//e.g we already own 2 but we need 5, therefore bid for 3
			
			//ALSO what if there is already a bid on the auction?
			auction.bid(getQuantityToBid(), this.getAcutalBidPrice());
		}
	}
	
	private int getQuantityToBid() {
		
		if(finalised)
			return this.quantityBid;
		
		if(this.peopleWhoWantMe.size() - auction.getNumberOwned() < auction.getBid().getQuantity())
			return auction.getBid().getQuantity();
		
		return (this.peopleWhoWantMe.size() - auction.getNumberOwned()) - auction.getBid().getQuantity();
	}
	
	public boolean isFullySatisfied(){
		return peopleWhoWantMe.size() <= auction.getNumberOwned();
	}
	
	public boolean isValid() {
		
		if(isFullySatisfied()) {
//			System.out.println("Auction closed and fully satisfied :)");
			return true;
		}
		// Make sure the auction is actually closed
		else if(auction.isClosed()) {
			System.out.print("Haven't won auction :(");
		}
		
		if(!auction.isClosed() && finalised && getAcutalBidPrice() >= auction.getAskPrice()) {
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

	private float getAcutalBidPrice() {
		if(finalised)
			return this.actualBid;
		
		return (float) Math.max(getOptimalBidPrice(), auction.getAskPrice() + 1);
	}
	
	abstract float getOptimalBidPrice();
	
	public float getCost(){
		if(this.isTheoretical){
			//TODO what if there is not only stuff already bought, but also a current bid on the auction
			return auction.agent.getCost(auction.AUCTION_ID) + this.getAcutalBidPrice()*getQuantityToBid();
		}
		else{
			Bid b = auction.getBid();
			return auction.agent.getCost(auction.AUCTION_ID) + b.getTotalPotentialCost();
		}
	}
	
	public void finalise() {
		this.finalised = true;
		
		this.actualBid = getAcutalBidPrice();
		this.quantityBid = getQuantityToBid();
	}
	
}
