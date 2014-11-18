package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction;
import java.util.ArrayList;
import java.util.HashMap;


public class FlightAuctionChangeStore {
	protected ArrayList<Double> changes = new ArrayList<Double>();
	protected HashMap<Integer,Double> hmp = new HashMap<Integer,Double>();
	
	public FlightAuctionChangeStore() {		
		for(int i = -10; i<=30; i++){
			hmp.put(i, 1.0);
		}
	}

	public void addChange(double change, int time, double priceBeforeChange){
		this.changes.add(change);
		
		for(int potentialUB = -10; potentialUB<=30; potentialUB++){
			double p = hmp.get(potentialUB);
			p = p * FlightAuctionHelper.probChangeGivenUBAndT(potentialUB,time,change,priceBeforeChange);
			hmp.put(potentialUB, p);
		}
	}
	
	public int changeCount(){
		return this.changes.size();
	}
	
	public ArrayList<Double> getChanges(){
		return this.changes;
	}
	
	public HashMap<Integer,Double> getRawProbabilities(){
		return this.hmp;
	}
	
	public HashMap<Integer,Double> getScaledProbabilities(){
		double total = 0.0;
		for (double value : hmp.values()) {
			total += value;
		}
		double scale = 1.0/total;
		
		HashMap<Integer,Double> scaledHmp = new HashMap<Integer,Double>();
		for (int key : hmp.keySet()) {
			scaledHmp.put(key, hmp.get(key)*scale);
		}
		
		return scaledHmp;	
	}
	
	public double getExpectedUpperBound(){	
		HashMap<Integer,Double> scaledHmp = this.getScaledProbabilities();
		
		//calculate expected UB
		double eUB = 0.0;
		for (int key : scaledHmp.keySet()) {
			eUB += key*scaledHmp.get(key);
		}
		
		return eUB;
	}

	@Override
	public String toString() {
		return "FlightAuctionChangeStore [changes=" + changes + "]";
	}
}
