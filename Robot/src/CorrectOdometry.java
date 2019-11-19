
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class CorrectOdometry implements Behavior {
	
	public boolean suppressed;
	private PilotRobot me;
	private MovePilot pilot;
	
	private int rotateCounter;
	
	private Coordinate[] corners = {
			new Coordinate(0, 0),
			new Coordinate(5, 0),
			new Coordinate(5, 6),
			new Coordinate(0, 6)
	};

    public CorrectOdometry(PilotRobot robot){
    	 me = robot;
    	 pilot = me.getPilot();
    }

	// When called, this should stop action()
	public void suppress(){
		suppressed = true;
	}
	
	// When called, determine if this behaviour should start
	public boolean takeControl(){
		return (me.getCellCounter() > 4 && !me.getMoving() && notInCorner() && notWalledIn() && !me.getDone() && !me.getMap().isCellBlocked(me.getHeading(), PilotRobot.FORWARD));
	}
	
	public boolean notInCorner() {
		for (int i = 0; i < corners.length; i++) {
			if (me.getMap().getCurrentPosition().getHash() == corners[i].getHash())
				return false;
		}
		
		return true;
	}
	
	public boolean notWalledIn() {
		boolean leftBlocked = me.getMap().isCellBlocked(me.getHeading(), PilotRobot.LEFT);
		boolean rightBlocked = me.getMap().isCellBlocked(me.getHeading(), PilotRobot.RIGHT);
		
		return (!(leftBlocked && rightBlocked));
	}
	
	public void adjustToLine() {
		rotateCounter = 0;
		
	    while(!suppressed && rotateCounter < 9) {
	    	if (me.getLBlack() && me.getRBlack()) {
	    		break;
	    		
	    	} else if (me.getLBlack() && !me.getRBlack()) {
	    		if (pilot.getMovement().getMoveType().name() != "ROTATE") {
	    			pilot.rotate(-3, true);
	    			rotateCounter++;
				
				    while(pilot.isMoving() && !suppressed) {
				        Thread.yield();  // wait till turn is complete or suppressed is called
				    }
	    		}
	    		
	    	} else if (!me.getLBlack() && me.getRBlack()) {
	    		if (pilot.getMovement().getMoveType().name() != "ROTATE")
	    			pilot.rotate(3, true);
    				rotateCounter++;
				
				    while(pilot.isMoving() && !suppressed) {
				        Thread.yield();  // wait till turn is complete or suppressed is called
				    }
	    		
	    	} else {
	    		if (pilot.getMovement().getMoveType().name() != "TRAVEL")
	    			pilot.forward();
	    		rotateCounter = 0;
	    	}

	    	me.update(false, 0);
	        Thread.yield();
	    }
	}
	
	public void move(double dist) {
	    if (!suppressed)
	    	pilot.travel(dist, true);
		
	    while(pilot.isMoving() && !suppressed) {
	    	me.update(false, 0);
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

	    pilot.setLinearSpeed(5);
	    pilot.forward();
	    pilot.setLinearAcceleration(100);
	    
	    //align with front
	    adjustToLine();
	    pilot.stop();

	    me.resetGyro();
	    me.resetAssumedAngle();

	    
	    //go back slightly, turn 90 degrees to the path direction
	    move(-8.5);
	    

	    //turn towards path direction if perpendicular, otherwise whichever side isn't blocked
	    if (!suppressed)
			if (me.getPath().size() > 0 && me.getPath().get(0) != me.getHeading()) {
				me.rotateTowards(me.getPath().get(0));
				
			} else {
		    	if (!me.getMap().isCellBlocked(me.getHeading(), PilotRobot.RIGHT))
		    		me.realRotate(PilotRobot.RIGHT);
		    
		    	else if (!me.getMap().isCellBlocked(me.getHeading(), PilotRobot.LEFT))
		    		me.realRotate(PilotRobot.LEFT);
			}

	    
		rotate();
	    me.stopRotating();
	    
	    
	    //align with side
	    adjustToLine();
	    pilot.stop();
	    
	    me.resetGyro();
	    me.resetAssumedAngle();
	    
	    
	    //go back slightly, turn towards path
	    move(-8);
	    
	    if (!suppressed) {
		    if (me.getPath().size() > 0)
				me.rotateTowards(me.getPath().get(0));
	    }
	    
		rotate();
	    me.stopRotating();

	    if (!suppressed)
 	    	me.resetCellCounter(false);
	    else
	    	me.resetCellCounter(true);
	    
	    pilot.setLinearSpeed(20);
	    pilot.setLinearAcceleration(50);
	}

}
