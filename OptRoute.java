// Environment code for project doctor2018
import java.util.*;
import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import java.util.Arrays; 
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.logging.*;

public class ParamedicEnv extends Environment {
	
    public static final int GSize = 6; // The bay is a 6x6 grid
    public static final int HOSPITAL  = 8; // hospital code in grid model
    public static final int VICTIM  = 16; // victim code in grid model
	public static final int OBSTACLE  = -1;
		
	private  double goalCost = 0.29;
	private  double turnCost = 0.04;
	private  double moveCost = 0.1;
	ParamedicEnv pe;
	
	public void init(ParamedicEnv pe) {
		this.pe = pe;
		
		
	
	}

	// returns victims locations	
	List<String> getVictimLocations(){
		List<String> victims = new ArrayList<String>(); 
		for(int y= 0; y < pe.map.length ; y++){
			for( int x = 0; x < pe.map.length; x++){
				if(pe.map[y][x] == VICTIM){
					victims.add(y+""+x);
				}
			}
		}
		return victims;
	}
	
	//Calcule the optimum route
	public List<String> optimumRoute(){
		List<List<String>> perms = generatePerm(getVictimLocations());
		
		double lowestCost = 100000.0;
		Lists<String> bestPerm;
		for (int i =0; i < perms.length ; i++){
			char direction = robot.direction;
			double cost = 0;
			for (a = 0; a < perms.get(i).length ; a++){ //iterate through the path
				if (a ==0){
					cost+= calcCost(String astarpath = Astar(direction,..... between robot.x and y and perms.get(i).get(a).charAt(0 and 1));
					direction = astapath.charAt(astarpath.length-1);
				}
				else if(a == perms.get(i).length -1){
					cost+= calcCost(String astarpath = Astar(direcion, between a and hospital);
					direction = astapath.charAt(astarpath.length-1);
				}
				
				else{
					cost+= calcCost(String astarpath = Astar(direction,.....perms.get(i).get(a-1).charAt(0 a)& a, robot.direction);
					direction = astapath.charAt(astarpath.length-1);
				}
			}
			if (cost < lowestCost){
				lowestCost = cost;
				bestPerm = perms.get(i);
			}
		}
		return bestPerm;
	}
					
					
	
	
	
	
	//every order
	public <String> List<List<String>> generatePerm(List<String> original) {
     if (original.size() == 0) {
       List<List<String>> result = new ArrayList<List<String>>(); 
       result.add(new ArrayList<String>()); 
       return result; 
     }
     String firstElement = original.remove(0);
     List<List<String>> returnValue = new ArrayList<List<String>>();
     List<List<String>> permutations = generatePerm(original);
     for (List<String> smallerPermutated : permutations) {
       for (int index=0; index <= smallerPermutated.size(); index++) {
         List<String> temp = new ArrayList<String>(smallerPermutated);
         temp.add(index, firstElement);
         returnValue.add(temp);
       }
     }
     return returnValue;
   }
	
   //calc cost
	public  double calcCost(String path, char start_Direction) {
		char direction = start_Direction;
		double cost = 0;
		cost += goalCost;
		for (int i = 0; i < path.length() ; i++) {
			if (direction != path.charAt(i)) {
				cost += turnCost ;
			}
			direction = path.charAt(i);
			cost += moveCost;
		}
		//System.out.println(cost);
		return cost;
		
	}
	//=====================================================================
}
