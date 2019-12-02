import java.util.ArrayList;

public class test {
	
	MappingGateway map;
	
	public test(MappingGateway m) {
		map = m;
	}	
	
	public static void main(String[] args) throws Exception {
		
		MappingGateway m = new MappingGateway(new Coordinate(0, 0));
		
		m.setSize(new Coordinate(6, 6));

		m.addVictim(new Coordinate(2, 3));
		m.addVictim(new Coordinate(3, 1));
		m.addVictim(new Coordinate(5, 5));
		m.addVictim(new Coordinate(0, 5));
		m.addVictim(new Coordinate(2, 5));
		m.getGridCell(1, 3).setStatus(GridCellStatus.OCCUPIED);
		m.getGridCell(1, 5).setStatus(GridCellStatus.OCCUPIED);
		m.getGridCell(2, 4).setStatus(GridCellStatus.OCCUPIED);
		m.getGridCell(3, 3).setStatus(GridCellStatus.OCCUPIED);
		
		test o = new test(m);
		
		ArrayList<Coordinate> perms = o.optimumRoute(m.getCurrentPosition(), m.getHospital(), m.getVictims());
		
		for (int i =0; i < perms.size() ; i++){
			Coordinate temp = perms.get(i);
			System.out.print(temp.x + "-" +temp.y+ "   " );	
			
			System.out.println("hey babycakes" + i);
		}
		
	}
	
	//Calculate the optimum route
	public ArrayList<Coordinate> optimumRoute(Coordinate start, Coordinate hospital, ArrayList<Coordinate> victims){
		ArrayList<ArrayList<Coordinate>> perms = generateAllPerms(start, hospital, victims);
		
		double lowestCost = 100000.0;
		ArrayList<Coordinate> bestPerm = new ArrayList<Coordinate>();

		for (int i = 0; i < perms.size(); i++) {

			double cost = 0;
			ArrayList<Coordinate> perm = perms.get(i);
			
			int direction = start.getDirection();

			
			for (int a = 0; a < perm.size() - 1; a++) {
				Coordinate s = perm.get(a);
				s.setDirection(direction);
				
				Coordinate goal = map.getPathInfoNode(s, perm.get(a + 1));

				cost += goal.getCost();
				direction = goal.getDirection();
			}
			
			if (cost < lowestCost){
				lowestCost = cost;
				bestPerm = perm;
			}
		}
		
		bestPerm.remove(0);
		bestPerm.remove(bestPerm.size() - 1);

		return bestPerm;
	}
					
	
	//every order
	public ArrayList<ArrayList<Coordinate>> generateAllPerms(Coordinate start, Coordinate hospital, ArrayList<Coordinate> victims) {
		ArrayList<ArrayList<Coordinate>> perms = generatePerm(victims);
		
		for (int i = 0; i < perms.size(); i++) {
			perms.get(i).add(hospital);
			perms.get(i).add(0, start);
		}
		
		return perms;
	}
	
	
	public ArrayList<ArrayList<Coordinate>> generatePerm(ArrayList<Coordinate> original) {
		if (original.size() == 0) {
			ArrayList<ArrayList<Coordinate>> result = new ArrayList<ArrayList<Coordinate>>(); 
			result.add(new ArrayList<Coordinate>()); 
			return result; 
		}
		
		Coordinate firstElement = original.remove(0);
		ArrayList<ArrayList<Coordinate>> returnValue = new ArrayList<ArrayList<Coordinate>>();
		ArrayList<ArrayList<Coordinate>> permutations = generatePerm(original);
		
		for (ArrayList<Coordinate> smallerPermutated : permutations) {
			for (int index=0; index <= smallerPermutated.size(); index++) {
				ArrayList<Coordinate> temp = new ArrayList<Coordinate>(smallerPermutated);
				temp.add(index, firstElement);
				returnValue.add(temp);
			}
		}
		
		return returnValue;
	}
}
