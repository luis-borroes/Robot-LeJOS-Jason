import java.io.*;
import java.net.*;

public class PCClientSend extends Thread {
	public static final int port = 1234;
	
	private Socket sock;
	
    public PCClientSend(Socket s){
    	this.setDaemon(true);
    	sock = s;
    }

    // The monitor writes various bits of robot state to the screen, then
    // sleeps.
    public void run(){
    	
    	while (true) {
			try{
				ObjectOutputStream oOut = new ObjectOutputStream(sock.getOutputStream());
				
				PCPacket r = new PCPacket();
				
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
					oOut.writeObject(r);
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
