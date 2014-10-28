package flightAuction.estimators;

public interface FlightAuctionEstimator {

	//returns a tuple of maxReduction and time of maxReduction
	public abstract double[] getMinPrice();

}