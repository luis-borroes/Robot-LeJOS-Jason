// BumperCarSimple.java
// 
// A simple application that uses the Subsumption architecture to create a
// bumper car, that drives forward, and changes direction given a collision.
//
// Terry Payne
// 1st October 2018
//

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Robot {

	public static void main(String[] args) {
		Coordinate initialPos = new Coordinate(0, 0);
		int heading = PilotRobot.NORTH;
		//IP Address: 192.168.70.96
		
		MappingGateway map = new MappingGateway(initialPos);
		PilotRobot me = new PilotRobot(map, heading);		
		PilotMonitor myMonitor = new PilotMonitor(me, map, 400);	
		PilotComm comm = new PilotComm(me, map);
		PilotSound sound = new PilotSound(me);

		// Set up the behaviours for the Arbitrator and construct it.
		//Behavior avoid = new Avoid(me);
		//Behavior plan = new Plan(me);
		Behavior moveTowards = new MoveTowards(me);
		Behavior correctOdometry = new CorrectOdometry(me);
		Behavior finish = new Finish(me);
		//Behavior calibrate = new Calibrate(me);

		Behavior [] bArray = {moveTowards, correctOdometry, finish};
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
	}
}
