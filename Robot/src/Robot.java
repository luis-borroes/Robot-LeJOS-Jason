import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Robot {

	public static void main(String[] args) {
		Coordinate initialPos = new Coordinate(0, 0);
		int heading = PilotRobot.NORTH;
		
		MappingGateway map = new MappingGateway(initialPos);
		PilotRobot me = new PilotRobot(map, heading);
		PilotInterface meInt = new PilotInterface(me);
		PilotMonitor myMonitor = new PilotMonitor(me, map, 400);	
		PilotComm comm = new PilotComm(me, meInt, map);
		PilotSound sound = new PilotSound(me);

		// Set up the behaviours for the Arbitrator and construct it.
		//Behavior avoid = new Avoid(me);
		//Behavior plan = new Plan(me);
		Behavior moveLocate = new MoveLocate(meInt);
		Behavior senseEnv = new SenseEnv(meInt);
		Behavior pathGenLocal = new PathGenLocal(meInt);
		Behavior moveTowards = new MoveTowards(me);
		Behavior correctOdometry = new CorrectOdometry(meInt);
		Behavior finish = new Finish(me);
		//Behavior calibrate = new Calibrate(me);

		Behavior [] bArray = {moveLocate, pathGenLocal, moveTowards, correctOdometry, senseEnv, finish};
		//Behavior [] bArray = {moveTowards, correctOdometry, finish};
		//Behavior [] bArray = {plan, moveTowards, correctOdometry, avoid, finish};
		//Behavior [] bArray = {calibrate};
		
		Arbitrator arby = new Arbitrator(bArray);

		// Note that in the Arbritrator constructor, a message is sent
		// to stdout.  The following prints eight black lines to clear
		// the message from the screen
        for (int i=0; i<8; i++)
        	System.out.println("");

        // Start the Pilot Monitor
		myMonitor.start();
		
		// Start the Communicator
		comm.start();
		
		sound.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
        
        // Start the Arbitrator
		arby.go();

		sound.ready();
	}
}
