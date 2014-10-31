package se.sics.tac.aw;

public enum Day {
	
	MONDAY (1),
	TUESDAY (2),
	WEDNESDAY (3),
	THURSDAY (4),
	FRIDAY (5);
	
	private final int day;
	
	Day (int day) {
		this.day = day;
	}
	
	public int getDayNumber() {
		return this.day;
	}
	
	public static Day getDay(int dayNumber) {
		for(Day day:Day.values()) {
			if(day.getDayNumber() == dayNumber)
				return day;
		}
		return MONDAY;
	}
	
}
