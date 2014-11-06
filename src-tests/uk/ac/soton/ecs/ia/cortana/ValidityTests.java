package uk.ac.soton.ecs.ia.cortana;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import se.sics.tac.aw.DayEnum;
import se.sics.tac.aw.TacTypeEnum;

public class ValidityTests {

	@Test
	public void auction_closed_have_enough() {
		System.err.println("Won Auction Test");
		Auction auction = new TestAuction(TacTypeEnum.GOOD_HOTEL, DayEnum.MONDAY, 1, 10, 10);
		auction.close();
		
		Position position = new HotelPositionInitial(auction, 15);
		position.isTheoretical = false;
		
		int people = new Random().nextInt(10);
		
		for(int i = 0; i < people; i++) {
			position.peopleWhoWantMe.add(new ClientPosition(null, null, null, null));
		}
		
		auction.setNumberOwned(people);
		
		Assert.assertTrue(position.isValid());
	}
	
	@Test
	public void auction_closed_did_not_win() {
		System.err.println("Lost Auction Test");
		Auction auction = new TestAuction(TacTypeEnum.GOOD_HOTEL, DayEnum.MONDAY, 1, 10, 10);
		auction.close();
		
		Position position = new HotelPositionInitial(auction, 15);
		position.isTheoretical = false;
		
		int people = new Random().nextInt(10);
		
		for(int i = 0; i < people; i++) {
			position.peopleWhoWantMe.add(new ClientPosition(null, null, null, null));
		}
		
		auction.setNumberOwned(people - 1);
		
		Assert.assertFalse(position.isValid());
	}
	
	@Test
	public void auction_bidding_enough() {
		System.err.println("Bidding Enough Test");
		Auction auction = new TestAuction(TacTypeEnum.GOOD_HOTEL, DayEnum.MONDAY, 1, 10, 10);
		
		Position position = new HotelPositionInitial(auction, 15);
		
		// Can't be valid for having enough
		int people = new Random().nextInt(10);
		for(int i = 0; i < people; i++) {
			position.peopleWhoWantMe.add(new ClientPosition(null, null, null, null));
		}
		auction.setNumberProbablyOwned(people - 1);
		
		Assert.assertTrue(position.isValid());
	}
	
	@Test
	public void auction_not_bidding_enough() {
		System.err.println("Not Bidding Enough Test");
		Auction auction = new TestAuction(TacTypeEnum.GOOD_HOTEL, DayEnum.MONDAY, 1, 10, 10);
		
		Position position = new HotelPositionInitial(auction, 5);
		
		// Can't be valid for having enough
		int people = new Random().nextInt(10);
		for(int i = 0; i < people; i++) {
			position.peopleWhoWantMe.add(new ClientPosition(null, null, null, null));
		}
		auction.setNumberProbablyOwned(people - 1);
		
		Assert.assertFalse(position.isValid());
	}
	

	@Test
	public void auction_open_probably_have_enough() {
		System.err.println("Probably Won Auction Test");
		Auction auction = new TestAuction(TacTypeEnum.GOOD_HOTEL, DayEnum.MONDAY, 1, 10, 10);
		
		Position position = new HotelPositionInitial(auction, 15);
		position.isTheoretical = false;
		
		int people = new Random().nextInt(10);
		
		for(int i = 0; i < people; i++) {
			position.peopleWhoWantMe.add(new ClientPosition(null, null, null, null));
		}
		
		auction.setNumberProbablyOwned(people);
		
		Assert.assertTrue(position.isValid());
	}
	
	@Test
	public void auction_open_probably_do_not_have_enough() {
		System.err.println("Probably Lost Auction Test");
		Auction auction = new TestAuction(TacTypeEnum.GOOD_HOTEL, DayEnum.MONDAY, 1, 10, 10);
		
		Position position = new HotelPositionInitial(auction, 15);
		position.isTheoretical = false;
		
		int people = new Random().nextInt(10);
		
		for(int i = 0; i < people; i++) {
			position.peopleWhoWantMe.add(new ClientPosition(null, null, null, null));
		}
		
		auction.setNumberProbablyOwned(people - 1);
		
		Assert.assertFalse(position.isValid());
	}
	
}
