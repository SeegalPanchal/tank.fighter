/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The class for a bullet (shot by a tank).
 */
package GameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import tankfighter.GameStateHandler;

public class Bullet extends GameObject{
    
    
    /////VARIABLE DECLARATIONS/////
    
    //final class variables (constants)
    final static public float DEFAULT_SPEED = 3;//pixels/frame
    final static public float DEFAULT_FAST_SPEED = (float)4.8;//pixels/frame. The speed of a fast bullet (some enemies shoot fast ones)  
    final static public int DIAMETER = 10;
    final static public int RADIUS = DIAMETER/2;
    //frames until the bullet can kill something (to avoid a character killing themself when they shoot out a bullet)
    final static public int START_KILL_FRAME = 8;
    //the number of frames that the bullet lasts until it explodes.
    final private static int LIFETIME = 300;    

    
    private float vX;  //x velocity
    private float vY;  //y velocity
    private int angle; //angle, in degrees, of the bullet's direction
    private int life;  //number of frames since the bullet was created (used for "explosion" animation and to avoid the shooter killing themself)
    private Color color;
    //if the bullet is imaginary. Enemies shoot imaginary bullets to test if there is a clear path to the player.
    private boolean imaginary;
    
    /**
     * Constructor.
     * @param x the x coordinate of the bullet's center
     * @param y the y coordinate of the bullet's center
     * @param angle the angle, in degrees, in which the bullet velocity is pointing
     * @param color the Bullet's color
     */
    public Bullet(float x, float y, int angle, Color color) {
        //call contructor from GameObject (initialises position)
        super(x, y);
        //calculate x and y velocities based on its speed and direction
        this.vX = (float)(DEFAULT_SPEED*Math.cos(Math.toRadians(angle)));
        this.vY = (float)(DEFAULT_SPEED*Math.sin(Math.toRadians(angle)));
        //set color to gray
        this.color = color.darker();
        //set the number of frames since this bullet has been created to 
        life = 0;
        //the default is that the bullet is not imaginary
        imaginary = false;

    }
    /**
     * Constructor, for creating a bullet,
     * including the boolean to determine if it fast or not and if it is imaginary or not
     * @param x the x coordinate of the bullet's center
     * @param y the y coordinate of the bullet's center
     * @param angle the angle, in degrees, in which the bullet velocity is pointing
     * @param fast whether or not the Bullet is a fast bullet
     * @param imaginary whether or not the bullet is imaginary
     * @param color the Bullet's color
     */
    public Bullet(float x, float y, int angle, boolean fast, boolean imaginary, Color color) {
        this(x, y, angle, color);
        if(fast){
            //recalculate vX and vY for faster speed
            this.vX = (float)(DEFAULT_FAST_SPEED*Math.cos(Math.toRadians(angle)));
            this.vY = (float)(DEFAULT_FAST_SPEED*Math.sin(Math.toRadians(angle)));
        }
        this.imaginary = imaginary;
    }
    
    /**
     * Draws the Bullet
     * @param g the Graphics2D Object to use to draw
     */
    @Override
    public void draw(Graphics2D g) {
        //if the bullet cannot kill yet (when it is just shot out of the tank), it is drawn as an "explosion"
        if(life < START_KILL_FRAME){
            //the "explosion" size expands then contracts.
            //therefore we take the time between the "middle" time of the explosion 
            //and the current time to calculate the current size of the explosion
            int currDiameter = 5*(4 - (Math.abs(life - 4)));
            int currRadius = currDiameter/2;
            
            //outer circle of the explosion is red
            g.setColor(Color.RED);
            g.fillOval((int)getX() - currDiameter, (int)getY() - currDiameter, currDiameter*2, currDiameter*2);
            //inner circle of the explosion is yellow
            g.setColor(Color.YELLOW);
            g.fillOval((int)getX() - currRadius, (int)getY()  - currRadius, currDiameter, currDiameter);
        }else if(LIFETIME - life < 8){
            //when the bullet is about to be removed it also is drawn as an explosion
            int currDiameter = 5*(4 - (Math.abs((LIFETIME - life) - 4)));
            int currRadius = currDiameter/2;
            //draw outer circle of explosion
            g.setColor(Color.RED);
            g.fillOval((int)getX() - currDiameter, (int)getY() - currDiameter, currDiameter*2, currDiameter*2);
            //draw inner circle of explosion            
            g.setColor(Color.YELLOW);
            g.fillOval((int)getX() - currRadius, (int)getY()  - currRadius, currDiameter, currDiameter);
        }
        //if the bullet is not just shot out or about to be removed, draw it as a "normal" bullet
        else{
            g.setColor(color);
            g.fillOval((int)getX()  - RADIUS, (int)getY() - RADIUS, DIAMETER, DIAMETER);        
        }
    }
    /**
     * Updates the Bullet's position and life.
     */
    @Override
    public void update() {
        setX(getX() + vX);
        setY(getY() + vY);
        life++;
    }
    /**
     * Updates the bullet, including bouncing off of walls and checking if it has reached the end of its lifetime.
     * @param walls the ArrayList of Walls in the current level
     * @return true if the Bullet still exits, false otherwise
     */
    public boolean update(ArrayList<Wall> walls){
        //update position and life
        this.update();
        
        //check for collisions with walls
        //to resolve these collisions, we must find the wall that the Bullet first collided with
        //because it may be "in between" 2 walls.
        //maxDistanceFromCollision2 is square of the distance between the bullet's current position and the collision position.
        //for example, if the bullet is in between 2 walls 
        //and is 5 pixels from one collision point and 8 pixels from the other collision point,
        //then the collision point 8 pixels is the first collision therefore it ignores the 5-pixel-away collision.
        double maxDistanceFromCollision2 = 0;
        //store the new positions.
        //we cannot directly change the position and velocity when testing each wall 
        //because we may find another wall later that the bullet collided with first
        float newX = 0;
        float newY = 0;
        boolean swapVX = false;
        boolean collidedWithAny = false;
        for (Wall currWall : walls) {
            //if true, it has collided with a Wall
            if (getX()  + RADIUS > currWall.getX() && getX()  - RADIUS < currWall.getX() + GameStateHandler.BLOCK_WIDTH
                    && getY()  + RADIUS > currWall.getY() && getY()  - RADIUS < currWall.getY() + GameStateHandler.BLOCK_HEIGHT) {
                if(currWall.isBreakable() && !imaginary){
                    walls.remove(currWall);
                    return false;
                }
                //seeking bullets cannot bounce off walls so we remove it if it hits a wall
                if(this instanceof SeekingBullet){
                    return false;
                }
                //if moving vertically
                if(vX == 0){
                    vY = -vY;
                    break;
                }
                //if moving horizontally
                else if(vY == 0){
                    vX = -vX;
                    break;
                }
                
                collidedWithAny = true;
                
                //the current collision with the wall's distance from the current bullet position, all squared
                double distanceFromCollision2;
                
                float wallBottom = currWall.getY() + GameStateHandler.BLOCK_HEIGHT;
                float wallRight = currWall.getX() + GameStateHandler.BLOCK_WIDTH;
                
                //calculate intercepts. These are where the bullet hit (or would have hit) the wall.
                //By calculating the distance from the wall and the intercept, we can find the resulting velocity of the bounced bullet.
                //rightIntercept is the y coordinate of where the right side of the bullet hits (or would have hit) the wall                
                float rightIntercept = (vY/vX)*((currWall.getX() - RADIUS) - getX()) + getY();
                //leftIntercept is the y coordinate of where the left side of the bullet hits (or would have hit) the wall                                
                float leftIntercept = (vY/vX)*((wallRight + RADIUS) - getX()) + getY();
                //topIntercept is the x coordinate of where the top of the bullet hits (or would have hit) the wall                               
                float topIntercept = (vX/vY)*(wallBottom + RADIUS - getY()) + getX();
                //bottomIntercept is the x coordinate of where the bottom of the bullet hits (or would have hit) the wall                               
                float bottomIntercept = (vX/vY)*(currWall.getY()  - RADIUS - getY()) + getX();

                if(vX > 0 && vY > 0){//moving down-right
                    if(rightIntercept - currWall.getY() > bottomIntercept - currWall.getX()){//hit left side of wall
                        distanceFromCollision2 = Math.pow(getX() + RADIUS - currWall.getX(), 2) + Math.pow(getY() - rightIntercept, 2);
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newX = currWall.getX() - RADIUS;
                            newY = rightIntercept;
                            swapVX = true;
                        }
                        
                    }else{//hit top of wall
                        distanceFromCollision2 = Math.pow(getX() - bottomIntercept, 2) + Math.pow(getY() + RADIUS - currWall.getY(), 2);                        
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newY = currWall.getY() - RADIUS;
                            newX = bottomIntercept;
                            swapVX = false;
                        }
                    }
                }else if(vX < 0 && vY > 0){//moving down-left
                    if(leftIntercept - currWall.getY() > wallRight - bottomIntercept){//hit right side of wall
                        distanceFromCollision2 = Math.pow(wallRight-(getX() - RADIUS), 2) + Math.pow(getY() - leftIntercept, 2);                        
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newX = wallRight + RADIUS;
                            newY = leftIntercept;
                            swapVX = true;
                        }
                    }else{//hit top of wall
                        distanceFromCollision2 = Math.pow(bottomIntercept - getX(), 2) + Math.pow(getY() + RADIUS - currWall.getY(), 2);                        
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newY = currWall.getY() - RADIUS;
                            newX = bottomIntercept;
                            swapVX = false;
                        }
                    }
                }else if(vX > 0 && vY < 0){//moving up-right
                    if((wallBottom) - rightIntercept > topIntercept - currWall.getX()){//hit left side of wall
                        distanceFromCollision2 = Math.pow((getX() + RADIUS) - currWall.getX(), 2) + Math.pow(rightIntercept - getY(), 2);                        
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newX = currWall.getX() - RADIUS;
                            newY = rightIntercept;
                            swapVX = true;
                        }
                      
                    }else{//hit bottom of wall
                        distanceFromCollision2 = Math.pow(getX() - topIntercept, 2) + Math.pow(wallBottom - (getY() - RADIUS), 2);                        
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newY = wallBottom + RADIUS;
                            newX = topIntercept;
                            swapVX = false;
                        }
                    }
                }else if(vX < 0 && vY < 0){//moving up-left
                    if(wallBottom - leftIntercept > wallRight - topIntercept){//hit right side of wall
                        distanceFromCollision2 = Math.pow(wallRight - (getX() - RADIUS), 2) + Math.pow(leftIntercept - getY(), 2);                    
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newX = wallRight + RADIUS;
                            newY = leftIntercept;
                            swapVX = true;
                        }
                    }else{//hit bottom of wall
                        distanceFromCollision2 = Math.pow(topIntercept - getX(), 2) + Math.pow(wallBottom - (getY() - RADIUS), 2);                    
                        if(distanceFromCollision2 > maxDistanceFromCollision2){
                            maxDistanceFromCollision2 = distanceFromCollision2;
                            newY = wallBottom + RADIUS;
                            newX = topIntercept;
                            swapVX = false;
                        }
                    }
                }
            }
        }
        //if the bullet has collided with at least 1 wall, update its position and velocity
        if(collidedWithAny){
            setX(newX);
            setY(newY);
            if(swapVX){
                vX = -vX;
            }
            else {
                vY = -vY;
            }
        }
        //if the bullet has lasted the max lifetime, return false 
        //(it will be removed from the bullets arraylist in the MainGame)
        if(life > LIFETIME) return false;
        //otherwise return true
        return true;
    }
    
    //getters
    public int getLife() {
        return life;
    }
    public float getvX() {
        return vX;
    }
    public float getvY() {
        return vY;
    }
    public int getAngle() {
        return angle;
    }
    
    //setters
    public void setvX(float vX) {
        this.vX = vX;
    }
    public void setvY(float vY) {
        this.vY = vY;
    }
    public void setAngle(int angle) {
        this.angle = angle;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    
    
}
