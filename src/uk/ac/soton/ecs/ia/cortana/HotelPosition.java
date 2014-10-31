package uk.ac.soton.ecs.ia.cortana;

public class HotelPosition extends Position {

	public HotelPosition(int AUCTION_ID) {
		super(AUCTION_ID);
	}

	@Override
	public float getPrice() {
		
		float price = 0.f;
		
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
	
}
