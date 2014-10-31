package uk.ac.soton.ecs.ia.cortana;

public class EntertainmentPosition extends Position {

	public EntertainmentPosition(int AUCTION_ID) {
		super(AUCTION_ID);
	}

	@Override
	public float getPrice() {
		return 0;
	}

}
