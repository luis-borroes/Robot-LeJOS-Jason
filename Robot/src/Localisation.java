import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Localisation {
	int[][] probmap;
			
		
/*
 * 0 unknown
 * -1 wall
 * 1 is No Wall
 * 2 is Blank
 * 3 is Victim
 * 8 is hospital
 * 
 * Match rule 1 : Unknown matches all
 * Match Rule 2 : non wall matches with no Wall
 * Match rule 3 : Victims location can be blank but <2
 * Match rule 4 : Victims location can be have no-wall
 * 
 * Match rule 5 : Blank doesn't match with Victims
 * 
 */
	
	private int[][] map ;
	int rotation = 0;
	 int roby = 6;
	int robx = 4;
	PilotInterface me;
//	int mapsCount =10;
	
	List<int[][]> mapss = new ArrayList<int[][]>();
	
	List<MapMatch> probmappos = new ArrayList<MapMatch>();

	public Localisation(PilotInterface me)  {
		this.me = me;
		
	}
	
	//find  matches of 2d matrixes at r rotation
	
	public boolean searchLocal() {
		roby = me.getPosy();
		robx = me.getPosx();
		
		if (me.Direction() =='n') {
			rotation = 0;
		} else if (me.Direction()=='e') {
			rotation = 1;
		}else if (me.Direction() == 'w') {
			rotation = 3;
		}else {
			rotation=2;
		}
		
		probmap = me.map;
		map = me.realmap;
		probmappos = new ArrayList<MapMatch>();
		int[][] matrix = map;
		int[][] submatrix = trim(probmap,0);
		loopR: for (int r = 0; r < 4 ; r++)
		{
			
			loopX: for (int y = 0; y < matrix.length - submatrix.length + 1; ++y)
			loopY: for (int x = 0; x < matrix[x].length - submatrix[0].length + 1; ++x)
			{
				int falseVictims = 0;
			    for (int yy = 0; yy < submatrix.length; ++yy)
			    loopZ: for (int xx = 0; xx < submatrix[0].length; ++xx)
			    {
			    	//does it match
			    	if (matrix[y + yy][x + xx] == submatrix[yy][xx])
			    	{
			    		continue loopZ;
			    	} 
			    	//rule 1
			    	else if (submatrix[yy][xx] == 0)
			    	{
			    		continue loopZ;
			    	}
			    	//rule 4
			    	else if ((matrix[y + yy][x + xx] == 3) && (submatrix[yy][xx] ==2) && falseVictims < 2)
			    	{
			    		falseVictims++;
			    		continue loopZ;
			    	}
			    	//if its wrong
			    	else if (submatrix[yy][xx] ==8 && (matrix[y + yy][x + xx] == 2|| matrix[y + yy][x + xx] == 3)) {
			    		
			    		continue loopY;
			    	}
			    	// if its wrong
			    	else if (submatrix[yy][xx] == -1 && matrix[y + yy][x + xx] != -1) {
			    		
			    		continue loopY;
			    	}
			    	
			    	//if its wrong
			    	else if (submatrix[yy][xx] > 2 && matrix[y + yy][x + xx] == 2) {
			    		
			    		continue loopY;
			    	}
			    	//if its wrong
			    	else if (submatrix[yy][xx] >= 1 && matrix[y + yy][x + xx] == -1) {
			    		
			    		continue loopY;
			    	}
			    	
			    	//if its wrong
			    	else if (submatrix[yy][xx] >=2 && matrix[y + yy][x + xx] == 8) {
			    		
			    		continue loopY;
			    	}
			    	//rule 2
			    	else if ((matrix[y + yy][x + xx] >= 2) && (submatrix[yy][xx] ==1))
			    	{
			    		continue loopZ;
			    	} 
			    	
			    	//rule 5
			    	else if ((matrix[y + yy][x + xx] == 3) && (submatrix[yy][xx] ==1))
			    	{
			    		
			    		continue loopZ;
			    	}
			    	
			        
			        continue loopY;
			        
			    }
		
			    // Found a submatrix!
			    Tuple tr =  trimbotPos(probmap, 0, roby, robx);
			    int rotationt =  (rotation+r) %4;
			   
			    
			   
			    
			    MapMatch mr;
			    if (r == 0) {
			    	 mr = new MapMatch(y,x, tr.y + y , tr.x + x , rotationt, r );
			    } else if (r == 1) {
			    	// mr = new MapMatch(y,x, map.length -((tr.x+x)), ((map.length) -(tr.y + y)), rotation, r );
			    	 mr = new MapMatch(y,x, ((tr.x+x)),  ((map.length-1)-(tr.y + y)), rotationt, r );
			    } else if(r==2) {
			    	 mr = new MapMatch(y,x, (map.length-1-(tr.y+y)),  (map.length-1-(tr.x + x)), rotationt, r );
			    	
			    }else { //r=3
			    	mr = new MapMatch(y,x, (map.length -1)-((tr.x+x)), ((tr.y + y)), rotationt, r );

			    	
			    }
			    
			    //System.out.println("map match: " + mr.mapy + "A" + mr.mapx + " robot "+(mr.roboty) + ", " + (mr.robotx) + ", rotation: " + mr.robotRotation + " offset:" + mr.offset +"  on the map" );
			    probmappos.add(mr );
			    continue loopX;
			    //break loopX;
			    
			}
			rotateMatrix(map);
			//System.out.println("Done baby");
		}
		me.probx = probmappos.get(0).robotx;
		me.proby = probmappos.get(0).roboty;
		me.probr = probmappos.get(0).robotRotation;
		me.off = probmappos.get(0).offset;

		return true;
	}
	
	
	// by 90 degrees in ant-clockwise direction 
	 void rotateMatrix(int mat[][]) 
	{ 
		int N = mat[0].length;
	    // Consider all squares one by one 
	    for (int x = 0; x < N / 2; x++) 
	    { 
	        // Consider elements in group of 4 in  
	        // current square 
	        for (int y = x; y < N-x-1; y++) 
	        { 
	            // store current cell in temp variable 
	            int temp = mat[x][y]; 
	  
	            // move values from right to top 
	            mat[x][y] = mat[y][N-1-x]; 
	  
	            // move values from bottom to right 
	            mat[y][N-1-x] = mat[N-1-x][N-1-y]; 
	  
	            // move values from left to bottom 
	            mat[N-1-x][N-1-y] = mat[N-1-y][x]; 
	  
	            // assign temp to left 
	            mat[N-1-y][x] = temp; 
	        } 
	    } 
	} 
	
	
	//Trims the array
	// trim the array using int[][] trim = trim(array, 0); 
	public int[][] trim(int[][] mtx, int rmin, int rmax, int cmin, int cmax) {
		   int[][] result = new int[rmax-rmin+1][];
		   for (int r = rmin, i = 0; r <= rmax; r++, i++) {
		      result[i] = Arrays.copyOfRange(mtx[r], cmin, cmax+1);
		   }
		   return result;
		}
	
	// finds The Position of the Bot in the trimmed matrix
	public  Tuple trimbotPos(int[][]mtx, int trimmed, int ypos, int xpos){
		
		int cmin = mtx[0].length;
		   int rmin = mtx.length;
		   int cmax = -1;
		   int rmax = -1;

		   for (int r = 0; r < mtx.length; r++)
		      for (int c = 0; c < mtx[0].length; c++)
		         if (mtx[r][c] != trimmed) {
		            if (cmin > c) cmin = c;
		            if (cmax < c) cmax = c;
		            if (rmin > r) rmin = r;
		            if (rmax < r) rmax = r;
		         }
		   
		return new Tuple(ypos - rmin,  xpos -cmin);
	}
	
	
	public  int[][] trim(int[][] mtx, int trimmed) {
	   int cmin = mtx[0].length;
	   int rmin = mtx.length;
	   int cmax = -1;
	   int rmax = -1;

	   for (int r = 0; r < mtx.length; r++)
	      for (int c = 0; c < mtx[0].length; c++)
	         if (mtx[r][c] != trimmed) {
	            if (cmin > c) cmin = c;
	            if (cmax < c) cmax = c;
	            if (rmin > r) rmin = r;
	            if (rmax < r) rmax = r;
	         }

	   return trim(mtx, rmin, rmax, cmin, cmax);
	}

	public  class Tuple{
		public int y, x;
		
		public Tuple(int y, int x) {
			this.y = y;
			this.x = x;
			
		}
	
	}
	
	
		   
		  
		
	
	
	

}
