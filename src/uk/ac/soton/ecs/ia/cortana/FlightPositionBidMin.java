package uk.ac.soton.ecs.ia.cortana;

import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators.FlightPriceEstimatorMonteCarlo;

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
			//TODO work out when this happens and put something worthwhile in 
			return (float) auction.getAskPrice();
		}
			
		return min;
	}

	@Override
	public void tick() {
		if(!isFullySatisfied()){
			
			FlightPriceEstimatorMonteCarlo e = getEstimator();
		
			this.min = e.getFutureMinPrice();
			this.shouldBid = this.shouldBuy(auction.getAskPrice(), this.min, this.auctionMaster.get10SecondChunkElapsed(), this.getExpectedUpperBound());
			
			if(shouldBid){
				((FlightAuction) auction).futureAveragePricesFromEstimator = e.priceAtTimeMean;
			}
			
			this.bidMe();
		}
	}

	private float getExpectedUpperBound() {
		return auctionMaster.getExpectedUpperBound((FlightAuction) auction);
	}
	
	private FlightPriceEstimatorMonteCarlo getEstimator() {
		return (FlightPriceEstimatorMonteCarlo)auctionMaster.getEstimatorForAuction((FlightAuction) auction);
	}
	
	@Override
	//Must be prevented as price will change as time goes on
	public void finalise() {		
		System.out.println("Finalisation prevented");
	}
	
	//TODO I expect that getCost needs to be overridden to actually be relavent

	private boolean shouldBuy(double currentPrice, double predictedFutureMinPrice, int currentTime, float expectation){
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
			System.out.println("#############################");
			System.out.println("***** Current price is " + currentPrice + " predictedFutureMinPrice is " + predictedFutureMinPrice);
			System.out.println("#############################");
			return true;
		}
		
		return false;
	}

}
