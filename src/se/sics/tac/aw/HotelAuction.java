package se.sics.tac.aw;


public class HotelAuction extends Auction {
	
	public HotelAuction(TacType hotelType, Day day, double price, int auctionId) {
		super(hotelType, day, auctionId, price);
	}
	
	public void addClient(Client client) {
		this.peopleWhoWantMe.add(client);
	}
	
	public double getBidPrice() {
		
		double price = 0.0;
		
		for(Client client:peopleWhoWantMe) {

			double hotelPrice = client.getHotelPrice();
			
			if(hotelPrice < 0) {
				System.out.println("Client " + client.CLIENT_ID + " is too expensive to get a hotel for!!!");
				continue;
			}
			
			price += hotelPrice;
		}
		
		price /= peopleWhoWantMe.size();
		
		return price;
	}
	
	@Override
	public void bidMe(TACAgent agent, double price) {
		System.out.println("Hotel on day: " + AUCTION_DAY + " is being bid on for " + price + " for this many people: " + peopleWhoWantMe.size());
		super.bidMe(agent, price);
	}
	
}
