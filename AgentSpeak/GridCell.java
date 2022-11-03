import java.io.Serializable;

public class GridCell implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GridCellStatus status;
	private int count;
	private int m;
	
	public GridCell() {
		this.status = GridCellStatus.UNOCCUPIED;
		this.count = 0;
		this.m = 0;
	}
	
	public GridCellStatus getStatus() {
		return this.status;
	}
	
	public void setStatus(GridCellStatus s) {
		this.status = s;
	}
	
	public int getM() {
		return m;
	}
	
	public int getCount() {
		return count;
	}

	public double getProbability() {
		if (count == 0) {
			return -1;
			
		} else {
			return  ((m + count) / (2 * count)) * 100;
		}
	}
	
	public void seen(boolean occupied) {
		this.count = this.count + 1;
		
		if (occupied)
			m++;
		else
			m--;
		
		
		if (m > 35)
			setStatus(GridCellStatus.OCCUPIED);
		else if (m > 0)
			setStatus(GridCellStatus.UNCERTAIN);
		else if (m < 0)
			setStatus(GridCellStatus.UNOCCUPIED);
		else
			setStatus(GridCellStatus.UNKNOWN);
	}
}

enum GridCellStatus {
	OCCUPIED, UNOCCUPIED, UNKNOWN, UNCERTAIN, VICTIM, NONCRITICAL
}