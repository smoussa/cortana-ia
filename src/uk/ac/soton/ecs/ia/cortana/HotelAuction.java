package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacTypeEnum;


public class HotelAuction extends Auction {
	
	public HotelAuction(TacTypeEnum hotelType, DayEnum day, double askPrice, double bidPrice, int auctionId) {
		super(hotelType, day, auctionId, askPrice, bidPrice);
	}
	
	@Override
	public void bid(TACAgent agent, int quantity, float price) {
		System.out.println("Hotel on day: " + AUCTION_DAY + " is being bid on");
		super.bid(agent, quantity, price);
	}
	
	@Override
	public void ask(TACAgent agent, int quantity, float price){
		System.err.println("Hotel selling is not allowed");
	}
	
}
