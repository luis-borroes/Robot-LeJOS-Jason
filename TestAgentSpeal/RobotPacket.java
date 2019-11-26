import java.io.Serializable;

public class RobotPacket implements Serializable {
	
	Status st = Status.WAITING;
	
	Color left = Color.WHITE;
	Color right = Color.WHITE;
	
	Coordinate pos = new Coordinate(0, 0);

	public RobotPacket() {
		
	}
}

enum Status {
	WAITING, MOVING, ODOMETRY
}
