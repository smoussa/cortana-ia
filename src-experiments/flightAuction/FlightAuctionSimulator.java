package flightAuction;
import helpers.Rand;

import java.util.Random;


public class FlightAuctionSimulator {
	private Random random = Rand.getRand();
	private double gameLength = 540;
	
	private int upperBound;
	private int time = 0;
	private double startPrice = 250.0 + random.nextInt(151);
	private double price = startPrice;
	private double lastChange = 0;
	
	public FlightAuctionSimulator() {
		this.upperBound = -10 + random.nextInt(41);
	}
	
	public FlightAuctionSimulator(int upperBound) {
		this.upperBound = upperBound;
	}
	
	public FlightAuctionSimulator(int upperBound, int time, double price) {
		this.upperBound = upperBound;
		this.time = time;
		this.price = price;
	}
	
	public boolean canTick(){
		return time<gameLength-10;
	}
	
	public void tick(){
		if (!this.canTick()){
			return;
		}
		
		time = time + 10;
		
		double change;
		
		double xt = 10 + (((double) this.getTime() / gameLength) * (upperBound - 10));
		int xti = (int) (xt + 0.5);
		if (xt < 0.0) {
			change = random.nextInt(-xti + 11) + xti;
		} else if (xt > 0.0) {
			change = -10 + random.nextInt(xti + 11);
		} else {
			change = -10 + random.nextInt(11);
		}
		
		// Price constraints
		if (price + change > 800) {
			change = 800 - price;
		} else if (price + change < 150) {
			change = 150 - price;
		}
		
		lastChange = change;
		price += change;
	}
	
	public int getTime(){
		return this.time;
	}

	public double getPriceChangeFromLastTick(){
		return this.lastChange;
	}
	
	public double getTotalPriceChange(){
		return this.getPrice() - this.getStartPrice();
	}
	
	public double getStartPrice(){
		return this.startPrice;
	}
	
	public double getPrice(){
		return this.price;
	}
	
	public double getPriceBeforeLastTick(){
		return this.price - this.lastChange;
	}
	
	public double getUpperBound(){
		return this.upperBound;
	}
	
	
}
