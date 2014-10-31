package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;

import se.sics.tac.aw.TACAgent;

public abstract class Position {

	protected final Auction auction;
	
	boolean isFullySatisfied;
	
	public List<Client> peopleWhoWantMe;

	private int owned;

	public Position(Auction auction) {
		this.auction = auction;
		this.isFullySatisfied = false;
		this.peopleWhoWantMe = new ArrayList<Client>();
		this.owned = 0;
	}
	
	public void bidMe(TACAgent agent) {
		auction.bidMe(agent, peopleWhoWantMe.size(), this.getPrice());
	}
	
	public boolean isValid() {
		return isFullySatisfied && !auction.isClosed();
	}

	public abstract float getPrice();

	public void setNumberOwned(int owned) {
		this.owned = owned;
		this.isFullySatisfied = peopleWhoWantMe.size() == owned;
	}
	
}
