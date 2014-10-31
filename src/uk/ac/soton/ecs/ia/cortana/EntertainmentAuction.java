package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;

public class EntertainmentAuction extends Auction {

	public EntertainmentAuction(TacType entertainmentType, Day day, double askingPrice, int auctionId) {
		super(entertainmentType, day, auctionId, askingPrice);
	}
	
	@Override
	public void bidMe(TACAgent agent, double price) {
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on for " + price);
		super.bidMe(agent, price);
	}

}
