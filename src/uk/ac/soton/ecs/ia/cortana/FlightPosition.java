package uk.ac.soton.ecs.ia.cortana;

public abstract class FlightPosition extends Position {

	public FlightPosition(Auction auction) {
		super(auction);
		this.shouldBid = false;
	}

	public abstract void tick();
	
}
