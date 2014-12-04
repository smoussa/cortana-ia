package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TACAgent;
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
		createClientPositions();
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
	 * Total number of tickets needed by all clients for every ticket type
	 * @return
	 */
	public Map<TacTypeEnum, Integer> ticketsNeeded() {
		
		int neededAW = 0;
		int neededAP = 0;
		int neededMU = 0;
		
		for (ClientPosition client : clients) {
			if (!client.hasEntertainmentTicket(AW))
				neededAW++;
			if (!client.hasEntertainmentTicket(AP))
				neededAP++;
			if (!client.hasEntertainmentTicket(MU))
				neededMU++;
		}
		
		// print
		System.out.println("We need " + neededAW + " tickets for Alligator Wrestling");
		System.out.println("We need " + neededAP + " tickets for Amusement Park");
		System.out.println("We need " + neededMU + " tickets for Museum");
		
		Map<TacTypeEnum, Integer> needed = new HashMap<>();
		needed.put(AW, neededAW);
		needed.put(AP, neededAP);
		needed.put(MU, neededMU);
		
		return needed;
	}
	
	/**
	 * Total number of tickets needed by all clients for a particular type of ticket
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
	
	/**
	 * Total number of tickets needed by all clients for a particular type of ticket and day
	 * @param ticketType
	 * @return
	 */
	public int ticketsNeeded(TacTypeEnum ticket, DayEnum day) {

		int needed = 0;
		for (ClientPosition client : clients) {
			if (client.isStaying(day) && !client.hasEntertainmentTicket(day) && !client.hasEntertainmentTicket(ticket)) {
				needed++;
			}
		}
		
		return needed;
	}
	
	private void createClientPositions() {
		
		for (ClientPreference cpref : master.clientPreferences.values()) {
			
			// TODO Days staying should be based on the actual flight days rather than the client preference
			
			FlightAuction inflight = master.getFlightAuction(TacTypeEnum.INFLIGHT, cpref.inFlight);
			FlightAuction outflight = master.getFlightAuction(TacTypeEnum.OUTFLIGHT, cpref.outFlight);
			
			List<EntertainmentAuction> eAuctions = new ArrayList<>();
			for (int d = cpref.inFlight.getDayNumber(); d < cpref.outFlight.getDayNumber(); d++) {
				int auctionIdAW = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, AW, DayEnum.getDay(d));
				int auctionIdAP = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, AP, DayEnum.getDay(d));
				int auctionIdMU = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, MU, DayEnum.getDay(d));
				EntertainmentAuction auctionAW = master.getEntertainmentAuction(auctionIdAW);
				EntertainmentAuction auctionAP = master.getEntertainmentAuction(auctionIdAP);
				EntertainmentAuction auctionMU = master.getEntertainmentAuction(auctionIdMU);
				eAuctions.add(auctionAW);
				eAuctions.add(auctionAP);
				eAuctions.add(auctionMU);
			}
			clients.add(new ClientPosition(cpref, inflight, outflight, null, eAuctions));
		}
		
	}
	
	private Map<TacTypeEnum, Integer> getTicketCost() {
		
		
		
		DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, AW, DayEnum.MONDAY);
	}
	
	
	
	
	
	/*
	 * STRATEGIES
	 */
	
	
	private int worthBuying(TacTypeEnum ticket) {
		
		int numWorthBuying = 0;
		
		for (int d = 1; d <= 5; d++) { // for each day
			DayEnum day = DayEnum.getDay(d);
			if (ticketsNeeded(ticket, day) > 0) {
				
				int highestBonus = 0;
				for (ClientPosition client : clients) {
					int bonus = client.getEntertainmentBonus(ticket);
					if (bonus > highestBonus) {
						highestBonus = bonus;
					}
				}
				
				int auctionId = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, ticket, day);
				EntertainmentAuction auction = master.getEntertainmentAuction(auctionId);
				
				if (highestBonus > auction.getAskPrice()) { // if a profit can be made
					numWorthBuying++;
				}
			}
		}
		
		/*
		 * if we don't have the tickets that are needed by clients,
		 * check how much each client is willing to pay (the bonuses)
		 * and whether they are staying for that day
		 * and if at least one bonus is higher than the cost of the ticket,
		 * buy the tickets
		 * 
		 * How many do we need?
		 * 
		 */
		
		return numWorthBuying;
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
