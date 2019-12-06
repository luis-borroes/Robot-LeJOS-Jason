import java.io.IOException;

import lejos.hardware.Sound;
import lejos.robotics.subsumption.Behavior;

public class SenseEnv implements Behavior {
	
	
	public boolean suppressed;
	private PilotInterface me;
	public boolean sensing;
	// Constructor - store a reference to the me
	public SenseEnv(PilotInterface pilot){
    	 me = pilot;
    }

	// When called, this should stop action()
	public void suppress(){
		suppressed = true;
		sensing = false;
	}

	
	public boolean takeControl(){
		// if there are objects to sense around itself
		return (calcKnowlGain("06-", me.map, me.getPosx(), me.getPosy()) > 0 && me.getRobot().getLoc());	
	}
	

	public void rotate() {
		while (me.getRobot().getAssumedAngle() != me.getRobot().getAngle() && !suppressed) {
			double diff = me.getRobot().getAssumedAngle() - me.getRobot().getAngle();
			me.getRobot().getPilot().rotate(diff, true);
			
		    while(me.getRobot().getPilot().isMoving() && !suppressed) {
		    	me.getRobot().update(false, diff);
		        Thread.yield();  // wait till turn is complete or suppressed is called
		    }
		}
	}
		
	public void action() {
		suppressed = false;
		sensing = true;
		
		//System.out.println("sense");
		
		if (sensing) {
			
			Sound.twoBeeps(); 
			
		}
		me.setProbMapCol();
		// Checking North Block
		// Will Check for that block no matter its position if unseen 
		
		Tuple lookedAt = lookingAt("n"); // the cell you are currently checking
		
		if ( (lookedAt.x != -1) && (lookedAt.y != -1) &&  me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1) { //if not wall and unseen, look at it and sense
			
			//me.rotateUntil(me.Direction()); //rotate to the pos that it is
			
			if (me.Direction() == 'n') {
				me.scanBlock(0);
				// look AHEAD "0 and return True if block has been seen there"
				if (me.scanBlock(0)) {											
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'e') {
				// look LEFT and return True if block has been seen.
				if (me.scanBlock(90)) {
					me.map[lookedAt.y][lookedAt.x]= - 1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'w') {
				// look RIGHT and return True if block has been seen.
				if (me.scanBlock(-90)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
			else if (me.Direction() == 's' && me.map[lookedAt.y][lookedAt.x] == 0) {
				me.rotateUntil('n');
				rotate();
				me.getRobot().stopRotating();
				// and look ahead
				
				if (me.scanBlock(0)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
		}
		/////////////////////// check east 2nd
		
		lookedAt = lookingAt("e"); // the cell you are currently checking
		
		if ( (lookedAt.x != -1) && (lookedAt.y != -1) &&  me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1) { //if not wall and unseen, look at it and sense
			
			//me.rotateUntil(me.Direction()); //rotate to the pos that it is
			
			if (me.Direction() == 'n') {
				// look 90 to the right
				if (me.scanBlock(-90)) {											
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'e') {
				// look ahead
				if (me.scanBlock(0)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'w' && me.map[lookedAt.y][lookedAt.x] == 0) {
				me.rotateUntil('e');
				rotate();
				me.getRobot().stopRotating();
				// look ahead
				if (me.scanBlock(0)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
			else if (me.Direction() == 's') {
				
				// and look 90 to the left
				
				if (me.scanBlock(90)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
		}
		
		
		// Checking NE Block
		// Will Check for that block no matter its position if unseen 
		
		lookedAt = lookingAt("ne"); // the cell you are currently checking
		
		if ( (lookedAt.x != -1) && (lookedAt.y != -1) &&  me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1 && ( me.map[lookedAt.y+1][lookedAt.x] >= 1)&&( me.map[lookedAt.y][lookedAt.x-1] >= 1) ) { //if not wall and unseen, look at it and sense
			
			//me.rotateUntil(me.Direction()); //rotate to the pos that it is
			
			if (me.Direction() == 'n') {
				// look AHEAD "right 45 and return True if block has been seen there"
				if (me.scanBlockDiagonal(-45)) {											
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'e') {
				// look  45 left and return True if block has been seen.
				if (me.scanBlockDiagonal(45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'w'&& me.map[lookedAt.y][lookedAt.x] ==0) {
				me.rotateUntil('n');
				rotate();
				me.getRobot().stopRotating();
				//rotate to north and rotate infrared 45
				if (me.scanBlockDiagonal(-45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
			else if (me.Direction() == 's' && me.map[lookedAt.y][lookedAt.x] ==0) {
				//rotate to east and look 45 left
				me.rotateUntil('e');
				rotate();
				me.getRobot().stopRotating();
				
				if (me.scanBlockDiagonal(45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
			
			
		}
		
		
			// Checking W Block
			// Will Check for that block no matter its position if unseen 
			
			
			lookedAt = lookingAt("w"); // the cell you are currently checking
			
			if ( (lookedAt.x != -1) && (lookedAt.y != -1)  &&  me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1) { //if not wall and unseen, look at it and sense
				
				//me.rotateUntil(me.Direction()); //rotate to the pos that it is
				
				if (me.Direction() == 'n') {
					// look 90 to the Left
					if (me.scanBlock(90)) {											
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				} 
				
				else if (me.Direction() == 'e' && me.map[lookedAt.y][lookedAt.x] ==0) {
					me.rotateUntil('w');
					rotate();
					me.getRobot().stopRotating();
					// look ahead
					if (me.scanBlock(0)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				} 
				
				else if (me.Direction() == 'w') {
					// look ahead
					if (me.scanBlock(0)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				}
				else if (me.Direction() == 's') {
					
					// and look 90 to the Right
					
					if (me.scanBlock(-90)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				}
			}
			
			// Checking NW Block
						// Will Check for that block no matter its position if unseen 
						
						
			lookedAt = lookingAt("nw"); // the cell you are currently checking
			
			if ( (lookedAt.x != -1) && (lookedAt.y != -1) &&  me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1 && ( me.map[lookedAt.y+1][lookedAt.x] >= 1)&&( me.map[lookedAt.y][lookedAt.x+1] >= 1) ) { //if not wall and unseen, look at it and sense
				
				//me.rotateUntil(me.Direction()); //rotate to the pos that it is
				
				if (me.Direction() == 'n') {
					// look left 45 and return True if block has been seen there"
					if (me.scanBlockDiagonal(45)) {											
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				} 
				
				else if (me.Direction() == 'e'&& me.map[lookedAt.y][lookedAt.x] ==0) {
					me.rotateUntil('n');
					rotate();
					me.getRobot().stopRotating();
					// look  45 left and return True if block has been seen.
					if (me.scanBlockDiagonal(45)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				} 
				
				else if (me.Direction() == 'w') {
					
					//right 45
					if (me.scanBlockDiagonal(-45)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				}
				else if (me.Direction() == 's'&& me.map[lookedAt.y][lookedAt.x] ==0) {
					
					me.rotateUntil('w');
					rotate();
					me.getRobot().stopRotating();
					//right 45
					
					if (me.scanBlockDiagonal(-45)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				}
			}
			
			
			// Checking South Block
			// Will Check for that block no matter its position if unseen 
			
			lookedAt = lookingAt("s"); // the cell you are currently checking
			
			if ( (lookedAt.x != -1) && (lookedAt.y != -1) &&    me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1) { //if not wall and unseen, look at it and sense
				
				//me.rotateUntil(me.Direction()); //rotate to the pos that it is
				
				if (me.Direction() == 'n'&& me.map[lookedAt.y][lookedAt.x] ==0) {
					me.rotateUntil('s');
					rotate();
					me.getRobot().stopRotating();
					// look ahead
					if (me.scanBlock(0)) {											
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				} 
				
				else if (me.Direction() == 'e') {
					// look right and return True if block has been seen.
					if (me.scanBlock(-90)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				} 
				
				else if (me.Direction() == 'w') {
					// look left and return True if block has been seen.
					if (me.scanBlock(90)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				}
				else if (me.Direction() == 's') {
					
					// and look ahead
					
					if (me.scanBlock(0)) {
						me.map[lookedAt.y][lookedAt.x]= -1;
					}
					else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
						me.map[lookedAt.y][lookedAt.x]= 1;
					}
					
				}
			}
			
			
			// Checking SW Block
		// Will Check for that block no matter its position if unseen 
				
		lookedAt = lookingAt("sw"); // the cell you are currently checking
		
		if ( (lookedAt.x != -1) && (lookedAt.y != -1) &&  me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1 && ( me.map[lookedAt.y-1][lookedAt.x] >= 1)&&( me.map[lookedAt.y][lookedAt.x+1] >= 1) ) { //if not wall and unseen, look at it and sense
			
			//me.rotateUntil(me.Direction()); //rotate to the pos that it is
			
			if (me.Direction() == 'n'&& me.map[lookedAt.y][lookedAt.x] ==0) {
				me.rotateUntil('w');
				rotate();
				me.getRobot().stopRotating();
				if (me.scanBlockDiagonal(45)) {											
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'e'&& me.map[lookedAt.y][lookedAt.x] ==0) {
				me.rotateUntil('s');
				rotate();
				me.getRobot().stopRotating();
				
				if (me.scanBlockDiagonal(-45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'w') {
				
				// 45
				if (me.scanBlockDiagonal(45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
			else if (me.Direction() == 's') {
				
				if (me.scanBlockDiagonal(-45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
		}
		
		
		// Checking SE Block
		// Will Check for that block no matter its position if unseen 
				
		lookedAt = lookingAt("se"); // the cell you are currently checking
		
		if ( (lookedAt.x != -1) && (lookedAt.y != -1) &&  me.map[lookedAt.y][lookedAt.x] < 2 && me.map[lookedAt.y][lookedAt.x] > -1 && ( me.map[lookedAt.y-1][lookedAt.x] >= 1)&&( me.map[lookedAt.y][lookedAt.x-1] >= 1) ) { //if not wall and unseen, look at it and sense
			
			//me.rotateUntil(me.Direction()); //rotate to the pos that it is
			
			if (me.Direction() == 'n'&& me.map[lookedAt.y][lookedAt.x] ==0) {
				me.rotateUntil('e');
				rotate();
				me.getRobot().stopRotating();
				if (me.scanBlockDiagonal(-45)) {											
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'e') {
				
				if (me.scanBlockDiagonal(-45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			} 
			
			else if (me.Direction() == 'w'&& me.map[lookedAt.y][lookedAt.x] ==0) {
				me.rotateUntil('s');
				rotate();
				me.getRobot().stopRotating();
				// 45
				if (me.scanBlockDiagonal(45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
			else if (me.Direction() == 's') {
				
				if (me.scanBlockDiagonal(45)) {
					me.map[lookedAt.y][lookedAt.x]= -1;
				}
				else if (me.map[lookedAt.y][lookedAt.x] == -1 || me.map[lookedAt.y][lookedAt.x] == 0){
					me.map[lookedAt.y][lookedAt.x]= 1;
				}
				
			}
		}
		
		
		//me.rotateUntil(me.Direction());
		
		//me.send = true;
		
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {}	
	
		}

	
	
	public Tuple lookingAt(String direction) {
		
		int rows = me.map.length-1;
		int cols = me.map[0].length-1;
		if ((direction.equals("ne"))&& (me.getPosx() != cols )&& (me.getPosy() != 0 )) {
			return new Tuple(-1, -1);
			//return new Tuple(me.getPosx()+1, me.getPosy()-1);
		}
		
		else if ((direction.equals("se"))&&(me.getPosy() != rows )&&(me.getPosx() != cols )) {
			return new Tuple(-1, -1);
		//	return new Tuple(me.getPosx()+1, me.getPosy()+1);
		}
		else if ((direction.equals("sw")) &&(me.getPosy() != rows )&&(me.getPosx() != 0)) {
			return new Tuple(-1, -1);
			//return new Tuple(me.getPosx()-1, me.getPosy()+1);
		}
		
		else if ((direction.equals("nw"))&&(me.getPosy() != 0 )&&(me.getPosx() != 0))  {
			return new Tuple(-1, -1);
			//return new Tuple(me.getPosx()-1, me.getPosy()-1);
		}
		
		else if ((direction.equals("n"))&&(me.getPosy() != 0 )) {
			
			return new Tuple(me.getPosx(), me.getPosy()-1);
			
		} 
		
		else if ((direction.equals("e"))&&(me.getPosx() != cols )) {
			
			return new Tuple(me.getPosx()+1, me.getPosy());
		}
		
		else if ((direction.equals("w"))&&(me.getPosx() != 0))  {
			return  new Tuple(me.getPosx()-1, me.getPosy());
		}
		
		else if ((direction.equals("s"))&&(me.getPosy() != rows )) {
			return new Tuple(me.getPosx(), me.getPosy()+1);
		}
		
		return new Tuple(-1, -1);
		
		
		
		
		
}
	
	public  double calcKnowlGain(String givenSector ,int arrA[][], int posx, int posy) {
		String arrk [][] = {
        		{"-","-","-","-","-","-","-","-","-","-","-","-"},
        		{"-","-","-","-","-","-","-","-","-","-","-","-"},
        		{"-","-","-","-","-","-","-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"},
        		{"-","-","-","-","-","-", "-","-","-","-","-","-"}
        		};
			
			
			// if surroundings are unseen
			//N
			if(posy!= 0 && arrA[posy-1][posx] == 0) { //above
				 mark(arrk,posy-1,posx, givenSector);
			}
			//LEFT
			if(posx!= 0 && arrA[posy][posx-1] == 0) { 
				mark(arrk,posy,posx-1, givenSector);
			}
			//LEFT N CORNER
			/*
			if((posx!= 0 && posy !=0) && arrA[posy-1][posx-1] == 0) { 
				if((posx!= 0 && arrA[posy][posx-1] > -1)&&(posy!= 0 && arrA[posy-1][posx] > -1)) { 
					//left or above is not a obs
					//I can potentially check the corner
					markD(arrk , posy-1,posx-1, givenSector);
				}	
			}
			*/
			//RIGHT
			if(posx!= arrA[posy].length-1 && arrA[posy][posx+1] == 0) { 
				mark(arrk,posy,posx+1, givenSector);
			}
			//RIGHT N CORNER
			/*
			if((posx!= arrA[posy].length-1  && posy !=0) && arrA[posy-1][posx+1] == 0) { 
				if((arrA[posy][posx+1] > -1)&&(arrA[posy-1][posx] > -1)) { 
			
					markD(arrk, posy-1,posx+1, givenSector);
				}	
			}
			*/
			//S
			if(posy!= arrA.length-1 && arrA[posy+1][posx] == 0) { //below
				mark(arrk,posy+1,posx, givenSector);
			}
			
			//LEFT S CORNER
			/*
			if((posx!= 0 && posy!= arrA.length-1) && arrA[posy+1][posx-1] == 0) { 
				if((arrA[posy][posx-1] > -1)&&( arrA[posy+1][posx] > -1)) { 
					markD(arrk , posy+1,posx-1, givenSector);
				}	
			}
			
			//RIGHT S CORNER
			if((posx!= arrA[posy].length-1  && posy!= arrA.length-1) && arrA[posy+1][posx+1] == 0) { 
				if((arrA[posy][posx+1] > -1)&&(arrA[posy+1][posx] > -1)) { 
					markD(arrk, posy+1,posx+1, givenSector);
				}	
			}
			*/
		
		
		//for(int i=0; i < arrk.length; i++) {
		//	 System.out.println(Arrays.toString(arrk[i]));
	//	}
	   
	    
	    
		// calculate all the knowledge gain.
		return calcK(arrk);
	}
	
	private  void markD(String[][] arrk, int posy, int posx, String givenSector) {
		if (arrk[posy][posx] == "-") {
			if (pathContains(givenSector, posy+""+posx)) {		// if in sector then i
				arrk[posy][posx] = "di";
			}
			else {
				arrk[posy][posx] = "do";
			}
		}
	}


	private  void mark(String[][] arrk, int posy, int posx, String givenSector) {
		if (pathContains(givenSector, posy+""+posx)) {		// if in sector then i
			arrk[posy][posx] = "I";
		}
		else {
			arrk[posy][posx] = "O";
		}
		
	}
	
	private  double calcK(String arrk[][]) {
		double knowledge = 0;
		for(int i=0; i < arrk.length; i++) {
			for(int c=0; c < arrk[0].length; c++) {
				if (arrk[i][c] == "I") {
					knowledge += 1;
				} else if (arrk[i][c] == "O") {
					knowledge += 1;
				} else if (arrk[i][c] == "di") {
					knowledge += 1;
				}else if (arrk[i][c] == "do") {
					knowledge += 1;
				}	 
			}
			 
		}
		//System.out.println(knowledge);
		return knowledge;
	}
	 boolean pathContains(String list, String str) {
			boolean ans = false;
			if (list.contains(str)){
				ans=true;
	        }
			return ans;
		}
	public class Tuple { 
		  public final int  x; 
		  public final int  y; 
		  public Tuple(int x, int y) { 
		    this.x = x; 
		    this.y = y; 
		  } 
		} 

}
