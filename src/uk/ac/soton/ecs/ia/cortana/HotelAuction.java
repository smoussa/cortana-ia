package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;


public class HotelAuction extends Auction {
	
	public HotelAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
	}
	
	@Override
	public void bid(int quantity, float price) {
		System.out.println("Hotel on day: " + AUCTION_DAY + " is being bid on");
		super.bid(quantity, price);
	}
	
	@Override
	public void ask(int quantity, float price){
		System.err.println("Hotel selling is not allowed");
	}
	
}
