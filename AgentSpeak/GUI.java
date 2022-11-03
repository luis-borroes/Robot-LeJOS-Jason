import java.awt.EventQueue;
import java.awt.GridLayout;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;
import java.io.*;
import java.net.*;
import javax.swing.*;

import java.awt.event.*;


public class GUI {

	static Socket sock;
	static GUI g;
	public static JFrame frame;
	public static JButton[][] btns;
    public static void buildgui() throws IOException {

		
    	g = new GUI();
		
		 
    	//String ip = "192.168.70.161"; 

    			
    		
    }
    
    	
    public static void updateMap() {
    	int column = PCClient.mapp[0].length;
    	int rows = PCClient.mapp.length;
    	
    	for (int i = 0; i < rows; i++){         // iterates each student
            for (int j = 0; j < column; j++){  // iterates each grade
                // do something with grade[i][j]
            	String Number = Integer.toString(PCClient.mapp[i][j]);
				
            	if (PCClient.pos.equals(new Coordinate(j, i))) {
					if (PCClient.direction == 'n') {
						btns[i][j].setText("^");
						
					} else if (PCClient.direction == 'e') {
						btns[i][j].setText(">");
						
					} else if (PCClient.direction == 'w') {
						btns[i][j].setText("<");
						
					}
					else if (PCClient.direction == 's') {
						btns[i][j].setText("v");
					}
					
				} else {
            	
            		btns[i][j].setText("" + Number);
            	}
            	if (PCClient.mapp[i][j] == 0) {
            		btns[i][j].setBackground(Color.white);
            	}else if (PCClient.mapp[i][j] >= 1) {
            		btns[i][j].setBackground(Color.GREEN);
            	}else if (PCClient.mapp[i][j] <= -1) {
            		btns[i][j].setBackground(Color.RED);
            	}
            }
        }
    }
    
	
	public static boolean allMap() {
		for (int a = 0; a < PCClient.mapp.length ; a++) {
    		for (int b=0; b < PCClient.mapp[0].length ; b++) {
    			if (PCClient.mapp[a][b] == 0)
    			{
    				return false;
    			}
    		}
		}
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
    public GUI() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                frame = new JFrame("Testing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
   

    public class TestPane extends JPanel {

        public TestPane() {
        	int column = PCClient.mapp[0].length;
        	int rows = PCClient.mapp.length;
        	
            setLayout(new GridLayout(rows, column));
            
            btns = new JButton[rows][column];
            

            for (int i = 0; i < rows; i++){         // iterates each student
                for (int j = 0; j < column; j++){  // iterates each grade
                    // do something with grade[i][j]
                	
                	String Number = Integer.toString(PCClient.mapp[i][j]);
                	btns[i][j] = new JButton(" " + Number);
                	if (PCClient.mapp[i][j] == 0) {
                		btns[i][j].setBackground(Color.white);
                	}else if (PCClient.mapp[i][j] >= 1) {
                		btns[i][j].setBackground(Color.GREEN);
                	}else if (PCClient.mapp[i][j] <= -1) {
                		btns[i][j].setBackground(Color.RED);
                	}
                	add(btns[i][j]);
                }
            }
            
        }

    }

}

