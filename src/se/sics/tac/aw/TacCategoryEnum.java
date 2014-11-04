package se.sics.tac.aw;

public enum TacCategoryEnum {
	
	CAT_FLIGHT(0),
	CAT_HOTEL(1),
	CAT_ENTERTAINMENT(2);
	
	private final int CODE;
	
	private TacCategoryEnum(int code) {
		this.CODE = code;
	}
	
	public int getCode() {
		return this.CODE;
	}
	
	public static TacCategoryEnum getCategory(int code) {
		for(TacCategoryEnum category:TacCategoryEnum.values()) {
			if(category.getCode() == code)
				return category;
		}
		return CAT_FLIGHT;
	}

}
