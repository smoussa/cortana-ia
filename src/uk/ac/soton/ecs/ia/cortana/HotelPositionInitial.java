package uk.ac.soton.ecs.ia.cortana;

public class HotelPositionInitial extends Position {

	private double price;

	public HotelPositionInitial(Auction auction, double price) {
		super(auction);
		this.price = price;
	}

	@Override
	public float getOptimalBidPrice() {
		return (float) this.price;
	}
	
}
