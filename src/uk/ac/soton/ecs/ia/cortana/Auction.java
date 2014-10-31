package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;

public abstract class Auction {

	private Position position;
	
	public final TacType AUCTION_TYPE;
	public final Day AUCTION_DAY;
	
	public final int AUCTION_ID;
	
	private double askingPrice;
	private double bidPrice;
	
	private boolean closed;
	
	public Auction(TacType auctionType, Day auctionDay, int auctionId, double askingPrice, double bidPrice) {
		this.AUCTION_TYPE = auctionType;
		this.AUCTION_DAY = auctionDay;
		
		this.AUCTION_ID = auctionId;
		
		this.askingPrice = askingPrice;
		this.bidPrice = bidPrice;
		
		this.closed = false;
	}
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
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
	
}