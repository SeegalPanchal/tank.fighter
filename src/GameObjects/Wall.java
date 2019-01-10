/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * A class representing a Wall object. The wall is impassable terrain. 
    There are two types of walls, breakable, and unbreakable. 
    The wall is created when " - "'s are read from the data file.
 */
package GameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import tankfighter.GameStateHandler;

public class Wall extends GameObject{
    private boolean breakable;
    private Color color;
    
    //this must be public because we are filling the row above this row in the Level with this color
    public static Color DEFAULT_COLOR = new Color(100, 100, 100);//gray
    private static Color BREAKABLE_COLOR = new Color(100, 100, 100, 80);
    
    /**
     * The constructor to intialize a wall
     * @param x x position of the wall
     * @param y y position of the wall
     */
    public  Wall(float x, float y) {
        //call contructor from GameObject (initialises position)
        super(x, y);
        breakable = false;
        this.color = DEFAULT_COLOR;
    }
    
    // chained constructor with includes a boolean determining if the wall is breakable or not
    public  Wall(float x, float y, boolean breakable) {
        this(x, y);
        if(breakable){ // if true that the wall is breakable
            this.breakable = true; // set breakable
            this.color = BREAKABLE_COLOR; // light gray is breakable color
        }
    }
    @Override
    /**
     * draw the wall 
     */
    public void draw(Graphics2D g) { 
        g.setColor(color);
        g.fillRect((int)getX(), (int)getY(), GameStateHandler.BLOCK_WIDTH, GameStateHandler.BLOCK_HEIGHT);
    }
    
    
    @Override
    // update method required as it is a game object, but our walls don't have health or move
    // so they dont need to be updated
    public void update() {
        
    }
    
    /**
     * creates a clone of the wall (isn't really needed in our game), might have some use in future
     * @return a wall with same x and y and if its breakable or not
     */
    public Wall clone(){
        return new Wall(getX(), getY(), breakable);
    }

    /**
     * Check if the wall is breakable
     * @return true: wall is breakable, false: wall is not breakable
     */
    public boolean isBreakable() {
        return breakable;
    }
}
