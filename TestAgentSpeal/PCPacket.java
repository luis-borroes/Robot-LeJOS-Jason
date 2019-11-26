import java.io.Serializable;

public class PCPacket implements Serializable {
	
	int id = 0;
	Type cmd = Type.NONE;
	Coordinate target = new Coordinate(0, 0);
	
	MapPacket map = new MapPacket();

	public PCPacket() {
		
	}
}

enum Type {
	NONE, MOVE, INIT
}
