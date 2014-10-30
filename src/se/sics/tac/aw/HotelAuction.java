package se.sics.tac.aw;

import java.util.ArrayList;
import java.util.List;

public class HotelAuction {

	public final Day DAY;
	public final TacType HOTEL_TYPE;
	public final int auctionId;
	
	public double price;
	
	public List<Client> peopleWhoWantMe;
	
	public HotelAuction(TacType hotelType, Day day, double price, int auctionId) {
		this.DAY = day;
		this.price = price;
		this.auctionId = auctionId;
		this.HOTEL_TYPE = hotelType;
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
		
		System.out.println("Hotel on day: " + DAY + " is being bid on for " + price + " for this many people: " + peopleWhoWantMe.size());
		
		Bid bid = new Bid(auctionId);
		bid.addBidPoint(peopleWhoWantMe.size(), (float)price);
		agent.submitBid(bid);
		
	}
	
}
