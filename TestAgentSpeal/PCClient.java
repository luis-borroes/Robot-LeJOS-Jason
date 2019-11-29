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
	private int NCvcounter;
	private Status oldState;
	private GoingTo goal;

	//ADDED THIS
	private ArrayList<Coordinate> non_criticals = new ArrayList<Coordinate>();
	
	public PCClient(ParamedicEnv e, MapPacket m) {
		map = m;
		sender = null;
		packet = new RobotPacket();
		env = e;
		
		victims = map.victims;
		vCounter = 0;
		NCvcounter = 0;
		oldState = packet.st;
		
		goal = GoingTo.NOWHERE;
	}

	
	//THIS IS ALL CAMERONS CODE REMOVE THIS SHIT IF STUFF STARTS TO BREAK!!!!!
	public void add_non_critical(int x, int y) {
		Coordinate toadd = new Coordinate(x,y);
		non_criticals.add(toadd);
	}

	public void go_to_noncriticals() {
		Coordinate victim = non_criticals.get(NCvcounter);
		NCvcounter++;
		
		goal = GoingTo.NON_CRITICAL;
		
		update(victim.x, victim.y);
	}
	//END OF MY ADDED CODE
	
	public void goToNextVictim() {
		Coordinate victim = victims.get(vCounter);
		vCounter++;
		System.out.println("going to victim java");

		goal = GoingTo.VICTIM;

		update(victim.x, victim.y);
	}
	
	public void goToHospital() {
		System.out.println("Hello");

		goal = GoingTo.HOSPITAL;

		update(map.hospital.x, map.hospital.y);
	}
	
	public void reached() {
		if (toHospital()) {
			env.at_hospital();

		} else if (goal == GoingTo.NON_CRITICAL) {
			env.at_non_critical(packet.pos.x, packet.pos.y);
		
		} else {
			env.seenVictim(packet.left.name().toLowerCase(), packet.pos.x, packet.pos.y);
		}

		goal = GoingTo.NOWHERE;
	}

	public boolean toHospital() {
		return (goal == GoingTo.HOSPITAL);
	}
	
	public void update(int x, int y) {
		if (sender != null) {
			sender.update(x, y, toHospital());
		}
	}
	
	public void done() {
		sender.done();
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

enum GoingTo {
	NOWHERE, VICTIM, HOSPITAL, NON_CRITICAL
}
