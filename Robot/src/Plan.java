import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class Plan implements Behavior {
	
	public boolean suppressed;
	private PilotRobot me;
	private MovePilot pilot;
	
	private Coordinate[] targets = {
			new Coordinate(0, 0),
			new Coordinate(5, 6),
			new Coordinate(0, 6),
			new Coordinate(5, 0),
			new Coordinate(0, 0)
	};

    public Plan(PilotRobot robot){
    	 me = robot;
    	 pilot = me.getPilot();
    }

	// When called, this should stop action()
	public void suppress(){
		suppressed = true;
	}
	
	// When called, determine if this behaviour should start
	public boolean takeControl(){
		return (me.getPath().size() == 0 && !me.getDone());
	}

	public void action() {
		// Allow this method to run
		suppressed = false;
		
		if (me.getPathCounter() < targets.length) {
			while (!me.setPath(targets[me.getPathCounter()])) {
				me.nextPath();
			
				if (me.getPathCounter() >= targets.length)
					break;
				
			}
		}
		
		if (me.getPathCounter() >= targets.length) {
			me.setPathToUnvisited();
		}
		
	    while(!suppressed) {
	        Thread.yield();
	    }
	}

}
