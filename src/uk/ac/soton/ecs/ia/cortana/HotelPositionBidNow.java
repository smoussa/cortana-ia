package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;

public class HotelPositionBidNow extends Position {

	private double price;

	public HotelPositionBidNow(Auction auction, double price) {
		super(auction);
		this.price = price;
		this.shouldBid = true;
	}

	@Override
	public float getOptimalBidPrice() {
		return (float) this.price;
	}
	
	@Override
	public float getCost(){
		// Haven't placed the bid yet
		if(this.isTheoretical){
			return auction.agent.getCost(auction.AUCTION_ID) + this.getActualBidPrice()*getQuantityToBid();
		}
		// Auction is closed, our cost is what we spent on the auction when it was open
		else if(this.auction.isClosed()) {
			return auction.agent.getCost(auction.AUCTION_ID);
		}
		// Auction open and bid has been placed
		else{
			Bid b = auction.getBid();
			return b.getTotalPotentialCost();
		}
	}
	
}
