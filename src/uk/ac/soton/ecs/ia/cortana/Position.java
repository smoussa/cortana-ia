package uk.ac.soton.ecs.ia.cortana;

// Sits on an auction and dictates what we want to do for that auction
public abstract class Position {

	protected final Auction auction;
	
//  indicates position has been put into play. i.e has been bid
	protected boolean isTheoretical = true;
	
	public int peopleWhoWantMe;
	
//	indicates peopleWhoWantMe, the bid price and quantity wont be changed	
	private boolean finalised;
	
	private float actualBid;
	private int quantityBid;
	
//	We might not want to actually bid on an auction, or might be waiting to bid
	protected boolean shouldBid;
	
	public Position(Auction auction) {
		this.auction = auction;
		this.peopleWhoWantMe = 0;
		this.finalised = false;
		this.shouldBid = false;
	}
	
	public void bidMe() {
		
		if (shouldBid && isTheoretical && !isFullySatisfied()) {
			int qtyToBid = getQuantityToBid();
			float actualBidPrice = this.getActualBidPrice();
			
			System.out.println("Bidding for " + qtyToBid + " at " + actualBidPrice);
			auction.bid(qtyToBid, actualBidPrice);
			this.isTheoretical = false;
			this.shouldBid = false;
		}
	}
	
	protected int getQuantityToBid() {
		
		if (finalised)
			return this.quantityBid;
		
		int numPeopleWhoWantMe = this.peopleWhoWantMe;
		int numOwned = auction.getNumberOwned();
		
		if (auction.getBid() == null)
			return numPeopleWhoWantMe - numOwned;
		
		if (numPeopleWhoWantMe - numOwned < auction.getHQW())
			return auction.getHQW();
		
		return numPeopleWhoWantMe - numOwned;
	}
	
	public boolean isFullySatisfied(){
		return peopleWhoWantMe <= auction.getNumberOwned();
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
		
		boolean isValid = true;
		
		if(!auction.isClosed() && finalised && getActualBidPrice() >= auction.getAskPrice()) {
//			System.out.println("Auction open and we are bidding enough :)");
		}
		else if(finalised) {
			System.out.print("Auction ask price is " + auction.getAskPrice() + ". Our bid price is " + getOptimalBidPrice() + " :(");
			isValid = false;
		}
		
		// Probably owned has potential for being wrong but seems to work :S
		if(!auction.isClosed() && !isTheoretical && peopleWhoWantMe <= auction.getNumberProbablyOwned() + auction.getNumberOwned()) {
//			System.out.println("Auction open and we placed a bid and we probably/do own enough :)");
		}
		else if(!auction.isClosed() && !isTheoretical) {
			System.out.print("We only have " + (auction.getHQW()+auction.getNumberOwned()) + " of " + peopleWhoWantMe + " :(");
			isValid = false;
		}
		
		if(!isValid)
			System.out.println("   Not Valid");
		
		return isValid;	
	}

	protected float getActualBidPrice() {
		if(finalised)
			return this.actualBid;
		
		if(auction.getBid() == null)
			return (float) Math.max(getOptimalBidPrice(), auction.getAskPrice());
					
		return (float) Math.max(Math.max(getOptimalBidPrice(), auction.getAskPrice()), auction.getBid().getHighestPrice());
	}
	
	abstract float getOptimalBidPrice();
	
	public abstract float getCost();
	
	// Locks in what we will bid on the auction
	public void finalise() {
		this.actualBid = getActualBidPrice();
		this.quantityBid = getQuantityToBid();
		
		this.finalised = true;
		
		System.out.println("Finalised to " + quantityBid + " at " + actualBid);
	}

}
