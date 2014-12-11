package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;

// Bids on a hotel for our true value and pads out the remaining possible tickets with bids of 1
public class HotelPositionBidNowPadded extends Position {

	private double price;

	public HotelPositionBidNowPadded(Auction auction, double price) {
		super(auction);
		this.price = price;
		this.shouldBid = true;
	}
	
	@Override
	public void bidMe() {
		if (shouldBid && isTheoretical) {
			
			HotelAuction auctionH = (HotelAuction) auction;
			auctionH.setup();
			
			if(isFullySatisfied() && !auction.isClosed()){
				auctionH.bidPoint(16, 1);
				auctionH.bidNow();
			}
			else if(!isFullySatisfied() && !auction.isClosed()){
				int qtyToBid = getQuantityToBid();
				float actualBidPrice = this.getActualBidPrice();
				
				System.out.println("Bidding for " + qtyToBid + " at " + actualBidPrice);
				
				if(qtyToBid>0){
					auctionH.bidPoint(qtyToBid, actualBidPrice);
				}
				auctionH.bidPoint(16-qtyToBid, 1);
				auctionH.bidNow();
			}
			
			this.isTheoretical = false;
			this.shouldBid = false;
		}
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
