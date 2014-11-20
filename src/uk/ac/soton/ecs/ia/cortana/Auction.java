package uk.ac.soton.ecs.ia.cortana;

import se.sics.tac.aw.Bid;
import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import se.sics.tac.aw.TacCategoryEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.FlightAuctionChangeStore;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators.FlightPriceEstimatorMonteCarlo;

public abstract class Auction {
	
	public final TacTypeEnum AUCTION_TYPE;
	public final TacCategoryEnum AUCTION_CAT;
	public final DayEnum AUCTION_DAY;
	
	public final int AUCTION_ID;

	public TACAgent agent;
	
	public Auction(TACAgent agent, Quote quote) {
		
		this.agent = agent;
		this.AUCTION_ID = quote.getAuction();
		this.AUCTION_TYPE = TacTypeEnum.getType(TacCategoryEnum.getCategory(TACAgent.getAuctionCategory(AUCTION_ID)), TACAgent.getAuctionType(AUCTION_ID));
		this.AUCTION_DAY = DayEnum.getDay(TACAgent.getAuctionDay(AUCTION_ID));
		this.AUCTION_CAT = TacCategoryEnum.getCategory(TACAgent.getAuctionCategory(AUCTION_ID));
	}

	public double getAskPrice() {
		return this.agent.getQuote(AUCTION_ID).getAskPrice();
	}
	
	public void bid(int quantity, float price) {
		
		if (price < this.agent.getQuote(AUCTION_ID).getBidPrice()){
			System.err.println("Invalid bid price. Must be higher than our current bid.");
			return;
		}
		if (this.agent.getQuote(AUCTION_ID).getBid() != null && quantity < this.agent.getQuote(AUCTION_ID).getBid().getQuantity()){
			System.err.println("Invalid bid quantity. Must be higher than our current bid.");
			return;
		}
		if (price < this.agent.getQuote(AUCTION_ID).getAskPrice()){
			System.err.println("Invalid bid price. The market is selling at a higher price than that.");
			return;
		}
				
		Bid bid = new Bid(AUCTION_ID);
		bid.addBidPoint(quantity, price);
		agent.submitBid(bid);
	}
	
	public boolean isClosed() {
		return this.agent.getQuote(AUCTION_ID).isAuctionClosed();
	}

	public int getNumberOwned(){
		return this.agent.getOwn(this.AUCTION_ID);
	}

	public int getNumberProbablyOwned(){
		return this.agent.getProbablyOwn(this.AUCTION_ID);
	}
	
	public Bid getBid() {
		return this.agent.getBid(this.AUCTION_ID);
	}
	
}