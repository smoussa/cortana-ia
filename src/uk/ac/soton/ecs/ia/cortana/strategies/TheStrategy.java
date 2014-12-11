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
//	Work out what each client should want; at the start of the game we go for exactly what the client wants
	public void createClientPositions() {
		for (ClientPreference c: auctionMaster.clientPreferences.values()){		
			FlightAuction inflight = auctionMaster.getFlightAuction(TacTypeEnum.INFLIGHT, c.inFlight);
			FlightAuction outflight = auctionMaster.getFlightAuction(TacTypeEnum.OUTFLIGHT, c.outFlight);
			
			TacTypeEnum hotelType = calculateBestHotel(c.hotelBonus, inflight, outflight);
			
			List<HotelAuction> hotelList = new ArrayList<HotelAuction>();
			List<EntertainmentAuction> eAuctions = new ArrayList<>();
			
			// For the duration of the client stay, put the relevant auctions into lists
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
			
			double hotelPricePerNight;
			
			int hotelBonus = 0;
			
			// Only include a hotel bonus if we stay in the good hotel
			if(hotelType == TacTypeEnum.GOOD_HOTEL)
				hotelBonus = c.hotelBonus;
			
			double profitMinusFlights = CortanaHeuristics.CLIENT_UTILITY - inflight.getAskPrice() - outflight.getAskPrice() - CortanaHeuristics.ATTEMPTED_PROFIT_PER_CLIENT;
			hotelPricePerNight = (hotelBonus + profitMinusFlights) / hotelList.size();
			
			// Store per day what this client will pay for the hotel
			Map<HotelAuction, Double> hotelMap = new HashMap<HotelAuction, Double>();
			
			for(HotelAuction hotelAuction:hotelList) {
				hotelMap.put(hotelAuction, Math.max(hotelPricePerNight, hotelAuction.getMinimumBid()));
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
//	Given what each client wants, work out what we have to bid in each auction
	public void createPositions() {
		
		// [Price, quantity of people]
		List<double[]> cheapHotelsSums = new ArrayList<double[]>(); 
		for(int i = 0; i < 5; i++) {
			cheapHotelsSums.add(new double[2]);
		}
		List<double[]> goodHotelsSums = new ArrayList<double[]>();
		for(int i = 0; i < 5; i++) {
			goodHotelsSums.add(new double[2]);
		}
		
		for(ClientPosition cpSuper: this.clientPositions){
			
			// If a client position has a gap in hotels then it's not feasible so ignore it
			if(!cpSuper.isFeasible())
				continue;
			
			ClientPositionVariableHotelPrice cp = (ClientPositionVariableHotelPrice)cpSuper;
			FlightAuction inflightAuction = cp.inFlight;
			FlightAuction outflightAuction = cp.outFlight;
	
			// Create positions when they have not already been made
			if (!auctionPositions.containsKey(inflightAuction)){
				Position flightPosition = new FlightPositionBidMin(inflightAuction);
				auctionPositions.put(inflightAuction, flightPosition);
			}
			// Increase the number of people who want that auction to be won
			// (Increases the quantity of our bid)
			auctionPositions.get(inflightAuction).peopleWhoWantMe++;
			
			if (!auctionPositions.containsKey(outflightAuction)){
				Position flightPosition = new FlightPositionBidMin(outflightAuction);
				auctionPositions.put(outflightAuction, flightPosition);
			}
			auctionPositions.get(outflightAuction).peopleWhoWantMe++;
			
			// Work out the prices to pay for each hotel night
			Map<HotelAuction, Double> clientHotelPrices = cp.clientHotelPrices;
			for(Entry<HotelAuction, Double> entry:clientHotelPrices.entrySet()) {
				double[] priceInfo;
				
				if(entry.getKey().AUCTION_TYPE == TacTypeEnum.CHEAP_HOTEL) {
					priceInfo = cheapHotelsSums.get(entry.getKey().AUCTION_DAY.getDayNumber());
				}
				else {
					priceInfo = goodHotelsSums.get(entry.getKey().AUCTION_DAY.getDayNumber());
				}
				
				priceInfo[0] += entry.getValue();
				priceInfo[1]++;
			}
			
		}
		
		// Make hotel price per night the mean price
		for(int i = 1; i < cheapHotelsSums.size(); i++) {
			cheapHotelsSums.get(i)[0] = Math.ceil(cheapHotelsSums.get(i)[0] / cheapHotelsSums.get(i)[1]);
			goodHotelsSums.get(i)[0] = Math.ceil(goodHotelsSums.get(i)[0] / goodHotelsSums.get(i)[1]);
		}
		
		// Create hotel positions
		for(int i = 1; i < 5; i++) {
			HotelAuction hotelAuctionCheap = auctionMaster.getHotelAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, TacTypeEnum.CHEAP_HOTEL, DayEnum.getDay(i)));
			Position hotelPositionCheap = new HotelPositionBidNowPadded(hotelAuctionCheap, cheapHotelsSums.get(i)[0]);
			auctionPositions.put(hotelAuctionCheap, hotelPositionCheap);
			hotelPositionCheap.peopleWhoWantMe = (int)cheapHotelsSums.get(i)[1];

			HotelAuction hotelAuctionGood = auctionMaster.getHotelAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_HOTEL, TacTypeEnum.GOOD_HOTEL, DayEnum.getDay(i)));
			Position hotelPositionGood = new HotelPositionBidNowPadded(hotelAuctionGood, goodHotelsSums.get(i)[0]);
			auctionPositions.put(hotelAuctionGood, hotelPositionGood);
			hotelPositionGood.peopleWhoWantMe = (int)goodHotelsSums.get(i)[1];
		}
	}
	
}
