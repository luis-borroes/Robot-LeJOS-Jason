import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class Calibrate implements Behavior {
	
	public boolean suppressed;
	private PilotRobot me;
	private MovePilot pilot;

    public Calibrate(PilotRobot robot){
    	 me = robot;
    	 pilot = me.getPilot();
    }

	// When called, this should stop action()
	public void suppress(){
		suppressed = true;
	}
	
	// When called, determine if this behaviour should start
	public boolean takeControl(){
		return (true);
	}

	public void action() {
		// Allow this method to run
		suppressed = false;

		pilot.rotate(90);
		pilot.rotate(90);
		pilot.rotate(90);
		pilot.rotate(90);
		
		pilot.rotate(90);
		pilot.rotate(90);
		pilot.rotate(90);
		pilot.rotate(90);
		
	    while(!suppressed) {
	        Thread.yield();
	    }
	}

}
