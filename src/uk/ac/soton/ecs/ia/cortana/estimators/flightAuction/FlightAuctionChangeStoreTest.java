package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class FlightAuctionChangeStoreTest {

	@Test
	public void basicSetupTest() {
		FlightAuctionChangeStore facs = new FlightAuctionChangeStore();
		HashMap<Integer,Double> p = facs.getScaledProbabilities();
		for (int key : p.keySet()) {
			assertEquals(1.0/41, p.get(key), 0.01);
		}
		
		assertEquals(10.0, facs.getExpectedUpperBound(), 0.01);
	}
	
	@Test
	public void basicTest() {
		FlightAuctionChangeStore facs = new FlightAuctionChangeStore();
		facs.addChange(10, 10, 250);
		HashMap<Integer,Double> p = facs.getScaledProbabilities();
		for (int key : p.keySet()) {
			assertEquals(1.0/41, p.get(key), 0.01);
		}
		
		assertEquals(10.0, facs.getExpectedUpperBound(), 0.01);
	}
	
	//TODO some more tests here, but it is super hard

}
