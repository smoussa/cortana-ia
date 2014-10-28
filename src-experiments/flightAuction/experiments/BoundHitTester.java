package flightAuction.experiments;

import flightAuction.FlightAuctionSimulator;

public class BoundHitTester {

	public static void main(String[] args) {
		int c800 = 0;
		int c150 = 0;
		for(int tries = 0; tries<1000; tries++){
			FlightAuctionSimulator fas = new FlightAuctionSimulator();
			while(fas.canTick()){
				fas.tick();
				if (fas.getPrice() == 800){
					c800++;
				}
				if (fas.getPrice() == 150){
					c150++;
				}
			}
		}
		System.out.println(c800);
		System.out.println(c150);

	}

}
