package uk.ac.soton.ecs.ia.cortana;

import java.util.List;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.TACAgent;

public abstract class Position {

	private final int AUCTION_ID;
	
	public List<Client> peopleWhoWantMe;

	public Position(int AUCTION_ID) {
		this.AUCTION_ID = AUCTION_ID;
	}
	
	public void bidMe(TACAgent agent) {
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(peopleWhoWantMe.size(), this.getPrice());
		agent.submitBid(bid);
	}

	public abstract float getPrice();
	
}
