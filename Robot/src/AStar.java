
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.*;

public class AStar {

    Comparator<Cell> cellFComparator = new Comparator<Cell>(){
            @Override
            public int compare(Cell f1, Cell f2) {
                return f1.getf() - f2.getf();
            }
    };
    PriorityQueue<Cell> opened = new PriorityQueue<>(100, cellFComparator);
    List<Cell> closed = new ArrayList<Cell>();
    List<Cell> cells = new ArrayList<Cell>();
    Cell start;
    Cell end;
    int grid_height;
    int grid_width;
    int[][] maze;

    public AStar(int[][] maze_in, int sx, int sy, int ex, int ey, char dir) {
        maze = maze_in;
        init_grid(maze_in);
        start = get_cell(sx,sy);
        end = get_cell(ex,ey);
        String dirr = "" + dir;
        start.setDirection(dirr.toUpperCase());
    }



    public void init_grid(int[][] maze) {
        boolean reachable = true;
        grid_height = maze.length;
        grid_width = maze[0].length;
        for(int x = 0; x < grid_width; x++) {
            for(int y = 0; y < grid_height; y++) {

                if(maze[y][x] <= -1) {
                    reachable = false;
                }
                Cell cell_temp = new Cell(x, y, reachable);
                cells.add(cell_temp);
                reachable = true;
            }
        }
    }

    //Calculate the heuristic (which is calculated from the distance to the
    //end point) this is needed for A*
    public int get_heuristic(Cell cell) {
        int return_val = 10 * (Math.abs(cell.getx() - end.getx()) + Math.abs(cell.gety() - end.gety()));
        return return_val;
    }


    //Given the x and y coordinates return the cell that should be at that
    //position
    public Cell get_cell(int x, int y) {
        return cells.get(x * grid_height + y);
    }


    public List<Cell> get_adjacent_cells(Cell cell) {
        List<Cell> cell_list = new ArrayList<Cell>();

        if (cell.getx() < grid_width - 1) {
            cell_list.add(get_cell(cell.getx() + 1, cell.gety()));
        }
        if (cell.gety() > 0) {
            cell_list.add(get_cell(cell.getx(), cell.gety() - 1));
        }
        if (cell.getx() > 0) {
            cell_list.add(get_cell(cell.getx() - 1, cell.gety()));
        }
        if(cell.gety() < grid_height -1) {
            cell_list.add(get_cell(cell.getx(), cell.gety() + 1));
        }

        return cell_list;
    }
//0631
    public String display_path() {
        String to_return = "";
        Cell cell = end;
        Stack<String> direction_queue = new Stack<>();

        while (cell != start){
            direction_queue.push(get_direction(cell.parent, cell));
            cell = cell.parent;
        }

        int length = direction_queue.size();

        for(int i = 0; i < length; i++) {
            to_return = to_return + direction_queue.pop();
        }

        return to_return;

    }

    public void update_cell(Cell adj, Cell cell) {
        //Update adjacent cell
        int g_inc = 10;
        if (direction_changed(adj,cell)) {
            g_inc += 10;
        }
        adj.setg(cell.getg() + g_inc);
        adj.seth(get_heuristic(adj));
        adj.setparent(cell);
        adj.setDirection(cell.getDirection());
        adj.setf(adj.geth() + adj.getg());

    }

    public Boolean direction_changed(Cell adj, Cell cell) {
        String direction = cell.getDirection();
        String future_move = get_direction(cell, adj);

        if((direction.equals("S")) || (direction.equals("N"))) {
            if((future_move.equals("S")) || (future_move.equals("N"))) {
                return false;
            }
        } else {
            if(future_move.equals("E") || (future_move.equals("W"))) {
                return false;
            }
        }
        return true;
    }


    public String get_direction(Cell parent, Cell child) {
       int parent_x = parent.getx();
       int parent_y = parent.gety();

       int child_x = child.getx();
       int child_y = child.gety();

       if(child_x == parent_x -1  ) {
           return "W";
       }else if (child_x == parent_x + 1) {
           return "E";
       }else if (child_y == parent_y - 1) {
           return "N";
       } else if (child_y == parent_y + 1) {
           return "S";
       }
       return "s";
    }

    public String process() {
       // Path path_add = new Path(opened, start.getf(), start);
        //heapq.put(opened, start);
        Boolean running = true;
        opened.add(start);

        while(true) {

            if(opened.size() == 0) {
                running = false;
                break;
            }

            Cell heappop = opened.poll(); //heapq.get(opened);

            closed.add(heappop);

            if (heappop == end) {
                break;
            }

            List<Cell> adj_cells = get_adjacent_cells(heappop);

            for (int i = 0; i < adj_cells.size(); i++) {
                Cell curr_cell = adj_cells.get(i);
                if ((curr_cell.is_reachable()) && (!closed.contains(curr_cell))) {
                    if (opened.contains(curr_cell)) {
                        if (curr_cell.getg() > heappop.getg() + 10) {
                            update_cell(curr_cell, heappop);
                        }
                    } else {
                        update_cell(curr_cell, heappop);
                        opened.add(curr_cell);
                    }
                }
            }
        }

            //cell
        if(running){
            return(display_path().toLowerCase());

        } else {
            return("");
        }
    }


}