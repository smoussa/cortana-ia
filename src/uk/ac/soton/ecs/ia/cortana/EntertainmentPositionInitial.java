package uk.ac.soton.ecs.ia.cortana;

public class EntertainmentPositionInitial extends Position {

	public EntertainmentPositionInitial(Auction auction) {
		super(auction);
	}

	@Override
	public float getOptimalBidPrice() {
		return 0;
	}

	@Override
	public float getCost() {
		return 0;
	}

}
