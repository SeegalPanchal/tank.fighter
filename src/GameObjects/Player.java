/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The class for a player. Stores the position of the player and takes the users reponse (if the user wants to move, etc.)
    Has the update method (takes the state of the game, 
    takes the players response: keys pressed, etc... and updates the position of the tank)
 */
package GameObjects;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class Player extends Tank{//Player is a subclass of Tank
    
    private static final float MAX_SPEED = 2;//pixels/frame
    
    //variables to store the mouse's position (to know where to point the turret at)
    private int mouseX;
    private int mouseY;
    //variables to store which buttons are pressed (for movement)
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean downPressed;
    //the number of frames since a bullet was last shot.
    //used to allow a cooldown time.
    private int framesSinceShot;
    
    private static final int COOLDOWN_TIME = 30;//frames until the player can shoot again after shooting a bullet
    
    /**
     * 
     * @param x x position of player
     * @param y y position of player
     * @param angle angle they are aiming at
     */
    public Player(float x, float y, int angle) {
        //call contructor from GameObject (initialises position)
        super(x, y, angle);
        this.bodyColor = new Color(60, 200, 20);
        this.turretColor = bodyColor.darker();
        
        // intially, pretend they havent pressed anything, to determine a change in state
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;
        //subtract 1 from cooldown so the player cannot shoot during
        //"Ready Set Fight" frames but they can shoot immediately after this
        framesSinceShot = COOLDOWN_TIME - 1;
        maxSpeed = MAX_SPEED;//set this Tank's maximum speed to the default max speed for a Player
    }
    
    // draw the player tank
    @Override
    public void draw(Graphics2D g) {
        //call draw method from Tank class
        super.draw(g);
    }

    // update the player
    @Override
    public void update() {
        
        // a cooldown timer, basically check how many frames since player has last shot, 
        // and if the number is high enough,
        // they can shoot again
        framesSinceShot++;
        
        //update the player's velocity according to the keys pressed
        
        //horizontal motion
        if(leftPressed)vX = -MAX_SPEED;
        else if(rightPressed) vX = MAX_SPEED;
        else vX = 0;
        
        //vertical motion
        if(upPressed) vY = -MAX_SPEED;
        else if(downPressed) vY = MAX_SPEED;
        else vY = 0;
        
        
        //call update method from Tank class
        super.update();
        //point the turret in the direction of the mouse
        pointTurret(mouseX, mouseY);
    }
    
    //checks for keys pressed and updates variables accordingly
    /**
     * 
     * @param e key pressed? which one was pressed?
     */
    public void keyPressed(KeyEvent e){
        double key = e.getKeyCode();
        // if the user has pressed left or A
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            // definitely not moving right
            // moving left
            leftPressed = true;
            rightPressed = false;
        }else if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D){
            // definitely not moving left
            // moving right
            rightPressed = true;
            leftPressed = false;
        }else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W){
            // definitely not moving down
            // moving up
            upPressed = true;
            downPressed = false;
        }else if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S){
            // definitely not moving up
            // moving down
            downPressed = true;
            upPressed = false;
        }
    }
    //checks for keys released and updates variables accordingly
    public void keyReleased(KeyEvent e){
        double key = e.getKeyCode();
        // if the key is released, you arent moving in that direction anymore, so set the movement booleans to false
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            leftPressed = false;
        }else if(key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D){
            rightPressed = false;
        }else if(key == KeyEvent.VK_UP || key == KeyEvent.VK_W){
            upPressed = false;
        }else if(key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S){
            downPressed = false;
        }
    }
    
    //record new mouse position if the mouse has moved (to point turret)
    public void mouseMoved(int[] newPos){
        mouseX = newPos[0];
        mouseY = newPos[1];
    }
    
    //clones the Player
    public Player clone(){
        return new Player(getX(), getY(), bodyAngle);
    }
    
    //returns a Bullet that the Player would shoot
    public Bullet shootBullet() {
        //if the cooldown time has not passed, do not shoot
        if(framesSinceShot < COOLDOWN_TIME)return null;
        //otherwise set the last time the player has shot to right now
        //and return the Bullet shot
        framesSinceShot = 0;
        return super.shootBullet(false);//false because the bullets is not imaginary
    }
    
}
