package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		
//		allocateTickets();
		allocateTickets2();
	}
	
	public void quoteUpdated(Quote quote) {
		
		int auctionId = quote.getAuction();
		EntertainmentAuction auction = master.getEntertainmentAuction(auctionId);
		
		int alloc = agent.getAllocation(auctionId) - agent.getOwn(auctionId);
		if (alloc != 0) {
			if (alloc < 0) {
				prices[auctionId] = 100f - (agent.getGameTime() * 120f) / 720000;
				auction.ask(alloc, prices[auctionId]);
			} else {
				prices[auctionId] = 20f + (agent.getGameTime() * 100f) / 720000;
				auction.bid(alloc, prices[auctionId]);
			}
		} else {
			
		}
	}
	
	public void update() {
		
		this.clients = master.getStrategy().getAllClientPositions();
		
		auctionsList = new ArrayList<EntertainmentAuction>(96);
		buyFromAuctions = new ArrayList<EntertainmentAuction>();
		
		agent.clearAllocation();
		allocateTickets2();
	}
	
	private void allocateTickets2() {
		
		/*

Auctions List:
MUSEUM on THURSDAY with Client 1 has bonus [194]
MUSEUM on WEDNESDAY with Client 1 has bonus [194]
MUSEUM on TUESDAY with Client 1 has bonus [194]
AMUSEMENT on WEDNESDAY with Client 0 has bonus [183]
AMUSEMENT on TUESDAY with Client 0 has bonus [183]
AMUSEMENT on MONDAY with Client 0 has bonus [183]
AMUSEMENT on THURSDAY with Client 1 has bonus [165]
AMUSEMENT on WEDNESDAY with Client 1 has bonus [165]
AMUSEMENT on TUESDAY with Client 1 has bonus [165]
ALLIGATOR_WRESTLING on THURSDAY with Client 3 has bonus [159]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 3 has bonus [159]
ALLIGATOR_WRESTLING on TUESDAY with Client 3 has bonus [159]
ALLIGATOR_WRESTLING on MONDAY with Client 3 has bonus [159]
AMUSEMENT on THURSDAY with Client 5 has bonus [143]
AMUSEMENT on WEDNESDAY with Client 5 has bonus [143]
ALLIGATOR_WRESTLING on TUESDAY with Client 7 has bonus [135]
ALLIGATOR_WRESTLING on MONDAY with Client 7 has bonus [135]
ALLIGATOR_WRESTLING on THURSDAY with Client 4 has bonus [124]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 4 has bonus [124]
ALLIGATOR_WRESTLING on TUESDAY with Client 4 has bonus [124]
AMUSEMENT on THURSDAY with Client 3 has bonus [122]
AMUSEMENT on TUESDAY with Client 3 has bonus [122]
AMUSEMENT on WEDNESDAY with Client 3 has bonus [122]
AMUSEMENT on MONDAY with Client 3 has bonus [122]
AMUSEMENT on WEDNESDAY with Client 6 has bonus [119]
ALLIGATOR_WRESTLING on THURSDAY with Client 5 has bonus [114]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 5 has bonus [114]
ALLIGATOR_WRESTLING on THURSDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on TUESDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 0 has bonus [99]
ALLIGATOR_WRESTLING on TUESDAY with Client 0 has bonus [99]
ALLIGATOR_WRESTLING on MONDAY with Client 0 has bonus [99]
AMUSEMENT on TUESDAY with Client 7 has bonus [91]
AMUSEMENT on MONDAY with Client 7 has bonus [91]
MUSEUM on THURSDAY with Client 3 has bonus [81]
MUSEUM on WEDNESDAY with Client 3 has bonus [81]
MUSEUM on TUESDAY with Client 3 has bonus [81]
MUSEUM on MONDAY with Client 3 has bonus [81]
MUSEUM on WEDNESDAY with Client 0 has bonus [80]
MUSEUM on TUESDAY with Client 0 has bonus [80]
MUSEUM on MONDAY with Client 0 has bonus [80]
MUSEUM on THURSDAY with Client 5 has bonus [77]
MUSEUM on WEDNESDAY with Client 5 has bonus [77]
AMUSEMENT on THURSDAY with Client 4 has bonus [56]
AMUSEMENT on WEDNESDAY with Client 4 has bonus [56]
AMUSEMENT on TUESDAY with Client 4 has bonus [56]
MUSEUM on THURSDAY with Client 2 has bonus [55]
MUSEUM on WEDNESDAY with Client 2 has bonus [55]
MUSEUM on TUESDAY with Client 2 has bonus [55]
MUSEUM on WEDNESDAY with Client 6 has bonus [54]
ALLIGATOR_WRESTLING on THURSDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on TUESDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 6 has bonus [32]
MUSEUM on THURSDAY with Client 4 has bonus [23]
MUSEUM on WEDNESDAY with Client 4 has bonus [23]
MUSEUM on TUESDAY with Client 4 has bonus [23]
MUSEUM on TUESDAY with Client 7 has bonus [21]
MUSEUM on MONDAY with Client 7 has bonus [21]
AMUSEMENT on THURSDAY with Client 2 has bonus [13]
AMUSEMENT on WEDNESDAY with Client 2 has bonus [13]
AMUSEMENT on TUESDAY with Client 2 has bonus [13]

Buy From Auctions List:
AMUSEMENT on WEDNESDAY with Client 0 has bonus [183]
AMUSEMENT on WEDNESDAY with Client 1 has bonus [165]
AMUSEMENT on THURSDAY with Client 5 has bonus [143]
AMUSEMENT on WEDNESDAY with Client 5 has bonus [143]
AMUSEMENT on WEDNESDAY with Client 6 has bonus [119]
ALLIGATOR_WRESTLING on THURSDAY with Client 5 has bonus [114]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 5 has bonus [114]
ALLIGATOR_WRESTLING on THURSDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 0 has bonus [99]
ALLIGATOR_WRESTLING on MONDAY with Client 0 has bonus [99]
AMUSEMENT on MONDAY with Client 7 has bonus [91]
MUSEUM on WEDNESDAY with Client 3 has bonus [81]
MUSEUM on MONDAY with Client 3 has bonus [81]
MUSEUM on WEDNESDAY with Client 0 has bonus [80]
MUSEUM on MONDAY with Client 0 has bonus [80]
AMUSEMENT on WEDNESDAY with Client 4 has bonus [56]
MUSEUM on WEDNESDAY with Client 6 has bonus [54]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 6 has bonus [32]
MUSEUM on WEDNESDAY with Client 4 has bonus [23]
MUSEUM on MONDAY with Client 7 has bonus [21]
AMUSEMENT on WEDNESDAY with Client 2 has bonus [13]

after strategy update


Auctions List:
MUSEUM on THURSDAY with Client 1 has bonus [194]
MUSEUM on WEDNESDAY with Client 1 has bonus [194]
MUSEUM on TUESDAY with Client 1 has bonus [194]
AMUSEMENT on TUESDAY with Client 0 has bonus [183]
AMUSEMENT on MONDAY with Client 0 has bonus [183]
AMUSEMENT on THURSDAY with Client 1 has bonus [165]
AMUSEMENT on WEDNESDAY with Client 1 has bonus [165]
AMUSEMENT on TUESDAY with Client 1 has bonus [165]
ALLIGATOR_WRESTLING on THURSDAY with Client 3 has bonus [159]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 3 has bonus [159]
ALLIGATOR_WRESTLING on TUESDAY with Client 3 has bonus [159]
ALLIGATOR_WRESTLING on MONDAY with Client 3 has bonus [159]
AMUSEMENT on THURSDAY with Client 5 has bonus [143]
ALLIGATOR_WRESTLING on TUESDAY with Client 7 has bonus [135]
ALLIGATOR_WRESTLING on MONDAY with Client 7 has bonus [135]
ALLIGATOR_WRESTLING on THURSDAY with Client 4 has bonus [124]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 4 has bonus [124]
ALLIGATOR_WRESTLING on TUESDAY with Client 4 has bonus [124]
AMUSEMENT on THURSDAY with Client 3 has bonus [122]
AMUSEMENT on WEDNESDAY with Client 3 has bonus [122]
AMUSEMENT on TUESDAY with Client 3 has bonus [122]
AMUSEMENT on MONDAY with Client 3 has bonus [122]
AMUSEMENT on WEDNESDAY with Client 6 has bonus [119]
ALLIGATOR_WRESTLING on THURSDAY with Client 5 has bonus [114]
ALLIGATOR_WRESTLING on THURSDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on TUESDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on TUESDAY with Client 0 has bonus [99]
ALLIGATOR_WRESTLING on MONDAY with Client 0 has bonus [99]
AMUSEMENT on TUESDAY with Client 7 has bonus [91]
AMUSEMENT on MONDAY with Client 7 has bonus [91]
MUSEUM on THURSDAY with Client 3 has bonus [81]
MUSEUM on WEDNESDAY with Client 3 has bonus [81]
MUSEUM on TUESDAY with Client 3 has bonus [81]
MUSEUM on MONDAY with Client 3 has bonus [81]
MUSEUM on TUESDAY with Client 0 has bonus [80]
MUSEUM on MONDAY with Client 0 has bonus [80]
MUSEUM on THURSDAY with Client 5 has bonus [77]
AMUSEMENT on THURSDAY with Client 4 has bonus [56]
AMUSEMENT on WEDNESDAY with Client 4 has bonus [56]
AMUSEMENT on TUESDAY with Client 4 has bonus [56]
MUSEUM on THURSDAY with Client 2 has bonus [55]
MUSEUM on WEDNESDAY with Client 2 has bonus [55]
MUSEUM on TUESDAY with Client 2 has bonus [55]
MUSEUM on WEDNESDAY with Client 6 has bonus [54]
ALLIGATOR_WRESTLING on THURSDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on TUESDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 6 has bonus [32]
MUSEUM on THURSDAY with Client 4 has bonus [23]
MUSEUM on WEDNESDAY with Client 4 has bonus [23]
MUSEUM on TUESDAY with Client 4 has bonus [23]
MUSEUM on TUESDAY with Client 7 has bonus [21]
MUSEUM on MONDAY with Client 7 has bonus [21]
AMUSEMENT on THURSDAY with Client 2 has bonus [13]
AMUSEMENT on WEDNESDAY with Client 2 has bonus [13]
AMUSEMENT on TUESDAY with Client 2 has bonus [13]

Buy From Auctions List:
AMUSEMENT on WEDNESDAY with Client 1 has bonus [165]
AMUSEMENT on THURSDAY with Client 5 has bonus [143]
AMUSEMENT on WEDNESDAY with Client 3 has bonus [122]
AMUSEMENT on WEDNESDAY with Client 6 has bonus [119]
ALLIGATOR_WRESTLING on THURSDAY with Client 5 has bonus [114]
ALLIGATOR_WRESTLING on THURSDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on MONDAY with Client 0 has bonus [99]
AMUSEMENT on MONDAY with Client 7 has bonus [91]
MUSEUM on WEDNESDAY with Client 3 has bonus [81]
MUSEUM on MONDAY with Client 3 has bonus [81]
MUSEUM on MONDAY with Client 0 has bonus [80]
AMUSEMENT on WEDNESDAY with Client 4 has bonus [56]
MUSEUM on WEDNESDAY with Client 6 has bonus [54]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 1 has bonus [48]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 6 has bonus [32]
MUSEUM on WEDNESDAY with Client 4 has bonus [23]
MUSEUM on MONDAY with Client 7 has bonus [21]
AMUSEMENT on WEDNESDAY with Client 2 has bonus [13]


		 
		 */
		
		
		
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
				return (e1.client.getEntertainmentBonus(e1.AUCTION_TYPE) <
						e2.client.getEntertainmentBonus(e2.AUCTION_TYPE)) ? 1 : -1;
			}
		};
		Collections.sort(auctionsList, comparator);
		
		// print auction list
		System.out.println();
		System.out.println("Auctions List:");
		for (EntertainmentAuction auction : auctionsList) {
			System.out.println(auction.AUCTION_TYPE + " on " + auction.AUCTION_DAY +
					" with Client " + auction.client.client.CLIENT_ID +
					" has bonus [" + auction.client.getEntertainmentBonus(auction.AUCTION_TYPE) + "]");
		}
		System.out.println();
		
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
				
			} else {
				itr.remove();
			}
		}
		
		// print to buy from list
		System.out.println("Buy From Auctions List:");
		for (EntertainmentAuction auction : auctionsList) {
			System.out.println(auction.AUCTION_TYPE + " on " + auction.AUCTION_DAY +
					" with Client " + auction.client.client.CLIENT_ID +
					" has bonus [" + auction.client.getEntertainmentBonus(auction.AUCTION_TYPE) + "]");
		}
		System.out.println();
	}
	
	private void buyNeeded() {
		
		/*
		 * 
		 * AMUSEMENT on WEDNESDAY with Client 1 has bonus [165]
AMUSEMENT on THURSDAY with Client 5 has bonus [143]
AMUSEMENT on WEDNESDAY with Client 3 has bonus [122]
AMUSEMENT on WEDNESDAY with Client 6 has bonus [119]
ALLIGATOR_WRESTLING on THURSDAY with Client 5 has bonus [114]
ALLIGATOR_WRESTLING on THURSDAY with Client 2 has bonus [104]
ALLIGATOR_WRESTLING on WEDNESDAY with Client 2 has bonus [104]

		 * for each auction in auction list
		 * get bonus
		 * 
		 * 
		 */
		
		
		
	}
	
	
	
	
	
	
	
	
//	public void allocateTickets() {
//		
//		// prioritise auctions by client bonuses
//		for (ClientPosition client : clients) {
//			for (TacTypeEnum ticket : ticketTypes) {
//				for (EntertainmentAuction auction : client.getEntertainmentAuctions(ticket)) {
//					
//					boolean containsAuction = false;
//					EntertainmentAuction chosenAuction = null;
//					for (EntertainmentAuction auc : auctionsList) {
//						if (auc.AUCTION_DAY == auction.AUCTION_DAY && auc.AUCTION_TYPE == auction.AUCTION_TYPE) {
//							containsAuction = true;
//							chosenAuction = auc;
//							break;
//						}
//					}
//					
//					if (containsAuction) {
//						chosenAuction.addClient(client);
//					} else {
//						chosenAuction = new EntertainmentAuction(agent, auction.getQuote());
//						chosenAuction.addClient(client);
//						auctionsList.add(chosenAuction);
//					}
//				}
//			}
//		}
//		
//		// sort auctions
//		Comparator<EntertainmentAuction> comparator = new Comparator<EntertainmentAuction>() {
//			@Override
//			public int compare(EntertainmentAuction e1, EntertainmentAuction e2) {
//				return (e1.highestBonus < e2.highestBonus) ? 1 : -1;
//			}
//		};
//		Collections.sort(auctionsList, comparator);
//		
//		// print before allocations
//		for (EntertainmentAuction auction : auctionsList) {
//			System.out.println();
//			System.out.println(auction.AUCTION_TYPE + " auction on day " + auction.AUCTION_DAY + " has bonus [" + auction.highestBonus + "]");
//			for (ClientPosition client : auction.clientsNeeding) {
//				System.out.println("\tClient " + client.client.CLIENT_ID + " with bonus [" + client.getEntertainmentBonus(auction.AUCTION_TYPE) + "]");
//			}
//		}
//		
//		// allocate
//		for (EntertainmentAuction auction : auctionsList) {
//			for (ClientPosition client : auction.clientsNeeding) {
//				
//				int owned = agent.getOwn(auction.AUCTION_ID);
//				int allocated = agent.getAllocation(auction.AUCTION_ID);
//				
//				if (owned > 0 && (owned - allocated) > 0) {
//					client.giveEntertainmentTicket(auction.AUCTION_DAY, auction.AUCTION_TYPE);
//					agent.setAllocation(auction.AUCTION_ID, allocated + 1);
////					auction.removeClient(client);
//				} else {
//					break;
//				}
//			}
////			if (auction.clientsNeeding.isEmpty()) {
////				auctionsList.remove(auction);
////			}
//		}
//		
//		// print allocations
//		for (EntertainmentAuction auction : auctionsList) {
//			System.out.println();
//			System.out.println(auction.AUCTION_TYPE + " auction on day " + auction.AUCTION_DAY + " has bonus [" + auction.highestBonus + "]");
//			for (ClientPosition client : auction.clientsNeeding) {
//				System.out.println("\tClient " + client.client.CLIENT_ID + " with bonus [" + client.getEntertainmentBonus(auction.AUCTION_TYPE) + "]");
//			}
//		}
//	}
	
	
	
	/*
	
MUSEUM auction on day THURSDAY has bonus [193]
	Client 1 with bonus [193]
	Client 0 with bonus [133]
	Client 7 with bonus [113]
	Client 3 with bonus [110]

AMUSEMENT auction on day THURSDAY has bonus [184]
	Client 0 with bonus [184]
	Client 7 with bonus [151]
	Client 3 with bonus [68]
	Client 1 with bonus [9]

AMUSEMENT auction on day WEDNESDAY has bonus [184]
	Client 0 with bonus [184]
	Client 7 with bonus [151]
	Client 3 with bonus [68]
	Client 4 with bonus [49]
	Client 5 with bonus [29]

ALLIGATOR_WRESTLING auction on day TUESDAY has bonus [180]
	Client 6 with bonus [180]
	Client 7 with bonus [135]
	Client 4 with bonus [113]
	Client 5 with bonus [4]

ALLIGATOR_WRESTLING auction on day MONDAY has bonus [180]
	Client 6 with bonus [180]
	Client 7 with bonus [135]
	Client 2 with bonus [104]
	Client 5 with bonus [4]

ALLIGATOR_WRESTLING auction on day THURSDAY has bonus [172]
	Client 1 with bonus [172]
	Client 3 with bonus [153]
	Client 7 with bonus [135]
	Client 0 with bonus [12]

ALLIGATOR_WRESTLING auction on day WEDNESDAY has bonus [153]
	Client 3 with bonus [153]
	Client 7 with bonus [135]
	Client 4 with bonus [113]
	Client 0 with bonus [12]
	Client 5 with bonus [4]

AMUSEMENT auction on day TUESDAY has bonus [151]
	Client 7 with bonus [151]
	Client 6 with bonus [62]
	Client 4 with bonus [49]
	Client 5 with bonus [29]

AMUSEMENT auction on day MONDAY has bonus [151]
	Client 7 with bonus [151]
	Client 6 with bonus [62]
	Client 2 with bonus [47]
	Client 5 with bonus [29]

MUSEUM auction on day WEDNESDAY has bonus [133]
	Client 0 with bonus [133]
	Client 5 with bonus [125]
	Client 7 with bonus [113]
	Client 3 with bonus [110]
	Client 4 with bonus [83]

MUSEUM auction on day TUESDAY has bonus [125]
	Client 5 with bonus [125]
	Client 7 with bonus [113]
	Client 4 with bonus [83]
	Client 6 with bonus [2]

MUSEUM auction on day MONDAY has bonus [125]
	Client 5 with bonus [125]
	Client 7 with bonus [113]
	Client 2 with bonus [60]
	Client 6 with bonus [2]
	
	
	
	
	

	 */
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
