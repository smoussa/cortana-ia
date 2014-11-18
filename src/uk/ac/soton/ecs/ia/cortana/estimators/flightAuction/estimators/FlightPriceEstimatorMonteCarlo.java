package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators;

import java.util.HashMap;

import uk.ac.soton.ecs.ia.cortana.Estimator;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.FlightAuctionChangeStore;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.FlightAuctionSimulator;

public class FlightPriceEstimatorMonteCarlo implements Estimator {
	FlightAuctionChangeStore facs;
	int currentTime;
	double currentPrice;
	
	public FlightPriceEstimatorMonteCarlo(FlightAuctionChangeStore facs, int currentTime, double currentPrice){
		this.facs = facs;
		this.currentTime = currentTime;
		this.currentPrice = currentPrice;
	}
	

	@Override
	public float getFutureMinPrice() {
		return (float) this.getMinPrice(1);
	}
	
	public double getMinPrice(int method){
		if (method==1){
			return this.getMinPriceMethod1();
		}
		else if (method==2){
			return this.getMinPriceMethod2();
		}
		else{
			return this.getMinPriceMethod3();
		}
	}
	
	//could chose from the random distribution repeatedly and then do a straight mean on the mins
	public double getMinPriceMethod1(){	
		int TRIES = 1000;
		HashMap<Integer,Double> probDist = facs.getScaledProbabilities();
		double minPriceSum = 0.0;
		for(int tries = 0; tries<TRIES; tries++){
			int upperBound = FlightAuctionEstimatorHelper.chooseRandomUpperBound(probDist);
			FlightAuctionSimulator fas = new FlightAuctionSimulator(upperBound, currentTime, currentPrice);
			
			double minPrice = currentPrice;
			while(fas.canTick()){
				fas.tick();
				if (fas.getPrice()<minPrice){
					minPrice = fas.getPrice();
				}
			}
						
			minPriceSum += minPrice;
		}
	
		return minPriceSum/TRIES;
	}
	
	//better (more efficient) to repeat x times for each UB, find min and mean each, and then scale mean based on UB's distrib
	public double getMinPriceMethod2(){	
		int TRIES = 1000/41;
		HashMap<Integer,Double> probDist = facs.getScaledProbabilities();
		//System.out.println(probDist);
		double minPriceSumSum = 0.0;
		for(int UB = -10; UB<=30; UB++){
			double scale = probDist.get(UB);
			double minPriceSum = 0.0;
			for(int tries = 0; tries<TRIES; tries++){
				FlightAuctionSimulator fas = new FlightAuctionSimulator(UB, currentTime, currentPrice);
				double minPrice = currentPrice;
				while(fas.canTick()){
					fas.tick();
					if (fas.getPrice()<minPrice){
						minPrice = fas.getPrice();
					}
				}
				minPriceSum += minPrice;
			}
			minPriceSumSum += minPriceSum/TRIES * scale;
		}
		
		return minPriceSumSum;
	}
	
	//even better (more efficient) to scale the number of times we do each UB by its distrib and then mean
	//	this means we spend CPU time proportionately to the UB distrib
	public double getMinPriceMethod3(){	
		return 0;
	}


}
