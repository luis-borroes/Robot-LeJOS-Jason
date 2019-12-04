import java.io.*;
import java.net.*;

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
	private PilotCommSend sender;
	
	private int packetID;
	
    public PilotComm(PilotRobot r, MappingGateway m){
    	this.setDaemon(true);
    	robot = r;
    	map = m;
    	packetID = -1;
    }

    public void handle(PCPacket p) {
    	if (p.id > packetID) {
	    	if (p.cmd == Type.MOVE) {
	    		robot.setPath(p.target);
	    	}
	    	
	    	if (p.cmd == Type.INIT) {
	    		map.load(p.map);
	    	}
	    	
	    	if (p.cmd == Type.DONE) {
	    		robot.done();
	    	}
	    	
	    	if (p.ambulance) {
	    		robot.startAmbulance();
	    		
	    	} else {
	    		robot.stopAmbulance();
	    	}
	    	
	    	packetID = p.id;
	    	robot.setPacketCounter(p.id);
    	}
    }
    
    public void run(){
    	
    	while (true) {
			try{
				server = new ServerSocket(port);
				Socket client = server.accept();
				
				sender = new PilotCommSend(robot, client);
				sender.start();
				
				ObjectInputStream oIn = new ObjectInputStream(client.getInputStream());
				
				while (true) {
					PCPacket packet = (PCPacket) oIn.readObject();
					handle(packet);
					
	    			try {
	    				Thread.sleep(50);
	    			} catch (InterruptedException e) {}
				}
			}
			
			catch(Exception e){
				//System.out.println(e);
			}
			
			finally {
				try {
					robot.resetRobot(PilotRobot.NORTH);
					server.close();
					sender.interrupt();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
    	}
    	
    }
    

}
