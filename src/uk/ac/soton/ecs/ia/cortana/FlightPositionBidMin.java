package uk.ac.soton.ecs.ia.cortana;

import java.util.HashMap;

import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators.FlightPriceEstimatorMonteCarlo;

public class FlightPositionBidMin extends FlightPosition {

	private float min;
	private AuctionMaster auctionMaster;
	
	private Estimator e;
	
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
		if(!isFullySatisfied()){
		
			this.min = getFutureMinPrice();
			this.shouldBid = this.shouldBuy(auction.getAskPrice(), this.min, this.auctionMaster.get10SecondChunkElapsed(), this.getExpectedUpperBound());
			
			if(shouldBid){
				((FlightAuction) auction).futureAveragePricesFromEstimator = getFutureAveragePricesFromEstimator();
			}
			
			this.bidMe();
		}
	}

	private float getExpectedUpperBound() {
		return auctionMaster.getExpectedUpperBound((FlightAuction) auction);
	}
	
	private HashMap<Integer, Double> getFutureAveragePricesFromEstimator() {
		FlightPriceEstimatorMonteCarlo e = (FlightPriceEstimatorMonteCarlo) getEstimator();
		return e.priceAtTimeMean;
	}
	
	private float getFutureMinPrice() {
		Estimator e = getEstimator();
		return e.getFutureMinPrice();
	}
	
	private Estimator getEstimator() {
		if (e==null){
			e = auctionMaster.getEstimatorForAuction((FlightAuction) auction);
		}
		return e;
	}
	
	@Override
	//Must be prevented as price will change as time goes on
	public void finalise() {		
		System.out.println("Finalisation prevented");
	}
	
	//TODO I expect that getCost needs to be overridden to actually be relavent

	private boolean shouldBuy(double currentPrice, double predictedFutureMinPrice, int currentTime, float expectation){
		if(currentTime<=200){
			return false;
		}
		
		if(currentTime>200 && currentTime<=250 && expectation<=10){
			return false;
		}
		
		double diff = currentPrice - predictedFutureMinPrice;
		
		if(diff<=35){
			System.out.println("#############################");
			System.out.println("***** Current price is " + currentPrice + " predictedFutureMinPrice is " + predictedFutureMinPrice);
			System.out.println("#############################");
			return true;
		}
		
		return false;
	}
}
