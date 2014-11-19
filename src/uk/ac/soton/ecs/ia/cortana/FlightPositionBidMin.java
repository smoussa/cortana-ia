package uk.ac.soton.ecs.ia.cortana;

public class FlightPositionBidMin extends FlightPosition {

	private float min;
	private AuctionMaster auctionMaster;
	
	public FlightPositionBidMin(Auction auction, AuctionMaster auctionMaster) {
		super(auction);
		this.auctionMaster = auctionMaster;
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
		this.shouldBid = this.shouldBuy(auction.getAskPrice(), this.min, this.auctionMaster.get10SecondChunkElapsed());
		
		this.bidMe();
	}

	private float getFutureMinPrice() {
		Estimator e = auctionMaster.getEstimatorForAuction((FlightAuction) auction);
		return e.getFutureMinPrice();
	}
	
	@Override
	//Must be prevented as price will change as time goes on
	public void finalise() {		
		System.out.println("Finalisation prevented");
	}
	
	//TODO I expect that getCost needs to be overridden to actually be relavent

	private boolean shouldBuy(double currentPrice, double predictedFutureMinPrice, int currentTime){
		if(currentTime<200){
			return false;
		}
		double diff = currentPrice - predictedFutureMinPrice;
		
		if(diff<=35){
			return true;
		}
		
		return false;
	}
}
