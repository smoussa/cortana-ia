package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacTypeEnum;

public abstract class Auction {
	
	public final TacTypeEnum AUCTION_TYPE;
	public final DayEnum AUCTION_DAY;
	
	public final int AUCTION_ID;
	
	private double askPrice;
	private double bidPrice;
	
	private boolean closed;
	
	private int owned, probabalyOwned;
	
	private double ourAskPrice;
	private double ourBidPrice;
	private int ourAskQuantity;
	private int ourBidQuantity;
	
	public Auction(TacTypeEnum auctionType, DayEnum auctionDay, int auctionId, double askingPrice, double bidPrice) {
		this.AUCTION_TYPE = auctionType;
		this.AUCTION_DAY = auctionDay;
		
		this.AUCTION_ID = auctionId;
		
		this.askPrice = askingPrice;
		this.bidPrice = bidPrice;
		
		this.ourAskPrice = 0;
		this.ourBidPrice = 0;
		this.ourAskQuantity = 0;
		this.ourBidQuantity = 0;
		
		this.closed = false;
		
		this.owned = 0;
		this.probabalyOwned = 0;
	}

	public double getAskPrice() {
		return this.askPrice;
	}
	
	public double getBidPrice() {
		return this.bidPrice;
	}

	public void bid(TACAgent agent, int quantity, float price) {
		if(price<ourBidPrice){
			System.err.println("Invalid bid price. Must be higher than our current bid.");
			return;
		}
		if(quantity<ourBidQuantity){
			System.err.println("Invalid bid quantity. Must be higher than our current bid.");
			return;
		}
		if(price<askPrice){
			System.err.println("Invalid bid price. The market is selling at a higher price than that.");
			return;
		}
				
		this.ourBidPrice = price;
		this.ourBidQuantity = quantity;
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}
	
	public void ask(TACAgent agent, int quantity, float price) {
		//TODO some error catching logic in here
		this.ourAskPrice = price;
		this.ourAskQuantity = quantity;
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(-1 * quantity, price);
		agent.submitBid(bid);
	}

	public void updatePrice(double askPrice, double bidPrice) {
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
	}

	public boolean isClosed() {
		return closed;
	}

	public void close() {
		this.closed = true;
	}

	public void setNumberOwned(int own) {
		this.owned = own;
	}
	
	public int getNumberOwned(){
		return owned;
	}

	public void setNumberProbablyOwned(int probablyOwn) {
		this.probabalyOwned = probablyOwn;
	}
	
	public int getNumberProbablyOwned(){
		return probabalyOwned;
	}
	
}