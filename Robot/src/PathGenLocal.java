import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.robotics.subsumption.Behavior;


import java.util.*;

public class PathGenLocal implements Behavior{
	
	PilotInterface me;
	public boolean suppressed;
    public int trDone, tlDone, brDone, blDone = 0; // 0 not attempted 1 current 2 done
    
	public int lastnOProbs = 0;
    public int lastDone = 0;  //1 n or s  2 e or w
	  
    public int hosy, hosx = -1;
    public PathGenLocal(PilotInterface r){
    	
    	me = r;
    }
    
    public void suppress(){
    	suppressed = true;
    }
    
    public boolean takeControl(){
	    
    	// || me.pathCalc.ispathObstructed(me.currentPath, me.map, me.getPosx(), me.getPosy())
		return ( !me.pathing && me.getRobot().getLoc() && ( me.loc.probmappos.size() == 1 || me.loc.probmappos.size() != lastnOProbs|| me.currentPath.length() == 0 ) ); // if there is a obstactle in its current path way or is the current sector is done	
}

    
    // Start driving and then yield (for a non-busy wait). If
    // suppressed, then stop the motors and quit.
    public void action(){
    	suppressed = false;
    	me.loc.searchLocal();
    	Sound.twoBeeps();
    	
		//System.out.println("pathgenlocal");
    	
		
    	lastnOProbs = me.loc.probmappos.size();
    	if (hosy == -1 || hosx  == -1) {
    		for (int y = 0; y < me.realmap.length; y++) {
    			for (int x = 0; x < me.realmap[0].length; x++) {
    				if (me.realmap[y][x] == 8) {
    					hosy = y;
    					hosx = x;
    				}
    			}
    			
    		}
    	}
    	MapMatch mm = me.loc.probmappos.get(0);
    	char direction;
    	if (mm.robotRotation == 0) {
    		direction = 'n';
    	}
    	else if (mm.robotRotation == 1) {
    		direction = 'e';
    	} 
    	else if (mm.robotRotation == 2) {
    		direction = 's';
    	} else {
    		direction = 'w';
    	}
		
    	AStar astar = new AStar(me.realmap, mm.robotx , mm.roboty, hosx, hosy, direction );
		String astarpath = astar.process();

		//me.currentPath = astarpath;
		me.currentPath = rotatePath( astarpath, mm.offset);
		

    	

    // DONE CELEBRATION
		if (me.loc.probmappos.size() == 1) {
			
			Sound.twoBeeps();
			me.getRobot().setLoc(false);

			me.getRobot().getMap().setPosition(new Coordinate(mm.robotx - 1, me.getRobot().getMap().getSize().y - mm.roboty));
			
			switch (direction) {
				case 'n':
					me.getRobot().setHeading(PilotRobot.NORTH);
					break;
					
				case 'e':
					me.getRobot().setHeading(PilotRobot.EAST);
					break;
					
				case 's':
					me.getRobot().setHeading(PilotRobot.SOUTH);
					break;
					
				case 'w':
					me.getRobot().setHeading(PilotRobot.WEST);
					break;
					
				default:
					break;
			}
			
		}

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {}
    
    //	beep wait 1000 
    	
	}
    
    public String rotatePath(String path, int rot) {
    	String genPath = "";
    	if (rot ==0 ) {
    		return path;
    	}
    	char[] charpath = path.toCharArray();
    	for (char p : charpath) {
    		if (p == 'n') {
    			genPath += 'w';
    		}else if (p == 'w') {
    			genPath += 's';
    		}else if (p == 's') {
    			genPath += 'e';
    		}else if (p == 'e') {
    			genPath += 'n';
    		}
    		
    	}
    	if (rot == 1) {
    		return genPath;
    	}else {
    		return rotatePath (genPath, rot -1);
    	}
    	
    }
    
	
    

}

