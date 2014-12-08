package uk.ac.soton.ecs.ia.cortana.strategies;

import java.util.ArrayList;
import java.util.HashMap;
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
import uk.ac.soton.ecs.ia.cortana.Strategy;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;

public class TheStrategy extends Strategy {

	public TheStrategy(AuctionMaster auctionMaster) {
		super(auctionMaster);
	}
	
	public TheStrategy(Strategy OldStrategy) {
		super(OldStrategy);
	}

	@Override
	public void createClientPositions() {
		for (ClientPreference c: auctionMaster.clientPreferences.values()){		
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, c.inFlight);
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT, c.outFlight);
			
			TacTypeEnum hotelType = calculateBestHotel(c.hotelBonus, inflight, outflight);
			
			List<HotelAuction> hotelList = new ArrayList<HotelAuction>();
			List<EntertainmentAuction> eAuctions = new ArrayList<>();
			
			for (int d = c.inFlight.getDayNumber(); d < c.outFlight.getDayNumber(); d++) {
				
				int auction = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, hotelType, DayEnum.getDay(d));
				HotelAuction hotelAuction = auctionMaster.getHotelAuction(auction);
				hotelList.add(hotelAuction);
				
				int auctionIdAW = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, TacTypeEnum.ALLIGATOR_WRESTLING, DayEnum.getDay(d));
				int auctionIdAP = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, TacTypeEnum.AMUSEMENT, DayEnum.getDay(d));
				int auctionIdMU = DummyAgent.getAuctionFor(TacCategoryEnum.CAT_ENTERTAINMENT, TacTypeEnum.MUSEUM, DayEnum.getDay(d));
				EntertainmentAuction auctionAW = auctionMaster.getEntertainmentAuction(auctionIdAW);
				EntertainmentAuction auctionAP = auctionMaster.getEntertainmentAuction(auctionIdAP);
				EntertainmentAuction auctionMU = auctionMaster.getEntertainmentAuction(auctionIdMU);
				eAuctions.add(auctionAW);
				eAuctions.add(auctionAP);
				eAuctions.add(auctionMU);
			}

			double nightPrice;
			
			// TODO use estimator on flights to get a better idea
			
			nightPrice = (CortanaHeuristics.CLIENT_UTILITY - inflight.getAskPrice() - outflight.getAskPrice() - CortanaHeuristics.ATTEMPTED_PROFIT_PER_CLIENT) / hotelList.size();
			
			Map<HotelAuction, Double> hotelMap = new HashMap<HotelAuction, Double>();
			
			for(HotelAuction hotelAuction:hotelList) {
				hotelMap.put(hotelAuction, Math.max(nightPrice, hotelAuction.getMinimumBid()));
			}
			
			ClientPositionVariableHotelPrice cp = new ClientPositionVariableHotelPrice(c, inflight, outflight, hotelMap, eAuctions);
			this.clientPositions.add(cp);
		}
	}
	
	protected TacTypeEnum calculateBestHotel(int hotelBonus, FlightAuction inflight, FlightAuction outflight) {
		
		//TODO do probability stuff
		if(hotelBonus < 70)
			return TacTypeEnum.CHEAP_HOTEL;

		return TacTypeEnum.GOOD_HOTEL;
	}

	@Override
	public void createPositions() {
		
		// [Price, quant of ppl.]
		List<double[]> cheapHotelsSums = new ArrayList<double[]>(); 
		for(int i = 0; i < 5; i++) {
			cheapHotelsSums.add(new double[2]);
		}
		List<double[]> premiumHotelsSums = new ArrayList<double[]>();
		for(int i = 0; i < 5; i++) {
			premiumHotelsSums.add(new double[2]);
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
	
	@Override
	public String toString() {
		return "Preference strategy";
	}

}
