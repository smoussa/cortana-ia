package se.sics.tac.aw;

public enum TacCategory {
	
	CAT_FLIGHT(0),
	CAT_HOTEL(1),
	CAT_ENTERTAINMENT(2);
	
	private final int CODE;
	
	private TacCategory(int code) {
		this.CODE = code;
	}
	
	public int getCode() {
		return this.CODE;
	}
	
	public static TacCategory getCategory(int code) {
		for(TacCategory category:TacCategory.values()) {
			if(category.getCode() == code)
				return category;
		}
		return CAT_FLIGHT;
	}

}
