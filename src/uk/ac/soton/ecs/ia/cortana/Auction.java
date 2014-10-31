package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;

public abstract class Auction {

	public List<Client> peopleWhoWantMe;
	
	public final TacType AUCTION_TYPE;
	public final Day AUCTION_DAY;
	
	public final int AUCTION_ID;
	
	private double askingPrice;
	private double bidPrice;
	
	public Auction(TacType auctionType, Day auctionDay, int auctionId, double askingPrice, double bidPrice) {
		
		this.peopleWhoWantMe = new ArrayList<>();
		
		this.AUCTION_TYPE = auctionType;
		this.AUCTION_DAY = auctionDay;
		
		this.AUCTION_ID = auctionId;
		
		this.askingPrice = askingPrice;
		this.bidPrice = bidPrice;
	}
	
	public double getAskingPrice() {
		return this.askingPrice;
	}
	
	public double getBidPrice() {
		return bidPrice;
	}

	public void bidMe(TACAgent agent, double price) {
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(peopleWhoWantMe.size(), (float)price);
		agent.submitBid(bid);
	}

	public void updatePrice(double askPrice, double bidPrice) {
		this.askingPrice = askPrice;
		this.bidPrice = bidPrice;
	}
	
}
