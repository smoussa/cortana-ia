package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.FlightAuctionChangeStore;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.FlightAuctionSimulator;

public class FlightPriceEstimatorMonteCarloTest {

	@Test
	public void test() {
		FlightAuctionSimulator fas = new FlightAuctionSimulator(-10);
		FlightAuctionChangeStore facs = new FlightAuctionChangeStore();
		for(int i=0;i<20;i++){
			fas.tick();
			facs.addChange(fas.getPriceChangeFromLastTick(), fas.getTime(), fas.getPriceBeforeLastTick());
		}
		
		System.out.println(fas.getTime());
		
		FlightPriceEstimatorMonteCarlo fpemc = new FlightPriceEstimatorMonteCarlo(facs, fas.getTime(), fas.getPrice());
		double[] a = fpemc.getMinPrice();
		System.out.println(a[0] + "@" + a[1]);
		
		FlightPriceEstimatorMonteCarlo fpemc2 = new FlightPriceEstimatorMonteCarlo(facs, fas.getTime(), fas.getPrice());
		double[] a2 = fpemc2.getMinPrice(2);
		System.out.println(a2[0] + "@" + a2[1]);
		
		double minPrice = fas.getPrice();
		int minPriceTime = fas.getTime();
		while(fas.canTick()){
			fas.tick();
			if (fas.getPrice()<minPrice){
				minPrice = fas.getPrice();
				minPriceTime = fas.getTime();
			}
		}
		System.out.println(minPrice + "@" + minPriceTime);

	}

}
