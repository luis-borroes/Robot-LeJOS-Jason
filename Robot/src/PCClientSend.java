import java.io.*;
import java.net.*;

public class PCClientSend extends Thread {
	private Socket sock;
	
    public PCClientSend(Socket s){
    	this.setDaemon(true);
    	sock = s;
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
			
			PCPacket packet = new PCPacket();
			
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
			System.out.println(e);
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
    	
    }

}
