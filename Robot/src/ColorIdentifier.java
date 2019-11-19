public class ColorIdentifier {

	private static double[] black = {0.01840398, 0.021024366, 0.020415225};
	private static double[] white = {0.226095735, 0.232010527, 0.202281577};
	private static double[] red = {0.214799598, 0.036123127, 0.034757065};
	private static double[] blue = {0.041270186, 0.173850207, 0.160650957};
	private static double[] green = {0.066836075, 0.176358856, 0.075501011};
	private static double[] yellow = {0.254819064, 0.15692042, 0.048367216};
	private static double[] burgundy = {0.066763988, 0.030896771, 0.032691754};
	
	public ColorIdentifier() {
		
	}
	
	public double distance(float[] point, double[] ref) {
		return Math.sqrt(Math.pow(point[0] - ref[0], 2) + Math.pow(point[1] - ref[1], 2) + Math.pow(point[2] - ref[2], 2));
	}
	
	public Color findNearest(float[] col) {
		Color minCol = Color.BLACK;
		double minDist = distance(col, black);
		double currDist = minDist;
		
		currDist = distance(col, white);
		if (currDist < minDist) {
			minCol = Color.WHITE;
			minDist = currDist;
		}
		
		currDist = distance(col, red);
		if (currDist < minDist) {
			minCol = Color.RED;
			minDist = currDist;
		}
		
		currDist = distance(col, blue);
		if (currDist < minDist) {
			minCol = Color.BLUE;
			minDist = currDist;
		}
		
		currDist = distance(col, green);
		if (currDist < minDist) {
			minCol = Color.GREEN;
			minDist = currDist;
		}
		
		currDist = distance(col, yellow);
		if (currDist < minDist) {
			minCol = Color.YELLOW;
			minDist = currDist;
		}
		
		currDist = distance(col, burgundy);
		if (currDist < minDist) {
			minCol = Color.BURGUNDY;
			minDist = currDist;
		}
		
		
		return minCol;
	}
}

enum Color {
	BLACK, WHITE, RED, BLUE, GREEN, YELLOW, BURGUNDY
}
