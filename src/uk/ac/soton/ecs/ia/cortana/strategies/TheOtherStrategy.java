package uk.ac.soton.ecs.ia.cortana.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;
import uk.ac.soton.ecs.ia.cortana.ClientPositionVariableHotelPrice;
import uk.ac.soton.ecs.ia.cortana.ClientPreference;
import uk.ac.soton.ecs.ia.cortana.CortanaHeuristics;
import uk.ac.soton.ecs.ia.cortana.FlightAuction;
import uk.ac.soton.ecs.ia.cortana.FlightPositionBidMin;
import uk.ac.soton.ecs.ia.cortana.HotelAuction;
import uk.ac.soton.ecs.ia.cortana.HotelPositionBidNowPadded;
import uk.ac.soton.ecs.ia.cortana.Position;
import uk.ac.soton.ecs.ia.cortana.allocator.FastOptimizerWrapper;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;

public class TheOtherStrategy extends TheStrategy {

	public TheOtherStrategy(AuctionMaster auctionMaster) {
		super(auctionMaster);
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

			double nightPrice;
			
			// Need to re-think this, take into consideration flights already bought
			nightPrice = (CortanaHeuristics.CLIENT_UTILITY - inflight.getAskPrice() - outflight.getAskPrice() - CortanaHeuristics.ATTEMPTED_PROFIT_PER_CLIENT) / hotelList.size();
			
			Map<HotelAuction, Double> hotelMap = new HashMap<HotelAuction, Double>();
			
			for(HotelAuction hotelAuction:hotelList) {
				hotelMap.put(hotelAuction, Math.max(nightPrice, hotelAuction.getMinimumBid()));
			}
			
			ClientPositionVariableHotelPrice cp = new ClientPositionVariableHotelPrice(auctionMaster.clientPreferences.get(index), inflight, outflight, hotelMap);
			this.clientPositions.add(cp);
			
			index++;
		}
		/*for(ClientPosition position:this.clientPositions) {
			ClientPositionVariableHotelPrice cpvp = (ClientPositionVariableHotelPrice) position;
			Map<HotelAuction, Double> clientHotelPrices = cpvp.clientHotelPrices;
			System.out.println("Printing client position, look for NAN!!!");
			for(Double price:clientHotelPrices.values()) {
				System.out.println(price);
			}
		}

		System.out.println();*/
	}

	@Override
	public void createPositions() {
		
		// [Price, quant of ppl.]
		List<double[]> cheapHotelsSums = new ArrayList<double[]>(); 
		for(int i = 0; i < 5; i++) {
			cheapHotelsSums.add(new double[]{0,0});
		}
		List<double[]> premiumHotelsSums = new ArrayList<double[]>();
		for(int i = 0; i < 5; i++) {
			premiumHotelsSums.add(new double[]{0,0});
		}
		
		for(ClientPosition cpSuper: this.clientPositions){
			
			if(!cpSuper.isFeasible())
				continue;
			
			ClientPositionVariableHotelPrice cp = (ClientPositionVariableHotelPrice)cpSuper;
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
			
			Map<HotelAuction, Double> clientHotelPrices = cp.clientHotelPrices;
			for(Entry<HotelAuction, Double> entry:clientHotelPrices.entrySet()) {
				double[] priceInfo;
				
				if(entry.getKey().AUCTION_TYPE == TacTypeEnum.CHEAP_HOTEL) {
					priceInfo = cheapHotelsSums.get(entry.getKey().AUCTION_DAY.getDayNumber());
				}
				else {
					priceInfo = premiumHotelsSums.get(entry.getKey().AUCTION_DAY.getDayNumber());
				}
				
				priceInfo[0] += entry.getValue();
				priceInfo[1]++;
			}
			
		}
		
		// Make hotels mean price
		for(int i = 1; i < cheapHotelsSums.size(); i++) {
			cheapHotelsSums.get(i)[0] = cheapHotelsSums.get(i)[0] / cheapHotelsSums.get(i)[1];
			premiumHotelsSums.get(i)[0] = premiumHotelsSums.get(i)[0] / premiumHotelsSums.get(i)[1];
		}
		
		for(int i = 1; i < 5; i++) {
			
			HotelAuction hotelAuctionCheap = auctionMaster.getHotelAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, TacTypeEnum.CHEAP_HOTEL, DayEnum.getDay(i)));
			HotelAuction hotelAuctionPremium = auctionMaster.getHotelAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, TacTypeEnum.GOOD_HOTEL, DayEnum.getDay(i)));
			
			Position hotelPositionCheap = new HotelPositionBidNowPadded(hotelAuctionCheap, cheapHotelsSums.get(i)[0]);
			Position hotelPositionPremium = new HotelPositionBidNowPadded(hotelAuctionPremium, premiumHotelsSums.get(i)[0]);
			
			auctionPositions.put(hotelAuctionCheap, hotelPositionCheap);
			auctionPositions.put(hotelAuctionPremium, hotelPositionPremium);
			
			hotelPositionCheap.peopleWhoWantMe = (int)cheapHotelsSums.get(i)[1];
			
			hotelPositionPremium.peopleWhoWantMe = (int)premiumHotelsSums.get(i)[1];
		}
		
	}

	

}
