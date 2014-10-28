package flightAuction.estimators;

import static org.junit.Assert.*;
import helpers.Rand;

import java.util.HashMap;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class FlightAuctionEstimatorHelperTest {
	
	@Test
	public void basicTest() {
		HashMap<Integer,Double> scaledP = new HashMap<Integer,Double>();
		HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
		
		for (int chosenUB = -10; chosenUB<=30; chosenUB++){
		
			for (int i = -10; i<=30; i++){
				scaledP.put(i, 0.0);
				count.put(i, 0);
			}
			
			scaledP.put(chosenUB, 1.0);
			
			for (int j = 0; j<100; j++){
				Integer chosen = FlightAuctionEstimatorHelper.chooseRandomUpperBound(scaledP);
				count.put(chosen, count.get(chosen)+1);
			}	
			
			for (int i = -10; i<=30; i++){
				if (i==chosenUB){
					assertEquals(100, count.get(chosenUB).intValue());
				}
				else{
					assertEquals(0, count.get(i).intValue());
				}
			}
		}
		
	}
	
	@Test
	public void basicTest2() {
		HashMap<Integer,Double> scaledP = new HashMap<Integer,Double>();
		HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
			
		for (int i = -10; i<=30; i++){
			scaledP.put(i, 0.0);
			count.put(i, 0);
		}
		
		scaledP.put(-10, 0.5);
		scaledP.put(30, 0.5);
		
		for (int j = 0; j<100000; j++){
			Integer chosen = FlightAuctionEstimatorHelper.chooseRandomUpperBound(scaledP);
			count.put(chosen, count.get(chosen)+1);
		}	
		
		assertEquals(1.0/2, (double)count.get(-10)/100000, 0.01);
		assertEquals(1.0/2, (double)count.get(30)/100000, 0.01);
		
		for (int i = -9; i<=29; i++){
			assertEquals(0, count.get(i).intValue());
		}
	}
	
	@Test
	public void basicTest3() {
		HashMap<Integer,Double> scaledP = new HashMap<Integer,Double>();
		HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
		
		for (int i = -10; i<=30; i++){
			scaledP.put(i, 1.0/41);
			count.put(i, 0);
		}
		
		for (int j = 0; j<1000000; j++){
			Integer chosen = FlightAuctionEstimatorHelper.chooseRandomUpperBound(scaledP);
			count.put(chosen, count.get(chosen)+1);
		}
		
		for (int i = -10; i<=30; i++){
			assertEquals(1.0/41, (double)count.get(i)/1000000, 0.01);
		}
		
	}
	
	@Test
	public void fullTest() {
		HashMap<Integer,Double> unscaledP = new HashMap<Integer,Double>();
		HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
		Random r = Rand.getRand();
		
		for (int i = -10; i<=30; i++){
			unscaledP.put(i, (double)r.nextInt(10));
			count.put(i, 0);
		}
				
		double total = 0.0;
		for (double value : unscaledP.values()) {
			total += value;
		}
		double scale = 1.0/total;
		HashMap<Integer,Double> scaledP = new HashMap<Integer,Double>();
		for (int key : unscaledP.keySet()) {
			scaledP.put(key, unscaledP.get(key)*scale);
		}
				
		for (int j = 0; j<1000000; j++){
			Integer chosen = FlightAuctionEstimatorHelper.chooseRandomUpperBound(scaledP);
			count.put(chosen, count.get(chosen)+1);
		}
		
		for (int i = -10; i<=30; i++){
			assertEquals(scaledP.get(i), (double)count.get(i)/1000000, 0.01);
		}
		
	}

}
