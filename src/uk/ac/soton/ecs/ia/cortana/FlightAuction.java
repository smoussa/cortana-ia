package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;


public class FlightAuction extends Auction {

	public FlightAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
	}
	
	@Override
	public void bid(int quantity, float price) {
		System.out.println("Flight " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		super.bid(quantity, price);
	}
	
}
