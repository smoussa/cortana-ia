package uk.ac.soton.ecs.ia.cortana;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.xeiam.xchart.Chart;
import com.xeiam.xchart.Series;
import com.xeiam.xchart.SeriesLineStyle;
import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.SwingWrapper;

import se.sics.tac.aw.Quote;
import se.sics.tac.aw.TACAgent;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.FlightAuctionChangeStore;
import uk.ac.soton.ecs.ia.cortana.estimators.flightAuction.estimators.FlightPriceEstimatorMonteCarlo;


public class FlightAuction extends Auction {

	//used for plotting graph
	float biddedPrice;
	float biddedTime;
	private HashMap<Integer, Double> futureAveragePricesFromEstimator;
	
	//used for prediction
	private FlightAuctionChangeStore facs = new FlightAuctionChangeStore();
	private List<Float> prices = new ArrayList<Float>();
	private Estimator e;
	private float minPriceEstimate;
	//just in case
	private int expectedT = 0;
	
	public FlightAuction(TACAgent agent, Quote quote) {
		super(agent, quote);
		minPriceEstimate = (float) this.getAskPrice();
	}
	
	@Override
	public void bid(int quantity, float price) {
		System.out.println("Flight " + AUCTION_TYPE + " on day: " + AUCTION_DAY + " is being bid on");
		super.bid(quantity, price);
		this.biddedPrice = price;
		this.biddedTime = 10 * prices.size();
		this.futureAveragePricesFromEstimator = ((FlightPriceEstimatorMonteCarlo)e).priceAtTimeMean;
	}
	
	public void tick(int tSecondsNearest10) {
		/*if (expectedT!=tSecondsNearest10){
			System.out.println("An auction got a tick for a crazy time");
			System.out.println("This should not happen. Talk to Sam");
			System.out.println("Expected " + expectedT);
			System.out.println("Got " + tSecondsNearest10);
			System.exit(1);
		}*/
		expectedT = expectedT + 10;
		
		prices.add((float) this.getAskPrice());

		if (tSecondsNearest10 > 0){
			double previousPrice = prices.get(prices.size()-2);
			double priceChange = this.getAskPrice() - previousPrice;
			
			facs.addChange(priceChange, (int) tSecondsNearest10, previousPrice);
			
			System.out.println(facs);	
			
			e = null;
			e = new FlightPriceEstimatorMonteCarlo(facs, tSecondsNearest10, this.getAskPrice());
			minPriceEstimate = e.getFutureMinPrice();
			System.out.println("Estimated min is " + minPriceEstimate);	
		}
	}
	
	public float getExpectedUpperBound() {
		return (float) facs.getExpectedUpperBound();
	}
	
	public float getFutureMinPrice(){
		return minPriceEstimate;
	}
	
	public void plot() {
		
		ArrayList<Integer> x = new ArrayList<Integer>();
		int i = 0;
		for (Float p : this.prices){
			x.add(i);
			i += 10;
		}
		
		Chart chart = new Chart(500, 500);
		
		Series series = chart.addSeries(("Price"), x, prices);
		series.setMarker(SeriesMarker.NONE);
		series.setLineStyle(SeriesLineStyle.SOLID);
		series.setLineColor(Color.BLACK);
		
		x = new ArrayList<Integer>();
		x.add((int) biddedTime);
		List<Float> b = new ArrayList<Float>();
		b.add(biddedPrice);
		Series bought = chart.addSeries(("Bought"), x, b);
		bought.setMarker(SeriesMarker.DIAMOND);
		bought.setLineStyle(SeriesLineStyle.NONE);
		
		if (futureAveragePricesFromEstimator != null) {
			x = new ArrayList<Integer>(futureAveragePricesFromEstimator.keySet());
			ArrayList<Double> y = new ArrayList<Double>();
			Collections.sort(x);
			
			for(int t: x) {
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
