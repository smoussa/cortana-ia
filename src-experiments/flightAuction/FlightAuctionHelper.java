package flightAuction;

public class FlightAuctionHelper {
	public static double probChangeGivenUBAndT(int UB, int time, double change, double priceBeforeChange){
		double finalPrice = priceBeforeChange+change;
		if(finalPrice>800 || finalPrice<150){
			return 0.0;
		}
		
		double xt = 10 + (((double) time / 540) * (UB - 10));
		int xti = (int) (xt + 0.5);
		
		if (xt < 0.0) {
			//[xti,10]
			if (change>10 || change<xti) return 0.0;
			
			double mult = 1.0;
			if (finalPrice==800) mult = 10 - change + 1;
			if (finalPrice==150) mult = -xti + change + 1;
			return mult/(-xti+10+1);
		}
		else if (xt > 0.0) {
			//[-10,xti]
			if (change<-10 || change>xti) return 0.0;
			
			double mult = 1.0;
			if (finalPrice==800) mult = xti - change + 1;
			if (finalPrice==150) mult = 10 + change + 1;
			return mult/(xti+10+1);
		}
		else {
			//[-10,10]
			if (change<-10 && change>10) return 0.0;
			
			double mult = 1.0;
			if (finalPrice==800) mult = 10 - change + 1;
			if (finalPrice==150) mult = 10 + change + 1;
			return mult/21;
		}
	}
}
