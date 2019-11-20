import java.io.Serializable;
import java.util.ArrayList;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.hardware.Sound;


public class PilotRobot {
	public static final int NORTH = 0;
	public static final int EAST = 90;
	public static final int SOUTH = 180;
	public static final int WEST = 270;

	public static final int RIGHT = 90;
	public static final int LEFT = -90;
	public static final int FORWARD = 0;
	
	private EV3ColorSensor leftColor, rightColor;	
	private EV3UltrasonicSensor usSensor;
	private EV3GyroSensor gSensor;
	private SampleProvider leftSP, rightSP, distSP, gyroSP, filterSP;
	private float[] leftSample, rightSample, distSample, angleSample; 
	private MovePilot pilot;
	private MappingGateway map;
	private ColorIdentifier colID = new ColorIdentifier();
	
	private int heading;
	private float assumedAngle;
	private boolean rotating;
	private boolean moving;
	private boolean isOverLine;
	private int cellCounter;
	private int pathCounter;
	private Coordinate target;
	private ArrayList<Integer> path;
	
	private boolean done;

	public PilotRobot(MappingGateway m, int h) {
		Brick myEV3 = BrickFinder.getDefault();

		leftColor = new EV3ColorSensor(myEV3.getPort("S1"));
		rightColor = new EV3ColorSensor(myEV3.getPort("S4"));
		usSensor = new EV3UltrasonicSensor(myEV3.getPort("S3"));
		gSensor = new EV3GyroSensor(myEV3.getPort("S2"));

		rightSP = rightColor.getRGBMode();
		leftSP = leftColor.getRGBMode();
		distSP = usSensor.getDistanceMode();
		gyroSP = gSensor.getAngleMode();
		
		filterSP = new MedianFilter(distSP, 5);
		
		leftSample = new float[leftSP.sampleSize()];
		rightSample = new float[rightSP.sampleSize()];
		distSample = new float[filterSP.sampleSize()];		// Size is 1
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
	    pilot.setAngularAcceleration(50);
	    
	    map = m;
	    heading = h;

		assumedAngle = 0;
		rotating = false;
		moving = false;
		isOverLine = false;
		cellCounter = 0;
		pathCounter = 0;
		
		done = false;
		
		target = map.getCurrentPosition();
		path = map.getDirections(target);
	    
		// Reset the value of the gyroscope to zero
		gSensor.reset();
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
    	filterSP.fetchSample(distSample, 0);
    	return distSample[0];
	}

	public float getAngle() {
    	gyroSP.fetchSample(angleSample, 0);
    	return angleSample[0];
	}
	
	public void resetGyro() {
		gSensor.reset();
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
	
	public void stopRotating() {
		rotating = false;
	}
	
	public boolean getMoving() {
		return moving;
	}
	
	public void stopMoving() {
		moving = false;
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
	
	public int getCellCounter() {
		return cellCounter;
	}
	
	public void resetCellCounter(boolean nextCell) {
		if (nextCell)
			cellCounter = 4;
		else
			cellCounter = 0;
	}
	
	public int getPathCounter() {
		return pathCounter;
	}
	
	public void nextPath() {
		pathCounter++;
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
			path = map.getDirections(target);
			
			if (path.size() == 0)
				return false;
			
			return true;
		}
		
		return false;
	}
	
	public void rePath() {
		path = map.getDirections(target);
		
		if (path.size() == 0)
			nextPath();
	}
	
	public void setPathToUnvisited() {
		target = map.getUnvisited();
		path = map.getDirections(target);
		
		if (path.size() == 0)
			done = true;
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
	
	public void cross() {
		map.setPosition(map.getNextCell(heading, PilotRobot.FORWARD));
		cellCounter++;
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

		//if rotating, only update cell ahead if we're looking at it
		if (Math.abs(diff) < 10) {
			Coordinate cellAhead = map.getNextCell(getHeading(), PilotRobot.FORWARD);
			if (getDistance() < 0.16)
				map.updateCell(cellAhead, true);
			else
				map.updateCell(cellAhead, false);
		}

	}
}
