import java.util.ArrayList;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.hardware.Sound;
import lejos.internal.ev3.EV3LED;


public class PilotRobot {
	public static final int NORTH = 0;
	public static final int EAST = 90;
	public static final int SOUTH = 180;
	public static final int WEST = 270;

	public static final int RIGHT = 90;
	public static final int LEFT = -90;
	public static final int FORWARD = 0;
	
	private EV3ColorSensor leftColor, rightColor;	
	private EV3IRSensor usSensor;
	private EV3GyroSensor gSensor;
	private SampleProvider leftSP, rightSP, distSP, gyroSP, filterSP;
	private float[] leftSample, rightSample, distSample, angleSample; 
	private MovePilot pilot;
	private EV3LED led;
	private MappingGateway map;
	private ColorIdentifier colID = new ColorIdentifier();
	
	private int heading;
	private float assumedAngle;
	private boolean rotating;
	private boolean moving;
	private boolean isOnOdometry;
	private boolean isOverLine;
	private boolean isAmbulance;
	private int cellCounter;
	private int pathCounter;
	private int packetCounter;
	private Coordinate target;
	private ArrayList<Integer> path;
	
	private boolean doingLoc;
	
	private boolean done;

	public PilotRobot(MappingGateway m, int h) {
		Brick myEV3 = BrickFinder.getDefault();

		leftColor = new EV3ColorSensor(myEV3.getPort("S1"));
		rightColor = new EV3ColorSensor(myEV3.getPort("S4"));
		usSensor = new EV3IRSensor(myEV3.getPort("S3"));
		gSensor = new EV3GyroSensor(myEV3.getPort("S2"));

		rightSP = rightColor.getRGBMode();
		leftSP = leftColor.getRGBMode();
		distSP = usSensor.getDistanceMode();
		gyroSP = gSensor.getAngleMode();
		
		//filterSP = new MedianFilter(distSP, 5);
		
		leftSample = new float[leftSP.sampleSize()];
		rightSample = new float[rightSP.sampleSize()];
		distSample = new float[distSP.sampleSize()];		// Size is 1
		angleSample = new float[gyroSP.sampleSize()];	// Size is 1

		// Set up the wheels by specifying the diameter of the
		// left (and right) wheels in centimeters, i.e. 4.05 cm.
		// The offset number is the distance between the centre
		// of wheel to the centre of robot (4.9 cm)
		// NOTE: this may require some trial and error to get right!!!
		Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, 4.23877).offset(-5.4188);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.D, 4.21099).offset(5.4188);
		
		Chassis myChassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);

	    pilot = new MovePilot(myChassis);
	    pilot.setLinearAcceleration(50);
	    pilot.setLinearSpeed(20);
	    //pilot.setAngularAcceleration(75);
	    pilot.setAngularSpeed(45);
//	    pilot.setAngularAcceleration(50);
//	    pilot.setAngularSpeed(40);
	    
	    led = new EV3LED();
	    
	    map = m;

	    resetRobot(h);
		
		target = map.getCurrentPosition();
		path = map.getDirections(target, heading);
	    
		// Reset the value of the gyroscope to zero
		gSensor.reset();
	}
	
	public void resetRobot(int h) {
	    heading = h;
		assumedAngle = 0;
		rotating = false;
		moving = false;
		isOnOdometry = false;
		isOverLine = false;
		isAmbulance = false;
		cellCounter = 0;
		pathCounter = 0;
		packetCounter = 0;
		done = false;
	}
	
	public void closeRobot() {
		leftColor.close();
		rightColor.close();
		usSensor.close();
		gSensor.close();
	}

	public float[] getLeftColor() {
    	leftSP.fetchSample(leftSample, 0);
    	return leftSample;
	}
	
	public float[] getRightColor() {
    	rightSP.fetchSample(rightSample, 0);
    	return rightSample;
	}
	
	public Color getLColID() {
		return colID.findNearest(getLeftColor(), false);
	}
	
	public Color getRColID() {
		return colID.findNearest(getRightColor(), true);
	}
	
	public boolean getLBlack() {
		return (getLColID() == Color.BLACK);
	}
	
	public boolean getRBlack() {
		return (getRColID() == Color.BLACK);
	}
	
	public float getDistance() {
    	distSP.fetchSample(distSample, 0);
    	return distSample[0];
	}

	public float getAngle() {
    	gyroSP.fetchSample(angleSample, 0);
    	return angleSample[0];
	}
	
	public void resetGyro() {
		gSensor.reset();
	}
	
	public void setHeading(int h) {
		heading = h;
	}
	
	public int getHeading() {
		return heading;
	}
	
	public float getAssumedAngle() {
		return assumedAngle;
	}
	
	public void resetAssumedAngle() {
		assumedAngle = getAngle();
	}
	
	public boolean getRotating() {
		return rotating;
	}
	
	public void startRotating() {
		rotating = true;
	}
	
	public void stopRotating() {
		rotating = false;
	}
	
	public boolean getMoving() {
		return moving;
	}
	
	public void startMoving() {
		moving = true;
	}
	
	public void stopMoving() {
		moving = false;
	}
	
	public void startOdometry() {
		isOnOdometry = true;
	}
	
	public void stopOdometry() {
		isOnOdometry = false;
	}
	
	public boolean getOdometry() {
		return isOnOdometry;
	}
	
	public MovePilot getPilot() {
		return pilot;
	}
	
	public boolean getOverLine() {
		return isOverLine;
	}
	
	public void setOverLine(boolean newOverLine) {		
		isOverLine = newOverLine;
	}
	
	public MappingGateway getMap() {
		return map;
	}
	
	public void incCellCounter() {
		cellCounter++;
	}
	
	public int getCellCounter() {
		return cellCounter;
	}
	
	public void resetCellCounter(boolean nextCell) {
		if (nextCell)
			cellCounter = 4;
		else
			cellCounter = 0;
		
		stopOdometry();
	}
	
	public int getPathCounter() {
		return pathCounter;
	}
	
	public void nextPath() {
		pathCounter++;
	}
	
	public void setPacketCounter(int pid) {
		packetCounter = pid;
	}
	
	public int getPacketCounter() {
		return packetCounter;
	}
	
	public void setLoc(boolean loc) {
		doingLoc = loc;
	}
	
	public boolean getLoc() {
		return doingLoc;
	}
	
	public void done() {
		done = true;
	}
	
	public boolean getDone() {
		return done;
	}
	
	public Coordinate getTarget() {
		return target;
	}
	
	public ArrayList<Integer> getPath() {
		return path;
	}
	
	public boolean setPath(Coordinate coords) {
		target = coords;
		
		if (coords.getHash() != map.getCurrentPosition().getHash()) {
			path = map.getDirections(target, heading);
			
			if (path.size() == 0)
				return false;
			
			return true;
		}
		
		return false;
	}
	
	public void rePath() {
		path = map.getDirections(target, heading);
		
		//if (path.size() == 0)
			//nextPath();
	}
	
	public void setPathToUnvisited() {
		target = map.getUnvisited();
		path = map.getDirections(target, heading);
		
		if (path.size() == 0)
			done();
	}
	
	
	public void realMove(int direction) {
		rotateTowards(direction);
		
		moving = true;
	}
	
	public void rotateTowards(int direction) {
		int rotAngle = direction - heading;
		if (rotAngle > 180)
			rotAngle -= 360;
		else if (rotAngle < -180)
			rotAngle += 360;
		
		realRotate(rotAngle);
	}
	
	public void realRotate(float angle) {
		assumedAngle += angle;
		heading += angle;
		
		while (heading >= 360)
			heading -= 360;
		while (heading < 0)
			heading += 360;

		rotating = true;
	}
	
	public void startAmbulance() {
		isAmbulance = true;
		led.setPattern(2, 1);
	}
	
	public void stopAmbulance() {
		isAmbulance = false;
		led.setPattern(0);
	}
	
	public boolean getAmbulance() {
		return isAmbulance;
	}
	
	public void cross() {
		//map.setPosition(map.getNextCell(heading, PilotRobot.FORWARD));
		//cellCounter++;
		Sound.beep();
	}

	public void update(boolean checkCross, double diff) {

		if (checkCross) {
			if (getLBlack() || getRBlack()) {
				setOverLine(true);
				
			} else if (getOverLine()) {
				cross();
				setOverLine(false);
			}
		}

		/*
		//if rotating, only update cell ahead if we're looking at it
		if (Math.abs(diff) < 10) {
			Coordinate cellAhead = map.getNextCell(getHeading(), PilotRobot.FORWARD);
			if (getDistance() < 0.16)
				map.updateCell(cellAhead, true);
			else
				map.updateCell(cellAhead, false);
		}
		*/

	}
}
