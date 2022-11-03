import java.util.ArrayList;

public class MappingGateway {
	private GridCell[][] map;
	private Coordinate currentPosition;
	private Coordinate hospital;
	private Coordinate size;
	private PathFinding pathFinding;
	
	private Coordinate[] corners;
	private ArrayList<Coordinate> victims;
	private ArrayList<Coordinate> nonCriticals;
	
	public MappingGateway(Coordinate initialPos) {
		this.setPosition(initialPos);
		
		setSize(new Coordinate(6, 6));

		setHospital(0, 0);
		getGridCell(initialPos).setStatus(GridCellStatus.UNOCCUPIED);

		victims = new ArrayList<Coordinate>();
		nonCriticals = new ArrayList<Coordinate>();
		
		pathFinding = new PathFinding(this);
	}
	
	public void setSize(Coordinate s) {
		size = s;
		
		map = new GridCell[size.x][];
		
		for (int x = 0; x < size.x; x++) {
			map[x] = new GridCell[size.y];
			
			for (int y = 0; y < size.y; y++) {
				map[x][y] = new GridCell();
			}
		}

		corners = new Coordinate[4];
		corners[0] = new Coordinate(0, 0);
		corners[1] = new Coordinate(size.x - 1, 0);
		corners[2] = new Coordinate(size.x - 1, size.y - 1);
		corners[3] = new Coordinate(0, size.y - 1);
	}
	
	public Coordinate getSize() {
		return size;
	}
	
	public Coordinate[] getCorners() {
		return corners;
	}

	public ArrayList<Coordinate> getVictims() {
		return victims;
	}

	public void addVictim(Coordinate v) {
		getGridCell(v).setStatus(GridCellStatus.VICTIM);
		victims.add(v);
	}

	public void removeVictim(Coordinate v) {
		getGridCell(v).setStatus(GridCellStatus.UNOCCUPIED);
		victims.remove(v);
	}

	public ArrayList<Coordinate> getNonCriticals() {
		return nonCriticals;
	}

	public void addNonCritical(Coordinate v) {
		getGridCell(v).setStatus(GridCellStatus.NONCRITICAL);
		nonCriticals.add(v);
	}

	public void removeNonCritical(Coordinate v) {
		getGridCell(v).setStatus(GridCellStatus.UNOCCUPIED);
		nonCriticals.remove(v);
	}
	
	public MapPacket save() {
		MapPacket m = new MapPacket();
		
		m.size = getSize();
		
		for (int x = 0; x < size.x; x++) {
			for (int y = 0; y < size.y; y++) {
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
		setSize(m.size);
		
		for (int i = 0; i < m.obstacles.size(); i++) {
			getGridCell(m.obstacles.get(i)).setStatus(GridCellStatus.OCCUPIED);
		}
		
		for (int i = 0; i < m.victims.size(); i++) {
			addVictim(m.victims.get(i));
		}
		
		hospital = m.hospital;
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
	
	public Coordinate getHospital() {
		return hospital;
	}
	
	public void setHospital(Coordinate pos) {
		hospital = pos;
	}
	
	public void setHospital(int x, int y) {
		Coordinate coord = new Coordinate(x, y);
		hospital = coord;
	}
	
	public Coordinate getNextCell(int heading, int direction) {
		Coordinate newCoords = new Coordinate(currentPosition);
		
		heading += direction;
		
		if (heading >= 360)
			heading -= 360;
		if (heading < 0)
			heading += 360;

		switch (heading) {
			case (PathFinding.NORTH):
				newCoords.y += 1;
				break;
				
			case (PathFinding.EAST):
				newCoords.x += 1;
				break;
			
			case (PathFinding.SOUTH):
				newCoords.y -= 1;
				break;
			
			case (PathFinding.WEST):
				newCoords.x -= 1;
				break;
			
			default:
				break;
		}
		
		return newCoords;
	}
	
	public boolean isWithinBounds(Coordinate coords) {
		return (coords.x >= 0 &&
				coords.x < size.x &&
				coords.y >= 0 &&
				coords.y < size.y);
	}
	
	public boolean isCellBlocked(int heading, int direction) {
		Coordinate coords = getNextCell(heading, direction);
		
		if (!isWithinBounds(coords))
			return true;

		else
			return (getGridCell(coords).getStatus() == GridCellStatus.OCCUPIED);
	}
	
	public ArrayList<Integer> getDirections(Coordinate coords, int direction) {
		Coordinate start = new Coordinate(currentPosition);
		start.setDirection(direction);
		
		ArrayList<Coordinate> path = pathFinding.find(start, coords);
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
	
	public Coordinate getPathInfoNode(Coordinate start, Coordinate end) {
		ArrayList<Coordinate> path = pathFinding.find(start, end);
		Coordinate finalNode = path.get(path.size() - 1);
		
		return finalNode;
	}
}
