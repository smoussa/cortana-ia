package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;


public class HotelAuction extends Auction {
	
	public HotelAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
	}
	
	@Override
	public void bid(int quantity, float price) {
		System.out.println("Hotel on day: " + AUCTION_DAY + " is being bid on");
		
		if (price < this.agent.getQuote(AUCTION_ID).getBidPrice()){
			System.err.println("Invalid bid price. Must be higher than our current bid.");
			return;
		}
		if (this.agent.getQuote(AUCTION_ID).getBid() != null && quantity < this.agent.getQuote(AUCTION_ID).getBid().getQuantity()){
			System.err.println("Invalid bid quantity. Must be higher than our current bid.");
			return;
		}
		if (price < this.agent.getQuote(AUCTION_ID).getAskPrice()){
			System.err.println("Invalid bid price. The market is selling at a higher price than that.");
			return;
		}
				
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		bid.addBidPoint(16 - quantity, 1);
		agent.submitBid(bid);
	}
	
}
