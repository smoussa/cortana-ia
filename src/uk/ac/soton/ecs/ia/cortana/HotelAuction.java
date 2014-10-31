package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;


public class HotelAuction extends Auction {
	
	public HotelAuction(TacType hotelType, Day day, double price, int auctionId) {
		super(hotelType, day, auctionId, price);
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
	
	@Override
	public void bidMe(TACAgent agent, double price) {
		System.out.println("Hotel on day: " + AUCTION_DAY + " is being bid on for " + price + " for this many people: " + peopleWhoWantMe.size());
		super.bidMe(agent, price);
	}
	
}
