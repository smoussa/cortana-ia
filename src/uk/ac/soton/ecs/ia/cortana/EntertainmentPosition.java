package uk.ac.soton.ecs.ia.cortana;

public class EntertainmentPosition extends Position {

	public EntertainmentPosition(Auction auction) {
		super(auction);
	}

	@Override
	public float getPrice() {
		return 0;
	}

}
