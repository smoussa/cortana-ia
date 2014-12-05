package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;


public class HotelAuction extends Auction {
	
	private Bid bid;
	
	public HotelAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
	}
	
	@Override
	public void bid(int quantity, float price) {
		throw new UnsupportedOperationException();
	}

	public void setup() {
		this.bid = new Bid(AUCTION_ID);
	}
	
	public void bidPoint(int qtyToBid, float actualBidPrice) {
		this.bid.addBidPoint(qtyToBid, actualBidPrice);		
	}

	public void bidNow() {
		System.out.println("Hotel on day: " + AUCTION_DAY + " is being bid on");
		agent.submitBid(this.bid);
	}
	
}
