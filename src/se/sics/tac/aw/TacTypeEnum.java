package se.sics.tac.aw;

public enum TacTypeEnum {

	MYSTERY(-1),
	INFLIGHT(1),
	OUTFLIGHT(0),
	GOOD_HOTEL(1),
	CHEAP_HOTEL(0),
	ALLIGATOR_WRESTLING(1),
	AMUSEMENT(2),
	MUSEUM(3);
	
	private final int CODE;
	
	private TacTypeEnum(int CODE) {
		this.CODE = CODE;
	}
	
	public int getCode() {
		return this.CODE;
	}
	
	public static TacTypeEnum getType(TacCategoryEnum category, int code) {
		switch(category) {
			case CAT_ENTERTAINMENT:
				if(code == 1)
					return TacTypeEnum.ALLIGATOR_WRESTLING;
				else if(code == 2)
					return TacTypeEnum.AMUSEMENT;
				else if(code == 3)
					return TacTypeEnum.MUSEUM;
			break;
			case CAT_FLIGHT:
				if(code == 1)
					return TacTypeEnum.INFLIGHT;
				else if(code == 0)
					return TacTypeEnum.OUTFLIGHT;
			break;
			case CAT_HOTEL:
				if(code == 1)
					return TacTypeEnum.GOOD_HOTEL;
				else if(code == 0)
					return TacTypeEnum.CHEAP_HOTEL;
			break;
		}
		
		return TacTypeEnum.MYSTERY;
	}
}
