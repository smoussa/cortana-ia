package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Day;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacType;


public class FlightAuction extends Auction {

	public FlightAuction(TacType flightType, Day day, double price, int auctionId) {
		super(flightType, day, auctionId, price);
	}
	
	public void addClient(Client client) {
		this.peopleWhoWantMe.add(client);
	}

	@Override
	public void bidMe(TACAgent agent, double price) {
		System.out.println("Flight " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on for " + price);
		super.bidMe(agent, price);
	}

}
