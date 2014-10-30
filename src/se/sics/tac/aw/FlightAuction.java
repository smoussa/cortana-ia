package se.sics.tac.aw;


public class FlightAuction extends Auction {

	public FlightAuction(TacType flightType, Day day, double price, int auctionId) {
		super(flightType, day, auctionId, price);
	}
	
	public void addClient(Client client) {
		this.peopleWhoWantMe.add(client);
	}

	public void bidMe(TACAgent agent, double price) {

		System.out.println("Flight " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on for " + price);
		
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(peopleWhoWantMe.size(), (float)price);
		agent.submitBid(bid);
		
	}

}
