package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;

public class ClientPosition {
	
	public ClientPreference client;
	public FlightAuction inFlight, outFlight;
	public List<HotelAuction> hotels;
	public EntertainmentAuction eAuction;
	
	private Map<DayEnum, TacTypeEnum> eTickets;

	public ClientPosition(
			ClientPreference client,
			FlightAuction inFlight,
			FlightAuction outFlight,
			List<HotelAuction> hotels) {
		
		this.client = client;
		this.inFlight = inFlight;
		this.outFlight = outFlight;
		this.hotels = hotels;
	}
	
	public double getTotalUtility(){


		if(!isFeasible())
			return -99999;
		
		// Under predicts because fun bonus is not considered!
		
		double ut = CortanaHeuristics.CLIENT_UTILITY;
		
		if (hotels.get(0).AUCTION_TYPE == TacTypeEnum.GOOD_HOTEL)
			ut += client.hotelBonus;
		
//		System.out.println("Ut hotel " + client.hotelBonus);
		
//		System.out.println("Inflight pref " + client.inFlight.getDayNumber() + " Inflight actual " + inFlight.AUCTION_DAY.getDayNumber());
		int inFlightDayError = Math.abs(inFlight.AUCTION_DAY.getDayNumber() - client.inFlight.getDayNumber());
		
//		System.out.println("Outflight pref " + client.outFlight.getDayNumber() + " Outflight actual " + outFlight.AUCTION_DAY.getDayNumber());
		int outFlightDayError = Math.abs(outFlight.AUCTION_DAY.getDayNumber() - client.outFlight.getDayNumber());
		
		ut -= 100 * (inFlightDayError + outFlightDayError);
		
//		System.out.println("Ut inflight error -" + (100 * (inFlightDayError + outFlightDayError)));
		
		return ut;
	}
	
	public double getEntertianmentUtility() {
		
		double ut = 0;
		for (TacTypeEnum ticket : eTickets.values()) {
			switch (ticket.getCode()) {
			case 1:
				ut += client.e1Bonus;
				break;
			case 2:
				ut += client.e2Bonus;
				break;
			case 3:
				ut += client.e3Bonus;
				break;
			default:
				break;
			}
		}
		
		return ut;
	}
	
	public boolean isFeasible() {
		
		if (this.outFlight == null || this.inFlight == null)
			return false;
		
		int stayLength = numDaysStaying();
		
		if (stayLength == 0)
			return false;
		
		if (hotels.size() != stayLength)
			return false;
		
		return true;
	}
	
	public void giveEntertainmentTicket(DayEnum day, TacTypeEnum ticket) {
		eTickets.put(day, ticket);
	}
	
	public TacTypeEnum getEntertainmentTicket(DayEnum day) {
		return eTickets.get(day);
	}
	
	public boolean hasEntertainmentTicket(DayEnum day) {
		return eTickets.get(day) != null;
	}
	
	public int numDaysStaying() {
		return outFlight.AUCTION_DAY.getDayNumber() - inFlight.AUCTION_DAY.getDayNumber();
	}
	
	public List<DayEnum> daysStaying() {
		
		List<DayEnum> days = new ArrayList<>();
		for (int i = outFlight.AUCTION_DAY.getDayNumber(); i < numDaysStaying(); i++) {
			days.add(DayEnum.getDay(i));
		}
		
		return days;
	}
	
	public boolean hasAllEntertainmentTickets() {
		return eTickets.size() >= 3;
	}

}
