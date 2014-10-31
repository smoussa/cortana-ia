package uk.ac.soton.ecs.ia.cortana;

public class FlightPosition extends Position {

	public FlightPosition(int AUCTION_ID) {
		super(AUCTION_ID);
	}

	@Override
	public float getPrice() {
		return 300;
	}

}
