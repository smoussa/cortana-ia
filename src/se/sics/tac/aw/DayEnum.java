package se.sics.tac.aw;

public enum DayEnum {
	
	MONDAY (1),
	TUESDAY (2),
	WEDNESDAY (3),
	THURSDAY (4),
	FRIDAY (5);
	
	private final int day;
	
	DayEnum (int day) {
		this.day = day;
	}
	
	public int getDayNumber() {
		return this.day;
	}
	
	public static DayEnum getDay(int dayNumber) {
		for(DayEnum day:DayEnum.values()) {
			if(day.getDayNumber() == dayNumber)
				return day;
		}
		return MONDAY;
	}
	
}
