package uk.ac.soton.ecs.ia.cortana;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Planner {

	public static Strategy makeStrategy(AuctionMaster auctionMaster, Strategy oldStrategy) {
		if(oldStrategy == null)
			return bestStrategy(auctionMaster);
		
		return bestStrategy(oldStrategy);
	}

	private static Strategy bestStrategy(Strategy oldStrategy) {
		return bestStrategy(oldStrategy.auctionMaster);
	}
	
	private static Strategy bestStrategy(AuctionMaster auctionMaster) {
		
		PriorityQueue<Strategy> strategies = new PriorityQueue<Strategy>(5, new Comparator<Strategy>() {

			@Override
			public int compare(Strategy s1, Strategy s2) {
				
				if(s1.getScore() < s2.getScore())
					return 1;
				else if(s2.getScore() < s1.getScore())
					return -1;
				
				return 0;
			}
			
		});
		
		strategies.add(new StrategyBidMoreBadHotel(auctionMaster));
		strategies.add(new StrategyBidMoreGoodHotel(auctionMaster));
		
		return strategies.poll();
	}

}
