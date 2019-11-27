public class ColorIdentifier {

	/*
	private static double[] black = {0.01840398, 0.021024366, 0.020415225};
	private static double[] white = {0.226095735, 0.232010527, 0.202281577};
	private static double[] red = {0.214799598, 0.036123127, 0.034757065};
	private static double[] blue = {0.041270186, 0.173850207, 0.160650957};
	private static double[] green = {0.066836075, 0.176358856, 0.075501011};
	private static double[] yellow = {0.254819064, 0.15692042, 0.048367216};
	private static double[] burgundy = {0.066763988, 0.030896771, 0.032691754};
	*/
	
	double[][] lColours = {{0.2834154, 0.2335666, 0.040032},
    		{0.2472585,0.0770178,0.023807},
    		{0.0509601,0.295394,0.141773},
    		{0.0782173,0.2842326,0.061965},
    		{0.073155,0.0620915,0.021919},
    		{0.2680412,0.3794252,0.188724},
    		{0.0208667,0.037092,0.015876}};
	
	double[][] rColours = {{0.140048,0.10313,0.020384},
    		{0.173475,0.042317,0.014107},
    		{0.03184,0.099986,0.058467},
    		{0.057086,0.127678,0.038712},
    		{0.03837,0.027224,0.004488},
    		{0.178621,0.0203765,0.14613}, //white
    		{0.014774,0.021024,0.009014}}; //black
	
	Color[] indexEnum = Color.values().clone();
	
	public ColorIdentifier() {
		
	}
	
	public double distance(float[] point, double[] ref) {
		return Math.sqrt(Math.pow(point[0] - ref[0], 2) + Math.pow(point[1] - ref[1], 2) + Math.pow(point[2] - ref[2], 2));
	}
	
	public Color findNearest(float[] col, boolean isRight) {
		double[][] colours;
		
		if (isRight)
			colours = rColours;
		else
			colours = lColours;
		
		
		Color minCol = Color.YELLOW;
		double minDist = distance(col, colours[0]);
		double currDist = minDist;
		
		for (int i = 1; i < indexEnum.length; i++) {
			currDist = distance(col, colours[i]);
		
			if (currDist < minDist) {
				minCol = indexEnum[i];
				minDist = currDist;
			}
		}
		
		return minCol;
	}
}

enum Color {
	YELLOW, RED, CYAN, GREEN, BURGANDY, WHITE, BLACK
}
