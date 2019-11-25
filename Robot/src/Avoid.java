
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class Avoid implements Behavior {
	
	public boolean suppressed;
	private PilotRobot me;
	private MovePilot pilot;

    public Avoid(PilotRobot robot){
    	 me = robot;
    	 pilot = me.getPilot();
    }

	// When called, this should stop action()
	public void suppress(){
		suppressed = true;
	}
	
	// When called, determine if this behaviour should start
	public boolean takeControl(){
		return (me.getDistance() < .10 && !me.getRotating() && me.getPath().size() > 0);
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
		
		pilot.stop();
		
		//Coordinate cellAhead = me.getMap().getNextCell(me.getHeading(), PilotRobot.FORWARD);
		//me.getMap().updateCell(cellAhead, true);
		
		me.rePath();
		
		if (me.getPath().size() > 0)
			me.rotateTowards(me.getPath().get(0));
		
		rotate();
		me.stopRotating();
	}

}
