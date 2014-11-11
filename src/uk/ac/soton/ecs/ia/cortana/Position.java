package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;

import se.sics.tac.aw.Bid;
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
			//TODO take into account stuff we already own for that auction
			//e.g we already own 2 but we need 5, therefore bid for 3
			
			//ALSO what if there is already a bid on the auction?
			auction.bid(peopleWhoWantMe.size(), this.getPrice());
		}
	}
	
	public boolean isFullySatisfied(){
		return peopleWhoWantMe.size() <= auction.getNumberOwned();
	}
	
public boolean isValid() {
		
		//TODO remove before final competition
		
		if( auction.quote.getBid()==null && auction.agent.getBid(auction.AUCTION_ID)!=null
			|| auction.quote.getBid()!=null && auction.agent.getBid(auction.AUCTION_ID)==null){
				System.out.println("The auction's quote's bid and the tacagent's bid have become different!!! ONE");
				System.out.println("This should never happen");
				System.exit(-1);
			}

		if(	auction.quote.getBid()!=null && !auction.quote.getBid().same(auction.agent.getBid(auction.AUCTION_ID)))
		{
			System.out.println("The auction's quote's bid and the tacagent's bid have become different!!! TWO");
			System.out.println("This should never happen");
			System.exit(-1);
		}
		
		if(isFullySatisfied()) {
//			System.out.println("Auction closed and fully satisfied :)");
			return true;
		}
		// Make sure the auction is actually closed
		else if(auction.isClosed()) {
			System.out.print("Haven't won auction :(");
		}
		
		if(!auction.isClosed() && isTheoretical && getPrice() >= auction.getAskPrice()) {
//			System.out.println("Auction open and we are bidding enough :)");
			return true;
		}
		else if(isTheoretical) {
			System.out.print("Auction ask " + auction.getAskPrice() + " Our Bid " + getPrice() + " :(");
		}
		
		if(!auction.isClosed() && !isTheoretical && peopleWhoWantMe.size() <= auction.getNumberProbablyOwned() + auction.getNumberOwned()) {
//			System.out.println("Auction open and we placed a bid and we probably/do own enough :)");
			return true;
		}
		else if(!auction.isClosed() && !isTheoretical) {
			System.out.print("We only have " + (auction.getNumberProbablyOwned()+auction.getNumberOwned()) + " of " + peopleWhoWantMe.size() + " :(");
		}
		
		System.out.println("   Not Valid");
		
		return false;	
	}

	public abstract float getPrice();
	
	public float getCost(){
		if(this.isTheoretical){
			//TODO what if there is not only stuff already bought, but also a current bid on the auction
			return auction.agent.getCost(auction.AUCTION_ID) + this.getPrice()*(peopleWhoWantMe.size()-auction.agent.getOwn(auction.AUCTION_ID));
		}
		else{
			Bid b = auction.quote.getBid();
			return auction.agent.getCost(auction.AUCTION_ID) + b.getTotalPotentialCost();
		}
	}
	
}
