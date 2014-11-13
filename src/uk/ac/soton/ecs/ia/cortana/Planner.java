package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public class Planner {

	public static Strategy makeStrategy(AuctionMaster auctionMaster) {
		
		Strategy strategy = new Strategy(auctionMaster);
		
		createClientPositions(strategy, auctionMaster);
		
		return strategy;
	}
	
	private static void createClientPositions(Strategy strategy, AuctionMaster auctionMaster) {
		List<ClientPosition> cpList = new ArrayList<ClientPosition>();
		
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
			cpList.add(cp);
		}
		
		strategy.clientPositions = cpList;
		
		createPositions(strategy, cpList);
	}
	
	/*
	private static double getHotelPricePerNight(FlightAuction inFlight, FlightAuction outFlight, int numberOfNights) {
		return (CortanaHeuristics.CLIENT_UTILITY - inFlight.getAskPrice() - outFlight.getAskPrice() - CortanaHeuristics.ATTEMPTED_PROFIT_PER_CLIENT) / numberOfNights;
	}*/

	private static void createPositions(Strategy strategy, List<ClientPosition> cpList) {
		
		for(ClientPosition cpSuper: cpList){
			ClientPositionFixedHotelPrice cp = (ClientPositionFixedHotelPrice)cpSuper;
			FlightAuction inflightAuction = cp.inFlight;
			FlightAuction outflightAuction = cp.outFlight;
			Collection<HotelAuction> hotelList = cp.hotels;

			if (!strategy.auctionPositions.containsKey(inflightAuction)){
				Position flightPosition = new FlightPositionBidNow(inflightAuction);
				strategy.auctionPositions.put(inflightAuction, flightPosition);
			}
			strategy.auctionPositions.get(inflightAuction).peopleWhoWantMe.add(cp);
			
			if (!strategy.auctionPositions.containsKey(outflightAuction)){
				Position flightPosition = new FlightPositionBidNow(outflightAuction);
				strategy.auctionPositions.put(outflightAuction, flightPosition);
			}
			strategy.auctionPositions.get(outflightAuction).peopleWhoWantMe.add(cp);
			
			for(HotelAuction hotelAuction:hotelList) {
				if (!strategy.auctionPositions.containsKey(hotelAuction)){
					Position hotelPosition = new HotelPositionBidNow(hotelAuction, cp.pricePerNight);
					strategy.auctionPositions.put(hotelAuction, hotelPosition);
				}
				strategy.auctionPositions.get(hotelAuction).peopleWhoWantMe.add(cp);
			}
		}
		
		
	}
	
}
