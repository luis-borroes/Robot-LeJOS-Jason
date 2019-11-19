import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Window extends JFrame {

    Panel panel;
    ArrayList<JLabel> labels;
    MappingGateway map;

    public Window() {
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280,768);
        getContentPane().setBackground(java.awt.Color.getHSBColor((float) 42 / 360, 0.05f, 0.95f));
        // Update it live
        // Show the robot's position, heading, the path it's taking, show information on the side
//        java.awt.Color.get
        setLayout(new GridLayout(7, 6));

        map = new MappingGateway(new Coordinate(0, 0));
        
        labels = new ArrayList<JLabel>();
        
        for (int i = 0; i < 42; i++) {
        	labels.add(new JLabel(parseInfo(i)));
        	add(labels.get(i));
        }
    }
    
    void repaintLabels() {
    	for (int i = 0; i < 42; i++) {
    		labels.get(i).setText(parseInfo(i));
        	labels.get(i).repaint();
        }
    }
    
    Coordinate indexToCoordinate(int index) {
    	int x = index % 6;
    	int y = index / 6;
    	
    	return new Coordinate(x, 6 - y);
    }
    
    String parseInfo(int index) {
    	Coordinate coordinate = indexToCoordinate(index);
    	if (coordinate.getHash() == map.getCurrentPosition().getHash()) {
    		return "<html><font color=\"#CD7817\"> Robot is here! </font></html>";
    	}
    	GridCell cell = map.getGridCell(coordinate);
    	
    	String info = "<html>";
    	info += getColor(cell);
    	info += "Cell (" + coordinate.x + ", " + coordinate.y + ") is";
    	info += "<br/>";
    	info += parseStatus(cell);
    	info += "</font></html>";
    	return info;
    }
    
    String getColor(GridCell cell) {
    	if (cell.getStatus() == GridCellStatus.OCCUPIED) {
    		return "<font color=\"#AC1900\">";
    	} else if (cell.getStatus() == GridCellStatus.UNCERTAIN) {
    		return "<font color=\"#A68464\">";
    	} else if (cell.getStatus() == GridCellStatus.UNKNOWN) {
    		return "<font color=\"#A68464\">";
    	} else if (cell.getStatus() == GridCellStatus.UNOCCUPIED) {
    		return "<font color=\"#99AA00\">";
    	} else {
    		return "<font color=\"black\">";
    	}
    }
    
    String parseStatus(GridCell cell) {
    	if (cell.getStatus() == GridCellStatus.OCCUPIED || cell.getStatus() == GridCellStatus.UNOCCUPIED) {
    		String status = cell.getProbability() + "%" + " likely to be " + cell.getStatus().name() + "<br />";
    		status += "m = " + cell.getM() + "<br />c = " + cell.getCount();
    		return status;
    	} else {
    		return cell.getStatus().name();
    	}
    }
}