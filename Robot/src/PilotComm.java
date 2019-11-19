import java.util.Arrays;
import java.io.*;
import java.net.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.Button;

// PillotMonitor.java
// 
// Based on the RobotMonitor class, this displays the robot
// state on the LCD screen; however, it works with the PilotRobot
// class that exploits a MovePilot to control the Robot.
//
// Luis Borroes
// 15th October 2019
//

public class PilotComm extends Thread {
	
	public static final int port = 1234;
	
	public PilotRobot robot;
	private MappingGateway map;
	private ServerSocket server;
	
    public PilotComm(PilotRobot r, MappingGateway m){
    	this.setDaemon(true);
    	robot = r;
    	map = m;
    }

    // The monitor writes various bits of robot state to the screen, then
    // sleeps.
    public void run(){
    	
    	while (true) {
			try{
				server = new ServerSocket(port);
				Socket client = server.accept();
				OutputStream out = client.getOutputStream();
				//DataOutputStream dOut = new DataOutputStream(out);
				ObjectOutputStream oOut = new ObjectOutputStream(out);
				
				boolean running = true;
				
				while (running) {
	    			//dOut.writeUTF("Angle: " + robot.getAngle() + " Assumed: " + robot.getAssumedAngle() + " Dist: " + robot.getDistance() + " Mov: " + robot.getMoving() + " T: " + robot.getTravelling());
	    			//dOut.writeUTF("Dist: " + robot.getDistance());
	    			//dOut.writeUTF("LCol: " + robot.getLBlack() + " RCol: " + robot.getRBlack() + " Dist: " + robot.getDistance());
	    			//dOut.writeUTF("Pose: " + robot.getPose());
	    			//dOut.writeUTF("x: " + map.getX() + " y: " + map.getY() + " h: " + robot.getHeading() + " Dist: " + robot.getDistance());
	    			//dOut.writeUTF("x: " + map.getX() + " y: " + map.getY() + " Dist: " + robot.getDistance());
					//dOut.writeUTF("x: " + map.getX() + " y: " + map.getY() + " line: " + robot.getOverLine());
	    			//dOut.writeUTF("LCol: " + robot.getLColID().name() + " RCol: " + robot.getRColID().name() + " Dist: " + robot.getDistance());
					//dOut.writeUTF("Angle: " + robot.getAngle() + " Assumed: " + robot.getAssumedAngle() + " H: " + robot.getHeading() + " Dist: " + robot.getDistance());
	//    			dOut.writeUTF("p: " + robot.getPath().size() + " d: " + robot.getDistance() + " f: " + robot.getDone() + " i: " + robot.getPathCounter());
	    			oOut.reset();
					oOut.writeObject(map);
	    			oOut.flush();
	//    			dOut.flush();
	    			
	    			try {
	    				Thread.sleep(200);
	    			} catch (InterruptedException e) {}
				}
				
				//server.close();
			}
			
			catch(Exception e){
				//System.out.println(e);
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
    	}
    	
    }
    

}
