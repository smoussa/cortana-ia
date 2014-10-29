package se.sics.tac.aw;

import java.util.ArrayList;
import java.util.List;

public class FlightAuction {

	public final int FLIGHT_TYPE;
	public final Day day;
	public double price;
	
	public final int auctionId;
	public List<Client> peopleWhoWantMe;
	
	public FlightAuction(int flightType, Day day, double price, int auctionId) {
		this.FLIGHT_TYPE = flightType;
		this.day = day;
		this.price = price;
		this.auctionId = auctionId;
		this.peopleWhoWantMe = new ArrayList<Client>();
	}
	
	public void addClient(Client client) {
		this.peopleWhoWantMe.add(client);
	}

	public void bidMe(TACAgent agent, double price) {

		System.out.println("Flight " + FLIGHT_TYPE + " on day: " + day + " is being bid on for " + price);
		
		Bid bid = new Bid(auctionId);
		bid.addBidPoint(peopleWhoWantMe.size(), (float)price);
		agent.submitBid(bid);
		
	}
	
}
