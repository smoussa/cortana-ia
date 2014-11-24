package uk.ac.soton.ecs.ia.cortana.entertainment;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public class EntertainmentAuction {

	public final int AUCTION_ID;
	public final TacTypeEnum AUCTION_TYPE;
	public final TacCategoryEnum AUCTION_CAT;
	public final DayEnum AUCTION_DAY;
	
	private TACAgent agent;

	public EntertainmentAuction(TACAgent agent, Quote quote) {
		this.agent = agent;
		this.AUCTION_ID = quote.getAuction();
		this.AUCTION_TYPE = TacTypeEnum.getType(TacCategoryEnum.getCategory(TACAgent.getAuctionCategory(AUCTION_ID)), TACAgent.getAuctionType(AUCTION_ID));
		this.AUCTION_DAY = DayEnum.getDay(TACAgent.getAuctionDay(AUCTION_ID));
		this.AUCTION_CAT = TacCategoryEnum.getCategory(TACAgent.getAuctionCategory(AUCTION_ID));
	}
	
	public void bid(int quantity, float price) {
		
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		
		if (quantity < 0) {
			System.err.println("Invalid bid quantity. Must be a positive number.");
			return;
		}
				
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}

	public void ask(int quantity, float price) {
		
		System.out.println("Entertainment " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " has been set an ask price");
		
		if (quantity > -1) {
			System.err.println("Invalid ask quantity. Must be a negative number.");
			return;
		}
		if (getQuote().getBid().getQuantity() > 0) {
			System.err.println("Invalid ask bid. It is illegal to sell to yourself.");
			return;
		}
		
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}
	
	public Quote getQuote() {
		return agent.getQuote(AUCTION_ID);
	}
	
	public Bid getBid() {
		return getQuote().getBid();
	}
	
	public double getCurrentBidPrice() {
		return getQuote().getBidPrice();
	}
	
	public double getCurrentAskPrice() {
		return getQuote().getAskPrice();
	}

	public int getNumberOwned() {
		return agent.getOwn(AUCTION_ID);
	}

	public int getNumberProbablyOwned(){
		return agent.getProbablyOwn(AUCTION_ID);
	}

}
