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
	private boolean goingToHospital;
	
	public PCClient(ParamedicEnv e, MapPacket m) {
		map = m;
		sender = null;
		packet = new RobotPacket();
		env = e;
		
		victims = map.victims;
		vCounter = 0;
		oldState = packet.st;
		
		goingToHospital = false;
	}
	
	public void goToNextVictim() {
		Coordinate victim = victims.get(vCounter);
		vCounter++;
		
		goingToHospital = false;
		update(victim.x, victim.y, goingToHospital);
	}
	
	public void goToHospital() {
		System.out.println("Hello");
		goingToHospital = true;
		update(map.hospital.x, map.hospital.y, goingToHospital);
	}
	
	public void reached() {
		if (goingToHospital) {
			env.at_hospital();
		
		} else {
			env.seenVictim(packet.left.name().toLowerCase(), packet.pos.x, packet.pos.y);
		}
	}
	
	public void update(int x, int y, boolean goingToHospital) {
		if (sender != null) {
			sender.update(x, y, goingToHospital);
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