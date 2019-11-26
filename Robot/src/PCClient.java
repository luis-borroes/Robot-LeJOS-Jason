import java.io.*;
import java.net.*;
 
public class PCClient {	

	public static void main(String[] args) {
		String ip = "192.168.70.161"; 
		
		if(args.length > 0)
			ip = args[0];
		
		boolean firstConnError = true;
		PCClientSend sender;
		
		//Window window = new Window();

		while (true) {
			
			try { //doesn't run through here, connection error
				Socket sock = new Socket(ip, 1234);
				
				firstConnError = true;
				
				System.out.println("Connected");
				
				MapPacket map= new MapPacket();
				map.addObj(1, 3);
				map.addObj(2, 4);
				map.addObj(1, 5);
				map.addObj(3, 3);
				map.addVictim(3, 1);
				map.addVictim(2, 5);
				map.addVictim(5, 5);

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