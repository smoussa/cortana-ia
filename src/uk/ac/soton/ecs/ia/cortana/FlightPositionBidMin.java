package uk.ac.soton.ecs.ia.cortana;

import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators.FlightPriceEstimatorMonteCarlo;

public class FlightPositionBidMin extends FlightPosition {

	private float min;
	private AuctionMaster auctionMaster;
		
	public FlightPositionBidMin(FlightAuction auction, AuctionMaster auctionMaster) {
		super(auction);
		this.auctionMaster = auctionMaster;
		min = 0;
	}

	@Override
	float getOptimalBidPrice() {
		if(min == 0) {
			//TODO work out when this happens and put something worthwhile in 
			return (float) auction.getAskPrice();
		}
			
		return min;
	}

	@Override
	public void tick(int tSecondsNearest10) {
		if(!isFullySatisfied()){
			System.out.println("Min tick");

			this.min = ((FlightAuction)auction).getFutureMinPrice();
			this.shouldBid = this.shouldBuy(auction.getAskPrice(), this.min, tSecondsNearest10,  ((FlightAuction)auction).getExpectedUpperBound());
			
			this.bidMe();
		}
	}
	
	@Override
	//Must be prevented as price will change as time goes on
	public void finalise() {		
		System.out.println("Finalisation prevented");
	}
	
	//TODO I expect that getCost needs to be overridden to actually be relavent

	private boolean shouldBuy(double currentPrice, double predictedFutureMinPrice, int currentTime, float expectation){
		System.out.println("#############################");
		System.out.println("***** Current price is " + currentPrice + " predictedFutureMinPrice is " + predictedFutureMinPrice);
		System.out.println("#############################");
		
		if(currentTime>=510){
			return true;
		}
		
		if(currentTime<=150){
			return false;
		}
		
		if(currentTime>150 && currentTime<=250 && expectation<=10){
			return false;
		}
		
		double diff = currentPrice - predictedFutureMinPrice;
		
		if(diff<=10){
			return true;
		}
		
		return false;
	}

}
