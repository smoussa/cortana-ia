package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;

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
	
	protected AuctionMaster master;
	protected TACAgent agent;
	protected List<ClientPosition> clients;
	protected static final int NUM_CLIENTS = 8;
	protected float[] prices;
	
	protected List<EntertainmentAuction> auctionsList;
	protected List<EntertainmentAuction> sellToAuctions;
	protected List<EntertainmentAuction> buyFromAuctions;
	protected static TacTypeEnum[] ticketTypes;
	
	protected static final TacTypeEnum AW = TacTypeEnum.ALLIGATOR_WRESTLING;
	protected static final TacTypeEnum AP = TacTypeEnum.AMUSEMENT;
	protected static final TacTypeEnum MU = TacTypeEnum.MUSEUM;
	
	public EntertainmentStrategy(AuctionMaster master) {
		
		this.master = master;
		this.agent = master.cortana.agent;
		this.clients = master.getStrategy().getAllClientPositions();
		
		prices = new float[agent.getAuctionNo()];
		ticketTypes = new TacTypeEnum[3];
		ticketTypes[0] = AW;
		ticketTypes[1] = AP;
		ticketTypes[2] = MU;
		
		auctionsList = new ArrayList<EntertainmentAuction>(96);
		sellToAuctions = new ArrayList<EntertainmentAuction>();
		buyFromAuctions = new ArrayList<EntertainmentAuction>();
		
		allocateTickets();
		bidRemaining();
	}
	
	public void quoteUpdated(Quote quote) {
		
		for (Iterator<EntertainmentAuction> itr = auctionsList.iterator(); itr.hasNext();) {
			EntertainmentAuction auc = itr.next(); // find client who wants
				
			// calculate how many we have
			int allocated = agent.getAllocation(auc.AUCTION_ID);
			int owned = agent.getOwn(auc.AUCTION_ID);
			int alloc = allocated - owned;
			
			// if client doesn't have ticket
			if (!(auc.client.hasEntertainmentTicket(auc.AUCTION_DAY) || auc.client.hasEntertainmentTicket(auc.AUCTION_TYPE))) {
				
				if (alloc < 0) { // if we have ticket
					auc.client.giveEntertainmentTicket(auc.AUCTION_DAY, auc.AUCTION_TYPE);
					agent.setAllocation(auc.AUCTION_ID, agent.getAllocation(auc.AUCTION_ID) + 1);
					itr.remove();
				} else if (alloc > 0 && !auc.client.biddingOnTicket(auc.AUCTION_DAY)) { // if we haven't already bid, then bid
					float askPrice = (float) auc.getAskPrice();
					auc.bid(1, askPrice - (agent.getGameTime() * 120f) / 720000);
					auc.client.bidOnTicket(auc.AUCTION_DAY, auc.AUCTION_TYPE);
				}
			} else { // client has ticket
				if (alloc < 0) { // if we have tickets, sell them
					float bidPrice = (float) auc.getCurrentBidPrice();
					auc.ask(alloc, bidPrice + (agent.getGameTime() * 120f) / 720000);
				}
			}
		}
		
//		int allocated = agent.getAllocation(quote.getAuction());
//		int owned = agent.getOwn(quote.getAuction());
//		int alloc = allocated - owned;
//		
//		System.out.println();
//		System.out.println();
//		System.out.println("===");
//		System.out.println(alloc);
//		System.out.println("===");
//		System.out.println();
//		System.out.println();
//		
//		if (alloc < 0) {
////			float price = 140f - (agent.getGameTime() * 100f) / 720000;
////			master.getEntertainmentAuction(quote.getAuction()).ask(alloc, price);
//			agent.setAllocation(quote.getAuction(), owned);
//		}
		
		
	}
	
	public void update() {
		this.clients = master.getStrategy().getAllClientPositions();
		agent.clearAllocation();
		auctionsList = new ArrayList<EntertainmentAuction>(96);
		buyFromAuctions = new ArrayList<EntertainmentAuction>();
		allocateTickets();
	}
	
	private void printAuctionList() {
		
		System.out.println();
		System.out.println("Auctions List:");
		for (EntertainmentAuction auction : auctionsList) {
			System.out.println(auction.AUCTION_TYPE + " on " + auction.AUCTION_DAY +
					" with Client " + auction.client.client.CLIENT_ID +
					" has bonus [" + auction.client.getEntertainmentBonus(auction.AUCTION_TYPE) + "]" +
					" bidding?: " + (auction.client.biddingOnTicket(auction.AUCTION_DAY)
							|| auction.client.biddingOnTicket(auction.AUCTION_TYPE)));
		}
		System.out.println();
	}
	
	private void allocateTickets() {
		
		// assign clients to auctions
		for (ClientPosition client : clients) {
			for (EntertainmentAuction auction : client.eAuctions) {
				EntertainmentAuction newAuction = new EntertainmentAuction(agent, auction.getQuote());
				newAuction.client = client;
				auctionsList.add(newAuction);
			}
		}
		
		// prioritise auctions
		Comparator<EntertainmentAuction> comparator = new Comparator<EntertainmentAuction>() {
			@Override
			public int compare(EntertainmentAuction e1, EntertainmentAuction e2) {
				if (e1.client.getEntertainmentBonus(e1.AUCTION_TYPE) <
						e2.client.getEntertainmentBonus(e2.AUCTION_TYPE)) {
					return 1;
				} else if (e1.client.getEntertainmentBonus(e1.AUCTION_TYPE) ==
						e2.client.getEntertainmentBonus(e2.AUCTION_TYPE)) {
					return 0;
				} else {
					return -1;
				}
			}
		};
		Collections.sort(auctionsList, comparator);
		
		// allocate
		for (Iterator<EntertainmentAuction> itr = auctionsList.iterator(); itr.hasNext();) {
			EntertainmentAuction auction = itr.next();
			
			if (!(auction.client.hasEntertainmentTicket(auction.AUCTION_DAY) ||
					auction.client.hasEntertainmentTicket(auction.AUCTION_TYPE))) {
			
				int owned = agent.getOwn(auction.AUCTION_ID);
				int allocated = agent.getAllocation(auction.AUCTION_ID);
				
				if (owned > 0 && (owned - allocated) > 0) {
					auction.client.giveEntertainmentTicket(auction.AUCTION_DAY, auction.AUCTION_TYPE);
					agent.setAllocation(auction.AUCTION_ID, allocated + 1);
					itr.remove();
				}
				
			}
		}
		
	}
	
	private void bidRemaining() {
		
		for (Iterator<EntertainmentAuction> itr = auctionsList.iterator(); itr.hasNext();) {
			EntertainmentAuction auction = itr.next();
			
			int bonus = auction.client.getEntertainmentBonus(auction.AUCTION_TYPE);
			float askPrice = (float) auction.getAskPrice();
			
			if (bonus < 100)
				break;
			if (askPrice > 0 && askPrice < bonus - 80) { // initial buy
				auction.bid(1, askPrice);
				auction.client.bidOnTicket(auction.AUCTION_DAY, auction.AUCTION_TYPE);
			}
		}
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
	
	private void speculatedFuturePrice() {
		
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
	
	private void getSuggestedBuyQuantity() {
		
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
	
	private void daysFreeToTrade() {
		
		/*
		 * Get the days clients won't need entertainment tickets
		 * and we are free to trade the rest owned/willing to buy
		 */
		
	}
	
	private void safeToTrade() {
		
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
