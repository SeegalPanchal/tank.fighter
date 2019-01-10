/*
    Seegal, Ian, Daniel
    June 13, 2017
    
    An object used to define a square, 50*50 area on the game, referred to as a node.
    Each node has its position, and a reference to a previous node. The reference allows 
    pathfinding to chain nodes together, then return the chain of nodes, which we refer to as a path.
 */
package tankfighter;

public class Node {
    private int x, y, cellX, cellY;
    private Node parent = null;
    private boolean visited = false; // check if the current node has been visited during the pathing sequence
    
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        cellX = (int)Math.round((double)x/50.0); // cell number 'x' (column number)
        cellY = (int)Math.round((double)y/50.0); // cell number 'y' (row number)
    }
 
    /**
     * 
     * @param endX the goal x value
     * @param endY the goal y value
     * @return  the euclidian distance between the block
     */
    int getDist(int endX, int endY) {
        return (int)Math.sqrt(Math.pow(Math.abs(endX - this.x), 2) + Math.pow(Math.abs(endY - this.y), 2));
    }
    
    public boolean equals(Node n) {
        return n.getCellX() == cellX && n.getCellY() == cellY; // if the x and y value (position of node) is the same, then they are equal
    }
    
    // getters and setters
    
    // retursn if node has been visited
    public boolean isVisited() {
        return visited;
    }

    // sets this node as visited
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    // returns the grid x position
    public int getCellX() {
        return cellX;
    }

    // sets the grid y position
    public void setCellX(int cellX) {
        this.cellX = cellX;
    }

    // returns the grid y position
    public int getCellY() {
        return cellY;
    }

    // sets the grid y position
    public void setCellY(int cellY) {
        this.cellY = cellY;
    }
    
    // set the reference of a previous node
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    // reference to previous node (use this as a chain connection in pathing)
    public Node getParent() {
        return parent;
    }
    
    // return the x position 
    public int getX() {
        return x;
    }

    // set x position
    public void setX(int x) {
        this.x = x;
    }

    // return y position
    public int getY() {
        return y;
    }

    // set y position
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * overrides the standard toString method
     * @return a string that prints the grid x and grid y position
     */
    public String toString(){
        return "[" + cellX + ", " + cellY + "]";
    }
    
    // creates a new node and returns it with the same x and y position
    public Node clone(){
        return new Node(x, y);
    }
}
