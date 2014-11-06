package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TacTypeEnum;

public class TestAuction extends Auction {

	public TestAuction(TacTypeEnum auctionType, DayEnum auctionDay, int auctionId, double askingPrice, double bidPrice) {
		super(auctionType, auctionDay, auctionId, askingPrice, bidPrice);
	}

}
