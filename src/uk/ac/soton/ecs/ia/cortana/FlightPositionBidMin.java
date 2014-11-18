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
			min = getFutureMinPrice();
			return min;
		}
			
		return min;
	}

	@Override
	public void tick() {
		this.min = getFutureMinPrice();
		this.shouldBid = this.shouldBuy(auction.getAskPrice(), this.min);
		
		this.bidMe();
	}

	private float getFutureMinPrice() {
		return auction.getEstimator().getFutureMinPrice();
	}
	
	@Override
	//Must be prevented as price will change as time goes on
	public void finalise() {		
		System.out.println("Finalisation prevented");
	}
	
	//TODO I expect that getCost needs to be overridden to actually be relavent

	private boolean shouldBuy(double currentPrice, double predictedFutureMinPrice){
		if(currentPrice<=predictedFutureMinPrice){
			return true;
		}
		double diff = currentPrice - predictedFutureMinPrice;
		return diff/currentPrice < 0.1;
	}
}
