package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;

public abstract class Auction {

	private final Position position;
	
	public final TacType AUCTION_TYPE;
	public final Day AUCTION_DAY;
	
	public final int AUCTION_ID;
	
	private double askingPrice;
	private double bidPrice;
	
	public Auction(TacType auctionType, Day auctionDay, int auctionId, double askingPrice, double bidPrice) {
		
		if(auctionType == TacType.ALLIGATOR_WRESTLING || auctionType == TacType.AMUSEMENT || auctionType == TacType.MUSEUM)
			this.position = new EntertainmentPosition(auctionId);
		else if(auctionType == TacType.ALLIGATOR_WRESTLING || auctionType == TacType.AMUSEMENT || auctionType == TacType.MUSEUM)
			this.position = new FlightPosition(auctionId);
		else
			this.position = new HotelPosition(auctionId);
		
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

	public void bidMe(TACAgent agent) {
		this.position.bidMe(agent);
	}

	public void updatePrice(double askPrice, double bidPrice) {
		this.askingPrice = askPrice;
		this.bidPrice = bidPrice;
	}
	
	public void addClient(Client client) {
		this.position.peopleWhoWantMe.add(client);
	}
	
}