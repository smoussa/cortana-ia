package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.ArrayList;
import java.util.List;

import uk.ac.soton.ecs.ia.cortana.ClientPosition;

public class EntertainmentPosition {
	
	private EntertainmentAuction auction;
	public List<ClientPosition> clientsWanting;
	
	public EntertainmentPosition(EntertainmentAuction auction) {
		this.auction = auction;
	}
	
	public boolean canBuy() {
		return auction.getQuote().getHQW() > 0;
	}
	
	private int getQuantityToBid() {
		
		int numClientsWanting = clientsWanting.size();
		
		// if we have enough tickets (same as clients wanting tickets)
		
		// if we have fewer tickets than are needed, return how many we need to buy
		
		// if there are fewer tickets available than are needed, return how many are left
		
		return 0;
	}
	
	/**
	 * The number of clients waiting for a package
	 * @return
	 */
	public List<ClientPosition> clientsWaiting() {
		
		List<ClientPosition> waiting = new ArrayList<>();
		for (ClientPosition client : clientsWanting) {
			if (!client.isFeasible()) {
				waiting.add(client);
			}
		}
		
		return waiting;
	}
	
	

}
