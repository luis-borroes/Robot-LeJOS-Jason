import java.util.ArrayList;

public class OptRoute {
	
	MappingGateway map;
	
	public OptRoute(MappingGateway m) {
		map = m;
	}
	
	//Calculate the optimum route
	public ArrayList<Coordinate> optimumRoute(Coordinate start, Coordinate hospital, ArrayList<Coordinate> victims){
		ArrayList<Coordinate> vicCopy = new ArrayList<Coordinate>(victims);
		ArrayList<ArrayList<Coordinate>> perms = generateAllPerms(start, hospital, vicCopy);
		
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
