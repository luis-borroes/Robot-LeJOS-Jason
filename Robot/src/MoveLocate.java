import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class MoveLocate implements Behavior {
	
	public boolean suppressed;
	private PilotRobot me;
	private PilotInterface meInt;
	private MovePilot pilot;
	
	private Localisation loc;

    public MoveLocate(PilotInterface robot){
    	 meInt = robot;
    	 me = robot.getRobot();
    	 pilot = me.getPilot();
    }

	// When called, this should stop action()
	public void suppress(){
		suppressed = true;
	}
	
	// When called, determine if this behaviour should start
	public boolean takeControl(){
		return (meInt.currentPath.length() > 0 && me.getLoc());
	}
	
	public void move(double dist) {
	    if (!suppressed)
	    	pilot.travel(dist, true);
		
	    while(pilot.isMoving() && !suppressed) {
	    	me.update(true, 0);
	        Thread.yield();
	    }
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
		
    	loc = meInt.loc;
    	meInt.setProbMapCol();
		
    	if (!suppressed && meInt.currentPath.length()>0) {
		
    		char nextMove = meInt.currentPath.charAt(0);
			me.startMoving();
			meInt.rotateUntil(nextMove);
			
			me.stopRotating();
			
			move(25.2);
			
    		if (meInt.currentPath.length() > 1) {
    			meInt.currentPath = meInt.currentPath.substring(1); //remove first letter in String currnetPath
    		
    		} else {
    			meInt.currentPath = "";
    		}
    		
			me.getMap().setPosition(new Coordinate(meInt.probx, meInt.proby));
			meInt.movePos(nextMove);
			me.incCellCounter();
	
		    me.stopMoving();
		    meInt.setProbMapCol();
	    	loc.searchLocal();
	    	
    	}
	}

}
