package uk.ac.soton.ecs.ia.cortana.strategies;

import java.util.ArrayList;
import java.util.Iterator;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.DummyAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.Auction;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;
import uk.ac.soton.ecs.ia.cortana.FlightAuction;
import uk.ac.soton.ecs.ia.cortana.HotelAuction;
import uk.ac.soton.ecs.ia.cortana.allocator.FastOptimizerWrapper;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;

public class StrategyUtils {
	
	public static class StrategyInfo {

		private float score;
		private float cost;
		private int[][] flightsToBuy;
		private int[][] prefs;
		
		public StrategyInfo(float score, float cost, int[][] flightsToBuy, int[][] prefs) {
			this.score = score;
			this.cost = cost;
			this.flightsToBuy = flightsToBuy;
			this.prefs = prefs;
		}
		
		public float getScore() {
			return score;
		}

		public float getAdditionalCosts() {
			return cost;
		}

		public int[][] getFlightsToBuy() {
			return flightsToBuy;
		}
		
		public int[][] getPrefs() {
			return prefs;
		}
		
	}

	public static StrategyInfo getScoreGivenUnlimitedFlights(AuctionMaster auctionMaster) {
		// Assume we have all flights to see if we need more once the optimiser runs
		FastOptimizerWrapper fastOptimizerWrapper = new FastOptimizerWrapper();
		fastOptimizerWrapper.addClientPreferences(new ArrayList<>(auctionMaster.clientPreferences.values()));
		
		
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
			fastOptimizerWrapper.addOwned(hotelAuction.AUCTION_TYPE, hotelAuction.AUCTION_DAY, hotelAuction.getNumberOwned());
		}
		
		Iterator<EntertainmentAuction> entertainmentAuctionIterator = auctionMaster.getEntertainmentAuctionIterator();
		while(entertainmentAuctionIterator.hasNext()) {
			EntertainmentAuction entertainmentAuction = entertainmentAuctionIterator.next();
			fastOptimizerWrapper.addOwned(entertainmentAuction.AUCTION_TYPE, entertainmentAuction.AUCTION_DAY, entertainmentAuction.getNumberOwned());
		}
		
		/*
		 * prefs[i][0] = c.inFlight.getDayNumber();
			prefs[i][1] = c.outFlight.getDayNumber();
			... etc.
		 */
		
		int[][] prefs = fastOptimizerWrapper.go();
		
		
		// If we could improve our score then work out if the flights are cheap enough to do so
		int latestScore = fastOptimizerWrapper.getLatestScore();
		
//		System.out.println("Score is " + currentScore + " if we had more flights it would be " + latestScore);
//		
//		if(latestScore < currentScore)
//			return;
		
		//[Day][inflight quant., outflight quant.]
		int[][] idealFlightTotals = new int[6][2];
		for(int i = 1; i < idealFlightTotals.length; i++) {
			idealFlightTotals[i][0] = 0;
			idealFlightTotals[i][1] = 0;
		}
		
		for(int[] preference:prefs) {
			idealFlightTotals[preference[0]][0]++;
			idealFlightTotals[preference[1]][1]++;
		}
		
		int[][] flightsToBuy = new int[6][2];

		// Set buy to what we need ...
		for(int i = 1; i < idealFlightTotals.length; i++) {
			flightsToBuy[i][0] = idealFlightTotals[i][0];
			flightsToBuy[i][1] = idealFlightTotals[i][1];
		}
		
		// ... then minus off what we already own
		Iterator<FlightAuction> flightAuctionIterator = auctionMaster.getFlightAuctionIterator();
		while(flightAuctionIterator.hasNext()) {
			FlightAuction flightAuction = flightAuctionIterator.next();
			int typeCode = 0;
			if(flightAuction.AUCTION_TYPE == TacTypeEnum.OUTFLIGHT)
				typeCode = 1;
			
			flightsToBuy[flightAuction.AUCTION_DAY.getDayNumber()][typeCode] -= flightAuction.getNumberOwned();
		}
		
		float totalExtraCost = 0;
		
		for(int i = 1; i < flightsToBuy.length; i++) {
			if(flightsToBuy[i][0] > 0) {
				Auction auction = auctionMaster.getAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_FLIGHT, TacTypeEnum.INFLIGHT, DayEnum.getDay(i)));

				// We do this right at the end of the game so don't wait for a minimum as the price is likely just going up and up
				// + 20 because the flight increment might go up before we can buy
				totalExtraCost += ((float)auction.getAskPrice() + 20) * flightsToBuy[i][0];
			}
			if(flightsToBuy[i][1] > 0) {
				Auction auction = auctionMaster.getAuction(DummyAgent.getAuctionFor(TacCategoryEnum.CAT_FLIGHT, TacTypeEnum.OUTFLIGHT, DayEnum.getDay(i)));
				totalExtraCost += ((float)auction.getAskPrice() + 20) * flightsToBuy[i][1];
			}
		}
		
		return new StrategyInfo(latestScore, totalExtraCost, flightsToBuy, prefs);
	}
	
	
	
}
