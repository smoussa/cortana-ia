package flightAuction.charts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.SwingWrapper;

import flightAuction.FlightAuctionSimulator;

public class ChangeOverTimeXChart {
	public static void main(String[] args) {
		
		Chart chart = new Chart(500, 500);
		
		int TRIES = 1000;
		
		ArrayList minXData = new ArrayList();
		ArrayList minYData = new ArrayList();
		
		for(int potentialUB = -10; potentialUB<=30; potentialUB++){
			HashMap<Integer,Double> expUBSum = new HashMap<Integer,Double>();
			for(int t = 0; t<540; t+=10){
				expUBSum.put(t, 0.0);
			}
			HashMap<Integer,Double> expUBSumSquared = new HashMap<Integer,Double>();
			for(int t = 0; t<540; t+=10){
				expUBSumSquared.put(t, 0.0);
			}
			double sumMin = 0.0;
			int sumMinTime = 0;
			
			for(int tries = 0; tries<TRIES; tries++){
				double min = 800.0;
				int minTime = 0;
		
				FlightAuctionSimulator fas = new FlightAuctionSimulator(potentialUB);
				
				expUBSum.put(0, 0.0);
				expUBSumSquared.put(0, 0.0);

				while(fas.canTick()){
					fas.tick();
					double x1 = fas.getTotalPriceChange();
					if (x1<min) {
						min = x1;
						minTime = fas.getTime();
					}
					expUBSum.put(fas.getTime(), expUBSum.get(fas.getTime()) + x1);
					expUBSumSquared.put(fas.getTime(), expUBSumSquared.get(fas.getTime()) + x1*x1);
				}
				
				sumMin = sumMin + min;
				sumMinTime = sumMinTime + minTime;
			}
			
			double[] x = new double[54];
			double[] y = new double[54];
			int i = 0;
			
			for(int t = 0; t<540; t+=10){
				x[i] = t;
				y[i]=expUBSum.get(t)/TRIES;
				i++;
			}
			
			double[] std = new double[54];
			int i1 = 0;
			
			for(int t = 0; t<540; t+=10){
				std[i1]=Math.sqrt( (expUBSumSquared.get(t) - ((expUBSum.get(t)*expUBSum.get(t))/TRIES)) / TRIES ) ;
				i1++;
			}
					
			Series series = chart.addSeries(Integer.toString(potentialUB), x, y);
			series.setMarker(SeriesMarker.NONE);
			System.out.println(potentialUB);
			System.out.println(sumMin/TRIES);
			System.out.println(sumMinTime/TRIES);
			minXData.add(sumMinTime/TRIES);
			minYData.add(sumMin/TRIES);
			
		}
		
		Series series = chart.addSeries("mins", minXData, minYData);
		series.setLineStyle(SeriesLineStyle.NONE);
		series.setMarker(SeriesMarker.DIAMOND);
		series.setMarkerColor(Color.BLACK);
		
		new SwingWrapper(chart).displayChart();
	}

}
