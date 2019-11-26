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
    }

    public PCPacket update(PCPacket p) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
        String coordinates = "";
      
        try {
			coordinates = reader.readLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        p.cmd = Type.MOVE;
        p.target.x = Character.getNumericValue(coordinates.charAt(0));
        p.target.y = Character.getNumericValue(coordinates.charAt(1));
    	
    	return p;
    }
    
    public void run(){
    	
		try {
			ObjectOutputStream oOut = new ObjectOutputStream(sock.getOutputStream());
			
			boolean running = true;
			
			while (running) {
				if (!initPacket)
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
			System.out.println(e);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
    	
    }

}
