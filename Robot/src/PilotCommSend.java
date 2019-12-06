import java.io.*;
import java.net.*;

public class PilotCommSend extends Thread {
	public static final int port = 1234;

	public PilotRobot robot;
	public PilotInterface meInt;
	private Socket client;
	boolean running;
	
    public PilotCommSend(PilotRobot r, PilotInterface i, Socket c){
    	this.setDaemon(true);
    	robot = r;
    	meInt = i;
    	client = c;
    	running = true;
    }

    public RobotPacket update(RobotPacket p) {
    	if (robot.getPath().size() > 0 || (robot.getRotating() || robot.getMoving())) {
    		p.st = Status.MOVING;
    		
    	} else {
    		p.st = Status.WAITING;
    	}
    	
    	if (robot.getOdometry())
    		p.st = Status.ODOMETRY;
    	
    	if (robot.getLoc()) {
    		p.st = Status.LOCALISING;
    	}
    	
		p.left = robot.getLColID();
		p.right = robot.getRColID();
		
		p.pos = robot.getMap().getCurrentPosition();
		p.heading = robot.getHeading();
		
		p.emap = meInt.map;
		p.direction = meInt.Direction();
		p.epos = new Coordinate(meInt.getPosx(), meInt.getPosy());
		
		return p;
    }
    
    public void interrupt() {
    	running = false;
    }
    
    public void run(){
    	
		try {
			ObjectOutputStream oOut = new ObjectOutputStream(client.getOutputStream());
			
			RobotPacket packet = new RobotPacket();
			
			while (running) {
				packet = update(packet);
				
    			oOut.reset();
				oOut.writeObject(packet);
    			oOut.flush();
    			
    			try {
    				Thread.sleep(200);
    			} catch (InterruptedException e) {}
			}
		}
		
		catch (Exception e) {
			//System.out.println(e);
		
		} finally {
			
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
    	
    }

}
