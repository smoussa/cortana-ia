package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;

public class EntertainmentAuction extends Auction {

	public EntertainmentAuction(TacType entertainmentType, Day day, double askingPrice, double bidPrice, int auctionId) {
		super(entertainmentType, day, auctionId, askingPrice, bidPrice);
	}
	
	@Override
	public void bidMe(TACAgent agent, int quantity, float price) {
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		super.bidMe(agent, quantity, price);
	}

}
