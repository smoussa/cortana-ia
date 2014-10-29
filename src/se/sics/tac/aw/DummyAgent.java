/**
 * TAC AgentWare
 * http://www.sics.se/tac        tac-dev@sics.se
 *
 * Copyright (c) 2001-2005 SICS AB. All rights reserved.
 *
 * SICS grants you the right to use, modify, and redistribute this
 * software for noncommercial purposes, on the conditions that you:
 * (1) retain the original headers, including the copyright notice and
 * this text, (2) clearly document the difference between any derived
 * software and the original, and (3) acknowledge your use of this
 * software in pertaining publications and reports.  SICS provides
 * this software "as is", without any warranty of any kind.  IN NO
 * EVENT SHALL SICS BE LIABLE FOR ANY DIRECT, SPECIAL OR INDIRECT,
 * PUNITIVE, INCIDENTAL OR CONSEQUENTIAL LOSSES OR DAMAGES ARISING OUT
 * OF THE USE OF THE SOFTWARE.
 *
 * -----------------------------------------------------------------
 *
 * Author  : Joakim Eriksson, Niclas Finne, Sverker Janson
 * Created : 23 April, 2002
 * Updated : $Date: 2005/06/07 19:06:16 $
 *	     $Revision: 1.1 $
 * ---------------------------------------------------------
 * DummyAgent is a simplest possible agent for TAC. It uses
 * the TACAgent agent ware to interact with the TAC server.
 *
 * Important methods in TACAgent:
 *
 * Retrieving information about the current Game
 * ---------------------------------------------
 * int getGameID()
 *  - returns the id of current game or -1 if no game is currently plaing
 *
 * getServerTime()
 *  - returns the current server time in milliseconds
 *
 * getGameTime()
 *  - returns the time from start of game in milliseconds
 *
 * getGameTimeLeft()
 *  - returns the time left in the game in milliseconds
 *
 * getGameLength()
 *  - returns the game length in milliseconds
 *
 * int getAuctionNo()
 *  - returns the number of auctions in TAC
 *
 * int getClientPreference(int client, int type)
 *  - returns the clients preference for the specified type
 *   (types are TACAgent.{ARRIVAL, DEPARTURE, HOTEL_VALUE, E1, E2, E3}
 *
 * int getAuctionFor(int category, int type, int day)
 *  - returns the auction-id for the requested resource
 *   (categories are TACAgent.{CAT_FLIGHT, CAT_HOTEL, CAT_ENTERTAINMENT
 *    and types are TACAgent.TYPE_INFLIGHT, TACAgent.TYPE_OUTFLIGHT, etc)
 *
 * int getAuctionCategory(int auction)
 *  - returns the category for this auction (CAT_FLIGHT, CAT_HOTEL,
 *    CAT_ENTERTAINMENT)
 *
 * int getAuctionDay(int auction)
 *  - returns the day for this auction.
 *
 * int getAuctionType(int auction)
 *  - returns the type for this auction (TYPE_INFLIGHT, TYPE_OUTFLIGHT, etc).
 *
 * int getOwn(int auction)
 *  - returns the number of items that the agent own for this
 *    auction
 *
 * Submitting Bids
 * ---------------------------------------------
 * void submitBid(Bid)
 *  - submits a bid to the tac server
 *
 * void replaceBid(OldBid, Bid)
 *  - replaces the old bid (the current active bid) in the tac server
 *
 *   Bids have the following important methods:
 *    - create a bid with new Bid(AuctionID)
 *
 *   void addBidPoint(int quantity, float price)
 *    - adds a bid point in the bid
 *
 * Help methods for remembering what to buy for each auction:
 * ----------------------------------------------------------
 * int getAllocation(int auctionID)
 *   - returns the allocation set for this auction
 * void setAllocation(int auctionID, int quantity)
 *   - set the allocation for this auction
 *
 *
 * Callbacks from the TACAgent (caused via interaction with server)
 *
 * bidUpdated(Bid bid)
 *  - there are TACAgent have received an answer on a bid query/submission
 *   (new information about the bid is available)
 * bidRejected(Bid bid)
 *  - the bid has been rejected (reason is bid.getRejectReason())
 * bidError(Bid bid, int error)
 *  - the bid contained errors (error represent error status - commandStatus)
 *
 * quoteUpdated(Quote quote)
 *  - new information about the quotes on the auction (quote.getAuction())
 *    has arrived
 * quoteUpdated(int category)
 *  - new information about the quotes on all auctions for the auction
 *    category has arrived (quotes for a specific type of auctions are
 *    often requested at once).

 * auctionClosed(int auction)
 *  - the auction with id "auction" has closed
 *
 * transaction(Transaction transaction)
 *  - there has been a transaction
 *
 * gameStarted()
 *  - a TAC game has started, and all information about the
 *    game is available (preferences etc).
 *
 * gameStopped()
 *  - the current game has ended
 *
 */

package se.sics.tac.aw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import se.sics.tac.util.ArgEnumerator;

public class DummyAgent extends AgentImpl {

	private static final Logger log = Logger.getLogger(DummyAgent.class.getName());

	private static final boolean DEBUG = false;

	private float[] prices;
	
	private Map<Integer, FlightAuction> flightAuctions = new HashMap<Integer, FlightAuction>();
	private Map<Integer, HotelAuction> hotelAuctions = new HashMap<Integer, HotelAuction>();
	private Map<Integer, Client> clients = new HashMap<Integer, Client>();
	

	protected void init(ArgEnumerator args) {
		prices = new float[TACAgent.getAuctionNo()];
	}

	public void quoteUpdated(Quote quote) {}

	boolean flightUpdated = false;
	boolean hotelUpdated = false;
	boolean bidsNotSent = true;
	
	public void quoteUpdated(int auctionCategory) {
		log.fine("All quotes for "
				+ TACAgent.auctionCategoryToString(auctionCategory)
				+ " has been updated");
		
		if(auctionCategory == TACAgent.CAT_FLIGHT)
			flightUpdated = true;
		if(auctionCategory == TACAgent.CAT_HOTEL)
			hotelUpdated = true;
		
		if(flightUpdated && hotelUpdated && bidsNotSent) {
			initialiseAuctions();
			calculateAllocation();
			sendBids();
			bidsNotSent = false;
		}
	}

	public void bidUpdated(Bid bid) {
		log.fine("Bid Updated: id=" + bid.getID() + " auction=" + bid.getAuction() + " state=" + bid.getProcessingStateAsString());
		log.fine("       Hash: " + bid.getBidHash());
	}

	public void bidRejected(Bid bid) {
		log.warning("Bid Rejected for auction " + bid.getAuction() +  ": " + bid.getID());
		log.warning("Auction Info: " + getAuctionInfo(bid.getAuction()));
		log.warning("      Reason: " + bid.getRejectReason() + " ("+ bid.getRejectReasonAsString() + ')');
	}

	private String getAuctionInfo(int auctionId) {
		return "Category: " + TACAgent.getAuctionCategory(auctionId) + "   Type: " + TACAgent.getAuctionTypeAsString(auctionId) + " Day: " + Day.getDay(TACAgent.getAuctionDay(auctionId));
	}

	public void bidError(Bid bid, int status) {
		log.warning("Bid Error in auction " + bid.getAuction() + ": " + status + " (" + agent.commandStatusToString(status) + ')');
	}

	public void gameStarted() {
		log.fine("Game " + agent.getGameID() + " started!");
	}
	
	private void initialiseAuctions() {
		
		for (int i = 0; i < TACAgent.getAuctionNo(); i++) {

			int auctionDay = TACAgent.getAuctionDay(i);
			int auctionType = TACAgent.getAuctionType(i);
			double price = agent.getQuote(i).getAskPrice();
			
			switch (TACAgent.getAuctionCategory(i)) {
				case TACAgent.CAT_FLIGHT:
					FlightAuction flightAuction = new FlightAuction(auctionType, Day.getDay(auctionDay), price, i);
					flightAuctions.put(i, flightAuction);
					
					System.out.println("Flight Price: " + price);
				break;
				case TACAgent.CAT_HOTEL:
					HotelAuction hotelAuction = new HotelAuction(Day.getDay(auctionDay), price, i, auctionType);
					hotelAuctions.put(i, hotelAuction);
					
					System.out.println("Hotel Price: " + price);
				break;
				case TACAgent.CAT_ENTERTAINMENT:
					// Later
				break;
				default:
				break;
			}
			
		}
		
	}
	
	private void calculateAllocation() {
		for (int i = 0; i < 8; i++) {
			
			int inFlightDay = agent.getClientPreference(i, TACAgent.ARRIVAL);
			int outFlightDay = agent.getClientPreference(i, TACAgent.DEPARTURE);
			int hotelType;

			int auction = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_INFLIGHT, inFlightDay);
			
			FlightAuction inflight = flightAuctions.get(auction);
			
			auction = TACAgent.getAuctionFor(TACAgent.CAT_FLIGHT, TACAgent.TYPE_OUTFLIGHT, outFlightDay);
			
			FlightAuction outflight = flightAuctions.get(auction);
			
			hotelType = TACAgent.TYPE_GOOD_HOTEL;
			
			List<HotelAuction> hotelList = new ArrayList<>();
			
			for (int d = inFlightDay; d < outFlightDay; d++) {
				auction = TACAgent.getAuctionFor(TACAgent.CAT_HOTEL, hotelType, d);
				HotelAuction hotelAuction = hotelAuctions.get(auction);
				
				hotelList.add(hotelAuction);
			}
			
			Client client = new Client(i, inflight, outflight, hotelList);
			clients.put(i, client);
			
			inflight.peopleWhoWantMe.add(client);
			outflight.peopleWhoWantMe.add(client);
			
			for(HotelAuction hotel:hotelList) {
				hotel.peopleWhoWantMe.add(client);
			}
			
		}
	}

	public void gameStopped() {
		log.fine("Game Stopped!");
	}

	public void auctionClosed(int auction) {
		log.fine("*** Auction " + auction + " closed!");
	}

	private void sendBids() {
		
		for(Entry<Integer, HotelAuction> entry:this.hotelAuctions.entrySet()) {
			HotelAuction hotelAuction = entry.getValue();
			hotelAuction.bidMe(agent, hotelAuction.getBidPrice());
		}
		
		for(Entry<Integer, FlightAuction> entry:this.flightAuctions.entrySet()) {
			FlightAuction flightAuction = entry.getValue();
			flightAuction.bidMe(agent, flightAuction.price);
		}
		
	}

	// -------------------------------------------------------------------
	// Only for backward compability
	// -------------------------------------------------------------------

	public static void main(String[] args) {
		TACAgent.main(args);
	}

} // DummyAgent
