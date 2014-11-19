package uk.ac.soton.ecs.ia.cortana.estimators.flightAuction;

import java.util.ArrayList;
import java.util.HashMap;

public class FlightAuctionChangeStoreResetable extends FlightAuctionChangeStore {

	/*
	 * 
	 * 
	 * WHY DID I MAKE THIS?
	 * 
	 * 
	 * 
	 */
	
	private ArrayList<Double> savedChanges;
	private HashMap<Integer,Double> savedHmp;
	
	public FlightAuctionChangeStoreResetable(FlightAuctionChangeStore facs){
		this.changes = new ArrayList<Double>();
		 for(Double d : facs.getChanges()){
			 changes.add(d);
		 }
		 this.hmp = new HashMap<Integer,Double>();
		 for(Integer k : facs.getRawProbabilities().keySet()){
			 hmp.put(k, facs.getRawProbabilities().get(k));
		 }
	}
	
	public void savePoint(){
		 this.savedChanges = new ArrayList<Double>();
		 for(Double d : changes){
			 savedChanges.add(d);
		 }
		 this.savedHmp = new HashMap<Integer,Double>();
		 for(Integer k : hmp.keySet()){
			 savedHmp.put(k, hmp.get(k));
		 }
	}
	
	public void reset(){
		this.changes = savedChanges;
		this.hmp = savedHmp;
	}
	
	
}
