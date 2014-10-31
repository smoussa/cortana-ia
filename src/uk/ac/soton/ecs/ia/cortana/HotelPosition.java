package uk.ac.soton.ecs.ia.cortana;

public class HotelPosition extends Position {

	public HotelPosition(Auction auction) {
		super(auction);
	}

	@Override
	public float getPrice() {
		
		float price = 0.f;
		
		for(Client client:peopleWhoWantMe) {

			// TODO do that calculation here!!!
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
	
}
