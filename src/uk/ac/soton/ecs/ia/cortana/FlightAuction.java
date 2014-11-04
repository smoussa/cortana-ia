package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacTypeEnum;


public class FlightAuction extends Auction {

	public FlightAuction(TacTypeEnum flightType, DayEnum day, double askPrice, double bidPrice, int auctionId) {
		super(flightType, day, auctionId, askPrice, bidPrice);
	}
	
	@Override
	public void bidMe(TACAgent agent, int quantity, float price) {
		System.out.println("Flight " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		super.bidMe(agent, quantity, price);
	}

}
