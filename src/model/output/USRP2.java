package model.output;


public class USRP2 extends UHD {
	
	
	public USRP2() {
		super("usrp2", 20.0, 31.0);
		
		rate.setValue("5000000");
		master_clock_rate.setValue("32768000");
	}
}
