/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The class for a seeking bullet.
 * since the updating and movement of a seeking bullet 
 * is fairly different from a regular bullet, a subclass is used.
 */
package GameObjects;

import java.awt.Color;
import java.util.ArrayList;

public class SeekingBullet extends Bullet{
    final private static float ACCELERATION_RATE = (float)0.1;
    final private static float MAX_SPEED = (float)3.0;
     
    private float aX;//x acceleration
    private float aY;//y acceleration
    
    public SeekingBullet(float x, float y, int angle, boolean imaginary, Color color){
        super(x, y, angle, false, imaginary, color);//the "false" means the Bullet is not a fast bullet.
        
        //reset the x and y velocities (since the seeking bullet is slower than the regular one)
        setvX((float) (MAX_SPEED * Math.cos(Math.toRadians(angle))));
        setvY((float) (MAX_SPEED * Math.sin(Math.toRadians(angle))));
        //the initial acceleration is 0
        aX = 0;
        aY = 0;
    }
    
    
    //updates the bullet (mainly, make the bullet accelerate towards the Player)
    //returns whether or not the Bullet still exists (ex. if it hits a wall or player then it does not exist anymore)
    public boolean update(Player p, ArrayList<Wall> walls) {
        
        //dX is the x distance from the center of the player to this bullet
        float dX = p.getCenterX() - getX();
        //dY is the y distance from the center of the player to this bullet
        float dY = p.getCenterY() - getY();
        
        //the ratio between the rate of acceleration and the distance to the player.
        //using this ratio we can determine the x and y accelerations of the bullet.
        float ratio = ACCELERATION_RATE / ((float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2)));
        //calculate x and y accelerations
        aX = ratio * dX;
        aY = ratio * dY;
        
        //increase the velocity to accelerate the bullet
        setvX(getvX() + aX);
        setvY(getvY() + aY);
        
        //slow down if reached max speed
        float speed = (float) Math.sqrt(Math.pow(getvX(), 2) + Math.pow(getvY(), 2));
        if(speed > MAX_SPEED) {
            float speedRatio = MAX_SPEED / speed;
            setvX(getvX() * speedRatio);
            setvY(getvY() * speedRatio);
        }

        //call update method from Bullet class. this returns whether or not hte bullet still exists
        return super.update(walls);
        
    }
}
