package uk.ac.soton.ecs.ia.cortana;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.SwingWrapper;

import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators.FlightPriceEstimatorMonteCarlo;


public class FlightAuction extends Auction {

	ArrayList<Float> ps = new ArrayList<Float>();
	float biddedPrice;
	float biddedTime;
	
	public HashMap<Integer, Double> futureAveragePricesFromEstimator;
	
	public FlightAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
	}
	
	@Override
	public void bid(int quantity, float price) {
		System.out.println("Flight " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		super.bid(quantity, price);
		this.biddedPrice = price;
		this.biddedTime = 10 * ps.size();
	}
	
	public void addP(float p){
		ps.add(p);
	}
	
	public void plot(){
		ArrayList<Integer> x = new ArrayList<Integer>();
		int i=0;
		for (Float p : this.ps){
			x.add(i);
			i+=10;
		}
		
		Chart chart = new Chart(500, 500);
		
		Series series = chart.addSeries(("Price"), x, ps);
		series.setMarker(SeriesMarker.NONE);
		series.setLineStyle(SeriesLineStyle.SOLID);
		series.setLineColor(Color.BLACK);
		
		x = new ArrayList<Integer>();
		x.add((int) biddedTime);
		ArrayList<Float> b = new ArrayList<Float>();
		b.add(biddedPrice);
		Series bought = chart.addSeries(("Bought"), x, b);
		bought.setMarker(SeriesMarker.DIAMOND);
		bought.setLineStyle(SeriesLineStyle.NONE);
		
		x = new ArrayList<Integer>();
		ArrayList<Double> y = new ArrayList<Double>();
		
		
		if(futureAveragePricesFromEstimator!=null){
			for(int t: futureAveragePricesFromEstimator.keySet()) {
				x.add(t);
				y.add(futureAveragePricesFromEstimator.get(t));
			}
			Series future = chart.addSeries(("future"), x, y);
			future.setMarker(SeriesMarker.NONE);
			future.setLineStyle(SeriesLineStyle.SOLID);
			series.setLineColor(Color.red);
		}
		
		new SwingWrapper(chart).displayChart();
	}
	
}
