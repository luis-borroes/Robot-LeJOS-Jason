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
		
		//System.out.println("movelocate");
		
    	loc = meInt.loc;
    	meInt.setProbMapCol();
		
    	if (!suppressed && meInt.currentPath.length()>0) {
		
    		char nextMove = meInt.currentPath.charAt(0);
			me.startMoving();
			meInt.rotateUntil(nextMove);
			rotate();
			me.stopRotating();
			
			move(25.3);
			
    		if (meInt.currentPath.length() > 1) {
    			meInt.currentPath = meInt.currentPath.substring(1); //remove first letter in String currnetPath
    		
    		} else {
    			meInt.currentPath = "";
    		}
    		
			meInt.movePos(nextMove);

			me.incCellCounter();
		    me.stopMoving();
		    
		    me.startRotating();
		    rotate();
		    me.stopRotating();
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
	
		    meInt.setProbMapCol();
	    	loc.searchLocal();
	    	
			me.getMap().setPosition(new Coordinate(meInt.probx - 1, me.getMap().getSize().y - meInt.proby));
	    	
    	}
	}

}
