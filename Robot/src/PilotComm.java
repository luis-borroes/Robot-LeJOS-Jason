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
				
				PilotCommSend sender = new PilotCommSend(client);
				ObjectInputStream oIn = new ObjectInputStream(client.getInputStream());
				
				sender.run();
				
				try {
					while (true) {
						PCPacket packet = (PCPacket) oIn.readObject();
						
						System.out.println(packet.test);
						
		    			try {
		    				Thread.sleep(200);
		    			} catch (InterruptedException e) {}
					}
					
				} catch (Exception e) {
					System.out.println("Error: " + e);
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
