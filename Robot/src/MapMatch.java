
public class MapMatch{
		public int mapy, mapx, roboty, robotx, robotRotation, offset;
		
		public MapMatch(int y, int x, int roboty, int robotx, int rotation, int offset) {
			this.mapy = y;
			this.mapx = x;
			this.roboty = roboty;  // the robots predicted location on the Real map, at time of the reading
			this.robotx = robotx;
			this.robotRotation = rotation;		// where the robot is facing on the Real Map not ProbMap  0 north 1 east 2 south 3 west
			this.offset = offset;		//its rotation offset from the RealMap and Rotation on ProbMap
				
		}
	}
		
