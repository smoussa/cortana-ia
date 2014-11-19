package uk.ac.soton.ecs.ia.cortana;

import java.util.HashMap;
import java.util.Map;

import se.sics.tac.aw.ClientPreferenceEnum;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.FlightAuctionChangeStore;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators.FlightPriceEstimatorMonteCarlo;

public class AuctionMaster {

	private Map<Integer, FlightAuction> flightAuctions;
	private Map<Integer, HotelAuction> hotelAuctions;
	private Map<Integer, EntertainmentAuction> entertainmentAuctions;
	public Map<Integer, ClientPreference> clientPreferences;
	
	private Map<FlightAuction, FlightAuctionChangeStore> flightAuctionChangeStores;
	private Map<FlightAuctionChangeStore, Double> previousPrices;
	
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
		
		flightAuctionChangeStores = new HashMap<FlightAuction, FlightAuctionChangeStore>();
		previousPrices = new HashMap<FlightAuctionChangeStore, Double>();
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

		this.strategy = Planner.makeStrategy(this, this.strategy);
		
		sendBids();
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
				FlightAuctionChangeStore facs = new FlightAuctionChangeStore();
				flightAuctionChangeStores.put(flightAuction, facs);
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
		
		if(auction.AUCTION_CAT == TacCategoryEnum.CAT_FLIGHT)
		{
			((FlightAuction) auction).addP((float) auction.getAskPrice());
			
			FlightAuctionChangeStore facs = flightAuctionChangeStores.get(auction);
			
			double tSecondsNearest10 = this.get10SecondChunkElapsed();
			
			if(tSecondsNearest10 == 0){
				previousPrices.put(facs, auction.getAskPrice());
			}
			else{
	
				double previousPrice = previousPrices.get(facs);
				double priceChange = auction.getAskPrice() - previousPrice;
				previousPrices.put(facs, auction.getAskPrice());
				
				facs.addChange(priceChange, (int) tSecondsNearest10, previousPrice);
			}
			
			System.out.println(facs);		
			
			if (this.strategy != null){
				Position p = this.strategy.getPosition(auction);
				if (p!=null){
					((FlightPosition) this.strategy.getPosition(auction)).tick();
				}
			}
		}
			
	}
	
	public FlightAuctionChangeStore getFlightAuctionChangeStore(FlightAuction f) {
		return flightAuctionChangeStores.get(f);
	}
	
	public Estimator getEstimatorForAuction(FlightAuction f) {
		FlightAuctionChangeStore facs = getFlightAuctionChangeStore(f);
		return new FlightPriceEstimatorMonteCarlo(facs, this.get10SecondChunkElapsed(), f.getAskPrice());
	}

	public synchronized void check() {
		if((strategy == null || !strategy.isStrategyValid()) && this.entertainmentAuctions.size()+this.flightAuctions.size()+this.hotelAuctions.size() == 28) {
			createStrategy();
		}
	}
	
	public int getClientPreference(int clientId, ClientPreferenceEnum preference) {
		return cortana.agent.getClientPreference(clientId, ClientPreferenceEnum.getCode(preference));
	}
	
	public int get10SecondChunkElapsed(){
		return (int) (Math.floor(cortana.agent.getGameTime()/1000 / 10) * 10);
	}

	public void gameEnd() {
		for(FlightAuction f: flightAuctions.values()){
			f.plot();
		}
	}

	public float getExpectedUpperBound(FlightAuction auction) {
		FlightAuctionChangeStore facs = getFlightAuctionChangeStore(auction);
		return (float) facs.getExpectedUpperBound();
	}

}
