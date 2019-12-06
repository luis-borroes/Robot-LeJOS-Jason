import java.io.*;
import java.net.*;

public class PCClientSend extends Thread {
	
	private Socket sock;
	private PCPacket packet;
	private boolean initPacket;
	
    public PCClientSend(Socket s, MapPacket map) {
    	this.setDaemon(true);
    	sock = s;
    	
    	initPacket = true;
    	packet = new PCPacket();
    	packet.cmd = Type.INIT;
    	packet.map = map;
		packet.localise = true;
    }
	
	public Coordinate getTarget() {
		return packet.target;
	}

    public void update(int x, int y, boolean goingToHospital) {
    	if (!initPacket) {
			packet.id++;
	        packet.cmd = Type.MOVE;
			
			packet.target.x = x;
			packet.target.y = y;
			
			if (goingToHospital) {
				packet.ambulance = true;
				
			} else {
				packet.ambulance = false;
			}
			
			System.out.println(x + " " + y);
    	}
    }
	
	public void done() {
		packet.id++;
		packet.cmd = Type.DONE;
		packet.ambulance = false;
	}
    
    public void run() {
    	
		try {
			ObjectOutputStream oOut = new ObjectOutputStream(sock.getOutputStream());
			
			boolean running = true;
			
			while (running) {
				oOut.reset();
				oOut.writeObject(packet);
				oOut.flush();
				
				System.out.println(packet.id + " " + packet.target.x + " " + packet.target.y);
    			
    			initPacket = false;
    			
    			try {
    				Thread.sleep(1500);
    			} catch (InterruptedException e) {}
			}
			
			//server.close();
		}
		
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
    	
    }

}
