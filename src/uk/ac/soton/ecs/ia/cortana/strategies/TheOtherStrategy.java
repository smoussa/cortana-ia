package uk.ac.soton.ecs.ia.cortana.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.Auction;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;
import uk.ac.soton.ecs.ia.cortana.FlightAuction;
import uk.ac.soton.ecs.ia.cortana.FlightPositionBidMin;
import uk.ac.soton.ecs.ia.cortana.HotelAuction;
import uk.ac.soton.ecs.ia.cortana.Position;
import uk.ac.soton.ecs.ia.cortana.Strategy;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;
import uk.ac.soton.ecs.ia.cortana.strategies.StrategyUtils.StrategyInfo;

public class TheOtherStrategy extends TheStrategy {

	private int currentScore;
	private StrategyInfo scoreGivenUnlimitedFlights;
	
	public TheOtherStrategy(Strategy oldStrategy) {
		super(oldStrategy);
	}
	
	@Override
	public void createClientPositions() {
		
		currentScore = this.auctionMaster.getCurrentScore();
		
		// Run allocator to find positions
		scoreGivenUnlimitedFlights = StrategyUtils.getScoreGivenUnlimitedFlights(auctionMaster);
		
		int index = 0;
		
		for(int[] preference:scoreGivenUnlimitedFlights.getPrefs()) {
			
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, DayEnum.getDay(preference[0]));
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT,  DayEnum.getDay(preference[1]));
			
			TacTypeEnum hotelType = null;
			
			if(preference[2] == 1)
				hotelType = TacTypeEnum.GOOD_HOTEL;
			else
				hotelType = TacTypeEnum.CHEAP_HOTEL;
				
			List<HotelAuction> hotelList = new ArrayList<HotelAuction>();
			List<EntertainmentAuction> eAuctions = new ArrayList<>();
			
			for (int d = preference[0]; d < preference[1]; d++) {
				
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

			ClientPosition cp = new ClientPosition(auctionMaster.clientPreferences.get(index), inflight, outflight, hotelList, eAuctions);
			this.clientPositions.add(cp);
			
			index++;
		}

	}

	@Override
	public void createPositions() {
		
		boolean buyFlights = true;
		
		// Strategy logic follows
		if(currentScore > scoreGivenUnlimitedFlights.getScore() - scoreGivenUnlimitedFlights.getAdditionalCosts())
			buyFlights = false;
		
		// Make flight positions
		if(buyFlights) {
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
		}
		
		// Carry over old hotel positions
		for(Entry<Auction, Position> entry:this.oldStrategy.auctionPositions.entrySet()) {
			if(entry.getKey().AUCTION_CAT == TacCategoryEnum.CAT_HOTEL && entry.getValue().isValid()) {
				auctionPositions.put(entry.getKey(), entry.getValue());
			}
		}
		
	}

	

}
