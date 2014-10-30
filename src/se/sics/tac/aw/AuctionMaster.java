package se.sics.tac.aw;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AuctionMaster {

	private Map<Integer, FlightAuction> flightAuctions;
	private Map<Integer, HotelAuction> hotelAuctions;
	
	boolean flightUpdated = false;
	boolean hotelUpdated = false;
	boolean bidsNotSent = true;
	
	public AuctionMaster() {
		flightAuctions = new HashMap<Integer, FlightAuction>();
		hotelAuctions = new HashMap<Integer, HotelAuction>();
	}
	
	public Auction getAuction(int auctionId) {
		
		if(flightAuctions.containsKey(auctionId)) {
			return flightAuctions.get(auctionId);
		}
		else if(hotelAuctions.containsKey(auctionId)) {
			return hotelAuctions.get(auctionId);
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
	
	public HotelAuction getHotelAuction(int auctionId) {
		if(hotelAuctions.containsKey(auctionId)) {
			return hotelAuctions.get(auctionId);
		}
		return null;
	}
	
	private void initialiseAuctions(TACAgent agent) {
		
		for (int i = 0; i < TACAgent.getAuctionNo(); i++) {

			TacCategory category = DummyAgent.getAuctionCategory(i);
			Day auctionDay = DummyAgent.getAuctionDay(i);
			TacType auctionType = DummyAgent.getAuctionType(category, i);
			double price = agent.getQuote(i).getAskPrice();
			
			switch (TACAgent.getAuctionCategory(i)) {
				case TACAgent.CAT_FLIGHT:
					FlightAuction flightAuction = new FlightAuction(auctionType, auctionDay, price, i);
					flightAuctions.put(i, flightAuction);
					
					System.out.println("Flight Price: " + price);
				break;
				case TACAgent.CAT_HOTEL:
					HotelAuction hotelAuction = new HotelAuction(auctionType, auctionDay, price, i);
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
	
	public void sendBids(TACAgent agent) {
		for(Entry<Integer, HotelAuction> entry:this.hotelAuctions.entrySet()) {
			HotelAuction hotelAuction = entry.getValue();
			hotelAuction.bidMe(agent, hotelAuction.getBidPrice());
		}
		
		for(Entry<Integer, FlightAuction> entry:this.flightAuctions.entrySet()) {
			FlightAuction flightAuction = entry.getValue();
			flightAuction.bidMe(agent, flightAuction.getAskingPrice());
		}
	}
	
	public void quoteUpdated(DummyAgent cortana, TacCategory category) {
		// Used to make sure code only runs once prices have been updated
		if(category == TacCategory.CAT_FLIGHT)
			flightUpdated = true;
		if(category == TacCategory.CAT_HOTEL)
			hotelUpdated = true;
		
		if(flightUpdated && hotelUpdated && bidsNotSent) {
			initialiseAuctions(cortana.agent);
			cortana.calculateAllocation();
			sendBids(cortana.agent);
			bidsNotSent = false;
		}
	}
	
}
