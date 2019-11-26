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

		hospital = new Coordinate(0, 0);
		getGridCell(initialPos).setStatus(GridCellStatus.UNOCCUPIED);
		
		pathFinding = new PathFinding(this);
	}
	
	public void load(MapPacket m) {
		for (int i = 0; i < m.obstacles.size(); i++) {
			getGridCell(m.obstacles.get(i)).setStatus(GridCellStatus.OCCUPIED);
		}
		
		for (int i = 0; i < m.victims.size(); i++) {
			getGridCell(m.victims.get(i)).setStatus(GridCellStatus.VICTIM);
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
	
	public Coordinate getNextCell(int heading, int direction) {
		Coordinate newCoords = new Coordinate(currentPosition);
		
		heading += direction;
		
		if (heading >= 360)
			heading -= 360;
		if (heading < 0)
			heading += 360;

		switch (heading) {
			case (PilotRobot.NORTH):
				newCoords.y += 1;
				break;
				
			case (PilotRobot.EAST):
				newCoords.x += 1;
				break;
			
			case (PilotRobot.SOUTH):
				newCoords.y -= 1;
				break;
			
			case (PilotRobot.WEST):
				newCoords.x -= 1;
				break;
			
			default:
				break;
		}
		
		return newCoords;
	}
	
	public boolean isWithinBounds(Coordinate coords) {
		return (coords.x >= 0 &&
				coords.x < 6 &&
				coords.y >= 0 &&
				coords.y < 7);
	}
	
	public boolean isCellBlocked(int heading, int direction) {
		Coordinate coords = getNextCell(heading, direction);
		
		if (!isWithinBounds(coords))
			return true;

		else
			return (getGridCell(coords).getStatus() == GridCellStatus.OCCUPIED);
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
