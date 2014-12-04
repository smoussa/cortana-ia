package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;
import uk.ac.soton.ecs.ia.cortana.ClientPositionFixedHotelPrice;
import uk.ac.soton.ecs.ia.cortana.ClientPreference;
import uk.ac.soton.ecs.ia.cortana.FlightAuction;
import uk.ac.soton.ecs.ia.cortana.HotelAuction;

public class EntertainmentStrategy {
	
	/*
	 * GENERAL NOTES:
	 * 
	 * - there are 3 (events) x 4 (days) x 8 (tickets) = 96 tickets available. We start with 12 of them
	 * - there are 3 (events) x 4 (days) = 12 auctions running throughout the game
	 * - competitors are more willing to trade at the end of games as client allocations have been made
	 * - it is better to hold tickets than to sell them cheap as it works in favour of competitors
	 * 
	 */
	
	private AuctionMaster master;
	public List<ClientPosition> clients;
	public static final int NUM_CLIENTS = 8;
	
	private static final TacTypeEnum AW = TacTypeEnum.ALLIGATOR_WRESTLING;
	private static final TacTypeEnum AP = TacTypeEnum.AMUSEMENT;
	private static final TacTypeEnum MU = TacTypeEnum.MUSEUM;
	
	public EntertainmentStrategy(AuctionMaster master) {
		this.master = master;
	}
	
	/**
	 * A list of clients that do not have the full set of entertainment packages
	 * @return
	 */
	public List<ClientPosition> clientsWaiting() {
		
		List<ClientPosition> waiting = new ArrayList<>();
		for (ClientPosition client : clients) {
			if (!client.isFeasible()) {
				waiting.add(client);
			}
		}
		
		return waiting;
	}
	
	/**
	 * The number of tickets needed for each of the ticket type
	 * @return
	 */
	public Map<TacTypeEnum, Integer> allTicketsNeeded() {
		
		Map<TacTypeEnum, Integer> needed = new HashMap<>();
		needed.put(AW, 0);
		needed.put(AP, 0);
		needed.put(MU, 0);
		
		for (ClientPosition client : clients) {
			if (!client.hasEntertainmentTicket(AW)) {
				needed.put(AW, needed.get(AW) + 1);
			}
			if (!client.hasEntertainmentTicket(AP)) {
				needed.put(AP, needed.get(AP) + 1);
			}
			if (!client.hasEntertainmentTicket(MU)) {
				needed.put(MU, needed.get(MU) + 1);
			}
		}
		
		// print
		System.out.println("We need " + needed.get(AW) + " tickets for Alligator Wrestling");
		System.out.println("We need " + needed.get(AP) + " tickets for Amusement Park Wrestling");
		System.out.println("We need " + needed.get(MU) + " tickets for Alligator Wrestling");
		
		return needed;
	}
	
	/**
	 * Number of tickets needed for a particular type of ticket to satisfy the clients
	 * @param ticketType
	 * @return
	 */
	public int ticketsNeeded(TacTypeEnum ticketType) {

		int needed = 0;
		for (ClientPosition client : clients) {
			if (!client.hasEntertainmentTicket(ticketType)) {
				needed++;
			}
		}
		
		return needed;
	}
	
	private void createClientPositions() {
		
		// create positions with any flight and hotel auctions as they won't affect entertainment
		for (ClientPreference cp: master.clientPreferences.values()) {
			clients.add(new ClientPosition(cp, null, null, null));
		}
		
	}
	
	private boolean worthBuying() {
		
		Map<TacTypeEnum, Integer> ticketsNeeded = allTicketsNeeded();
		Map<TacTypeEnum, Integer> ticketCosts = new HashMap<>();
		Map<ClientPosition, Integer> clientBonuses = new HashMap<>();
		
		
		
		/*
		 * if we don't have the tickets that are needed by clients,
		 * check how much each client is willing to pay (the bonuses)
		 * and if at least one bonus is higher than the cost of the ticket,
		 * buy the tickets
		 * 
		 * But then how many do we need? getSuggestedBuyQuantity()
		 * 
		 */
		
	}
	
	private void shortSell() {
		
		/*
		 * Sell tickets we might not/don't have at a higher price if prices may fall later.
		 * Keep a track of how much we bought and how much we paid to make a profit later.
		 * 
		 * How do we know if the quantity will increase? speculatedFuturePrice()
		 * 
		 */
	}
	
	private double speculatedFuturePrice() {
		
		/*
		 * The future price (or direction of change) of a ticket depends on the following parameters:
		 * - the current day
		 * - how many days left
		 * - the current bid price
		 * - the current ask price
		 * - the number of tickets (of each type) left
		 * 
		 */
		
	}
	
	private int getSuggestedBuyQuantity() {
		
		/*
		 * How much of a particular ticket do we buy given the following parameters?
		 * - quantity owned of a particular type
		 * - quantity required by a client
		 * - the current day
		 * - current bid price
		 * - current ask price
		 * 
		 */
		
	}
	
	private int daysFreeToTrade() {
		
		/*
		 * Get the days clients won't need entertainment tickets
		 * and we are free to trade the rest owned/willing to buy
		 */
		
	}
	
	private boolean safeToTrade() {
		
		/*
		 * Check whether we have satisfied as many clients as possible
		 * and are able to safely play/trade in the auction
		 * Get the days clients won't need entertainment tickets. daysFreeToTrade()
		 */
		
	}
	
	private void escapeBuy() {
		
		/*
		 * If near the end of the game we have sold tickets that we don't have,
		 * we want to avoid the $200 penalty
		 * and therefore buy as many tickets as possible under 200
		 * to minimise the penalty
		 * 
		 * Vary the exact time/conditions we do this
		 */
		
	}
	
	public void extremeBuyStrategy() {
		
		/*
		 * Buy at really low/high prices + high/low quantity
		 * Maybe add a threshold and not buy as much. getBuyingThreshold()
		 */
		
	}
	
	public void extremeSellStrategy() {
		
		/*
		 * Sell at really low/high prices + high/low quantity
		 * Maybe add a threshold and not sell as much. getSellingThreshold()
		 */
		
	}
	
	public void winAllBidsStrategy() {
		
		/*
		 * Constantly bid lower than the lowest bid price to always win tickets.
		 * Maybe change the rate at which we buy depending on how many days are left.
		 */
		
	}
	
	public void ignoreClientsStrategy() {
		
		/*
		 * Ignore client bonuses completely and focus on trading and profiting on tickets in auctions
		 */
		
	}
	
	public void naiveStrategy() {
		
		/*
		 * Gradually raise the buying price for shortage/needed tickets
		 * and gradually lower the ask price for tickets being sold
		 * over time
		 * 
		 * Change the rate - based on a linear/polynomial function of time
		 * 
		 */
		
	}

}
