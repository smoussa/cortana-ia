package flightAuction.estimators;

import java.util.HashMap;
import java.util.Random;

import flightAuction.FlightAuctionChangeStore;
import flightAuction.FlightAuctionChangeStoreResetable;
import flightAuction.FlightAuctionSimulator;

public class FlightPriceEstimatorMonteCarlo implements FlightAuctionEstimator {
	FlightAuctionChangeStore facs;
	int currentTime;
	double currentPrice;
	
	public FlightPriceEstimatorMonteCarlo(FlightAuctionChangeStore facs, int currentTime, double currentPrice){
		this.facs = facs;
		this.currentTime = currentTime;
		this.currentPrice = currentPrice;
	}
	
	//returns a tuple of MinPrice and time of MinPrice
	@Override
	public double[] getMinPrice(){
		return this.getMinPrice(1);
	}
	
	public double[] getMinPrice(int method){
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
	public double[] getMinPriceMethod1(){	
		int TRIES = 1000;
		HashMap<Integer,Double> probDist = facs.getScaledProbabilities();
		double minPriceSum = 0.0;
		int minPriceTimeSum = 0;
		for(int tries = 0; tries<TRIES; tries++){
			int upperBound = FlightAuctionEstimatorHelper.chooseRandomUpperBound(probDist);
			FlightAuctionSimulator fas = new FlightAuctionSimulator(upperBound, currentTime, currentPrice);
			
			double minPrice = currentPrice;
			int minPriceTime = currentTime;
			while(fas.canTick()){
				fas.tick();
				if (fas.getPrice()<minPrice){
					minPrice = fas.getPrice();
					minPriceTime = fas.getTime();
				}
			}
						
			minPriceSum += minPrice;
			minPriceTimeSum += minPriceTime;
		}
	
		double[] r = {minPriceSum/TRIES,minPriceTimeSum/TRIES};
		return r;
	}
	
	//better (more efficient) to repeat x times for each UB, find min and mean each, and then scale mean based on UB's distrib
	public double[] getMinPriceMethod2(){	
		int TRIES = 1000;
		HashMap<Integer,Double> probDist = facs.getScaledProbabilities();
		System.out.println(probDist);
		double minPriceSumSum = 0.0;
		int minPriceTimeSumSum = 0;
		for(int UB = -10; UB<=30; UB++){
			double scale = probDist.get(UB);
			double minPriceSum = 0.0;
			int minPriceTimeSum = 0;
			for(int tries = 0; tries<TRIES; tries++){
				FlightAuctionSimulator fas = new FlightAuctionSimulator(UB, currentTime, currentPrice);
				double minPrice = currentPrice;
				int minPriceTime = currentTime;
				while(fas.canTick()){
					fas.tick();
					if (fas.getPrice()<minPrice){
						minPrice = fas.getPrice();
						minPriceTime = fas.getTime();
					}
				}
				minPriceSum += minPrice;
				minPriceTimeSum += minPriceTime;
			}
			minPriceSumSum += minPriceSum/TRIES * scale;
			minPriceTimeSumSum += minPriceTimeSum/TRIES * scale;
		}
		
		double[] r = {minPriceSumSum,minPriceTimeSumSum};
		return r;
	}
	
	//even better (more efficient) to scale the number of times we do each UB by its distrib and then mean
	//	this means we spend CPU time proportionately to the UB distrib
	public double[] getMinPriceMethod3(){	
		double[] a = new double[2];
		
		return a;
	}


}
