package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;

public class InitialStrategy extends Strategy {

	public InitialStrategy(AuctionMaster auctionMaster) {
		super(auctionMaster);
		
		List<ClientPositionFixedHotelPrice> cpList = new ArrayList<ClientPositionFixedHotelPrice>();
		
		for (ClientPreference c: auctionMaster.clientPreferences.values()){		
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, c.inFlight);
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT, c.outFlight);
			
			TacTypeEnum hotelType = TacTypeEnum.GOOD_HOTEL;
			
			List<HotelAuction> hotelList = new ArrayList<HotelAuction>();
			
			for (int d = c.inFlight.getDayNumber(); d < c.outFlight.getDayNumber(); d++) {
				int auction = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, hotelType, DayEnum.getDay(d));
				HotelAuction hotelAuction = auctionMaster.getHotelAuction(auction);
				hotelList.add(hotelAuction);
			}
			
			double nightPrice = getHotelPricePerNight(inflight, outflight, hotelList.size());
			ClientPositionFixedHotelPrice cp = new ClientPositionFixedHotelPrice(c, inflight, outflight, hotelList, nightPrice);
			cpList.add(cp);
		}
		
		createPositions(cpList);
	}
	
	private void createPositions(List<ClientPositionFixedHotelPrice> cpList) {
		
		for(ClientPositionFixedHotelPrice cp: cpList){
			FlightAuction inflightAuction = cp.inFlight;
			FlightAuction outflightAuction = cp.outFlight;
			Collection<HotelAuction> hotelList = cp.hotels;

			if (!auctionPositions.containsKey(inflightAuction)){
				Position flightPosition = new FlightPositionInitial(inflightAuction);
				auctionPositions.put(inflightAuction, flightPosition);
			}
			auctionPositions.get(inflightAuction).peopleWhoWantMe.add(cp);
			
			if (!auctionPositions.containsKey(outflightAuction)){
				Position flightPosition = new FlightPositionInitial(outflightAuction);
				auctionPositions.put(outflightAuction, flightPosition);
			}
			auctionPositions.get(outflightAuction).peopleWhoWantMe.add(cp);
			
			for(HotelAuction hotelAuction:hotelList) {
				if (!auctionPositions.containsKey(hotelAuction)){
					Position hotelPosition = new HotelPositionInitial(hotelAuction);
					auctionPositions.put(hotelAuction, hotelPosition);
				}
				auctionPositions.get(hotelAuction).peopleWhoWantMe.add(cp);
			}
		}
		
		
	}
	
	public double getHotelPricePerNight(FlightAuction inFlight, FlightAuction outFlight, int numberOfNights) {
		return (CortanaHeuristics.CLIENT_UTILITY - inFlight.getAskPrice() - outFlight.getAskPrice() - CortanaHeuristics.ATTEMPTED_PROFIT_PER_CLIENT) / numberOfNights;
	}
	
}
