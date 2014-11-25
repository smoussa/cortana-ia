package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public class StrategyBidMoreBadHotel extends Strategy {

	public StrategyBidMoreBadHotel(AuctionMaster auctionMaster) {
		super(auctionMaster);
	}
	
	public StrategyBidMoreBadHotel(Strategy oldStrategy) {
		super(oldStrategy);
	}

	@Override
	public void createClientPositions() {
		for (ClientPreference c: auctionMaster.clientPreferences.values()){		
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, c.inFlight);
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT, c.outFlight);
			
			TacTypeEnum hotelType = TacTypeEnum.CHEAP_HOTEL;
			
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
			double nightPrice = highestHotelPrice + 100;
			ClientPositionFixedHotelPrice cp = new ClientPositionFixedHotelPrice(c, inflight, outflight, hotelList, nightPrice);
			this.clientPositions.add(cp);
		}
	}
	
	@Override
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
			auctionPositions.get(inflightAuction).peopleWhoWantMe++;
			
			if (!auctionPositions.containsKey(outflightAuction)){
				Position flightPosition = new FlightPositionBidNow(outflightAuction);
				auctionPositions.put(outflightAuction, flightPosition);
			}
			auctionPositions.get(outflightAuction).peopleWhoWantMe++;
			
			for(HotelAuction hotelAuction:hotelList) {
				if (!auctionPositions.containsKey(hotelAuction)){
					Position hotelPosition = new HotelPositionBidNow(hotelAuction, cp.pricePerNight);
					auctionPositions.put(hotelAuction, hotelPosition);
				}
				auctionPositions.get(hotelAuction).peopleWhoWantMe++;
			}
		}
	}
	
	@Override
	public String toString() {
		return "Bid More Bad Hotel";
	}
	
}
