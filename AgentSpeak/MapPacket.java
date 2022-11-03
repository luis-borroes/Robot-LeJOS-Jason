import java.io.Serializable;
import java.util.ArrayList;

public class MapPacket implements Serializable {
	
	Coordinate size = new Coordinate(6, 7);
	ArrayList<Coordinate> obstacles = new ArrayList<Coordinate>();
	ArrayList<Coordinate> victims = new ArrayList<Coordinate>();
	Coordinate hospital = new Coordinate(0, 0);

	public MapPacket() {
		
	}
	
	public void addObj(Coordinate coord) {
		obstacles.add(coord);
	}
	
	public void addObj(int x, int y) {
		Coordinate coord = new Coordinate(x, y);
		obstacles.add(coord);
	}
	
	public void addVictim(Coordinate coord) {
		victims.add(coord);
	}
	
	public void addVictim(int x, int y) {
		Coordinate coord = new Coordinate(x, y);
		victims.add(coord);
	}
	
	public void addHospital(Coordinate coord) {
		hospital = coord;
	}
	
	public void addHospital(int x, int y) {
		Coordinate coord = new Coordinate(x, y);
		hospital = coord;
	}
}
