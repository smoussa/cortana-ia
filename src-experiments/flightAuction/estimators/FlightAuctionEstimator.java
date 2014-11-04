package flightAuction.estimators;

public interface FlightAuctionEstimator {

	//returns a tuple of MinPrice and time of MinPrice
	public abstract double[] getMinPrice();

}