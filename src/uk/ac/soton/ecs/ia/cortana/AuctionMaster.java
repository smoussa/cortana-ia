package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import se.sics.tac.aw.ClientPreferenceEnum;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.allocator.FastOptimizerWrapper;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentStrategy;
import uk.ac.soton.ecs.ia.cortana.strategies.StrategyUtils;
import uk.ac.soton.ecs.ia.cortana.strategies.StrategyUtils.StrategyInfo;
import uk.ac.soton.ecs.ia.cortana.strategies.TheOtherStrategy;
import uk.ac.soton.ecs.ia.cortana.strategies.TheStrategy;

public class AuctionMaster {

	private Map<Integer, FlightAuction> flightAuctions;
	private Map<Integer, HotelAuction> hotelAuctions;
	private Map<Integer, EntertainmentAuction> entertainmentAuctions;
	
	public Map<Integer, ClientPreference> clientPreferences;
	
	public final DummyAgent cortana;
	
	boolean flightUpdated = false;
	boolean hotelUpdated = false;
	boolean bidsNotSent = true;
	
	private Strategy strategy;
	private EntertainmentStrategy entertainmentStrategy;

	private Timer lastMinStrategyTimer;
	
	public AuctionMaster(DummyAgent cortana) {

		flightAuctions = new HashMap<Integer, FlightAuction>();
		hotelAuctions = new HashMap<Integer, HotelAuction>();
		entertainmentAuctions = new HashMap<Integer, EntertainmentAuction>();
		
		clientPreferences = new HashMap<Integer, ClientPreference>();
		
		this.cortana = cortana;
		
		// Put the client bonus information into client preference classes
		createClientPreferences();
		
		// Start timer to run last min strategy 8 min + 10 seconds into the game
		lastMinStrategyTimer = new Timer();
		lastMinStrategyTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				lastMinuteStrategy();
			}
		}, 60000 * 8 + 10000, 20000);
	}
	
	public Auction getAuction(int auctionId) {
		
		if(flightAuctions.containsKey(auctionId)) {
			return flightAuctions.get(auctionId);
		}
		else if(hotelAuctions.containsKey(auctionId)) {
			return hotelAuctions.get(auctionId);
		}
		else if(entertainmentAuctions.containsKey(auctionId)) {
			return entertainmentAuctions.get(auctionId);
		}
		
		return null;
		
	}
	
	public Auction getAuction(Quote quote) {
		return getAuction(quote.getAuction());
	}
	
	public FlightAuction getFlightAuction(int auctionId) {
		if(flightAuctions.containsKey(auctionId)) {
			return flightAuctions.get(auctionId);
		}
		return null;
	}
	
	public FlightAuction getFlightAuction(TacTypeEnum type, DayEnum auctionDay) {
		return getFlightAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_FLIGHT, type, auctionDay));
	}
	
	public HotelAuction getHotelAuction(int auctionId) {
		if(hotelAuctions.containsKey(auctionId)) {
			return hotelAuctions.get(auctionId);
		}
		return null;
	}
	
	public EntertainmentAuction getEntertainmentAuction(int auctionId) {
		if(entertainmentAuctions.containsKey(auctionId)) {
			return entertainmentAuctions.get(auctionId);
		}
		return null;
	}
	
	public void createClientPreferences() {
		
		for (int i = 0; i < 8; i++) {
			
			DayEnum inFlightDay = DayEnum.getDay(this.getClientPreference(i, ClientPreferenceEnum.ARRIVAL));
			DayEnum outFlightDay = DayEnum.getDay(this.getClientPreference(i, ClientPreferenceEnum.DEPARTURE));
			int hotelBonus = this.getClientPreference(i, ClientPreferenceEnum.HOTEL_VALUE);
			int e1Bonus = this.getClientPreference(i, ClientPreferenceEnum.E1);
			int e2Bonus = this.getClientPreference(i, ClientPreferenceEnum.E2);
			int e3Bonus = this.getClientPreference(i, ClientPreferenceEnum.E3);
			
			ClientPreference client = new ClientPreference(i, inFlightDay, outFlightDay, hotelBonus, e1Bonus, e2Bonus, e3Bonus);
			clientPreferences.put(i, client);
		}
		
	}
	
	private synchronized void createStrategy() {
		System.out.println("MAKING A STRATEGY");

		this.strategy = new TheStrategy(this);
		this.entertainmentStrategy = new EntertainmentStrategy(this);
		
		sendBids();
	}
	
	private void createOtherStrategy() {
		System.out.println("MAKING OTHER STRATEGY");
		this.strategy = new TheOtherStrategy(this.strategy);
		sendBids();
		this.entertainmentStrategy.update();
	}
	
	public int getCurrentScore() {
		FastOptimizerWrapper fastOptimizerWrapper = new FastOptimizerWrapper();
		fastOptimizerWrapper.addClientPreferences(new ArrayList<>(this.clientPreferences.values()));
		
		for(FlightAuction flightAuction:this.flightAuctions.values()) {
			fastOptimizerWrapper.addOwned(flightAuction.AUCTION_TYPE, flightAuction.AUCTION_DAY, flightAuction.getNumberOwned());
		}
		for(HotelAuction hotelAuction:this.hotelAuctions.values()) {
			fastOptimizerWrapper.addOwned(hotelAuction.AUCTION_TYPE, hotelAuction.AUCTION_DAY, hotelAuction.getNumberOwned());
		}
		for(EntertainmentAuction entertainmentAuction:this.entertainmentAuctions.values()) {
			fastOptimizerWrapper.addOwned(entertainmentAuction.AUCTION_TYPE, entertainmentAuction.AUCTION_DAY, entertainmentAuction.getNumberOwned());
		}
		
		fastOptimizerWrapper.go();
		
		return fastOptimizerWrapper.getLatestScore();
	}
	
	public synchronized void lastMinuteStrategy() {
		lastMinStrategyTimer.cancel();
		
		System.out.println("LAST MIN FLIGHT PURCHASES");
		
		// Work out what would happen if we didn't buy anything
		int currentScore = getCurrentScore();
		
		StrategyInfo unlimitedFlightsInfo = StrategyUtils.getScoreGivenUnlimitedFlights(this, false);
		
		// Check if the improvement in score is worth it given the price of flights
		System.out.println("Score could be " + unlimitedFlightsInfo.getScore() + " with the flights its only " + (unlimitedFlightsInfo.getScore() - unlimitedFlightsInfo.getAdditionalCosts()) + " compared to our original " + currentScore);
		if(unlimitedFlightsInfo.getScore() - unlimitedFlightsInfo.getAdditionalCosts() < currentScore)
			return;
		
		System.out.println("WE'RE BUYING TICKETS!");
		
		int[][] flightsToBuy = unlimitedFlightsInfo.getFlightsToBuy();
		
		// Print out tickets
		for(int i = 1; i < flightsToBuy.length; i++) {
			System.out.println(DayEnum.getDay(i) + " " + flightsToBuy[i][0] + " " + flightsToBuy[i][1]);
		}
		
		for(int i = 1; i < flightsToBuy.length; i++) {
			if(flightsToBuy[i][0] > 0) {
				Auction auction = getAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_FLIGHT, TacTypeEnum.INFLIGHT, DayEnum.getDay(i)));
				auction.bid(flightsToBuy[i][0], (float)auction.getAskPrice() + 20);
			}
			if(flightsToBuy[i][1] > 0) {
				Auction auction = getAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_FLIGHT, TacTypeEnum.OUTFLIGHT, DayEnum.getDay(i)));
				auction.bid(flightsToBuy[i][1], (float)auction.getAskPrice() + 20);
			}
		}
		
	}
	
	public void sendBids() {
		this.strategy.sendBids();
	}
	
	private void createAuction(TACAgent agent, Quote quote) {
		
		int auctionId = quote.getAuction();
		
		switch (TACAgent.getAuctionCategory(auctionId)) {
			case TACAgent.CAT_FLIGHT:
				FlightAuction flightAuction = new FlightAuction(agent, quote);
				flightAuctions.put(auctionId, flightAuction);
			break;
			case TACAgent.CAT_HOTEL:
				HotelAuction hotelAuction = new HotelAuction(agent, quote);
				hotelAuctions.put(auctionId, hotelAuction);
			break;
			case TACAgent.CAT_ENTERTAINMENT:
				EntertainmentAuction entertainmentAuction = new EntertainmentAuction(agent, quote);
				entertainmentAuctions.put(auctionId, entertainmentAuction);
			break;
			default:
			break;
		}
		
	}
	
	public synchronized void quoteUpdated(TACAgent agent, Quote quote) {
		
		Auction auction = this.getAuction(quote);
		
		if(auction == null) {
			this.createAuction(agent, quote);
			auction = this.getAuction(quote);
		}
		
		if(auction.AUCTION_CAT == TacCategoryEnum.CAT_FLIGHT) {
			((FlightAuction) auction).tick(cortana.agent.get10SecondChunkElapsed());	
			
			if (this.strategy != null){
				Position p = this.strategy.getPosition(auction);
				if (p!=null){
					((FlightPosition) this.strategy.getPosition(auction)).tick(this.cortana.agent.get10SecondChunkElapsed());
				}
			}
		} 
		else if (auction.AUCTION_CAT == TacCategoryEnum.CAT_ENTERTAINMENT) {
			if (entertainmentStrategy != null) {
				entertainmentStrategy.quoteUpdated(quote);
			}
		}
			
	}

	public synchronized void check() {
		// If we don't have a strategy and all the auction objects have been made, then we can start planning
		if(strategy == null && this.entertainmentAuctions.size()+this.flightAuctions.size()+this.hotelAuctions.size() == 28) {
			createStrategy();
		}
		else if(strategy != null && !strategy.isStrategyValid()) {
			createOtherStrategy();
		}
	}
	
	public int getClientPreference(int clientId, ClientPreferenceEnum preference) {
		return cortana.agent.getClientPreference(clientId, ClientPreferenceEnum.getCode(preference));
	}

	public void gameEnd() {
//		Code to print out flight purchase information, shows if we bought at the true minimum price
//		for(FlightAuction f: flightAuctions.values()){
//			f.plot();
//		}
//		System.out.println("We predicted our score would be " + strategy.getScore());
	}

	public Strategy getStrategy() {
		return this.strategy;
	}
	
	public Iterator<FlightAuction> getFlightAuctionIterator() {
		return this.flightAuctions.values().iterator();
	}
	
	public Iterator<HotelAuction> getHotelAuctionIterator() {
		return this.hotelAuctions.values().iterator();
	}
	
	public Iterator<EntertainmentAuction> getEntertainmentAuctionIterator() {
		return this.entertainmentAuctions.values().iterator();
	}

}
