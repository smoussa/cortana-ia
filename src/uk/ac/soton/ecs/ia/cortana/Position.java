package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;

import se.sics.tac.aw.TACAgent;

public abstract class Position {

	protected final Auction auction;
	protected boolean isTheoretical = true;
	
	public List<ClientPosition> peopleWhoWantMe;
	
	public Position(Auction auction) {
		this.auction = auction;
		this.peopleWhoWantMe = new ArrayList<ClientPosition>();
	}
	
	public void bidMe(TACAgent agent) {
		this.isTheoretical=false;
		auction.bid(agent, peopleWhoWantMe.size(), this.getPrice());
	}
	
	public boolean isFullySatisfied(){
		return peopleWhoWantMe.size() == auction.getNumberOwned();
	}
	
	public boolean isValid() {
		return !( 
				auction.isClosed() && !isFullySatisfied() ||
				!auction.isClosed() && isTheoretical && getPrice() < auction.getAskPrice() ||
				!auction.isClosed() && !isTheoretical && peopleWhoWantMe.size() > auction.getNumberProbablyOwned()+auction.getNumberOwned()
				);
	}

	public abstract float getPrice();
	
}
