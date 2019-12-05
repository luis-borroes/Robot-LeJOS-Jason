import java.util.ArrayList;

public class test {
	
	MappingGateway map;
	
	public int[][] mapy ={
	        {-1,-1,-1,-1,-1,-1,-1,-1},
			{-1,2,3,2,2,3,2,-1},
			{-1,2,2,2,2,2,2,-1},
			{-1,2,2,3,2,-1,2,-1},
			{-1,3,2,-1,-1,2,2,-1},
			{-1,2,2,2,2,2,3,-1},
			{-1,8,2,2,2,2,-1,-1},
			{-1,-1,-1,-1,-1,-1,-1,-1}}; 
	
	public test(MappingGateway m) {
		map = m;
	}	
	
	public static void main(String[] args) throws Exception {
		
		MappingGateway m = new MappingGateway(new Coordinate(0, 0));
		
		m.setSize(new Coordinate(6, 6));

//		m.setHospital(new Coordinate(0, 0));
//		m.addVictim(new Coordinate(2, 0));
//		m.addVictim(new Coordinate(2, 2));
//		m.addVictim(new Coordinate(2, 4));
//		m.addVictim(new Coordinate(0, 5));
//		m.addVictim(new Coordinate(5, 4));
//		m.getGridCell(1, 1).setStatus(GridCellStatus.OCCUPIED);
//		m.getGridCell(1, 4).setStatus(GridCellStatus.OCCUPIED);
//		m.getGridCell(4, 1).setStatus(GridCellStatus.OCCUPIED);
//		m.getGridCell(4, 4).setStatus(GridCellStatus.OCCUPIED);
		
		m.setHospital(new Coordinate(0, 0));
		m.addVictim(new Coordinate(0, 2));
		m.addVictim(new Coordinate(1, 5));
		m.addVictim(new Coordinate(2, 3));
		m.addVictim(new Coordinate(4, 5));
		m.addVictim(new Coordinate(5, 1));
		m.getGridCell(5, 0).setStatus(GridCellStatus.OCCUPIED);
		m.getGridCell(4, 3).setStatus(GridCellStatus.OCCUPIED);
		m.getGridCell(3, 2).setStatus(GridCellStatus.OCCUPIED);
		m.getGridCell(2, 2).setStatus(GridCellStatus.OCCUPIED);
		
		test o = new test(m);
		
		int[][] realmap = o.genRealMap();
		
		for (int i = 0; i < realmap.length; i++) {
			for (int j = 0; j < realmap[i].length; j++) {
				System.out.print(o.mapy[i][j]);
				System.out.print(realmap[i][j] + ", ");
			}
			
			System.out.println();
		}
		
	}
	
	public int[][] genMap() {
		Coordinate size = map.getSize();
		int[][] m = new int[(size.y * 2) + 2][(size.x * 2) + 2];
		
		for (int y = 0; y < (size.y * 2) + 2; y++) {
			
			for (int x = 0; x < (size.x * 2) + 2; x++) {
				m[y][x] = 0;
			}
		}
		
		return m;
	}
	
	public int[][] genRealMap() {
		Coordinate size = map.getSize();
		
		int[][] rmap = new int[size.y + 2][size.x + 2];
		int value = -1;
		

		for (int y = 0; y < (size.y + 2); y++) {
			
			for (int x = 0; x < (size.x + 2); x++) {
				
				//if surrounding coordinates
				if (x == 0 || y == 0 || x == (size.x + 1) || y == (size.y + 1)) {
					value = -1;
					
				} else {

					int realX = x - 1;
					int realY = y - 1;
					Coordinate current = new Coordinate(realX, realY);

					
					if (map.getHospital().equals(current)) {
						value = 8;

					} else {
						GridCell cell = map.getGridCell(current);
						
						switch (cell.getStatus()) {
							case OCCUPIED:
								value = -1;
								break;
								
							case UNOCCUPIED:
								value = 2;
								break;
								
							case UNKNOWN:
								value = 2;
								break;
								
							case UNCERTAIN:
								value = -1;
								break;
								
							case VICTIM:
								value = 3;
								break;
								
							case NONCRITICAL:
								value = 3;
								break;
								
							default:
								value = 1;
								break;
						}
					}
				}

				//rmap[(size.y + 1) - y][(size.x + 1) - x] = value;
				rmap[(size.y + 1) - y][x] = value;
				
			}
		}
		
		return rmap;
		
	}
}
