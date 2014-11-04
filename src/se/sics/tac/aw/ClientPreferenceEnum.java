package se.sics.tac.aw;

public enum ClientPreferenceEnum {

	ARRIVAL(0),
	DEPARTURE(1),
	HOTEL_VALUE(2),
	E1(3),
	E2(4),
	E3(5);
	
	private final int CODE;
	
	private ClientPreferenceEnum(int CODE) {
		this.CODE = CODE;
	}
	
	public static int getCode(ClientPreferenceEnum status) {
		return status.CODE;
	}
	
	public static ClientPreferenceEnum getType(int code) {
		for(ClientPreferenceEnum preference:ClientPreferenceEnum.values()) {
			if(preference.CODE == code)
				return preference;
		}
		return ClientPreferenceEnum.ARRIVAL;
	}
	
}
