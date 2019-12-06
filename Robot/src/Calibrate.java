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
	
	public void rotate() {
		while (me.getAssumedAngle() != me.getAngle() && !suppressed) {
			double diff = me.getAssumedAngle() - me.getAngle();
			pilot.rotate(diff, true);
			
		    while(pilot.isMoving() && !suppressed) {
		    	me.update(false, diff);
		        Thread.yield();  // wait till turn is complete or suppressed is called
		    }
		}
	}
	
	public void action() {
		// Allow this method to run
		suppressed = false;

		me.realRotate(90);
		rotate();
		me.stopRotating();
		
		me.realRotate(90);
		rotate();
		me.stopRotating();
		
		me.realRotate(90);
		rotate();
		me.stopRotating();
		
		me.realRotate(-180);
		rotate();
		me.stopRotating();
		
		me.realRotate(-180);
		rotate();
		me.stopRotating();
		
		me.realRotate(-270);
		rotate();
		me.stopRotating();
		
		me.realRotate(360);
		rotate();
		me.stopRotating();
		
		
	    while(!suppressed) {
	        Thread.yield();
	    }
	}

}
