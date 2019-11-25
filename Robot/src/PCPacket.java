import java.io.Serializable;

public class PCPacket implements Serializable {
	
	Type cmd = Type.NONE;
	Coordinate target = new Coordinate(0, 0);
	
	MapPacket map = new MapPacket();

	public PCPacket() {
		
	}
}

enum Type {
	NONE, MOVE, INIT
}
