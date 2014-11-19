package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction;

import static org.junit.Assert.*;

import org.junit.Test;

public class FlightAuctionHelperTest {

	@Test
	public void outsideBoundTest() {
		//xt==0
		assertEquals(0.0, FlightAuctionHelper.probChangeGivenUBAndT(-10,270,10,795), 0.001);
		assertEquals(0.0, FlightAuctionHelper.probChangeGivenUBAndT(-10,270,-10,155), 0.001);
		
		//xt>0
		assertEquals(0.0, FlightAuctionHelper.probChangeGivenUBAndT(-7,270,2,799), 0.001);
		assertEquals(0.0, FlightAuctionHelper.probChangeGivenUBAndT(-7,270,-10,155), 0.001);
		
		//xt<0
		assertEquals(0.0, FlightAuctionHelper.probChangeGivenUBAndT(-10,360,10,795), 0.001);
		assertEquals(0.0, FlightAuctionHelper.probChangeGivenUBAndT(-10,360,-2,151), 0.001);
	}
	
	@Test
	public void insideBoundTest() {
		//xt==0
		assertEquals(1.0/21, FlightAuctionHelper.probChangeGivenUBAndT(-10,270,4,795), 0.001);
		assertEquals(1.0/21, FlightAuctionHelper.probChangeGivenUBAndT(-10,270,-4,155), 0.001);
		
		//xt>0
		assertEquals(1.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-7,270,0,799), 0.001);
		assertEquals(1.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-7,270,-4,155), 0.001);
		
		//xt<0
		assertEquals(1.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-10,360,4,795), 0.001);
		assertEquals(1.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-10,360,0,151), 0.001);
	}
	
	@Test
	public void onBoundTest() {
		//xt==0
		assertEquals(6.0/21, FlightAuctionHelper.probChangeGivenUBAndT(-10,270,5,795), 0.001);
		assertEquals(6.0/21, FlightAuctionHelper.probChangeGivenUBAndT(-10,270,-5,155), 0.001);
		
		//xt>0
		assertEquals(2.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-7,270,1,799), 0.001);
		assertEquals(6.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-7,270,-5,155), 0.001);
		
		//xt<0
		assertEquals(6.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-10,360,5,795), 0.001);
		assertEquals(2.0/13, FlightAuctionHelper.probChangeGivenUBAndT(-10,360,-1,151), 0.001);
				
	}

}
