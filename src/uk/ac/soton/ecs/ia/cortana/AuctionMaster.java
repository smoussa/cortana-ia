package uk.ac.soton.ecs.ia.cortana;

import java.util.HashMap;
import java.util.Map;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.ClientPreferenceEnum;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public class AuctionMaster {

	private Map<Integer, FlightAuction> flightAuctions;
	private Map<Integer, HotelAuction> hotelAuctions;
	private Map<Integer, EntertainmentAuction> entertainmentAuctions;
	public Map<Integer, ClientPreference> clientPreferences;
	
	private DummyAgent cortana;
	
	boolean flightUpdated = false;
	boolean hotelUpdated = false;
	boolean bidsNotSent = true;
	
	private Strategy strategy;

	public AuctionMaster(DummyAgent cortana) {
		this.cortana = cortana;
		flightAuctions = new HashMap<Integer, FlightAuction>();
		hotelAuctions = new HashMap<Integer, HotelAuction>();
		entertainmentAuctions = new HashMap<Integer, EntertainmentAuction>();
		clientPreferences = new HashMap<Integer, ClientPreference>();
		createClientPreferences();
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
		strategy = Planner.makeStrategy(this);
		sendBids(cortana.agent);
	}
	
	public void sendBids(TACAgent agent) {
		this.strategy.sendBids(agent);
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
		
		if(auction == null)
			this.createAuction(agent, quote);
		else
			auction.update();
	}
	
	public synchronized void check() {
		if((strategy == null || !strategy.isStrategyValid()) && this.entertainmentAuctions.size()+this.flightAuctions.size()+this.hotelAuctions.size() == 28) {
			createStrategy();
		}
	}
	
	public int getClientPreference(int clientId, ClientPreferenceEnum preference) {
		return cortana.agent.getClientPreference(clientId, ClientPreferenceEnum.getCode(preference));
	}

	public void bidUpdated(Bid bid) {
		Auction auction = this.getAuction(bid.getAuction());
		auction.update();
	}
	
}
