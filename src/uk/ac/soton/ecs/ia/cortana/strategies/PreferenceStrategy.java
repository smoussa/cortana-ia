package uk.ac.soton.ecs.ia.cortana.strategies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;
import uk.ac.soton.ecs.ia.cortana.ClientPositionFixedHotelPrice;
import uk.ac.soton.ecs.ia.cortana.ClientPositionVariableHotelPrice;
import uk.ac.soton.ecs.ia.cortana.ClientPreference;
import uk.ac.soton.ecs.ia.cortana.CortanaHeuristics;
import uk.ac.soton.ecs.ia.cortana.FlightAuction;
import uk.ac.soton.ecs.ia.cortana.FlightPositionBidMin;
import uk.ac.soton.ecs.ia.cortana.HotelAuction;
import uk.ac.soton.ecs.ia.cortana.HotelPositionBidNow;
import uk.ac.soton.ecs.ia.cortana.Position;
import uk.ac.soton.ecs.ia.cortana.Strategy;

public class PreferenceStrategy extends Strategy {

	public PreferenceStrategy(AuctionMaster auctionMaster) {
		super(auctionMaster);
	}
	
	public PreferenceStrategy(Strategy OldStrategy) {
		super(OldStrategy);
	}

	@Override
	public void createClientPositions() {
CLIENT:	for (ClientPreference c: auctionMaster.clientPreferences.values()){		
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, c.inFlight);
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT, c.outFlight);
			
			TacTypeEnum hotelType = calculateBestHotel(c.hotelBonus, inflight, outflight);
			
			List<HotelAuction> hotelList = new ArrayList<HotelAuction>();
			
			for (int d = c.inFlight.getDayNumber(); d < c.outFlight.getDayNumber(); d++) {
				int auction = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, hotelType, DayEnum.getDay(d));
				HotelAuction hotelAuction = auctionMaster.getHotelAuction(auction);
				if(hotelAuction.isClosed())
					continue CLIENT;
				hotelList.add(hotelAuction);
			}

			double nightPrice;
			
			// TODO use estimator on flights to get a better idea
			
			nightPrice = (CortanaHeuristics.CLIENT_UTILITY - inflight.getAskPrice() - outflight.getAskPrice() - CortanaHeuristics.ATTEMPTED_PROFIT_PER_CLIENT) / hotelList.size();
			
			Map<HotelAuction, Double> hotelMap = new HashMap<HotelAuction, Double>();
			
			for(HotelAuction hotelAuction:hotelList) {
				hotelMap.put(hotelAuction, Math.max(nightPrice, hotelAuction.getMinimumBid()));
			}
			
			ClientPositionVariableHotelPrice cp = new ClientPositionVariableHotelPrice(c, inflight, outflight, hotelMap);
			this.clientPositions.add(cp);
		}
	}
	
	private TacTypeEnum calculateBestHotel(int hotelBonus, FlightAuction inflight, FlightAuction outflight) {
		
		int cheapAskSum = 0;
		int premiumAskSum = -hotelBonus;
		
		for (int d = inflight.AUCTION_DAY.getDayNumber(); d < outflight.AUCTION_DAY.getDayNumber(); d++) {
			int auction = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, TacTypeEnum.GOOD_HOTEL, DayEnum.getDay(d));
			HotelAuction hotelAuction = auctionMaster.getHotelAuction(auction);
			
			premiumAskSum += hotelAuction.getMinimumBid();
		
			if(hotelAuction.isClosed())
				premiumAskSum -= 99999;
			
			auction = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, TacTypeEnum.CHEAP_HOTEL, DayEnum.getDay(d));
			hotelAuction = auctionMaster.getHotelAuction(auction);

			cheapAskSum += hotelAuction.getMinimumBid();
			
			if(hotelAuction.isClosed())
				cheapAskSum -= 99999;
		}

		if(cheapAskSum < premiumAskSum)
			return TacTypeEnum.CHEAP_HOTEL;

		return TacTypeEnum.GOOD_HOTEL;
	}

	@Override
	public void createPositions() {
		for(ClientPosition cpSuper: this.clientPositions){
			
			if(!cpSuper.isFeasible())
				continue;
			
			ClientPositionFixedHotelPrice cp = (ClientPositionFixedHotelPrice)cpSuper;
			FlightAuction inflightAuction = cp.inFlight;
			FlightAuction outflightAuction = cp.outFlight;
			Collection<HotelAuction> hotelList = cp.hotels;
	
			if (!auctionPositions.containsKey(inflightAuction)){
				Position flightPosition = new FlightPositionBidMin(inflightAuction, this.auctionMaster);
				auctionPositions.put(inflightAuction, flightPosition);
			}
			auctionPositions.get(inflightAuction).peopleWhoWantMe.add(cp);
			
			if (!auctionPositions.containsKey(outflightAuction)){
				Position flightPosition = new FlightPositionBidMin(outflightAuction, this.auctionMaster);
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
	
	@Override
	public String toString() {
		return "Preference strategy";
	}

}
