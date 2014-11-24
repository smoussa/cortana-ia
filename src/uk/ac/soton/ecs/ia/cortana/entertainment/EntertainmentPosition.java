package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.List;

import se.sics.tac.aw.TACAgent;
import uk.ac.soton.ecs.ia.cortana.Auction;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;

/*
 * Entertainment
 */

public class EntertainmentPosition {
	
	private Auction auction;
	public List<ClientPosition> clientsWanting;
	
	public EntertainmentPosition(Auction auction) {
		this.auction = auction;
	}
	
	public float getAskPrice() {
		return 0;
	}

	public float getBidPrice() {
		return auction.getBid().
	}
	
	public void bid(Auction auction) {
		System.out.println("Bidding for " + auction.AUCTION_TYPE);
		
	}
	
	public void ask() {
		
		
		
	}
	
	public int getNumOwned() {
		return auction.get
	}
	
	private int getQuantityToBid() {
		
		int numClientsWanting = clientsWanting.size();
		
		// if we have enough tickets (same as clients wanting tickets)
		
		// if we have fewer tickets than are needed, return how many we need to buy
		
		// if there are fewer tickets available than are needed, return how many are left
		
		return 0;
	}
	
	

}
