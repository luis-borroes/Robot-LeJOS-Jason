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
	public static int[][]map = {
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,0}};
	static Socket sock;
	static GUI g;
	public static JFrame frame;
	public static JButton[][] btns;
    public static void buildgui() throws IOException {

		
    	g = new GUI();
		
		 
		/*
    	String ip = "192.168.70.161"; 
    	
		if(args.length > 0)
			ip = args[0];
		sock = new Socket(ip, 1234);
		System.out.println("Connected");
		while (true) {
			
		InputStream in = sock.getInputStream();
		DataInputStream dIn = new DataInputStream(in);
		String str = dIn.readUTF();
		
		
    	System.out.println(str);
    	//updates the map
    	
    	String smap ="";
    	int c =0;
    	
    	if (smap.length()!=0 && !str.equals(smap) && !allMap()) {
    		
    		smap = str;
    		String[] smapp = smap.split("a");
    		for (int a = 0; a < 7 ; a++) {
    			for (int b=0; b <6 ; b++) {
    			
    				map[a][b] = Integer.parseInt(smapp[c]);
    				c++;
    				allMap();
    			}
    		}
    	}
    	sock.close();
		}
		*/
    			
    		
    }
    
    	
    public static void updateMap() {
    	int column = map[0].length;
    	int rows = map.length;
    	
    	for (int i = 0; i < rows; i++){         // iterates each student
            for (int j = 0; j < column; j++){  // iterates each grade
                // do something with grade[i][j]
            	String Number = Integer.toString(PCClient.mapp[i][j]);
            	if (Number.equals("85")) {
            		btns[i][j].setText("^");
            		
            	} else if (Number.equals("86")) {
            		btns[i][j].setText(">");
            		
            	} else if (Number.equals("87")) {
            		btns[i][j].setText("<");
            		
            	}
            	else if (Number.equals("88")) {
            		btns[i][j].setText("v");
            		
            	}else {
            	
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
		for (int a = 0; a < map.length ; a++) {
    		for (int b=0; b <map[0].length ; b++) {
    			if (map[a][b] == 0)
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
        	int column = map[0].length;
        	int rows = map.length;
        	
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

