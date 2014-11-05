package uk.ac.soton.ecs.ia.cortana;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import se.sics.tac.aw.ClientPreferenceEnum;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public class AuctionMaster {

	private static final long UPDATE_TIMER_MILLISECONDS = 5000;
	
	private Map<Integer, FlightAuction> flightAuctions;
	private Map<Integer, HotelAuction> hotelAuctions;
	private Map<Integer, EntertainmentAuction> entertainmentAuctions;
	public Map<Integer, ClientPreference> clientPreferences;
	
	private DummyAgent cortana;
	
	boolean flightUpdated = false;
	boolean hotelUpdated = false;
	boolean bidsNotSent = true;
	
	private Timer updTimer;
	private Strategy strategy;
	
	public AuctionMaster(DummyAgent cortana) {
		this.cortana = cortana;
		flightAuctions = new HashMap<Integer, FlightAuction>();
		hotelAuctions = new HashMap<Integer, HotelAuction>();
		entertainmentAuctions = new HashMap<Integer, EntertainmentAuction>();
		clientPreferences = new HashMap<Integer, ClientPreference>();
		createClientPreferences();
	}
	
	//call when actually have some initial prices
	public void gameStart(){
		initialiseAuctions(cortana.agent);
		createStrategy();
		sendBids(cortana.agent);
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
		
		System.err.println("Could not find auction with id " + auctionId);
		return null;
		
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
	
	private void initialiseAuctions(TACAgent agent) {
		
		for (int i = 0; i < TACAgent.getAuctionNo(); i++) {

			TacCategoryEnum category = DummyAgent.getAuctionCategory(i);
			DayEnum auctionDay = DummyAgent.getAuctionDay(i);
			TacTypeEnum auctionType = DummyAgent.getAuctionType(category, i);
			double askPrice = agent.getQuote(i).getAskPrice();
			double bidPrice = agent.getQuote(i).getBidPrice();
			
			switch (TACAgent.getAuctionCategory(i)) {
				case TACAgent.CAT_FLIGHT:
					FlightAuction flightAuction = new FlightAuction(auctionType, auctionDay, askPrice, bidPrice, i);
					flightAuctions.put(i, flightAuction);
					
					System.out.println("Flight Price: " + askPrice);
				break;
				case TACAgent.CAT_HOTEL:
					HotelAuction hotelAuction = new HotelAuction(auctionType, auctionDay, askPrice, bidPrice, i);
					hotelAuctions.put(i, hotelAuction);
					
					System.out.println("Hotel Price: " + askPrice);
				break;
				case TACAgent.CAT_ENTERTAINMENT:
					EntertainmentAuction entertainmentAuction = new EntertainmentAuction(auctionType, auctionDay, askPrice, bidPrice, i);
					entertainmentAuctions.put(i, entertainmentAuction);
					
					System.out.println("Entertainment Price: " + askPrice);
				break;
				default:
				break;
			}
		}
		
		createAuctionUpdater(agent);
	}
	
	private synchronized void createStrategy() {
		System.out.println("MAKING A STRATEGY");
		strategy = Planner.makeStrategy(this);
	}
	
	private synchronized void updateAuctions(TACAgent agent) {
		
		for (int i = 0; i < TACAgent.getAuctionNo(); i++) {
			double askPrice = agent.getQuote(i).getAskPrice();
			double bidPrice = agent.getQuote(i).getBidPrice();
			getAuction(i).updatePrice(askPrice, bidPrice);
			
			if(agent.getQuote(i).isAuctionClosed())
				getAuction(i).close();
			
			if(agent.getOwn(i) > 0)
				getAuction(i).setNumberOwned(agent.getOwn(i));
			
			getAuction(i).setNumberProbablyOwned(agent.getProbablyOwn(i));
		}
		
		if(!strategy.isStrategyValid()) {
			createStrategy();
		}
		
	}
	
	public void sendBids(TACAgent agent) {
		this.strategy.sendBids(agent);
	}
	
	public void quoteUpdated() {
		if (!bidsNotSent){
			updateAuctions(cortana.agent);
		}
	}
	
	public void quoteUpdated(TacCategoryEnum category) {
		// Used to make sure code only runs once prices have been updated
		if(category == TacCategoryEnum.CAT_FLIGHT)
			flightUpdated = true;
		if(category == TacCategoryEnum.CAT_HOTEL)
			hotelUpdated = true;
		
		if(flightUpdated && hotelUpdated && bidsNotSent) {
			this.gameStart();
			updateAuctions(cortana.agent);
			bidsNotSent = false;
		}
		
		if (!bidsNotSent){
			updateAuctions(cortana.agent);
		}
	
	}
	
	public int getClientPreference(int clientId, ClientPreferenceEnum preference) {
		return cortana.agent.getClientPreference(clientId, ClientPreferenceEnum.getCode(preference));
	}
	
	private void createAuctionUpdater(final TACAgent agent) {
		updTimer = new Timer();
		updTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				updateAuctions(agent);
			}
		}, 0, UPDATE_TIMER_MILLISECONDS);
	}
	
	public void kill() {
		if(this.updTimer != null)
			updTimer.cancel();
	}
	
}
