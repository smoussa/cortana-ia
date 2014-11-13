package uk.ac.soton.ecs.ia.cortana;

public class FlightPositionBidNow extends FlightPosition {

	public FlightPositionBidNow(Auction auction) {
		super(auction);
		shouldBid = true;
	}

	@Override
	public float getOptimalBidPrice() {
		return (float) this.auction.getAskPrice();
	}

	@Override
	public void tick() {}

}
