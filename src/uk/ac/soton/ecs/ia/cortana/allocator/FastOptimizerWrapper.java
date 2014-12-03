package uk.ac.soton.ecs.ia.cortana.allocator;

import java.util.List;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.ClientPreference;

public class FastOptimizerWrapper {
	
	FastOptimizer fo;
	
	int prefs[][] = new int[8][6];
	int owns[][] = new int[5][7];
	
	public FastOptimizerWrapper(){
		fo = new FastOptimizer();
	}
	
	public void addClientPreferences(List<ClientPreference> cps){
		int i = 0;
		for (ClientPreference c: cps){
			prefs[i][0] = c.inFlight.getDayNumber();
			prefs[i][1] = c.outFlight.getDayNumber();
			prefs[i][2] = c.hotelBonus;
			prefs[i][3] = c.e1Bonus;
			prefs[i][4] = c.e2Bonus;
			prefs[i][5] = c.e3Bonus;
			i++;
		}
	}
	
	public void addOwned(TacTypeEnum type, DayEnum day, int owned){
		int firstIndex = -1;
		
		switch (type) {
			case ALLIGATOR_WRESTLING:
				firstIndex = 4;
				break;
			case AMUSEMENT:
				firstIndex = 5;
				break;
			case CHEAP_HOTEL:
				firstIndex = 3;
				break;
			case GOOD_HOTEL:
				firstIndex = 2;
				break;
			case INFLIGHT:
				firstIndex = 0;
				break;
			case MUSEUM:
				firstIndex = 6;
				break;
			case OUTFLIGHT:
				firstIndex = 1;
				break;
			default:
				break;
		}
		
		owns[day.getDayNumber()-1][firstIndex] = owned;
	}
	
	public int[][] go(){
		fo.setClientData(prefs, owns);
		fo.solve();
		return fo.getLatestAllocation();
	}
	
	public int getLatestScore() {
		return fo.getLatestScore();
	}

}
