package uk.ac.soton.ecs.ia.cortana.entertainment;

import se.sics.tac.aw.Quote;
import uk.ac.soton.ecs.ia.cortana.AuctionMaster;

public class DummyEntertainmentStrategy extends EntertainmentStrategy {

	public DummyEntertainmentStrategy(AuctionMaster master) {
		super(master);
	}

	@Override
	public void quoteUpdated(Quote quote) {
		
		int auctionId = quote.getAuction();
		EntertainmentAuction auction = master.getEntertainmentAuction(auctionId);
		
//		for (int i = 0; i < agent.getAuctionNo(); i++) {
//			EntertainmentAuction eauc = master.getEntertainmentAuction(i);
//			if (eauc != null) {
//				eauc.ask(5, 250);
//			}
//		}
		
		int alloc = agent.getAllocation(auctionId) - agent.getOwn(auctionId);
		if (alloc != 0) {
			if (alloc < 0) {
				prices[auctionId] = 100f - (agent.getGameTime() * 120f) / 720000;
				auction.ask(alloc, prices[auctionId]);
			} else {
				prices[auctionId] = 20f + (agent.getGameTime() * 100f) / 720000;
				auction.bid(alloc, prices[auctionId]);
			}
		}
		
	}

}