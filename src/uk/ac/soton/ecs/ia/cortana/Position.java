package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;

import se.sics.tac.aw.TACAgent;

public abstract class Position {

	protected final Auction auction;
	
	public List<ClientPosition> peopleWhoWantMe;
	
	public Position(Auction auction) {
		this.auction = auction;
		this.peopleWhoWantMe = new ArrayList<ClientPosition>();
	}
	
	public void bidMe(TACAgent agent) {
		auction.bid(agent, peopleWhoWantMe.size(), this.getPrice());
	}
	
	public boolean isFullySatisfied(){
		return peopleWhoWantMe.size() == auction.getNumberOwned();
	}
	
	public boolean isValid() {
		return !( 
				auction.isClosed() && !isFullySatisfied() ||
				!auction.isClosed() && getPrice()<auction.getBidPrice()
				);
	}

	public abstract float getPrice();
	
}
