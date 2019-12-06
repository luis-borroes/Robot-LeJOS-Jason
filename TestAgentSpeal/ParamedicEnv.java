// Environment code for project doctor2018

import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.logging.*;

public class ParamedicEnv extends Environment {
	
    public static final int GSize = 6; // The bay is a 6x6 grid
    public static final int HOSPITAL  = 8; // hospital code in grid model
    public static final int VICTIM  = 16; // victim code in grid model
	public static final int NONCRITICAL = 32;
	public static final int ROBOT = 64;

	public int currentangle = PathFinding.NORTH;
	public boolean going_hospital = false;
	
	public int counter = 0;
	public int robot_x = 0;
	public int robot_y = 0;
    private Logger logger = Logger.getLogger("testing"+ParamedicEnv.class.getName());
    
    // Create objects for visualising the bay.  
    // This is based on the Cleaning Robots code.
    private RobotBayModel model;
    private RobotBayView view;
	
	private MappingGateway map;
	private PCClient client;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        //addPercept(ASSyntax.parseLiteral("percept(demo)"));
        model = new RobotBayModel();
        view  = new RobotBayView(model);
        model.setView(view);
		
		map = new MappingGateway(new Coordinate(0, 0));
		map.setSize(new Coordinate(6, 6));
		
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        try {
        	if (action.getFunctor().equals("addVictim")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.addVictim(x,y);
                logger.info("adding victim at: "+x+","+y);
            } else if (action.getFunctor().equals("addObstacle")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.addObstacle(x,y);
                logger.info("adding obstacle at: "+x+","+y);
            } else if (action.getFunctor().equals("addHospital")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.addHospital(x,y);
                logger.info("adding hospital at: "+x+","+y);
				
			}else if (action.getFunctor().equals("move_towards_victim")) {
				model.move_towards_victim();

			} else if (action.getFunctor().equals("addNonCritical")) {
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
				model.addNonCritical(x, y);
				
			} else if (action.getFunctor().equals("go_to_hospital")) {
				going_hospital = true;
				model.tell_repaint();
				model.go_to_hospital();
				
			} else if (action.getFunctor().equals("add_robot")) {
				model.add_robot(0,0,ROBOT);	
			
			
			} else if (action.getFunctor().equals("remove_critical")) {
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
				model.remove_critical(x, y);
			} else if (action.getFunctor().equals("remove_non_critical")) {
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
				model.remove_non_critical(x, y);
			} else if (action.getFunctor().equals("remove_victim")) {
				int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
				model.removeVictim(x, y);
			}else if (action.getFunctor().equals("pickupnoncritical")) {
				pickupnoncritical();
			}else if (action.getFunctor().equals("startserver")) {
				start_server();
				
			} else if (action.getFunctor().equals("repaint")) {
				model.tell_repaint();
				
			} else if (action.getFunctor().equals("pickupnoncritical")) {
				pickupnoncritical();
			} else if (action.getFunctor().equals("finish")) {
				System.out.println("woooo we done");
				done();
            } else {
                logger.info("executing: "+action+", but not implemented!");
                return true;
                // Note that technically we should return false here.  But that could lead to the
                // following Jason error (for example):
                // [ParamedicEnv] executing: addObstacle(2,2), but not implemented!
                // [paramedic] Could not finish intention: intention 6: 
                //    +location(obstacle,2,2)[source(doctor)] <- ... addObstacle(X,Y) / {X=2, Y=2, D=doctor}
                // This is due to the action failing, and there being no alternative.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
           
        informAgsEnvironmentChanged();
        return true;       
    }
	
	public void seenVictim(String colour, int xpos, int ypos) {
		if (colour.equals("white")) {
			//addPercept(Literal.parseLiteral("location(" + "victim" + "," + 4 + "," + 3 + ")"));
			//informAgsEnvironmentChanged();
			model.removeVictim(xpos,ypos);
			addPercept(Literal.parseLiteral("noVictim("+xpos+","+ypos+")"));   //removes belief
		} else {
			System.out.println(colour + " " + xpos + " " + ypos);
			addPercept(Literal.parseLiteral("victim_found(" + colour + ", " + xpos + ", " + ypos + " )"));
		}
	}
	
	public void start_server() {
		client = new PCClient(this, map);
		client.start();
	}
	
	public void done() {
		client.done();
	}
	public void update_robot_position(int x, int y, int robot) {
		model.update_robot_position(x,y,robot);
	}
	
	public void pickupnoncritical() {
		client.go_to_noncriticals();
	}
	
	
	public void at_hospital() {
		
		System.out.println("at_hospital(" + counter + ")");
		going_hospital = false;
		model.tell_repaint();
		addPercept("paramedic",Literal.parseLiteral("at_hospital(" + counter + ")"));
		counter++;
	}
	
	public void at_non_critical(int xpos, int ypos) {
		
		addPercept("paramedic",Literal.parseLiteral("at_non_critical(" + xpos + "," + ypos +")"));

	}
	
	public void connected() {
		addPercept(Literal.parseLiteral("connected"));
	}

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
    
    // ======================================================================
    class RobotBayModel extends GridWorldModel {

        private RobotBayModel() {
            super(GSize, GSize, 1);	// The third parameter is the number of agents

			try {
				//setAgPos(0, 0, 0);
			} catch (Exception e) {
				e.printStackTrace();	
			}
            // initial location of Obstacles
            // Note that OBSTACLE is defined in the model (value 4), as
            // is AGENT (2), but we have to define our own code for the
            // victim and hospital (uses bitmaps, hence powers of 2)
        }
		
		void move_towards_victim() {
			System.out.println("Moving Towards Victim");
			//String colour = "cyan";
			//int X = 3;
			//int Y = 2;
			//addPercept(Literal.parseLiteral("victim_found(" + colour + ", " + X + ", " +Y + " )"));
			//seenVictim("white",1,5);
			
			client.goToNextVictim();
		}
		
		void go_to_hospital() {
			System.out.println("kejalksrjvnlksdnvlksjdnfvlkjdsfnvlkjndflkjvn");
			client.goToHospital();
			//addPercept(Literal.parseLiteral("at_hospital"));
		}
		

		

        
        // The JASON GridWorldView assumes that the origin is in the top left
        // hand corner, but all of the COMP329 descriptions assume 0,0 is in
        // the bottom left.  As we want to simply visualise the map, this fudge
        // ollows us to fix it.
        // It will probably cause issues elsewhere - SO DON'T USE THIS CLASS in
        // your solution!!!
        public int invertY(int x) { return (GSize-1)-x; }

		
		void addNonCritical(int x, int y) {
			client.add_non_critical(x,y);
			remove(VICTIM, x, invertY(y));
			add(NONCRITICAL, x, invertY(y));
			//ParamedicEnv.view.repaint();
			view.repaint();
		}
		
		void tell_repaint() {
			view.repaint();	
		}
		

		void remove_critical(int x, int y) {
			remove(VICTIM, x, invertY(y));
			view.repaint();
		}
		
				
		void remove_non_critical(int x, int y) {
			remove(NONCRITICAL, x, invertY(y));
			view.repaint();
		}
		
		void add_robot(int x, int y, int robot) {
			add(robot, x, invertY(y));
			
			System.out.println(robot);
			
			robot_x = x;
			robot_y = y;
			//view.repaint();
		}
		
		void update_robot_position(int x, int y, int heading) {
			remove(ROBOT, robot_x, invertY(robot_y));
			add(ROBOT, x, invertY(y));
			currentangle = heading;
			
			robot_x = x;
			robot_y = y;
			view.repaint();
		}
		
		
        void removeVictim(int x, int y) { 
			remove(VICTIM, x, invertY(y));
			view.repaint();
		}
		
        void addVictim(int x, int y) {
            add(VICTIM, x, invertY(y));
			map.addVictim(new Coordinate(x, y));
        }
        void addHospital(int x, int y) {
            add(HOSPITAL, x, invertY(y));
			map.setHospital(x, y);
        }
        void addObstacle(int x, int y) {
            add(OBSTACLE, x, invertY(y));
			map.getGridCell(x, y).setStatus(GridCellStatus.OCCUPIED);
        }

    }
    
    // ======================================================================
    // This is a simple rendering of the map from the actions of the paramedic
    // when getting details of the victim and obstacle locations
    // You should not feel that you should use this code, but it can be used to
    // visualise the bay layout, especially in the early parts of your solution.
    // However, you should implement your own code to visualise the map.
    class RobotBayView extends GridWorldView {

        public RobotBayView(RobotBayModel model) {
            super(model, "COMP329 6x6 Robot Bay", 300);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }
        
        
        /** draw application objects */
        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
            case ParamedicEnv.VICTIM:
                drawVictim(g, x, y);
                break;
            case ParamedicEnv.HOSPITAL:
                drawHospital(g, x, y);
                break;
			case ParamedicEnv.NONCRITICAL:
				drawNonCritical(g, x, y);
				break;
			case ParamedicEnv.ROBOT:
				drawRobot(g, x, y);
				break;
           }
        }
        
        public void drawVictim(Graphics g, int x, int y) {
            //super.drawObstacle(g, x, y);
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, "!");
        }

        public void drawHospital(Graphics g, int x, int y) {
            //super.drawObstacle(g, x, y);
            g.setColor(Color.green);
            drawString(g, x, y, defaultFont, "H");
        }
		
		public void drawNonCritical(Graphics g, int x, int y) {
			g.setColor(Color.orange);
			drawString(g, x, y, defaultFont, ":|");
		}
		
		public void drawRobot(Graphics g, int x, int y) {
			if(going_hospital) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.black);	
			}
			//g.setColor(Color.black);
			
			String current;
			switch (currentangle) {
				case (PathFinding.NORTH):
					current = "^";
					break;
					
				case (PathFinding.EAST):
					current = ">";
					break;
				
				case (PathFinding.SOUTH):
					current = "v";
					break;
				
				case (PathFinding.WEST):
					current = "<";
					break;
				
				default:
					current = "?";
					break;
			}
			
			drawString(g, x, y, defaultFont, current);
		}
		
    }
    // ======================================================================
}
