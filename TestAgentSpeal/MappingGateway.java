import java.util.ArrayList;

public class MappingGateway {
	private GridCell[][] map;
	private Coordinate currentPosition;
	private Coordinate hospital;
	private PathFinding pathFinding;
	
	public MappingGateway(Coordinate initialPos) {
		map = new GridCell[6][];
		this.setPosition(initialPos);
		
		for (int x = 0; x < 6; x++) {
			map[x] = new GridCell[7];
			
			for (int y = 0; y < 7; y++) {
				map[x][y] = new GridCell();
			}
		}

		setHospital(0, 0);
		getGridCell(initialPos).setStatus(GridCellStatus.UNOCCUPIED);
		
		pathFinding = new PathFinding(this);
	}
	
	public MapPacket save() {
		MapPacket m = new MapPacket();
		
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 7; y++) {
				if (map[x][y].getStatus() == GridCellStatus.OCCUPIED)
					m.addObj(x, y);
				
				if (map[x][y].getStatus() == GridCellStatus.VICTIM)
					m.addVictim(x, y);
			}
		}
		
		m.addHospital(hospital.x, hospital.y);
		
		return m;
	}
	
	public void load(MapPacket m) {
		for (int i = 0; i < m.obstacles.size(); i++) {
			getGridCell(m.obstacles.get(i)).setStatus(GridCellStatus.OCCUPIED);
		}
		
		for (int i = 0; i < m.victims.size(); i++) {
			getGridCell(m.victims.get(i)).setStatus(GridCellStatus.VICTIM);
		}
		
		setHospital(m.hospital);
	}
	
	public Coordinate getCurrentPosition() {
		return currentPosition;
	}
	
	public int getX() {
		return currentPosition.x;
	}
	
	public int getY() {
		return currentPosition.y;
	}
	
	public void setPosition(Coordinate pos) {
		currentPosition = pos;
	}
	
	public Coordinate getHospital(Coordinate pos) {
		return hospital;
	}
	
	public void setHospital(Coordinate pos) {
		hospital = pos;
	}
	
	public void setHospital(int x, int y) {
		Coordinate coord = new Coordinate(x, y);
		hospital = coord;
	}
	
	public boolean isWithinBounds(Coordinate coords) {
		return (coords.x >= 0 &&
				coords.x < 6 &&
				coords.y >= 0 &&
				coords.y < 7);
	}
	
	public ArrayList<Integer> getDirections(Coordinate coords) {
		ArrayList<Coordinate> path = pathFinding.find(currentPosition, coords);
		ArrayList<Integer> directions = new ArrayList<Integer>();
		
		for (int i = 1; i < path.size(); i++) {
			Coordinate curr = path.get(i);
			directions.add(curr.getDirection());
		}
		
		return directions;
	}
	
	public Coordinate getUnvisited() {
		Coordinate unvisited = pathFinding.findUnvisited(currentPosition);
		return unvisited;
	}
	
	public GridCell getGridCell(Coordinate coords) {
		return map[coords.x][coords.y];
	}
	
	public GridCell getGridCell(int x, int y) {
		return map[x][y];
	}
	
	public void updateCell(Coordinate coords, boolean occupied) {
		if (isWithinBounds(coords)) {
			getGridCell(coords).seen(occupied);
		}
	}
}
