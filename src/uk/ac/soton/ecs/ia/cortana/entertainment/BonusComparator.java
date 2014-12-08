package uk.ac.soton.ecs.ia.cortana.entertainment;

import java.util.Comparator;

import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.ClientPosition;

public class BonusComparator implements Comparator<ClientPosition> {

	TacTypeEnum ticket;
	
	public BonusComparator(TacTypeEnum ticket) {
		this.ticket = ticket;
	}
	
	@Override
	public int compare(ClientPosition c1, ClientPosition c2) {
		return (c1.getEntertainmentBonus(ticket) >= c2.getEntertainmentBonus(ticket)) ? 1 : -1;
	}

}
