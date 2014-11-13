package uk.ac.soton.ecs.ia.cortana;

public class Planner {

	public static Strategy makeStrategy(AuctionMaster auctionMaster, Strategy oldStrategy) {
		
		if(oldStrategy == null)
			return new Strategy(auctionMaster);
		
		return new Strategy(oldStrategy);
	}

}
