package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators;

import java.util.HashMap;
import java.util.Random;

import uk.ac.soton.ecs.ia.cortana.estimators.helpers.Rand;

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
