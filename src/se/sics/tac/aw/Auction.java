package se.sics.tac.aw;

import java.util.ArrayList;
import java.util.List;

public abstract class Auction {

	public List<Client> peopleWhoWantMe;
	
	public final TacType AUCTION_TYPE;
	public final Day AUCTION_DAY;
	
	public final int AUCTION_ID;
	
	private double askingPrice;
	
	public Auction(TacType auctionType, Day auctionDay, int auctionId, double askingPrice) {
		
		this.peopleWhoWantMe = new ArrayList<>();
		
		this.AUCTION_TYPE = auctionType;
		this.AUCTION_DAY = auctionDay;
		
		this.AUCTION_ID = auctionId;
		
		this.askingPrice = askingPrice;
	}
	
	public double getAskingPrice() {
		return this.askingPrice;
	}
	
}
