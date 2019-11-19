import java.io.*;
import java.net.*;
 
public class PCClient {	

	public static void main(String[] args) throws IOException {
		String ip = "192.168.70.161"; 
		
		if(args.length > 0)
			ip = args[0];
		
		boolean firstConnError = true;
		
		//Window window = new Window();

		while (true) {
			
			try { //doesn't run through here, connection error
				Socket sock = new Socket(ip, 1234);
				
				firstConnError = true;
				
				System.out.println("Connected");

				PCClientSend sender = new PCClientSend(sock);
				ObjectInputStream oIn = new ObjectInputStream(sock.getInputStream());
				
				sender.run();
				
				try {
					while (true) {
						PCPacket packet = (PCPacket) oIn.readObject();
						System.out.println(packet.test);
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