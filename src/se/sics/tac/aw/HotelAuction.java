package se.sics.tac.aw;

import java.util.ArrayList;
import java.util.List;

public class HotelAuction {

	public final Day day;
	public double price;
	public int hotelType;
	
	public final int auctionId;
	public List<Client> peopleWhoWantMe;
	
	public HotelAuction(Day day, double price, int auctionId, int hotelType) {
		this.day = day;
		this.price = price;
		this.auctionId = auctionId;
		this.hotelType = hotelType;
		this.peopleWhoWantMe = new ArrayList<Client>();
	}
	
	public void addClient(Client client) {
		this.peopleWhoWantMe.add(client);
	}
	
	public double getBidPrice() {
		
		double price = 0.0;
		
		for(Client client:peopleWhoWantMe) {
			price += client.getHotelPrice();
		}
		
		price /= peopleWhoWantMe.size();
		
		return price;
	}
	
	public void bidMe(TACAgent agent, double price) {
		
		System.out.println("Hotel on day: " + day + " is being bid on for " + price + " for this many people: " + peopleWhoWantMe.size());
		
		Bid bid = new Bid(auctionId);
		bid.addBidPoint(peopleWhoWantMe.size(), (float)price);
		agent.submitBid(bid);
		
	}
	
}
