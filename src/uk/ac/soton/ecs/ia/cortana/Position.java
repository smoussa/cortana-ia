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
		if(!this.isFullySatisfied()) {
			this.isTheoretical=false;
			auction.bid(peopleWhoWantMe.size(), this.getPrice());
		}
	}
	
	public boolean isFullySatisfied(){
		return peopleWhoWantMe.size() <= auction.getNumberOwned();
	}
	
	public boolean isValid() {
		
		int validCount = 0;
		
		if(isFullySatisfied()) {
//			System.out.println("Auction closed and fully satisfied :)");
			validCount++;
		}
		// Make sure the auction is actually closed
		else if(auction.isClosed()) {
			System.out.print("Haven't won auction :(");
		}
		
		if(!auction.isClosed() && isTheoretical && getPrice() >= auction.getAskPrice()) {
//			System.out.println("Auction open and we are bidding enough :)");
			validCount++;
		}
		else if(isTheoretical && validCount == 0) {
			System.out.print("Auction ask " + auction.getAskPrice() + " Our Bid " + getPrice() + " :(");
		}
		
		if(!auction.isClosed() && !isTheoretical && peopleWhoWantMe.size() <= auction.getNumberProbablyOwned() + auction.getNumberOwned()) {
//			System.out.println("Auction open and we placed a bid and we probably/do own enough :)");
			validCount++;
		}
		else if(!auction.isClosed() && !isTheoretical && validCount == 0) {
			System.out.print("We only have " + (auction.getNumberProbablyOwned()+auction.getNumberOwned()) + " of " + peopleWhoWantMe.size() + " :(");
		}
		
		if(validCount == 0)
			System.out.println("   Not Valid");
		
		return validCount > 0;	
	}

	public abstract float getPrice();
	
	public float getCost(){
		//TODO this stuff here is a lie
		return this.getPrice()*peopleWhoWantMe.size();
	}
	
}
