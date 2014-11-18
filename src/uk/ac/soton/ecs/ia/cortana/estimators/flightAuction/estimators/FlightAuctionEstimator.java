package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators;

public interface FlightAuctionEstimator {

	//returns a tuple of MinPrice and time of MinPrice
	public abstract double[] getMinPrice();

}