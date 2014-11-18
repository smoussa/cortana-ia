package uk.ac.soton.ecs.ia.cortana;

public class FlightPositionBidMin extends FlightPosition {

	private float min;
	
	public FlightPositionBidMin(Auction auction) {
		super(auction);
		min = 0;
	}

	@Override
	float getOptimalBidPrice() {
		if(min == 0) {
			min = findMin();
			return min;
		}
			
		return min;
	}

	@Override
	public void tick() {
		this.min = findMin();
		if(auction.getAskPrice() <= min)
			this.shouldBid = true;
		
		this.bidMe();
	}

	private float findMin() {
		return auction.getEstimator().getMin();
	}
	
	@Override
	//Must be prevented as price will change as time goes on
	public void finalise() {		
		System.out.println("Finalisation prevented");
	}
	
	//TODO I expect that getCost needs to be overridden to actually be relavent

}
