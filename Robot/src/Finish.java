import lejos.robotics.navigation.MovePilot;
import lejos.hardware.Sound;
import java.io.File;
import lejos.robotics.subsumption.Behavior;

public class Finish implements Behavior {
	
	public boolean suppressed;
	private PilotRobot me;
	private MovePilot pilot;
	File file = new File("raspberry.wav");
	
	public Finish(PilotRobot robot)  {
		me = robot;
		pilot = me.getPilot();
	}
	
	public void suppress() {
		suppressed = true;
	}
	
	//check if the map is finished
	public boolean takeControl() {
		return (me.getDone());
	}
	
	public void rotate() {
		while (me.getAssumedAngle() != me.getAngle() && !suppressed) {
			double diff = me.getAssumedAngle() - me.getAngle();
			pilot.rotate(diff, true);
			
		    while(pilot.isMoving() && !suppressed) {
		    	me.update(false, diff);
		        Thread.yield();  // wait till turn is complete or suppressed is called
		    }
		}
	}
	
	public void action() {
		suppressed = false;

		Sound.playSample(file, 100);
		Sound.beepSequenceUp();
		
		me.realRotate(360 * 3);
		rotate();
		me.stopRotating();
		
	    while(!suppressed) {
	        Thread.yield();
	    }
	}
}