import java.io.*;
import java.net.*;

import javax.swing.JFrame;

/**
 * Maximum LEGO EV3: Building Robots with Java Brains
 * ISBN-13: 9780986832291
 * Variant Press (C) 2014
 * Chapter 14 - Client-Server Robotics
 * Robot: EV3 Brick
 * Platform: LEGO EV3
 * @author Brian Bagnall
 * @version July 20, 2014
 */
public class PCClient {	

	public static void main(String[] args) throws IOException {
		String ip = "192.168.70.96"; 
		
		if(args.length > 0)
			ip = args[0];
		
		boolean firstConnError = true;
		
		Window window = new Window();

		while (true) {
			
			try { //doesn't run through here, connection error
				Socket sock = new Socket(ip, 1234);
				
				firstConnError = true;
				
				System.out.println("Connected");
				InputStream in = sock.getInputStream();
				
				ObjectInputStream dIn = new ObjectInputStream(in);

				try {
					while (true) {
						MappingGateway map = (MappingGateway) dIn.readObject();
						window.map = map;
						window.repaintLabels();
					}
					
				} catch (Exception e) {
					System.out.println("Error: " + e);
				}
				
				sock.close();
				
			} catch (IOException disc) {
				if (firstConnError) {
					firstConnError = false;
					System.out.println("IO Error: " + disc);
					System.out.println("Retrying...");
				}
			}
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
	}
}