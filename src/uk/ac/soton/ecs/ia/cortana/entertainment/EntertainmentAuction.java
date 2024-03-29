package uk.ac.soton.ecs.ia.cortana.entertainment;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import uk.ac.soton.ecs.ia.cortana.Auction;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;

public class EntertainmentAuction extends Auction {
	
	public ClientPosition client;
	public boolean biddingFor = false;
	public boolean alreadySelling = false;

	public EntertainmentAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
		client = null;
	}
	
	public void bid(int quantity, float price) {
		
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		
		if (quantity < 0) {
			System.err.println("Invalid bid quantity. Must be a positive number.");
			return;
		}

		System.out.println("Entertainment BIDDING for " + quantity + " at price " + price);
				
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}

	public Bid ask(int quantity, float price) {
		
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " has been set an ask price");
		
		if (quantity > -1) {
			System.err.println("Invalid ask quantity. Must be a negative number.");
			return null;
		}
		
		System.out.println("Entertainment ASKING for " + quantity + " at price " + price);
		
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
		
		return bid;
	}
	
	public Quote getQuote() {
		return agent.getQuote(AUCTION_ID);
	}
	
	public Bid getBid() {
		return getQuote().getBid();
	}
	
	public double getCurrentBidQuantity() {
		return getBid().getQuantity();
	}
	
	public double getCurrentBidPrice() {
		return getQuote().getBidPrice();
	}

	public int getNumberOwned() {
		return agent.getOwn(AUCTION_ID);
	}

	public int getNumberProbablyOwned(){
		return agent.getProbablyOwn(AUCTION_ID);
	}

}
