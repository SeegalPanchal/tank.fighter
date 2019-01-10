/*
    Seegal, Ian, Daniel
    June 13, 2017

    A pathfinder which takes the current x and y position (start position), and the target position (endX, endY)
    and a map of nodes, which will be used to determine a path. The pathfinder most important return is the
    path return which will return an arrayList of nodes that an object (in our game, enemy, or AI) has to traverse
    to reach the target. Diagonal movement is allowed, but not around walls, as you cannot diagonally move THROUGH a wall.
 */

package tankfighter;

import java.util.ArrayList;

public class Pathfinding {
    
    // http://gregtrowbridge.com/a-basic-pathfinding-algorithm/ 
    
    private int startX, startY, endX, endY;
    private Node start;
    private Node end;
    private Node map[][];

    private ArrayList<Node> queue;
    private ArrayList<Node> path;
    
    /**
     * the pathfinder constructor, which sets the variables and takes a map of obstacles that we want to travel through
     * @param startX start X position
     * @param startY start Y position
     * @param endX target X position
     * @param endY target Y position
     * @param map the map with obstacles you want to path around
     */
    public Pathfinding(int startX, int startY, int endX, int endY, Node[][] map) {
        queue = new ArrayList<>();
        path = new ArrayList<>();
        this.map = map;
        start = new Node(startX, startY);
        end = new Node(endX, endY);
        queue.add(start); // add the start node to the queue
        for (Node[] nodes : map) {
            for (Node node : nodes) {
                if(node != null){
                    node.setVisited(false);
                }
            }
        }
    }       
    
    /**
     * find path method which takes the next item in the queue, and checks all its neighbours
     */
    public void findPath() {
       // System.out.println("finding");
        while (queue.size() > 0) {
            //System.out.println("looping");
            //take the first location from the queue
            Node currentNode = queue.get(0);
            
            //remove the first location from the queue (essentially shift whole queue left 1)
            queue.remove(0);
            
            //check if we are done finding the path
         
            if (currentNode.equals(end)) {
                path.add(currentNode.getParent());
                //store the path to the path ArrayList
                while(path.size() > 0 && currentNode.getParent() != null && path.get(path.size()-1).getParent() != null) {
                    path.add(path.get(path.size()-1).getParent());
                }
                queue.clear(); // empty the array so while doesnt loop anymore
                  
            }
            //Find neighbours of current node
            checkNeighbours(currentNode);
            
        }
      //  System.out.println(path.size());
    }
    
    /**
     * expand to all of the neighbours and reference them to current node (basically path everywhere and record it)
     * @param current the node you want to check the neighbours of
     */
    public void checkNeighbours(Node current) {
        
        // grid x and y position
        int cellX = current.getCellX();
        int cellY = current.getCellY() - 1;
        
        
        // check North
        if (cellY-1 >= 0 // not off map
                && map[cellX][cellY-1] != null // not a wall
                && !map[cellX][cellY-1].isVisited()) { // not visited
            
            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX][cellY-1].setParent(current);
            map[cellX][cellY-1].setVisited(true);
            queue.add(map[cellX][cellY-1]);
            
        } 
        
        // check South
        if (cellY+1 < 12 // not off map
                && map[cellX][cellY+1] != null // not a wall
                && !map[cellX][cellY+1].isVisited()) { // not visited
            
            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX][cellY+1].setParent(current);
            map[cellX][cellY+1].setVisited(true);
            queue.add(map[cellX][cellY+1]);
        
        }
        
        // check East
        if (cellX+1 < 16 
                && map[cellX+1][cellY] != null 
                && !map[cellX+1][cellY].isVisited()) {
            
            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX+1][cellY].setParent(current);
            map[cellX+1][cellY].setVisited(true);
            queue.add(map[cellX+1][cellY]);
            
        }
        
        // check West 
        if (cellX-1 >= 0 // not off map
                && map[cellX-1][cellY] != null // not a wall
                && !map[cellX-1][cellY].isVisited()) { // not visited
            
            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX-1][cellY].setParent(current);
            map[cellX-1][cellY].setVisited(true);
            queue.add(map[cellX-1][cellY]);
            
        }
        
        // check North East
        if (cellX+1 < 16 && cellY-1 >= 0 // not off map
                && map[cellX+1][cellY-1] != null // not a wall
                && map[cellX][cellY-1] != null // not a wall
                && map[cellX+1][cellY] != null // not a wall
                && !map[cellX+1][cellY-1].isVisited()) { // not visited

            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX+1][cellY-1].setParent(current);
            map[cellX+1][cellY-1].setVisited(true);
            queue.add(map[cellX+1][cellY-1]);
            
        }
        
        
        // check South East
        if (cellX+1 < 16 && cellY+1 < 12 // not off map
                && map[cellX+1][cellY+1] != null // not a wall
                && map[cellX][cellY+1] != null // not a wall
                && map[cellX+1][cellY] != null // not a wall
                && !map[cellX+1][cellY+1].isVisited()) { // not visited

            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX+1][cellY+1].setParent(current);
            map[cellX+1][cellY+1].setVisited(true);
            queue.add(map[cellX+1][cellY+1]);
            
        }
        
        
        // check South West
        if (cellX-1 >= 0 && cellY+1 < 12 // not off map
                && map[cellX-1][cellY+1] != null // not a wall
                && map[cellX][cellY+1] != null // not a wall
                && map[cellX-1][cellY] != null // not a wall
                && !map[cellX-1][cellY+1].isVisited()) { // not visited

            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX-1][cellY+1].setParent(current);
            map[cellX-1][cellY+1].setVisited(true);
            queue.add(map[cellX-1][cellY+1]);
            
        }
        
        // check North west
        if (cellX-1 >= 0 && cellY-1 >= 0 // not off map
                && map[cellX-1][cellY-1] != null // not a wall
                && map[cellX-1][cellY] != null // not a wall
                && map[cellX][cellY-1] != null // not a wall
                && !map[cellX-1][cellY-1].isVisited()) { // not visited
            
            // if the node is available, add it to queue, and set the parent as the current node
            // basically path to this node if its available and set it as the next node to check neighbours of
            map[cellX-1][cellY-1].setParent(current);
            map[cellX-1][cellY-1].setVisited(true);
            queue.add(map[cellX-1][cellY-1]);
            
        }
    }
    
    // returns the path the algorithm finds
    public ArrayList<Node> getPath() {
        return path;
    }

    //Getters and Setters
    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }
    
}
