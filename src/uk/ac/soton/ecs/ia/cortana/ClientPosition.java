package uk.ac.soton.ecs.ia.cortana;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TacTypeEnum;
import uk.ac.soton.ecs.ia.cortana.entertainment.EntertainmentAuction;

public class ClientPosition implements Comparator<Integer> {
	
	public ClientPreference client;
	public FlightAuction inFlight, outFlight;
	public List<HotelAuction> hotels;
	public List<EntertainmentAuction> eAuctions;
	
	private Map<DayEnum, TacTypeEnum> eTickets;
	private Map<DayEnum, TacTypeEnum> bidedTickets;
	private Map<DayEnum, TacTypeEnum> sellingTickets;
	private int numETickets;

	public ClientPosition(
			ClientPreference client,
			FlightAuction inFlight,
			FlightAuction outFlight,
			List<HotelAuction> hotels,
			List<EntertainmentAuction> eAuctions) {
		
		this.client = client;
		this.inFlight = inFlight;
		this.outFlight = outFlight;
		this.hotels = hotels;
		this.eAuctions = eAuctions;
		
		numETickets = 0;
		eTickets = new HashMap<>();
		bidedTickets = new HashMap<>();
		sellingTickets = new HashMap<>();
	}
	
	public double getTotalUtility(){

		if(!isFeasible())
			return -99999;
		
		// This function under-predicts because fun bonus is not considered!
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
	
	public int getEntertainmentBonus(TacTypeEnum ticket) {
		if (ticket == TacTypeEnum.ALLIGATOR_WRESTLING) {
			return client.e1Bonus;
		} else if (ticket == TacTypeEnum.AMUSEMENT) {
			return client.e2Bonus;
		} else if (ticket == TacTypeEnum.MUSEUM) {
			return client.e3Bonus;
		}
		return 0;
	}
	
	public List<EntertainmentAuction> getEntertainmentAuctions(TacTypeEnum ticketType) {
		List<EntertainmentAuction> auctions = new ArrayList<EntertainmentAuction>(4);
		for (EntertainmentAuction auction : eAuctions) {
			if (auction.AUCTION_TYPE == ticketType) {
				auctions.add(auction);
			}
		}
		return auctions;
	}
	
	public int numTicketsHeld() {
		return numETickets;
	}
	
	public void giveEntertainmentTicket(DayEnum day, TacTypeEnum ticket) {
		numETickets++;
		eTickets.put(day, ticket);
	}
	
	public TacTypeEnum getEntertainmentTicket(DayEnum day) {
		return eTickets.get(day);
	}
	
	public boolean biddingOnTicket(DayEnum day) {
		return bidedTickets.get(day) != null;
	}
	
	public boolean biddingOnTicket(TacTypeEnum ticket) {
		return bidedTickets.containsValue(ticket);
	}
	
	public void bidOnTicket(DayEnum day, TacTypeEnum ticket) {
		bidedTickets.put(day, ticket);
	}
	
	public boolean sellingTicket(DayEnum day) {
		return sellingTickets.get(day) != null;
	}
	
	public boolean sellingTicket(TacTypeEnum ticket) {
		return sellingTickets.containsValue(ticket);
	}
	
	public void sellTicket(DayEnum day, TacTypeEnum ticket) {
		sellingTickets.put(day, ticket);
	}
	
	public boolean hasEntertainmentTicket(DayEnum day) {
		return eTickets.get(day) != null;
	}
	
	public boolean hasEntertainmentTicket(TacTypeEnum ticket) {
		return eTickets.containsValue(ticket);
	}

//	public void resetMarkers(DayEnum day, TacTypeEnum ticket) {
//		
//		if (bidedTickets.containsKey(day) && bidedTickets.containsValue(ticket))
//	}
	
	public int numDaysStaying() {
		return outFlight.AUCTION_DAY.getDayNumber() - inFlight.AUCTION_DAY.getDayNumber();
	}
	
	public boolean isStaying(DayEnum day) { 
		// should be based on actual days staying rather than pref?
		return client.inFlight.getDayNumber() <= day.getDayNumber() && day.getDayNumber() < client.outFlight.getDayNumber();
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

	@Override
	public int compare(Integer e1, Integer e2) {
		return (e1 < e2) ? 1 : -1;
	}

}
