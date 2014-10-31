package uk.ac.soton.ecs.ia.cortana;

public class FlightPosition extends Position {

	public FlightPosition(Auction auction) {
		super(auction);
	}

	@Override
	public float getPrice() {
		return (float) this.auction.getAskingPrice();
	}

}
