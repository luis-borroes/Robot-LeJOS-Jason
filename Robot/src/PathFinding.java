import java.io.Serializable;
import java.util.ArrayList;

public class PathFinding implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MappingGateway map;
	private Coordinate start;
	private Coordinate current;
	private Coordinate parent;
	private Coordinate goal;
	
	private ArrayList<Coordinate> agenda;
	private boolean[][] visited;
	
	public PathFinding(MappingGateway m) {
		map = m;
	}
	
	public ArrayList<Coordinate> find(Coordinate s, Coordinate g) {
		start = s;
		current = s;
		parent = s;
		goal = g;
		
		agenda = new ArrayList<Coordinate>();
		resetVisited();
		
		ArrayList<Coordinate> path = new ArrayList<Coordinate>();
		
		while (current.getHash() != goal.getHash()) {
			getAdjNodes(current);
			
			if (agenda.size() == 0)
				return new ArrayList<Coordinate>();
			
			current = getLowestCost();
		}
		
		path.add(current);
		
		while (current.getHash() != start.getHash()) {
			parent = current.getParent();
			path.add(0, parent);
			current = parent;
		}
		
		return path;
	}
	
	public Coordinate findUnvisited(Coordinate s) {
		start = s;
		current = s;

		agenda = new ArrayList<Coordinate>();
		resetVisited();
		
		GridCell cell = map.getGridCell(current);
		
		while (cell.getStatus() != GridCellStatus.UNKNOWN && cell.getStatus() != GridCellStatus.UNCERTAIN) {
			getAdjNodes(current);
			
			if (agenda.size() == 0)
				return map.getCurrentPosition();
			
			current = agenda.get(0);
			agenda.remove(0);

			cell = map.getGridCell(current);
		}
		
		return current;
	}
	
	public Coordinate getLowestCost() {
		int minIndex = 0;
		double minCost = 99999;
		double cost = 0;
		
		for (int i = 0; i < agenda.size(); i++) {
			cost = agenda.get(i).getCost();
			
			if (cost < minCost) {
				minCost = cost;
				minIndex = i;
			}
		}
		
		Coordinate low = agenda.get(minIndex);
		agenda.remove(minIndex);
		
		return low;
	}
	
	public void getAdjNodes(Coordinate node) {
		
		Coordinate[] possible = {
				new Coordinate(node.x - 1, node.y, node, PilotRobot.WEST),
				new Coordinate(node.x + 1, node.y, node, PilotRobot.EAST),
				new Coordinate(node.x, node.y - 1, node, PilotRobot.SOUTH),
				new Coordinate(node.x, node.y + 1, node, PilotRobot.NORTH)
		};
		
		for (int i = 0; i < 4; i++) {
			
			if (map.isWithinBounds(possible[i])) { // node within bounds
				
				if (!visited[possible[i].x][possible[i].y] && map.getGridCell(possible[i]).getStatus() != GridCellStatus.OCCUPIED) { // if not visited or occupied
					possible[i].calculateCost(start, goal);
					agenda.add(possible[i]);
					visited[possible[i].x][possible[i].y] = true;
				}
			}
		}
	}
	
	public void resetVisited() {
		visited = new boolean[6][];
		
		for (int x = 0; x < 6; x++) {
			visited[x] = new boolean[7];
			
			for (int y = 0; y < 7; y++) {
				visited[x][y] = false;
			}
		}
	}

}
