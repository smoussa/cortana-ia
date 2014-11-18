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

import java.util.logging.Logger;

import se.sics.tac.util.ArgEnumerator;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;

public class DummyAgent extends AgentImpl {

	private static final Logger log = Logger.getLogger(DummyAgent.class.getName());

	private static final boolean DEBUG = false;

	protected void init(ArgEnumerator args) {}

	private AuctionMaster auctionMaster;
	
	public DummyAgent() {
	}
	
	public void quoteUpdated(Quote quote) {
		System.out.println("@@@@@@ Single quote updated");
		auctionMaster.quoteUpdated(agent, quote);
	}
	
	public void quoteUpdated(int auctionCategory) {
		System.out.println("@@@@@@ Category quote updated");
		log.fine("All quotes for " + TACAgent.auctionCategoryToString(auctionCategory) + " has been updated");
		auctionMaster.check();
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
		return "Category: " + TACAgent.getAuctionCategory(auctionId) + "   Type: " + TACAgent.getAuctionTypeAsString(auctionId) + " Day: " + DummyAgent.getAuctionDay(auctionId);
	}

	public void bidError(Bid bid, int status) {
		log.warning("Bid Error in auction " + bid.getAuction() + ": " + status + " (" + agent.commandStatusToString(status) + ')');
	}

	public void gameStarted() {
		log.fine("Game " + agent.getGameID() + " started!");
		this.auctionMaster = new AuctionMaster(this);
	}
	
	public void gameStopped() {
		log.fine("Game Stopped!");
		auctionMaster.gameEnd();
	}

	public void auctionClosed(int auction) {
		log.fine("*** Auction " + auction + " closed!");
	}

	/*
	 * Helper methods to convert ints to enums
	 */
	
	public static DayEnum getAuctionDay(int auctionId) {
		return DayEnum.getDay(TACAgent.getAuctionDay(auctionId));
	}
	
	public static TacTypeEnum getAuctionType(TacCategoryEnum category, int auction) {
		return TacTypeEnum.getType(category, TACAgent.getAuctionType(auction));
	}
	
	public static TacCategoryEnum getAuctionCategory(int i) {
		return TacCategoryEnum.getCategory(TACAgent.getAuctionCategory(i));
	}
	
	public static int getAuctionFor(TacCategoryEnum category, TacTypeEnum type, DayEnum day) {
		return TACAgent.getAuctionFor(category.getCode(), type.getCode(), day.getDayNumber());
	}

	// -------------------------------------------------------------------
	// Only for backward compability
	// -------------------------------------------------------------------

	public static void main(String[] args) {
		TACAgent.main(args);
	}

} 
