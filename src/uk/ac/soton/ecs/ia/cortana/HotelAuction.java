package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;


public class HotelAuction extends Auction {
	
	public HotelAuction(TacType hotelType, Day day, double askPrice, double bidPrice, int auctionId) {
		super(hotelType, day, auctionId, askPrice, bidPrice);
	}
	
	@Override
	public void bidMe(TACAgent agent) {
		System.out.println("Hotel on day: " + AUCTION_DAY + " is being bid on");
		super.bidMe(agent);
	}
	
}
