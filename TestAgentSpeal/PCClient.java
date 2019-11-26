import java.io.*;
import java.net.*;
import java.util.ArrayList;
 
public class PCClient extends Thread {
	
	private MapPacket map;
	private RobotPacket packet;
	private PCClientSend sender;
	
	private ParamedicEnv env;
	
	private ArrayList<Coordinate> victims;
	private int vCounter;
	private Status oldState;
	
	public PCClient(ParamedicEnv e, MapPacket m) {
		map = m;
		sender = null;
		packet = new RobotPacket();
		env = e;
		
		victims = map.victims;
		vCounter = 0;
		oldState = packet.st;
	}
	
	public void goToNextVictim() {
		Coordinate victim = victims.get(vCounter);
		vCounter++;
		
		update(victim.x, victim.y);
	}
	
	public void reached() {
		env.seenVictim(packet.left.name().toLowerCase(), packet.pos.x, packet.pos.y);
	}
	
	public void update(int x, int y) {
		if (sender != null) {
			sender.update(x, y);
		}
	}
	
	public RobotPacket getPacket() {
		return packet;
	}

	public void run() {
		String ip = "192.168.70.161"; 
		
		boolean firstConnError = true;
		
		//Window window = new Window();

		while (true) {
			
			try { //doesn't run through here, connection error
				Socket sock = new Socket(ip, 1234);
				
				firstConnError = true;
				
				System.out.println("Connected");

				sender = new PCClientSend(sock, map);
				sender.start();
				
				ObjectInputStream oIn = new ObjectInputStream(sock.getInputStream());
				
				env.connected();
				
				try {
					while (true) {
						packet = (RobotPacket) oIn.readObject();
						System.out.println(packet.st + " " + packet.left + " " + packet.right + " " + packet.pos.x + " " + packet.pos.y);
						
						if (oldState != packet.st && packet.st == Status.WAITING)
							reached();
						
						oldState = packet.st;
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