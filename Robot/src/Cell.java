
public class Cell {

    public boolean reachable;
    public int x;
    public int y;
    public Cell parent;
    public int g;
    public int h;
    private int f;
    private String direction;


    public Cell(int x_in, int y_in, boolean reachable_in) {
        reachable = reachable_in;
        x = x_in;
        y = y_in;
        g = 0;
        h = 0;
        f = 0;
        direction = "";
    }


    public int getf(){
        return f;
    }

    public void setf(int in_f){
        f = in_f;
    }

    public int getx() {
        return x;
    }

    public int gety() {
        return y;
    }

    public Cell getparent(){
        return parent;
    }

    public void setparent(Cell in_parent) {
        parent = in_parent;
    }

    public int getg() {
        return g;
    }

    public int geth(){
        return h;
    }

    public void setg(int in_g){
        g = in_g;
    }

    public void seth(int in_h) {
        h = in_h;
    }

    public Boolean is_reachable() {
        return reachable;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}