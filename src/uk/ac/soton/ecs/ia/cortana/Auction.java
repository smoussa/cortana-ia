package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacTypeEnum;

public abstract class Auction {
	
	public final TacTypeEnum AUCTION_TYPE;
	public final DayEnum AUCTION_DAY;
	
	public final int AUCTION_ID;
	
	private double askingPrice;
	private double bidPrice;
	
	private boolean closed;
	
	private int owned;
	
	public Auction(TacTypeEnum auctionType, DayEnum auctionDay, int auctionId, double askingPrice, double bidPrice) {
		this.AUCTION_TYPE = auctionType;
		this.AUCTION_DAY = auctionDay;
		
		this.AUCTION_ID = auctionId;
		
		this.askingPrice = askingPrice;
		this.bidPrice = bidPrice;
		
		this.closed = false;
		
		this.owned = 0;
	}

	public double getAskingPrice() {
		return this.askingPrice;
	}
	
	public double getBidPrice() {
		return bidPrice;
	}

	public void bidMe(TACAgent agent, int quantity, float price) {
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}

	public void updatePrice(double askPrice, double bidPrice) {
		this.askingPrice = askPrice;
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
	
}