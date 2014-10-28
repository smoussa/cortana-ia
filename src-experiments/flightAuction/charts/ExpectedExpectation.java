package flightAuction.charts;
import java.util.HashMap;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.SwingWrapper;

import flightAuction.FlightAuctionChangeStore;
import flightAuction.FlightAuctionSimulator;


public class ExpectedExpectation {

	public static void main(String[] args) {	
		
		Chart chart = new Chart(500, 500);
		
		int TRIES = 100;
		
		for(int potentialUB = -10; potentialUB<=30; potentialUB++){
			HashMap<Integer,Double> expUBSum = new HashMap<Integer,Double>();
			for(int t = 10; t<540; t+=10){
				expUBSum.put(t, 0.0);
			}
			HashMap<Integer,Double> expUBSumSquared = new HashMap<Integer,Double>();
			for(int t = 10; t<540; t+=10){
				expUBSumSquared.put(t, 0.0);
			}
			
			for(int tries = 0; tries<TRIES; tries++){
				FlightAuctionSimulator fas = new FlightAuctionSimulator(potentialUB);
				FlightAuctionChangeStore facs = new FlightAuctionChangeStore();
				
				while(fas.canTick()){
					fas.tick();
					facs.addChange(fas.getPriceChangeFromLastTick(), fas.getTime(), fas.getPriceBeforeLastTick());
					
					double x = facs.getExpectedUpperBound();
					expUBSum.put(fas.getTime(), expUBSum.get(fas.getTime()) + x);
					expUBSumSquared.put(fas.getTime(), expUBSumSquared.get(fas.getTime()) + x*x);
				}
			}
			
			double[] y = new double[53];
			int i = 0;
			
			for(int t = 10; t<540; t+=10){
				y[i]=expUBSum.get(t)/TRIES;
				i++;
			}
			
			double[] std = new double[53];
			int i1 = 0;
			
			for(int t = 10; t<540; t+=10){
				std[i1]=Math.sqrt( (expUBSumSquared.get(t) - ((expUBSum.get(t)*expUBSum.get(t))/TRIES)) / TRIES ) ;
				i1++;
			}
					
			Series series = chart.addSeries(Integer.toString(potentialUB), null, y, std);
			series.setMarker(SeriesMarker.NONE);
		}
		
		new SwingWrapper(chart).displayChart();
		
	}

}
