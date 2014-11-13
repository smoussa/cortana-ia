package uk.ac.soton.ecs.ia.cortana;

public class FlightPositionBidNow extends Position {

	public FlightPositionBidNow(Auction auction) {
		super(auction);
	}

	@Override
	public float getOptimalBidPrice() {
		return (float) this.auction.getAskPrice();
	}

}
