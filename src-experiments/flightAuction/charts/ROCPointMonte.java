package flightAuction.charts;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.StatUtils;

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
		
		int TRIES = 100;
		
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
				
				while(fas.canTick()){
					fas.tick();
					facs.addChange(fas.getPriceChangeFromLastTick(), fas.getTime(), fas.getPriceBeforeLastTick());
					
					FlightPriceEstimatorMonteCarlo fpemc = new FlightPriceEstimatorMonteCarlo(facs, fas.getTime(), fas.getPrice(), fas.getMinPrice(), fas.getMinPriceTime());
					double[] a = fpemc.getMinPrice();
					
					points.get(fas.getTime()).add(a[0]);
				}
				
				for(Map.Entry<Integer, ArrayList<Double>> e: points.entrySet()){
					for(Double estimate: e.getValue()){
						totalPoints.get(e.getKey()).add(Pair.of(fas.getMinPrice(), estimate));
					}
				}
				
			}
			
		}
		
		//First pointy plot
		Chart chart = new Chart(500, 500);
		//chart.getStyleManager().setChartType(ChartType.Scatter);
		
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
		
		//point difference plot
		Chart chart2 = new Chart(500, 500);
		
		for(Map.Entry<Integer,ArrayList<Pair<Double,Double>>> e: totalPoints.entrySet()){
			double[] x1 = new double[TRIES*41];
			double[] y1 = new double[TRIES*41];
			int i = 0;
			ArrayList<Pair<Double,Double>> listOfPoint = e.getValue();
			for(Pair<Double, Double> p: listOfPoint){
				x1[i] = e.getKey();
				y1[i] = p.getRight() - p.getLeft();
				i++;
			}
			
			Series series1 = chart2.addSeries(Integer.toString(e.getKey()), x1, y1);
			series1.setLineStyle(SeriesLineStyle.NONE);
		}
		
		new SwingWrapper(chart2).displayChart();
		
		//point difference plot average
		Chart chart3 = new Chart(500, 500);
		double[] x1 = new double[54];
		double[] y1 = new double[54];
		double[] std1 = new double[54];
		int k = 0;
		for(Map.Entry<Integer,ArrayList<Pair<Double,Double>>> e: totalPoints.entrySet()){
			ArrayList<Pair<Double,Double>> listOfPoint = e.getValue();
			double[] diff = new double[listOfPoint.size()];
			int i = 0;
			for(Pair<Double, Double> p: listOfPoint){
				diff[i] = p.getRight() - p.getLeft();
				i++;
			}
			x1[k] = e.getKey();
			y1[k] = StatUtils.mean(diff);
			std1[k] = Math.sqrt(StatUtils.variance(diff));
			k++;
		}

		Series series1 = chart3.addSeries("Mean", x1, y1, std1);
		series1.setLineStyle(SeriesLineStyle.NONE);
		
		new SwingWrapper(chart3).displayChart();
		
	}

}
