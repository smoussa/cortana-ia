package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;

public class EntertainmentAuction extends Auction {

	public EntertainmentAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
	}
	
	@Override
	public void bid(int quantity, float price) {
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		super.bid(quantity, price);
	}

	public void ask(int quantity, float price) {
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " has been set an ask price");
		
		if (quantity > -1) {
			System.err.println("Invalid ask quantity. Must be a negative number.");
			return;
		}
		
		if (price > this.agent.getQuote(AUCTION_ID).getAskPrice()) {
			System.err.println("Invalid ask price. Must be lower than the current ask price.");
			return;
		}
		
		if (this.agent.getQuote(AUCTION_ID).getBid().getQuantity() > 0) {
			System.err.println("Invalid ask bid. It is illegal to sell to yourself.");
			return;
		}
		
		// more checks
				
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}

}
