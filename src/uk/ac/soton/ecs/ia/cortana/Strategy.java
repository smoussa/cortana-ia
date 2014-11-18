package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public class Strategy {

	private static final int VALIDITY_WAIT_TIME = 1;
	private static final int MAX_VALIDITY = 5;

	protected Map<Auction, Position> auctionPositions;
	protected List<ClientPosition> clientPositions;
	
	protected AuctionMaster auctionMaster;
	
	private Strategy oldStrategy;
	
	Date lastTimeFailed;
	private int validity;
	
	// Updated Strategy
	public Strategy(Strategy oldStrategy) {
		this.auctionMaster = oldStrategy.auctionMaster;
		this.oldStrategy = oldStrategy;
		this.auctionPositions = new HashMap<Auction, Position>();
		this.clientPositions = new ArrayList<ClientPosition>();
		this.validity = MAX_VALIDITY;
		this.createClientPositions();
		this.createPositions();
		this.finalizePositions();
	}

	// Initial Strategy
	public Strategy(AuctionMaster auctionMaster) {
		this.auctionMaster = auctionMaster;
		this.auctionPositions = new HashMap<Auction, Position>();
		this.clientPositions = new ArrayList<ClientPosition>();
		this.validity = MAX_VALIDITY;
		this.createClientPositions();
		this.createPositions();
		this.finalizePositions();
	}
	
	private void finalizePositions() {
		for (Position p:auctionPositions.values()){
			p.finalise();
		}
	}
	
	public void sendBids() {
		for(Position position:this.auctionPositions.values()) {
			position.bidMe();
		}
	}

	public boolean isStrategyValid() {
		boolean isStrategyValid = true;
		
		List<Integer> invalidPositions = new ArrayList<>();
		
		for(Position position:this.auctionPositions.values()) {
			System.out.println("Aucion " + position.auction.AUCTION_TYPE + " " + position.auction.AUCTION_DAY.getDayNumber());
			if(!position.isValid()) {
				isStrategyValid = false;
				invalidPositions.add(position.auction.AUCTION_ID);
			}
		}
		
		if(!isStrategyValid) {
			decreaseValidity();
		}
		
		if(isStrategyValid)
			validity = MAX_VALIDITY;
		
		if(validity <= 0)
			System.out.println("REPLAN BECAUSE OF " + invalidPositions);
		
		return validity > 0;
		
	}
	
	private void decreaseValidity() {
		Date currentTime = Calendar.getInstance().getTime();
		
		// Only accept a validity failure if the last one was over X seconds ago
		if(lastTimeFailed != null) {
			long diff = currentTime.getTime() - lastTimeFailed.getTime();
	        long diffSeconds = diff / 1000 % 60;
	        
	        if(diffSeconds >= VALIDITY_WAIT_TIME) {
	        	validity--;
				lastTimeFailed = currentTime;
	        }
		}
		else {
			validity--;
			lastTimeFailed = currentTime;
		}
	}

	public boolean isStrategySatisfied() {
			
		boolean flag = true;
		for(Position position:this.auctionPositions.values()) {
			if(!position.isFullySatisfied())
				flag = false;
		}
		
		return flag;
	}
	
	public float getScore(){
		float ut = 0;
		for(ClientPosition cp: clientPositions){
			ut += cp.getUtility();
		}
		float cost = 0;
		for(Position p: auctionPositions.values()){
			cost += p.getCost();
		}
		return ut - cost;	
	}

	public Position getPosition(Auction auction) {
		return this.auctionPositions.get(auction);
	}

	public void createClientPositions() {
		for (ClientPreference c: auctionMaster.clientPreferences.values()){		
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, c.inFlight);
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT, c.outFlight);
			
			TacTypeEnum hotelType = TacTypeEnum.GOOD_HOTEL;
			
			double highestHotelPrice = 0;
			
			List<HotelAuction> hotelList = new ArrayList<HotelAuction>();
			
			for (int d = c.inFlight.getDayNumber(); d < c.outFlight.getDayNumber(); d++) {
				int auction = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, hotelType, DayEnum.getDay(d));
				HotelAuction hotelAuction = auctionMaster.getHotelAuction(auction);
				hotelList.add(hotelAuction);
				
				if(hotelAuction.getAskPrice() > highestHotelPrice)
					highestHotelPrice = hotelAuction.getAskPrice();
			}
			
			// For testing we take the highest hotel price and bid that
			double nightPrice = highestHotelPrice + 100; //getHotelPricePerNight(inflight, outflight, hotelList.size());
			ClientPositionFixedHotelPrice cp = new ClientPositionFixedHotelPrice(c, inflight, outflight, hotelList, nightPrice);
			this.clientPositions.add(cp);
		}
	}
	
	public void createPositions() {
		for(ClientPosition cpSuper: this.clientPositions){
			ClientPositionFixedHotelPrice cp = (ClientPositionFixedHotelPrice)cpSuper;
			FlightAuction inflightAuction = cp.inFlight;
			FlightAuction outflightAuction = cp.outFlight;
			Collection<HotelAuction> hotelList = cp.hotels;
	
			if (!auctionPositions.containsKey(inflightAuction)){
				Position flightPosition = new FlightPositionBidNow(inflightAuction);
				auctionPositions.put(inflightAuction, flightPosition);
			}
			auctionPositions.get(inflightAuction).peopleWhoWantMe.add(cp);
			
			if (!auctionPositions.containsKey(outflightAuction)){
				Position flightPosition = new FlightPositionBidNow(outflightAuction);
				auctionPositions.put(outflightAuction, flightPosition);
			}
			auctionPositions.get(outflightAuction).peopleWhoWantMe.add(cp);
			
			for(HotelAuction hotelAuction:hotelList) {
				if (!auctionPositions.containsKey(hotelAuction)){
					Position hotelPosition = new HotelPositionBidNow(hotelAuction, cp.pricePerNight);
					auctionPositions.put(hotelAuction, hotelPosition);
				}
				auctionPositions.get(hotelAuction).peopleWhoWantMe.add(cp);
			}
		}
	}
	
}
