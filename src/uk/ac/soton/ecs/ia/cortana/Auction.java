package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public abstract class Auction {
	
	public final TacTypeEnum AUCTION_TYPE;
	public final DayEnum AUCTION_DAY;
	
	public final int AUCTION_ID;

	public Quote quote;
	
	public TACAgent agent;
	
	public Auction(TACAgent agent, Quote quote) {

		this.AUCTION_ID = quote.getAuction();
		this.AUCTION_TYPE = TacTypeEnum.getType(TacCategoryEnum.getCategory(TACAgent.getAuctionType(AUCTION_ID)), TACAgent.getAuctionCategory(AUCTION_ID));
		this.AUCTION_DAY = DayEnum.getDay(TACAgent.getAuctionDay(AUCTION_ID));
		
		this.quote = quote;
		
		this.agent = agent;
		
	}

	public double getAskPrice() {
		return this.quote.getAskPrice();
	}
	
	public double getBidPrice() {
		return this.quote.getBidPrice();
	}

	public void bid(int quantity, float price) {
		
		if(price<this.quote.getBidPrice()){
			System.err.println("Invalid bid price. Must be higher than our current bid.");
			return;
		}
		if(quote.getBid() != null && quantity<this.quote.getBid().getQuantity()){
			System.err.println("Invalid bid quantity. Must be higher than our current bid.");
			return;
		}
		if(price<this.quote.getAskPrice()){
			System.err.println("Invalid bid price. The market is selling at a higher price than that.");
			return;
		}
				
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}
	
	public void ask(int quantity, float price) {
		//TODO some error catching logic in here
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(-1 * quantity, price);
		agent.submitBid(bid);
	}

	public boolean isClosed() {
		return this.quote.isAuctionClosed();
	}

	public int getNumberOwned(){
		return this.agent.getOwn(this.AUCTION_ID);
	}

	public int getNumberProbablyOwned(){
		return this.agent.getProbablyOwn(this.AUCTION_ID);
	}
	
	public void update() {
		this.quote = agent.getQuote(this.AUCTION_ID);
		this.quote.setBid(agent.getBid(this.AUCTION_ID));
	}
	
}