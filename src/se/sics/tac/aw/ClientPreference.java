package se.sics.tac.aw;

public enum ClientPreference {

	ARRIVAL(0),
	DEPARTURE(1),
	HOTEL_VALUE(2),
	E1(3),
	E2(4),
	E3(5);
	
	private final int CODE;
	
	private ClientPreference(int CODE) {
		this.CODE = CODE;
	}
	
	public static int getCode(ClientPreference status) {
		return status.CODE;
	}
	
	public static ClientPreference getType(int code) {
		for(ClientPreference preference:ClientPreference.values()) {
			if(preference.CODE == code)
				return preference;
		}
		return ClientPreference.ARRIVAL;
	}
	
}
