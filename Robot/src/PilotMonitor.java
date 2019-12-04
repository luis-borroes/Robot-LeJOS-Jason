import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.Button;
import java.text.DecimalFormat;

public class PilotMonitor extends Thread {
	private int delay;
	private MappingGateway map;
	public PilotRobot robot;
	
	private int mapID = 1;
	
    GraphicsLCD lcd = LocalEV3.get().getGraphicsLCD();
    DecimalFormat df = new DecimalFormat();

    private static final int MAP_X = 5;
    private static final int INFO_X = 90;
	
    // Make the monitor a daemon and set
    // the robot it monitors and the delay
    public PilotMonitor(PilotRobot r, MappingGateway m, int d){
    	this.setDaemon(true);
    	delay = d;
    	map = m;
    	robot = r;
    }
    
    public void drawTextMap() {
    	String current;
    	
    	Coordinate size = map.getSize();
    	
		lcd.drawString("========", MAP_X, 20, 0);
    	
    	for (int y = 0; y < size.y; y++) {
    		current = "=";
    		
    		for (int x = 0; x < size.x; x++) {
    			
    			if (x == map.getX() && y == map.getY()) {
    				
    				switch (robot.getHeading()) {
    					case (PilotRobot.NORTH):
    						current += "^";
    						break;
    						
    					case (PilotRobot.EAST):
    						current += ">";
    						break;
    					
    					case (PilotRobot.SOUTH):
    						current += "v";
    						break;
    					
    					case (PilotRobot.WEST):
    						current += "<";
    						break;
    					
    					default:
    						current += "?";
    						break;
    				}
    				
    			} else {
	    			GridCellStatus cellState = map.getGridCell(x, y).getStatus();
	    			
	    			if (cellState == GridCellStatus.OCCUPIED)
	        			current += "O";
	    			
	    			else if (cellState == GridCellStatus.UNCERTAIN)
	    				current += "~";
	    			
	    			else if (cellState == GridCellStatus.VICTIM)
	    				current += "&";
	    			
	    			else if (cellState == GridCellStatus.UNOCCUPIED)
	    				current += " ";
	        			
	    			else
	    				current += "?";
    			}
    		}
    		
    		current += "=";
    		
    		lcd.drawString(current, MAP_X, 20 + (size.y * 10) - (y * 10), 0);
    	}
    	
		lcd.drawString("========", MAP_X, 30 + (size.y * 10), 0);
    }
    
    public void drawShapeMap() {
    	char current = '?';
    	
    	Coordinate size = map.getSize();
    	
        lcd.setStrokeStyle(GraphicsLCD.SOLID);

		lcd.fillRect(MAP_X, 20, 80, 10);
		lcd.fillRect(MAP_X, 30, 10, size.y * 10);
		lcd.fillRect(MAP_X + 70, 30, 10, size.y * 10);
		lcd.fillRect(MAP_X, 30 + (size.y * 10), 80, 10);
    	
    	for (int y = 0; y < size.y; y++) {
    		
    		for (int x = 0; x < size.x; x++) {
    			
    			if (x == map.getX() && y == map.getY()) {
    				
    				switch (robot.getHeading()) {
    					case (PilotRobot.NORTH):
    						current = '^';
    						break;
    						
    					case (PilotRobot.EAST):
    						current = '>';
    						break;
    					
    					case (PilotRobot.SOUTH):
    						current = 'v';
    						break;
    					
    					case (PilotRobot.WEST):
    						current = '<';
    						break;
    					
    					default:
    						current = '?';
    						break;
    				}
    				
    				lcd.drawChar(current, MAP_X + x * 10 + 12, y * (-10) + 22 + (size.y * 10), 0);
    				
    			} else {
	    			GridCellStatus cellState = map.getGridCell(x, y).getStatus();
	    			
	    			if (cellState == GridCellStatus.OCCUPIED) {
	    		        lcd.setStrokeStyle(GraphicsLCD.SOLID);
	    				lcd.fillRect(MAP_X + x * 10 + 10, y * (-10) + 20 + (size.y * 10), 10, 10);
	    			
	    			} else if (cellState == GridCellStatus.UNCERTAIN) {
	    		        lcd.setStrokeStyle(GraphicsLCD.SOLID);
	    				lcd.drawRect(MAP_X + x * 10 + 11, y * (-10) + 21 + (size.y * 10), 8, 8);
		    			
	    			} else if (cellState == GridCellStatus.VICTIM) {
	    		        lcd.setStrokeStyle(GraphicsLCD.DOTTED);
	    				lcd.drawRect(MAP_X + x * 10 + 13, y * (-10) + 23 + (size.y * 10), 4, 4);
	    			
	    			} else if (cellState == GridCellStatus.UNOCCUPIED) {
	    				//do nothing
	        			
	    			} else {
	    		        lcd.setStrokeStyle(GraphicsLCD.DOTTED);
	    				lcd.drawRect(MAP_X + x * 10 + 13, y * (-10) + 23 + (size.y * 10), 4, 4);
	    			}
    			}
    		}
    	}
    }
    
    public void drawProbabilityMap() {
    	char current = '?';
    	
    	Coordinate size = map.getSize();
    	
        lcd.setStrokeStyle(GraphicsLCD.SOLID);

		lcd.fillRect(MAP_X + 15, 20, 130, 10);
		lcd.fillRect(MAP_X + 15, 30, 10, size.y * 10);
		lcd.fillRect(MAP_X + 135, 30, 10, size.y * 10);
		lcd.fillRect(MAP_X + 15, 30 + (size.y * 10), 130, 10);
    	
    	for (int y = 0; y < size.y; y++) {
    		
    		for (int x = 0; x < size.x; x++) {
    			
    			if (x == map.getX() && y == map.getY()) {
    				
    				switch (robot.getHeading()) {
    					case (PilotRobot.NORTH):
    						current = '^';
    						break;
    						
    					case (PilotRobot.EAST):
    						current = '>';
    						break;
    					
    					case (PilotRobot.SOUTH):
    						current = 'v';
    						break;
    					
    					case (PilotRobot.WEST):
    						current = '<';
    						break;
    					
    					default:
    						current = '?';
    						break;
    				}
    				
    				lcd.drawChar(current, MAP_X + x * 18 + 32, y * (-10) + 22 + (size.y * 10), 0);
    				
    			} else {
    				
	    			double cellProb = map.getGridCell(x, y).getProbability();
	    			
	    			if (cellProb != -1) {
	    		        df.setMaximumFractionDigits(1);
	    				lcd.drawString(df.format(cellProb), MAP_X + x * 18 + 28, y * (-10) + 22 + (size.y * 10), 0);
	    			}
    			}
    		}
    	}
    }
    
    private String getHeadingString() {
    	int heading = robot.getHeading();
    	
		switch (heading) {
			case (PilotRobot.NORTH):
				return "N";
				
			case (PilotRobot.EAST):
				return "E";
			
			case (PilotRobot.SOUTH):
				return "S";
			
			case (PilotRobot.WEST):
				return "W";
			
			default:
				return "?";
		}
    }
    
    private void drawInfo() {
        df.setMaximumFractionDigits(4);
		lcd.drawString("heading: " + getHeadingString(), INFO_X, 20, 0);
		lcd.drawString("angle: " + robot.getAngle(), INFO_X, 30, 0);
		lcd.drawString("dist: " + df.format(robot.getDistance()), INFO_X, 40, 0);
		lcd.drawString("l col: " + robot.getLColID().name(), INFO_X, 50, 0);
		lcd.drawString("r col: " + robot.getRColID().name(), INFO_X, 60, 0);
		lcd.drawString("line: " + robot.getOverLine(), INFO_X, 70, 0);
		lcd.drawString("c count: " + robot.getCellCounter(), INFO_X, 80, 0);
		lcd.drawString("packet: " + robot.getPacketCounter(), INFO_X, 90, 0);
		lcd.drawString("p size: " + robot.getPath().size(), INFO_X, 100, 0);
		lcd.drawString("target: (" + robot.getTarget().x + ", " + robot.getTarget().y + ")", INFO_X, 110, 0);
		lcd.drawString("done: " + robot.getDone(), INFO_X, 120, 0);
    }
    
    public void run(){
    	//try {
    	
    	while (true) {
    		lcd.clear();
    		lcd.setFont(Font.getDefaultFont());
    		lcd.drawString("Robot Monitor", lcd.getWidth()/2, 0, GraphicsLCD.HCENTER);
    		lcd.setFont(Font.getSmallFont());
    		
    		switch (mapID) {
	    		case 0:
		    		drawTextMap();
		    		drawInfo();
		    		break;
		    		
	    		case 1:
	    			drawShapeMap();
		    		drawInfo();
		    		break;
		    		
	    		case 2:
	    			drawProbabilityMap();
		    		break;
    		}
    		

    		if (Button.ESCAPE.isDown()) {
    			System.exit(0);
    		}
    		
    		if (Button.RIGHT.isDown()) {
    			mapID++;
    			
    			if (mapID > 2)
    				mapID = 0;
    			
    		} else if (Button.LEFT.isDown()) {
    			mapID--;
    			
    			if (mapID < 0)
    				mapID = 2;
    		}
    		

    		try{
    			sleep(delay);
    		}
    		catch (Exception e){}
	    }
	    	
    	//} catch (Exception e) {
    		//run();
    		
    	//}
    }
    

}
