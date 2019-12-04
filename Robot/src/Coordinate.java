import java.io.Serializable;

public class Coordinate implements Serializable {

	private static final long serialVersionUID = -3863889454805899844L;
	
	int x;
	int y;

	private double cost;
	private double costPlusHeuristic;

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
		costPlusHeuristic = 0;
		parent = p;
		direction = d;
	}
	
	public Coordinate(Coordinate coord) {
		this.x = coord.x;
		this.y = coord.y;
		cost = 0;
		costPlusHeuristic = 0;
	}
	
	public double getCost() {
		return cost;
	}
	
	public double getCostPlusHeuristic() {
		return costPlusHeuristic;
	}
	
	public void calculateCost(Coordinate goal) {
		double turnCost = 0;
		
		if (parent.getDirection() != direction)
			turnCost = 1;
		
		cost = parent.getCost() + 1 + turnCost; //cumulative cost + cost of turns
		double hn = Math.abs(x - goal.x) + Math.abs(y - goal.y);
		
		costPlusHeuristic = cost + hn;
	}
	
	public Coordinate getParent() {
		return parent;
	}
	
	public void setDirection(int dir) {
		direction = dir;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public int getHash() {
		return ((x * 1000) + (y * 10)) * 3;
	}

    @Override
    public boolean equals(Object o) {
  
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        /* Check if o is an instance of Complex or not 
          "null instanceof [type]" also returns false */
        if (!(o instanceof Coordinate)) { 
            return false; 
        } 
          
        // typecast o to Complex so that we can compare data members  
        Coordinate c = (Coordinate) o; 
          
        // Compare the data members and return accordingly  
        return (this.x == c.x && this.y == c.y);
    } 
}
