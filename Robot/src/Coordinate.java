import java.io.Serializable;

public class Coordinate implements Serializable {

	private static final long serialVersionUID = -3863889454805899844L;
	
	int x;
	int y;
	private double cost;
	private Coordinate parent;
	private int direction;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
		cost = 0;
	}
	
	public Coordinate(int x, int y, Coordinate p, int d) {
		this.x = x;
		this.y = y;
		cost = 0;
		parent = p;
		direction = d;
	}
	
	public Coordinate(Coordinate coord) {
		this.x = coord.x;
		this.y = coord.y;
		cost = 0;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void calculateCost(Coordinate start, Coordinate goal) {
		double turnCost = 0;
		
		if (parent.getDirection() != direction)
			turnCost = 1.5;
		
		double gn = Math.abs(x - start.x) + Math.abs(y - start.y) + turnCost; //manhattan distance for cost to node + cost for turns
		//double hn = Math.sqrt(Math.pow(goal.x - x, 2) + Math.pow(goal.y - y, 2)); //euclidian distance for heuristic from node to goal
		double hn = Math.abs(x - goal.x) + Math.abs(y - goal.y);
		
		cost = gn + hn;
	}
	
	public Coordinate getParent() {
		return parent;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public int getHash() {
		return ((x * 1000) + (y * 10)) * 3;
	}
}
