package uk.ac.soton.ecs.ia.cortana;

public class FlightPositionInitial extends Position {

	public FlightPositionInitial(Auction auction) {
		super(auction);
	}

	@Override
	public float getPrice() {
		return (float) this.auction.getAskingPrice();
	}

}
