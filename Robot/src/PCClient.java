import java.io.*;
import java.net.*;
 
public class PCClient extends Thread {
	
	MapPacket map;
	
	public PCClient(MapPacket m) {
		map = m;
	}

	public void run() {
		String ip = "192.168.70.161"; 
		
		boolean firstConnError = true;
		PCClientSend sender;
		
		//Window window = new Window();

		while (true) {
			
			try { //doesn't run through here, connection error
				Socket sock = new Socket(ip, 1234);
				
				firstConnError = true;
				
				System.out.println("Connected");

				sender = new PCClientSend(sock, map);
				sender.start();
				
				ObjectInputStream oIn = new ObjectInputStream(sock.getInputStream());
				
				try {
					while (true) {
						RobotPacket packet = (RobotPacket) oIn.readObject();
						System.out.println(packet.st + " " + packet.left + " " + packet.right);
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