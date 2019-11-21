import java.io.Serializable;
import java.util.ArrayList;

public class MapPacket implements Serializable {
	
	Coordinate size = new Coordinate(6, 7);
	ArrayList<Coordinate> obstacles = new ArrayList();
	ArrayList<Coordinate> victims = new ArrayList();

	public MapPacket() {
		
	}
	
	public void addObj(Coordinate coord) {
		obstacles.add(coord);
	}
	
	public void addVictim(Coordinate coord) {
		victims.add(coord);
	}
}
