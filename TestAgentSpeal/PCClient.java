import java.io.*;
import java.net.*;
import java.util.ArrayList;
 
public class PCClient extends Thread {
	
	private MappingGateway map;
	private MapPacket mPack;
	private RobotPacket packet;
	private PCClientSend sender;
	private OptRoute optRoute;
	
	private ParamedicEnv env;
	
	private ArrayList<Coordinate> route;
	private int rCounter;
	private Status oldState;
	private GoingTo goal;
	
	public static int[][] mapp = {
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0}};
			
	public static char direction = 'n';
	
	public static Coordinate pos = new Coordinate(6, 6);
			
	public static GUI gui;
	String smap = "";
	
	public PCClient(ParamedicEnv e, MappingGateway m) {
		map = m;
		mPack = m.save();

		sender = null;
		packet = new RobotPacket();
		env = e;
		optRoute = new OptRoute(map);
		
		route = optRoute.optimumRoute(map.getCurrentPosition(), mPack.hospital, map.getVictims());
		rCounter = 0;

		oldState = packet.st;
		
		goal = GoingTo.NOWHERE;
	}

	
	//THIS IS ALL CAMERONS CODE REMOVE THIS SHIT IF STUFF STARTS TO BREAK!!!!!
	public void add_non_critical(int x, int y) {
		Coordinate toadd = new Coordinate(x,y);
		map.addNonCritical(toadd);
	}

	public void go_to_noncriticals() {
		Coordinate victim = map.getNonCriticals().get(rCounter);
		rCounter++;
		
		goal = GoingTo.NON_CRITICAL;
		
		update(victim.x, victim.y);
	}
	//END OF MY ADDED CODE
	
	public void goToNextVictim() {
		Coordinate victim = route.get(rCounter);
		rCounter++;

		System.out.println("going to victim java");

		goal = GoingTo.VICTIM;

		update(victim.x, victim.y);
	}
	
	public void goToHospital() {
		System.out.println("Hello");

		goal = GoingTo.HOSPITAL;

		update(mPack.hospital.x, mPack.hospital.y);
	}
	
	public void reached() {
		if (toHospital()) {
			env.at_hospital();
			System.out.println(map.getVictims());
			route = optRoute.optimumRoute(packet.pos, mPack.hospital, map.getVictims());
			rCounter = 0;

		} else if (goal == GoingTo.NON_CRITICAL) {
			env.at_non_critical(packet.pos.x, packet.pos.y);
			map.removeNonCritical(packet.pos);
		
		} else {
			env.seenVictim(packet.left.name().toLowerCase(), packet.pos.x, packet.pos.y);
			map.removeVictim(packet.pos);
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
	
	public void updateModel() {
		env.update_robot_position(packet.pos.x, packet.pos.y, packet.heading);
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

				sender = new PCClientSend(sock, mPack);
				sender.start();
				
				ObjectInputStream oIn = new ObjectInputStream(sock.getInputStream());
				
				env.connected();
				
				GUI.buildgui();
				
				try {
					while (true) {
						packet = (RobotPacket) oIn.readObject();
						System.out.println(packet.st + " " + packet.left + " " + packet.right + " " + packet.pos.x + " " + packet.pos.y);
						
						updateModel();
						
						if (oldState != packet.st && packet.st == Status.WAITING) {
							
							if (oldState == Status.LOCALISING) {
								route = optRoute.optimumRoute(packet.pos, mPack.hospital, map.getVictims());
								rCounter = 0;
								goToNextVictim();
							}
							
							if (sender.getTarget().equals(packet.pos)) {
								reached();
							}
						}
						
						oldState = packet.st;
						
						mapp = packet.emap;
						direction = packet.direction;
						pos = packet.epos;
						
						GUI.updateMap();
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
