package uk.ac.soton.ecs.ia.cortana;

public class HotelPositionInitial extends Position {

	public HotelPositionInitial(Auction auction) {
		super(auction);
	}

	@Override
	public float getPrice() {
		
		float price = 0.f;
		
		for(ClientPosition client:peopleWhoWantMe) {

			ClientPositionFixedHotelPrice clientFixed = (ClientPositionFixedHotelPrice) client;
			
			// TODO do that calculation here!!!
			double hotelPrice = clientFixed.pricePerNight;
			
			if(hotelPrice < 0) {
				System.out.println("Client " + client.client.CLIENT_ID + " is too expensive to get a hotel for!!!");
				continue;
			}
			
			price += hotelPrice;
		}
		
		price /= peopleWhoWantMe.size();
		
		return price;
	}
	
}
