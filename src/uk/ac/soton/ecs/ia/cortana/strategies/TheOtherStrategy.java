package uk.ac.soton.ecs.ia.cortana.strategies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.Auction;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;
import uk.ac.soton.ecs.ia.cortana.ClientPreference;
import uk.ac.soton.ecs.ia.cortana.FlightAuction;
import uk.ac.soton.ecs.ia.cortana.FlightPositionBidMin;
import uk.ac.soton.ecs.ia.cortana.HotelAuction;
import uk.ac.soton.ecs.ia.cortana.Position;
import uk.ac.soton.ecs.ia.cortana.Strategy;
import uk.ac.soton.ecs.ia.cortana.allocator.FastOptimizerWrapper;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;

public class TheOtherStrategy extends TheStrategy {

	public TheOtherStrategy(Strategy oldStrategy) {
		super(oldStrategy);
	}
	
	@Override
	public void createClientPositions() {
		
		// Run allocator to find positions
		FastOptimizerWrapper fastOptimizerWrapper = new FastOptimizerWrapper();
		
		fastOptimizerWrapper.addClientPreferences(new ArrayList<ClientPreference>(auctionMaster.clientPreferences.values()));
		
		fastOptimizerWrapper.addOwned(TacTypeEnum.INFLIGHT, DayEnum.MONDAY, 8);
		fastOptimizerWrapper.addOwned(TacTypeEnum.INFLIGHT, DayEnum.TUESDAY, 8);
		fastOptimizerWrapper.addOwned(TacTypeEnum.OUTFLIGHT, DayEnum.TUESDAY, 8);
		fastOptimizerWrapper.addOwned(TacTypeEnum.INFLIGHT, DayEnum.WEDNESDAY, 8);
		fastOptimizerWrapper.addOwned(TacTypeEnum.OUTFLIGHT, DayEnum.WEDNESDAY, 8);
		fastOptimizerWrapper.addOwned(TacTypeEnum.INFLIGHT, DayEnum.THURSDAY, 8);
		fastOptimizerWrapper.addOwned(TacTypeEnum.OUTFLIGHT, DayEnum.THURSDAY, 8);
		fastOptimizerWrapper.addOwned(TacTypeEnum.OUTFLIGHT, DayEnum.FRIDAY, 8);
		
		Iterator<HotelAuction> hotelAuctionIterator = auctionMaster.getHotelAuctionIterator();
		while(hotelAuctionIterator.hasNext()) {
			HotelAuction hotelAuction = hotelAuctionIterator.next();
			fastOptimizerWrapper.addOwned(hotelAuction.AUCTION_TYPE, hotelAuction.AUCTION_DAY, hotelAuction.getNumberOwned() + hotelAuction.getNumberProbablyOwned());
		}
		
		Iterator<EntertainmentAuction> entertainmentAuctionIterator = auctionMaster.getEntertainmentAuctionIterator();
		while(entertainmentAuctionIterator.hasNext()) {
			EntertainmentAuction entertainmentAuction = entertainmentAuctionIterator.next();
			fastOptimizerWrapper.addOwned(entertainmentAuction.AUCTION_TYPE, entertainmentAuction.AUCTION_DAY, entertainmentAuction.getNumberOwned());
		}
		
		/*
			inflight
			outflight
			1 = good, 0 = cheap
			e1 day 
			e2 day
			e3 day
		 */
		int[][] prefs = fastOptimizerWrapper.go();

		int index = 0;
		
		for(int[] preference:prefs) {
			
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, DayEnum.getDay(preference[0]));
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT,  DayEnum.getDay(preference[1]));
			
			TacTypeEnum hotelType = null;
			
			if(preference[2] == 1)
				hotelType = TacTypeEnum.GOOD_HOTEL;
			else
				hotelType = TacTypeEnum.CHEAP_HOTEL;
				
			List<HotelAuction> hotelList = new ArrayList<HotelAuction>();
			
			for (int d = preference[0]; d < preference[1]; d++) {
				int auction = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, hotelType, DayEnum.getDay(d));
				HotelAuction hotelAuction = auctionMaster.getHotelAuction(auction);
				hotelList.add(hotelAuction);
			}

			ClientPosition cp = new ClientPosition(auctionMaster.clientPreferences.get(index), inflight, outflight, hotelList, null);
			this.clientPositions.add(cp);
			
			index++;
		}

	}

	@Override
	public void createPositions() {
		
		for(ClientPosition cp: this.clientPositions){
			
			if(!cp.isFeasible())
				continue;
			
			FlightAuction inflightAuction = cp.inFlight;
			FlightAuction outflightAuction = cp.outFlight;
	
			if (!auctionPositions.containsKey(inflightAuction)){
				Position flightPosition = new FlightPositionBidMin(inflightAuction, this.auctionMaster);
				auctionPositions.put(inflightAuction, flightPosition);
			}
			auctionPositions.get(inflightAuction).peopleWhoWantMe++;
			
			if (!auctionPositions.containsKey(outflightAuction)){
				Position flightPosition = new FlightPositionBidMin(outflightAuction, this.auctionMaster);
				auctionPositions.put(outflightAuction, flightPosition);
			}
			auctionPositions.get(outflightAuction).peopleWhoWantMe++;
		}
		
		for(Entry<Auction, Position> entry:this.oldStrategy.auctionPositions.entrySet()) {
			if(entry.getKey().AUCTION_CAT == TacCategoryEnum.CAT_HOTEL && entry.getValue().isValid()) {
				auctionPositions.put(entry.getKey(), entry.getValue());
			}
		}
		
	}

	

}
