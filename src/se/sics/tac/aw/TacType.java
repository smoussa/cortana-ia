package se.sics.tac.aw;

public enum TacType {

	MYSTERY(-1),
	INFLIGHT(1),
	OUTFLIGHT(0),
	GOOD_HOTEL(1),
	CHEAP_HOTEL(0),
	ALLIGATOR_WRESTLING(1),
	AMUSEMENT(2),
	MUSEUM(3);
	
	private final int CODE;
	
	private TacType(int CODE) {
		this.CODE = CODE;
	}
	
	public int getCode() {
		return this.CODE;
	}
	
	public static TacType getType(TacCategory category, int code) {
		switch(category) {
			case CAT_ENTERTAINMENT:
				if(code == 1)
					return TacType.ALLIGATOR_WRESTLING;
				else if(code == 2)
					return TacType.AMUSEMENT;
				else if(code == 3)
					return TacType.MUSEUM;
			break;
			case CAT_FLIGHT:
				if(code == 1)
					return TacType.INFLIGHT;
				else if(code == 0)
					return TacType.OUTFLIGHT;
			break;
			case CAT_HOTEL:
				if(code == 1)
					return TacType.GOOD_HOTEL;
				else if(code == 0)
					return TacType.CHEAP_HOTEL;
			break;
		}
		
		return TacType.MYSTERY;
	}
}
