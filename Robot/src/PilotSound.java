import lejos.hardware.Sound;
// PillotMonitor.java
// 
// Based on the RobotMonitor class, this displays the robot
// state on the LCD screen; however, it works with the PilotRobot
// class that exploits a MovePilot to control the Robot.
//
// Luis Borroes
// 15th October 2019
//

public class PilotSound extends Thread {
	
	public PilotRobot robot;
	
    public PilotSound(PilotRobot r){
    	this.setDaemon(true);
    	robot = r;
    	
    	Sound.setVolume(2);
    }

	public void ready() {
		Sound.twoBeeps();
	}
    
    public void run() {
    	while (true) {
    		if (robot.getAmbulance()) {
    			Sound.playTone(220, 650);
    			Sound.playTone(294, 650);
    			
    		} else {
    			try {
    				Thread.sleep(400);
    			} catch (InterruptedException e) {}
    		}
    	}
    }

}

