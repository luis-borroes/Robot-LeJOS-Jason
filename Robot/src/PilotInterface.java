import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;

public class PilotInterface {
	
	public static final float scanBlockThreshold = 19f;
	public static final float scanBlockDiagThreshold = 33f;
	
	private PilotRobot me;
	private MappingGateway mapping;
	
	public Localisation loc;
	
	public int[][] realmapy;
	public int[][] map;
	
	public int[][] realmap ={
	        {-1,-1,-1,-1,-1,-1,-1,-1},
			{-1,2,2,2,2,2,8,-1},
			{-1,2,2,2,2,2,2,-1},
			{-1,2,2,2,2,-1,3,-1},
			{-1,2,3,-1,2,3,-1,-1},
			{-1,2,2,2,2,-1,3,-1},
			{-1,3,2,2,2,2,2,-1},
			{-1,-1,-1,-1,-1,-1,-1,-1}}; 

	public int probx;
	public int proby;
	public int probr;
	public int off;
	
	public int mapX;
	public int mapY;
	
	public String currentPath;
	public boolean pathing;

	
	public PilotInterface(PilotRobot robot) {
		me = robot;
		mapping = me.getMap();
		
		realmapy = genRealMap();
		map = genMap();

		probx = 0;
		proby = 0;
		probr = 0;
		off = 0;
		
		Coordinate size = mapping.getSize();
		mapX = size.x;
		mapY = size.y;

		map[getPosy()][getPosx()] = 1;
		
		currentPath = "";
		pathing = false;
		
		loc = new Localisation(this);
	}
	
	public PilotRobot getRobot() {
		return me;
	}
	
	
	public int getPosx() {
		return mapX;
	}
	
	public int getPosy() {
		return mapY;
	}
	
	
	public char Direction() {
    	int heading = me.getHeading();
    	
		switch (heading) {
			case (PilotRobot.NORTH):
				return 'n';
				
			case (PilotRobot.EAST):
				return 'e';
			
			case (PilotRobot.SOUTH):
				return 's';
			
			case (PilotRobot.WEST):
				return 'w';
			
			default:
				return '?';
	    }
	}
	
	
//	public float North() {
//		int rotAngle = PilotRobot.NORTH - me.getHeading();
//		
//		if (rotAngle > 360)
//			rotAngle -= 360;
//		else if (rotAngle < 0)
//			rotAngle += 360;
//		
//		return (float) rotAngle;
//	}
//	
//	public float East() {
//		int rotAngle = PilotRobot.EAST - me.getHeading();
//		
//		if (rotAngle > 360)
//			rotAngle -= 360;
//		else if (rotAngle < 0)
//			rotAngle += 360;
//		
//		return (float) rotAngle;
//	}
//	
//	public float South() {
//		int rotAngle = PilotRobot.SOUTH - me.getHeading();
//		
//		if (rotAngle > 360)
//			rotAngle -= 360;
//		else if (rotAngle < 0)
//			rotAngle += 360;
//		
//		return (float) rotAngle;
//	}
//	
//	public float West() {
//		int rotAngle = PilotRobot.WEST - me.getHeading();
//		
//		if (rotAngle > 360)
//			rotAngle -= 360;
//		else if (rotAngle < 0)
//			rotAngle += 360;
//		
//		return (float) rotAngle;
//	}
	
	
	public void rotateUntil( char direction ) {
		switch (direction) {
			case 'n':
				me.rotateTowards(PilotRobot.NORTH);
				break;
				
			case 'e':
				me.rotateTowards(PilotRobot.EAST);
				break;
				
			case 's':
				me.rotateTowards(PilotRobot.SOUTH);
				break;
				
			case 'w':
				me.rotateTowards(PilotRobot.WEST);
				break;
				
			default:
				break;
		}
		
		while (me.getAssumedAngle() != me.getAngle()) {
			double diff = me.getAssumedAngle() - me.getAngle();
			me.getPilot().rotate(diff, true);
			
		    while(me.getPilot().isMoving()) {
		    	me.update(false, diff);
		        Thread.yield();  // wait till turn is complete or suppressed is called
		    }
		}
		
	}
	
	public void movePos(char pos) {
		if (pos == 'n') {
			mapY -= 1;
				
		} else if (pos == 's') {
			mapY += 1;
			
		} else if (pos == 'e') {
			mapX += 1;
			
		} else if (pos == 'w') {
			mapX -= 1;
			
		} else {
			Sound.beep();
			Sound.beep();
			Sound.beep();
		}
		
	}
	
	public String sendMap () {
		String smap = getPosy()+""+getPosx();
		if (Direction() =='n'){
			smap += "85";
		}
		else if (Direction() =='e'){
			smap += "86";
		}
		else if (Direction() =='w'){
			smap += "87";
		}
		else if (Direction() =='s'){
			smap += "88";
		}
		smap+="a";
		for (int a =0 ; a < map.length ; a++) {
			for (int b=0; b <map[0].length ; b++) {
				smap += this.map[a][b] + "a";
			}
		}
		
		
		return smap;
		
	}
	
	public float scan(int wantedAngle) {
		Motor.C.rotate(wantedAngle);
		float distance = 0;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}

		distance = me.getDistance();
		
		Motor.C.rotate(-wantedAngle);
		return distance; 
	}

	public boolean scanBlock(int angle) {
		float distance = 0;
	
		distance = scan(angle);
		
		if (distance < scanBlockThreshold) { // there is a block
			return true;
		}
		return false;
		
	}
	
	public boolean scanBlockDiagonal(int angle) {
		float distance = 100;
		distance = scan(angle);
		
		
		
		if (distance < scanBlockDiagThreshold) { // there is a block
			return true;
			
		}
		return false;
		
	}
	
	
	public void setProbMapCol() {
		Color col = me.getLColID();
		
		if (col == Color.WHITE || col == Color.RED || col == Color.GREEN)
			map[mapY][mapX] = 2;
		
		else if (col == Color.YELLOW)
			map[mapY][mapX] = 8;
		
		else if (col == Color.CYAN || col == Color.BURGANDY)
			map[mapY][mapX] = 3;
			
	}
	
	
	public int[][] genMap() {
		Coordinate size = mapping.getSize();
		int[][] m = new int[(size.y * 2)][(size.x * 2)];
		
		for (int y = 0; y < (size.y * 2); y++) {
			
			for (int x = 0; x < (size.x * 2); x++) {
				m[y][x] = 0;
			}
		}
		
		return m;
	}
	
	public int[][] genRealMap() {
		Coordinate size = mapping.getSize();
		
		int[][] rmap = new int[size.y + 2][size.x + 2];
		int value = -1;
		

		for (int y = 0; y < (size.y + 2); y++) {
			
			for (int x = 0; x < (size.x + 2); x++) {
				
				//if surrounding coordinates
				if (x == 0 || y == 0 || x == (size.x + 1) || y == (size.y + 1)) {
					value = -1;
					
				} else {

					int realX = x - 1;
					int realY = y - 1;
					Coordinate current = new Coordinate(realX, realY);

					
					if (mapping.getHospital().equals(current)) {
						value = 8;

					} else {
						GridCell cell = mapping.getGridCell(current);
						
						switch (cell.getStatus()) {
							case OCCUPIED:
								value = -1;
								break;
								
							case UNOCCUPIED:
								value = 2;
								break;
								
							case UNKNOWN:
								value = 2;
								break;
								
							case UNCERTAIN:
								value = -1;
								break;
								
							case VICTIM:
								value = 3;
								break;
								
							case NONCRITICAL:
								value = 3;
								break;
								
							default:
								value = 1;
								break;
						}
					}
				}
				
				rmap[y][(size.x + 1) - x] = value;
				
			}
		}
		
		return rmap;
		
	}
}
