package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;

public abstract class EntertainmentStrategy {
	
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
	
	protected static TacTypeEnum[] ticketTypes;
	protected Set<EntertainmentAuction> sellToAuctions;
	protected Set<EntertainmentAuction> buyFromAuctions;
	
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
		
		sellToAuctions = new HashSet<EntertainmentAuction>();
		buyFromAuctions = new HashSet<EntertainmentAuction>();
		
		start();
	}
	
	public abstract void start();
	
	public abstract void quoteUpdated(Quote quote);
	
	/*
	 * 
	 */
	
	public void update() {
		this.clients = master.getStrategy().getAllClientPositions();
//		allocateTickets();
//		allocateTickets3();
	}
	
	protected void allocateTickets() {
		
		/*
		 * For each client, see what they want and if we have tickets for them,
		 * allocate the tickets to them in order of highest bonus.
		 */
		
		for (TacTypeEnum ticket : ticketTypes) {
			for (ClientPosition client : getClientsByHighestBonus(ticket)) {
				if (!client.hasEntertainmentTicket(ticket)) {
					for (EntertainmentAuction auction : client.eAuctions) {
						if (auction.AUCTION_TYPE == ticket && !client.hasEntertainmentTicket(auction.AUCTION_DAY)) {
							
							int owned = agent.getOwn(auction.AUCTION_ID);
							int allocated = agent.getAllocation(auction.AUCTION_ID);

							if (owned > 0 && (owned - allocated) > 0) {
								client.giveEntertainmentTicket(auction.AUCTION_DAY, auction.AUCTION_TYPE);
								agent.setAllocation(auction.AUCTION_ID, allocated + 1);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	
	
	/*
	 
MUSEUM auction on day THURSDAY has bonus [2] :
	Client: 4 with bonus [2
	Client: 7 with bonus [189

MUSEUM auction on day MONDAY has bonus [123] :
	Client: 0 with bonus [123
	Client: 6 with bonus [161
	Client: 2 with bonus [178
	Client: 5 with bonus [187

MUSEUM auction on day TUESDAY has bonus [123] :
	Client: 0 with bonus [123
	Client: 1 with bonus [151
	Client: 6 with bonus [161
	Client: 2 with bonus [178

MUSEUM auction on day WEDNESDAY has bonus [2] :
	Client: 4 with bonus [2
	Client: 3 with bonus [7
	Client: 0 with bonus [123
	Client: 2 with bonus [178

AMUSEMENT auction on day MONDAY has bonus [22] :
	Client: 6 with bonus [22
	Client: 2 with bonus [50
	Client: 5 with bonus [147
	Client: 0 with bonus [166

AMUSEMENT auction on day TUESDAY has bonus [22] :
	Client: 6 with bonus [22
	Client: 2 with bonus [50
	Client: 1 with bonus [107
	Client: 0 with bonus [166

AMUSEMENT auction on day WEDNESDAY has bonus [50] :
	Client: 2 with bonus [50
	Client: 3 with bonus [142
	Client: 4 with bonus [146
	Client: 0 with bonus [166

ALLIGATOR_WRESTLING auction on day MONDAY has bonus [26] :
	Client: 2 with bonus [26
	Client: 5 with bonus [69
	Client: 6 with bonus [97
	Client: 0 with bonus [189

ALLIGATOR_WRESTLING auction on day TUESDAY has bonus [26] :
	Client: 2 with bonus [26
	Client: 1 with bonus [78
	Client: 6 with bonus [97
	Client: 0 with bonus [189

ALLIGATOR_WRESTLING auction on day WEDNESDAY has bonus [26] :
	Client: 2 with bonus [26
	Client: 3 with bonus [65
	Client: 4 with bonus [82
	Client: 0 with bonus [189

ALLIGATOR_WRESTLING auction on day THURSDAY has bonus [82] :
	Client: 4 with bonus [82
	Client: 7 with bonus [95

AMUSEMENT auction on day THURSDAY has bonus [110] :
	Client: 7 with bonus [110
	Client: 4 with bonus [146
	 
	 */
	
	
	
	
	public void allocateTickets3() {
		
		/* 
		 * 
		 * 
		 * PRIORITISE
		 * 
		 * create empty set of ent auctions
		 * 
		 * for each client
		 * for each ticket type
		 * for each client ent auction of this ticket type
		 * 		if this auction already exists in set (by searching for day and ticket type - don't compare objects)
		 * 			add client to the existing auction's queue
		 *		else
		 *			create new ent auction
		 *			add client to auction's queue
		 *
		 * for each auction in set
		 * 		add to priority queue of auctions based on highest client bonus
		 * 
		*/
		
		List<EntertainmentAuction> auctionsList = new ArrayList<EntertainmentAuction>(12);
		
		for (ClientPosition client : clients) {
			for (TacTypeEnum ticket : ticketTypes) {
				for (EntertainmentAuction auction : client.getEntertainmentAuctions(ticket)) {
					
					boolean containsAuction = false;
					EntertainmentAuction chosenAuction = null;
					
					for (EntertainmentAuction auc : auctionsList) {
						if (auc.AUCTION_DAY == auction.AUCTION_DAY && auc.AUCTION_TYPE == auction.AUCTION_TYPE) {
							containsAuction = true;
							chosenAuction = auc;
							break;
						}
					}
					
					if (containsAuction) {
						chosenAuction.addClient(client);
					} else {
						chosenAuction = new EntertainmentAuction(agent, auction.getQuote());
						chosenAuction.addClient(client);
						auctionsList.add(chosenAuction);
					}
				}
			}
		}
		
		Comparator<EntertainmentAuction> comparator = new Comparator<EntertainmentAuction>() {
			@Override
			public int compare(EntertainmentAuction e1, EntertainmentAuction e2) {
				return (e1.highestBonus < e2.highestBonus) ? 1 : -1;
			}
		};
		Collections.sort(auctionsList, comparator);
		
		/* 
		 * 
		 * ALLOCATE
		 * 
		 * for each ent auction in queue (max 12)
		 * for each client in auction's queue
		 * if we own tickets in this auction (can allocate)
		 * 		allocate ticket to client by removing client from queue
		 * 		updated auction allocation
		 * else we can't allocate anymore
		 * 		break;
		 * end for each client
		 * if auction's queue is empty (no client wants this auction at all)
		 * 		remove this auction from queue of ent auctions
		 * 
		 * (we have now allocated the tickets we started with in order of highest bonus across all auctions and clients
		 * 	we are then left with (in the ent auction queue) all auctions and their respective (queued) clients
		 * 	who still need tickets)
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		
		for (EntertainmentAuction auction : auctionsList) {
			
			System.out.println();
			System.out.println(auction.AUCTION_TYPE + " auction on day " + auction.AUCTION_DAY + " has bonus [" + auction.highestBonus + "]");
			
			for (ClientPosition client : auction.clientsNeeding) {
				
				System.out.println("\tClient " + client.client.CLIENT_ID + " with bonus [" + client.getEntertainmentBonus(auction.AUCTION_TYPE) + "]");
				
				int owned = agent.getOwn(auction.AUCTION_ID);
				int allocated = agent.getAllocation(auction.AUCTION_ID);

				if (owned > 0 && (owned - allocated) > 0) {
					client.giveEntertainmentTicket(auction.AUCTION_DAY, auction.AUCTION_TYPE);
					agent.setAllocation(auction.AUCTION_ID, allocated + 1);
					break;
				}
				
				// ... update auction allocation
			}
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void allocateTickets2() {
		
		/*
		 * We want to optimise the bonuses which means selling the highest bonus across
		 * ticket types for each client.
		 * 
		 * for each client
		 * for each of the ticket types, get the bonus and save the highest
		 * add the client to the ticket type priority queue for the highest ticket type bonus
		 * 
		 * for each of the queues, get the ticket type
		 * for each client in the queue
		 * get the bonus
		 * get the list of ent auctions for that client
		 * for each ent auction
		 * get the auction day
		 * if the client is staying on this day
		 * 	if we own tickets on this day
		 * 	 remove auction from list of auctions we need to sell tickets to
		 * 	 keep and add auction to list of auctions to watch as we can potentially sell tickets if the sell price is higher than the client bonus
		 * 	 [rest of code to allocate to client]
		 *  otherwise (we need to buy tickets on this day as they are staying on this day)
		 * 	 remove auction from list of auctions we need to sell tickets to
		 * 	 add this auction to the list of auctions we should buy from - the ask/buy price may be lower than the bonus
		 * otherwise client isn't staying on this day
		 * 	if we own tickets on this day
		 * 	 add auction to list of auctions we have to sell to as no client wants it (with quantity we want to sell)
		 * 
		 * get the list of auctions we need to buy from
		 * for each auction, get auction type
		 * for each client in the queue of clients for that auction/ticket type
		 * if the client doesn't have a ticket of this type on this auction day and the ask price is lower than their bonus
		 * 	put a bid in for that ticket
		 * 
		 * get the list of auctions we need to sell to
		 * get the quantity we need to sell
		 * get optimal price we can sell at (depending on time of the game)
		 * sell all
		 * 
		 * 
		 */
		
//		@SuppressWarnings("unchecked")
//		PriorityQueue<ClientPosition>[] ticketQueues = new PriorityQueue[3];
//		
//		int i = 0;
//		for (TacTypeEnum ticket : ticketTypes) {
//			PriorityQueue<ClientPosition> queue =
//					new PriorityQueue<ClientPosition>(8, new BonusComparator(ticket));
//			for (ClientPosition client : clients) {
//				queue.add(client);
//			}
//			ticketQueues[i] = queue;
//			i++;
//		}
//		
//		
//		for (i = 0; i < ticketQueues.length; i++) {
//			TacTypeEnum ticket = ticketTypes[i];
//			
//			for (ClientPosition client : ticketQueues[i]) {
//				if (!client.hasEntertainmentTicket(ticket)) {
//					int bonus = client.getEntertainmentBonus(ticket);
//					
//					for (EntertainmentAuction auction : client.getEntertainmentAuctions(ticket)) {
//						if (!client.hasEntertainmentTicket(auction.AUCTION_DAY)) {
//							int owned = agent.getOwn(auction.AUCTION_ID);
//							if (client.isStaying(auction.AUCTION_DAY)) { // METHOD MUST REFLECT TRUE STAYING DAYS
//								sellToAuctions.remove(auction);
//								if (owned > 0) {
//									int allocated = agent.getAllocation(auction.AUCTION_ID);
//									if (owned - allocated > 0) {
//										client.giveEntertainmentTicket(auction.AUCTION_DAY, auction.AUCTION_TYPE);
//										agent.setAllocation(auction.AUCTION_ID, allocated + 1);
//									}
//								} else {
//									buyFromAuctions.add(auction);
//								}
//							} else if (owned > 0) {
//								sellToAuctions.add(auction);
//							}
//						}
//					}
//				}
//			}
//		}
		
	}
	
	public TreeSet<ClientPosition> getClientsByHighestBonus(TacTypeEnum ticket) {
		
		final TacTypeEnum t = ticket;
		Comparator<ClientPosition> comparator = new Comparator<ClientPosition>() {
			@Override
			public int compare(ClientPosition c1, ClientPosition c2) {
				return (c1.getEntertainmentBonus(t) < c2.getEntertainmentBonus(t)) ? 1 : -1;
			}
		};
		TreeSet<ClientPosition> set = new TreeSet<ClientPosition>(comparator);
		
		for (ClientPosition client : clients) {
			set.add(client);
		}
		return set;
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
