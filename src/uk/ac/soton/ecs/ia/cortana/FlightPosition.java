package uk.ac.soton.ecs.ia.cortana;


public abstract class FlightPosition extends Position {

	public FlightPosition(Auction auction) {
		super(auction);
		this.shouldBid = false;
	}

	public abstract void tick();
	
	@Override
	public float getCost(){
		if(this.isTheoretical){
			return auction.agent.getCost(auction.AUCTION_ID) + this.getActualBidPrice()*getQuantityToBid();
		}
		else{
			return auction.agent.getCost(auction.AUCTION_ID);
		}
	}
	
}
