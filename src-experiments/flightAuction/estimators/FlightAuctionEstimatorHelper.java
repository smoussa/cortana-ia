package flightAuction.estimators;

import helpers.Rand;

import java.util.HashMap;
import java.util.Random;

public class FlightAuctionEstimatorHelper {
	public static int chooseRandomUpperBound(HashMap<Integer,Double> probDist){
		Random r = Rand.getRand();
		
		double aim = r.nextDouble();
		
		double sum = 0.0;
		
		for (int i = -10; i<=30; i++){
			sum = sum + probDist.get(i);
			if (sum>=aim){
				return i;
			}
		}
		
		return 0;
	}
}
