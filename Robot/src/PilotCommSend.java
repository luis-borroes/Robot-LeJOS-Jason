import java.io.*;
import java.net.*;

public class PilotCommSend extends Thread {
	public static final int port = 1234;

	public PilotRobot robot;
	private Socket client;
	
    public PilotCommSend(PilotRobot r, Socket c){
    	this.setDaemon(true);
    	robot = r;
    	client = c;
    }

    public RobotPacket update(RobotPacket p) {
    	
    	if (robot.getPath().size() > 0 || (robot.getRotating() || robot.getMoving()))
    		p.st = Status.MOVING;
    	else
    		p.st = Status.WAITING;
    	
    	if (robot.getCellCounter() > 4)
    		p.st = Status.ODOMETRY;
    	
    	
		p.left = robot.getLColID();
		p.right = robot.getRColID();
		
		return p;
    }
    
    public void run(){
    	
		try {
			ObjectOutputStream oOut = new ObjectOutputStream(client.getOutputStream());
			
			RobotPacket packet = new RobotPacket();
			
			boolean running = true;
			
			while (running) {
				packet = update(packet);
				
    			oOut.reset();
				oOut.writeObject(packet);
    			oOut.flush();
    			
    			try {
    				Thread.sleep(200);
    			} catch (InterruptedException e) {}
			}
			
			//server.close();
		}
		
		catch(Exception e) {
			//System.out.println(e);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
    	
    }

}
