package model;



public class Packet extends Data {

	
	public Packet(String packetType) {
		super();
		
		getType().set(packetType);	
	}

}
