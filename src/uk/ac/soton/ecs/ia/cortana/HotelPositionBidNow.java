package uk.ac.soton.ecs.ia.cortana;

public class HotelPositionBidNow extends Position {

	private double price;

	public HotelPositionBidNow(Auction auction, double price) {
		super(auction);
		this.price = price;
		this.shouldBid = true;
	}

	@Override
	public float getOptimalBidPrice() {
		return (float) this.price;
	}
	
}
