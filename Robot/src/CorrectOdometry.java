
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class CorrectOdometry implements Behavior {
	
	public boolean suppressed;
	private PilotRobot me;
	private PilotInterface meInt;
	private MovePilot pilot;
	
	private int rotateCounter;



	public boolean find_wall_left() {
		//me.getMap().isCellBlocked(me.getHeading(), PilotRobot.LEFT
		if (me.getMap().isCellBlocked(me.getHeading(), PilotRobot.LEFT)) {
			return true;
		} else if (me.getMap().isCellBlocked(me.getHeading(), PilotRobot.RIGHT)) {
			return false;
		}
		
		return false;
	}

	public boolean is_wall() {
		if (me.getMap().isCellBlocked(me.getHeading(), PilotRobot.LEFT)) {
			return true;
		} else if (me.getMap().isCellBlocked(me.getHeading(), PilotRobot.RIGHT)) {
			return true;
		} else {
			return false;
		}
	}


	// public void wallCenter() {
	// 	boolean whichWay = find_wall_left();

	// 	me.realRotate(opposite(whichWay));
	// 	rotate();
	//     me.stopRotating();

	// 	move(-18);
	// 	move(2.5);

		/*
		whichWay = find_wall_left();
		me.realRotate(opposite(whichWay));
		rotate();
	    me.stopRotating();

		move(-18);
		move(2.5);

*/

		//reversefor 15 or so
		//movebackforward
		
	

	public int opposite(boolean left) {
		if (left) {
			return PilotRobot.RIGHT;
		} else {
			return PilotRobot.LEFT;
		}
	}


	// 	if ((dirr == 'n') || (dirr == 's')) {
	// 		if(outside_map(x-1 ,y)) {
	// 			return 'w';
	// 		} else if (me.map[y][x-1] <= -1) {
	// 			return 'w';
	// 		}
			
	// 		if(outside_map(x+1 ,y)) {
	// 			return 'e';
	// 		} else if (me.map[y][x+1] <= -1) {
	// 			return 'e';
	// 		}
	// 	} else if ((dirr == 'e') || (dirr == 'w')) {
	// 		if(outside_map(x ,y-1)) {
	// 			return 'n';
	// 		} else if (me.map[y-1][x] <= -1) {
	// 			return 'n';
	// 		}
			
	// 		if(outside_map(x ,y+1)) {
	// 			return 's';
	// 		} else if (me.map[y+1][x] <= -1) {
	// 			return 's';
	// 		}
			

	// 	}
	// 	return 'x';

		
	// }

    public CorrectOdometry(PilotInterface robot){
    	 meInt = robot;
    	 me = meInt.getRobot();
    	 pilot = me.getPilot();
    }

	// When called, this should stop action()
	public void suppress(){
		suppressed = true;
	}
	
	// When called, determine if this behaviour should start
	public boolean takeControl(){
		//remove corner and blocked in
		return (me.getCellCounter() > 3 && !me.getMoving() && !me.getDone());
		//REMOVED && notInCorner() && notWalledIn()
		//AND THIS && !me.getMap().isCellBlocked(me.getHeading(), PilotRobot.FORWARD)
	}

	public boolean notInCorner() {
		Coordinate[] corners = me.getMap().getCorners();
		
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

	public void odometry_step() {
		if (!is_wall()) {
			
			//turn towards path direction if perpendicular, otherwise whichever side isn't blocked
			if (!suppressed) {
				if (me.getPath().size() > 0 && me.getPath().get(0) != me.getHeading()) {
					me.rotateTowards(me.getPath().get(0));
					
				} else {
					if (!me.getMap().isCellBlocked(me.getHeading(), PilotRobot.RIGHT))
						me.realRotate(PilotRobot.RIGHT);
				
					else if (!me.getMap().isCellBlocked(me.getHeading(), PilotRobot.LEFT))
						me.realRotate(PilotRobot.LEFT);
				}
			}
			
			rotate();
			me.stopRotating();

			adjustToLine();
			pilot.stop();

			me.resetGyro();
			me.resetAssumedAngle();

			//go back slightly, turn 90 degrees to the path direction
			move(-8);

		} else {
			boolean is_left = find_wall_left();
			me.realRotate(opposite(is_left));
			rotate();
			me.stopRotating();
	
			move(-14);

			me.resetGyro();
			me.resetAssumedAngle();

			move(3);

		}
	}
	
	public void action() {
		// Allow this method to run
		suppressed = false;
		
		int oldHeading = me.getHeading();
		
		//System.out.println("odometry");
		
		if (me.getLoc()) {
			switch (meInt.probr) {
				case 0:
					me.setHeading(PilotRobot.NORTH);
					break;
					
				case 1:
					me.setHeading(PilotRobot.EAST);
					break;
					
				case 2:
					me.setHeading(PilotRobot.SOUTH);
					break;
					
				default:
					me.setHeading(PilotRobot.WEST);
					break;
					
			}
		}
		
		int middleHeading = me.getHeading();
		
		me.startOdometry();

		pilot.setLinearSpeed(4.5);
		pilot.setLinearAcceleration(100);

		odometry_step();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		odometry_step();


		// if (!suppressed) {
		// 	if (me.getPath().size() > 0)
		// 		me.rotateTowards(me.getPath().get(0));
		// }
		
		// rotate();
		// me.stopRotating();

		
		if (me.getLoc()) {
			me.rotateTowards(middleHeading);
			rotate();
			me.stopRotating();
			
			me.setHeading(oldHeading);
		}


	    if (!suppressed)
 	    	me.resetCellCounter(false);
	    else
	    	me.resetCellCounter(true);
	    
	    pilot.setLinearSpeed(20);
	    pilot.setLinearAcceleration(50);
	}

}
