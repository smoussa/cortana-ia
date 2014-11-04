package flightAuction.charts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.SwingWrapper;

import flightAuction.FlightAuctionChangeStore;
import flightAuction.FlightAuctionSimulator;
import flightAuction.estimators.FlightPriceEstimatorMonteCarlo;


public class ROCPointMonte {

	public static void main(String[] args) {	
		
		Chart chart = new Chart(500, 500);
		//chart.getStyleManager().setChartType(ChartType.Scatter);
		int TRIES = 10;
		
		//time to a list of pairs
		HashMap<Integer, ArrayList<Pair<Double,Double>>> totalPoints = new HashMap<Integer, ArrayList<Pair<Double,Double>>>();
		for(int t = 10; t<540; t+=10){
			ArrayList<Pair<Double,Double>> l = new ArrayList<Pair<Double,Double>>();
			totalPoints.put(t, l);
		}
		
		for(int potentialUB = -10; potentialUB<=30; potentialUB++){
			System.out.println("UB: " + potentialUB);
			for(int tries = 0; tries<TRIES; tries++){
				System.out.println("try: " + tries);
				FlightAuctionSimulator fas = new FlightAuctionSimulator(potentialUB);
				FlightAuctionChangeStore facs = new FlightAuctionChangeStore();
				
				//time to a list of estimates
				HashMap<Integer, ArrayList<Double>> points = new HashMap<Integer, ArrayList<Double>>();
				for(int t = 10; t<540; t+=10){
					ArrayList<Double> l = new ArrayList<Double>();
					points.put(t, l);
				}
				
				double minPrice = fas.getPrice();
				int minPriceTime = fas.getTime();
				
				while(fas.canTick()){
					fas.tick();
					facs.addChange(fas.getPriceChangeFromLastTick(), fas.getTime(), fas.getPriceBeforeLastTick());
					
					FlightPriceEstimatorMonteCarlo fpemc = new FlightPriceEstimatorMonteCarlo(facs, fas.getTime(), fas.getPrice());
					double[] a = fpemc.getMinPrice();
					
					points.get(fas.getTime()).add(a[0]);
					
					if (fas.getPrice()<minPrice){
						minPrice = fas.getPrice();
						minPriceTime = fas.getTime();
					}
				}
				
				for(Map.Entry<Integer, ArrayList<Double>> e: points.entrySet()){
					for(Double estimate: e.getValue()){
						totalPoints.get(e.getKey()).add(Pair.of(minPrice, estimate));
					}
				}
				
			}
			
		}
		
		for(Map.Entry<Integer,ArrayList<Pair<Double,Double>>> e: totalPoints.entrySet()){
			double[] x = new double[TRIES*41];
			double[] y = new double[TRIES*41];
			int i = 0;
			ArrayList<Pair<Double,Double>> listOfPoint = e.getValue();
			for(Pair<Double, Double> p: listOfPoint){
				x[i] = p.getLeft();
				y[i] = p.getRight();
				i++;
			}
			
			Series series = chart.addSeries(Integer.toString(e.getKey()), x, y);
			series.setLineStyle(SeriesLineStyle.NONE);
		}
		
		double[] x = new double[(400-150)/10];
		double[] y = new double[(400-150)/10];
		int j = 0;
		for (int i = 150; i<400; i+=10){
			x[j] = i;
			y[j] = i;
			j++;
		}
		Series series = chart.addSeries(("Equal"), x, y);
		series.setMarker(SeriesMarker.NONE);
		series.setLineStyle(SeriesLineStyle.DASH_DASH);
		series.setLineColor(Color.BLACK);
		
		new SwingWrapper(chart).displayChart();
		
	}

}
