/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * The major tank class with the size of the tank, its speed, its rotation speed (in degrees).
    All of the tanks properties. This class is a gameobject, which means it has an x and y position and can be drawn.
    This class is mostly used to draw, move, and shoot bullets out of the tank. 
 */
package GameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import tankfighter.GameStateHandler;

public class Tank extends GameObject{

    // three integers that determine the type of the tank when we create a new tank 
    // e.g. if its 2, its a red tank that has seeking bullets
    final static public int NORMAL_TYPE = 0;    
    final static public int FAST_TYPE = 1;
    final static public int SEEKING_TYPE = 2;  
    
    // final static ints that cannot be changed
    // the base stats of each tank, player and enemy, (e.g. width, height, turret etc..)
    public static final int BODY_WIDTH = 50;
    public static final int BODY_HEIGHT = 50;
    public static final int TURRET_LENGTH = 30;
    public static final int TURRET_WIDTH = 5;
    public static final int BODY_ROTATION_SPEED = 5;//degrees per frame
    
    // the variable that change for each tank, enemy and player
    // the x and y speed, its rotation, where the turret is pointing, etc...
    protected float vX;
    protected float vY;
    protected float maxSpeed;
    protected Color bodyColor;
    protected Color turretColor;
    protected int bodyAngle;
    protected int turretAngle;
    protected int type;
    private int rotatedThisFrame = 0; // how much the tank has rotated (can only rotate x degrees per frame, our value is 5 above)
    
    /**
     * constructor (to create a tank, you need these base stats)
     * @param x x position of the tank
     * @param y y position of the tank
     * @param angle angle of the turret and body
     */
    public Tank(float x, float y, int angle) {
        //call contructor from GameObject (initialises position)
        super(x, y);
        turretAngle = angle;
        bodyAngle = angle;
        bodyColor = Color.LIGHT_GRAY;
        turretColor = Color.BLACK;
        type = NORMAL_TYPE;
    }
    
    /**
     * chained constructor that also takes the type of tank
     */
    public Tank(float x, float y, int angle, int type) {
        this(x, y, angle);
        this.type = type;
    }
    

    @Override
    /**
     * take the 2d graphics variable that we are using to draw
     */
    public void draw(Graphics2D g) {
        
        //get variables and cast into integers now 
        //(to avoid excessive method calls and casting later
        int tankX = (int)getX();
        int tankY = (int)getY();
        int tankCenterX = (int)getCenterX();
        int tankCenterY = (int)getCenterY();
        
        
        //TANK BODY
        AffineTransform old = g.getTransform();
        g.rotate(Math.toRadians(bodyAngle), tankCenterX, tankCenterY);
        
        //draw shape/image (will be rotated)
        g.setColor(bodyColor);
        g.fillRect(tankX, tankY, BODY_WIDTH - 5, BODY_HEIGHT);
        g.fillPolygon(new int[]{tankX + BODY_WIDTH - 7, tankX + BODY_WIDTH - 7, tankX + BODY_WIDTH + 2}, new int[]{tankY, tankY + BODY_HEIGHT, tankY + BODY_HEIGHT/2}, 3);
        //draw treads
        g.setColor(Color.BLACK);
        g.fillRect(tankX, tankY, BODY_WIDTH - 5, BODY_HEIGHT/6);
        g.fillRect(tankX, tankY + (int)Math.round(BODY_HEIGHT*(5.0/6.0)), BODY_WIDTH - 5, BODY_HEIGHT/6);
        g.setTransform(old);
        
        //TANK TURRET
        old = g.getTransform();
        g.rotate(Math.toRadians(turretAngle), tankCenterX, tankCenterY);
        //draw shape/image (will be rotated)
        g.setColor(turretColor);
        g.fillRect(tankCenterX - (TURRET_WIDTH)/2, tankCenterY - TURRET_WIDTH/2, TURRET_LENGTH, TURRET_WIDTH);
        g.fillRect(tankCenterX - 10, tankCenterY - 10, 20, 20);
        g.setTransform(old);
        
    }
    
    @Override
    /**
     * update the tanks variables (x, y, angle, rotation, etc ...)
     */
    public void update() {
        
        //if it is not moving straight, fix its speed so that it is moving at TANK_SPEED
        double totalSpeed = Math.sqrt(vX*vX + vY*vY);
        // combined vX and vY would be faster than actualy max speed, change it back to MAX speed
        if(totalSpeed>maxSpeed){
            double ratio = (double)maxSpeed / totalSpeed;
            vX *= ratio;
            vY *= ratio;
        }
        
        //move tank
        setX(getX() + vX);
        setY(getY() + vY);
        
        //"CORRECT" angles to be between 0 degrees and 360 degrees
        if(bodyAngle < 0) bodyAngle += 360;
        if(bodyAngle >= 360) bodyAngle = bodyAngle%(360);
        
        if(turretAngle < 0) turretAngle += 360;        
        if(turretAngle >= 360) turretAngle = turretAngle%(360);
        
        
        if(vX != 0 || vY != 0){
            rotateBodyToMovement();
        }
        
    }
    /**
     * updates the tank (checks if it is colliding with a bullet, wall, another tank, etc...)
     * @param walls arrayList of walls
     * @param bullets arrayList of bullets
     * @param enemies arrayList of enemies
     * @param player the player (there is only 1)
     * @return whether or not the player is still alive
     */
    public boolean update(ArrayList<Wall> walls, ArrayList<Bullet> bullets, ArrayList<Enemy> enemies, Tank player){
        rotatedThisFrame = 0;
        this.update();
        
        resolveWallCollisions(walls);
        resolveTankCollisions(enemies, player);
        
        return !isCollidingBullet(bullets);
    }
    
    /**
     * checks to see if the tank being references has collided with a bullet or not
     * @param bullets arrayList of bullets (to keep track of all the bullets)
     * @return if the tank has hit a bullet
     */
    protected boolean isCollidingBullet(ArrayList<Bullet> bullets){
        float tankX = getX();
        float tankY = getY();
        
        //check collisions with bullets
        for (Bullet bullet : bullets) { // for each bullet in bullets
            // if it collided (within body of tank)
            if(bullet.getLife() > Bullet.START_KILL_FRAME
                && bullet.getX() > tankX && bullet.getX() < tankX + BODY_WIDTH
                && bullet.getY() > tankY && bullet.getY() < tankY + BODY_HEIGHT){
                    bullets.remove(bullet);
                    //it is hit by the bullet
                    return true;
            }
        }
        //it has not hit the bullet
        return false;
    }
    
    /**
     * method that updates what happens when two tanks are moving in opposite direction and collide (they stop moving)
     * if they are slightly angled though, they keep moving in their direction (stop is only for directly opposite)
     * parameters are all the tanks that exist on the map
     * @param enemies arrayList of enemies
     * @param player the player
     */
    private void resolveTankCollisions(ArrayList<Enemy> enemies, Tank player){
        float tank1X = getX();
        float tank1Y = getY();
        ArrayList<Tank> tanks = new ArrayList<>();
        for (Enemy enemy : enemies) { // for each Enemy tank in the enemies arrayList
            tanks.add(enemy); // add them to the newly created arrayList
        }
        tanks.add(player);
        //the tank cannot collide with itself so we do not need to test it for collisions
        tanks.remove(this); 
        float tank2X;
        float tank2Y;
        
        for (Tank tank2 : tanks) { // for each tank in tanks arrayList (new one that holds everyone)
            tank2X = tank2.getX();
            tank2Y = tank2.getY();
            //only do accurate collisions if the tanks are actually close to avoid excessive calculations
            // if they are colliding
            if (tank1X + BODY_WIDTH * 3/2 > tank2X && tank1X - BODY_WIDTH/2 < tank2X + BODY_WIDTH
            && tank1Y + BODY_HEIGHT * 3/2 > tank2Y && tank1Y - BODY_HEIGHT/2 < tank2Y + BODY_HEIGHT) {
                Vector[] tank1Corners = new Vector[4]; // array of the tanks corners assigned as vectors
                
                // angle of tank rotation
                double angle2 = Math.toRadians((bodyAngle + 45)%90);
                float tankRadius = (float)Math.sqrt(2*Math.pow(BODY_WIDTH/2,2));//distance from center of tank to a corner
                
                // rotated tank radius in x direction
                float a = tankRadius*(float)(Math.cos(angle2));
                // rotated tank radius in y direction
                float b = tankRadius*(float)(Math.sin(angle2));                
                
                // center x and y of the tank
                float centerX = getCenterX();
                float centerY = getCenterY();                
                
                // create 4 vectors for the tank corners
                tank1Corners[0] = new Vector(centerX + a, centerY + b);//bottom right
                tank1Corners[1] = new Vector(centerX - b, centerY + a);//bottom left
                tank1Corners[2] = new Vector(centerX - a, centerY - b);//top left                
                tank1Corners[3] = new Vector(centerX + b, centerY - a);//top right
                
                //get Wall corners
                Vector[] tank2Corners = new Vector[4];
                
                // angle of rotation for second tank that might be colliding
                angle2 = Math.toRadians((bodyAngle + 45)%90);
                
                // x and y radius of rotated tank
                a = tankRadius*(float)(Math.cos(angle2));
                b = tankRadius*(float)(Math.sin(angle2));                
                
                // x and y pos center of rotated tank
                centerX = tank2.getCenterX();
                centerY = tank2.getCenterY();                
                
                // create the 4 vectors for each corner of the tank
                tank2Corners[0] = new Vector(centerX + a, centerY + b);//bottom right
                tank2Corners[1] = new Vector(centerX - b, centerY + a);//bottom left
                tank2Corners[2] = new Vector(centerX - a, centerY - b);//top left                
                tank2Corners[3] = new Vector(centerX + b, centerY - a);//top right
                
                // check if any of the corners or sides are colliding
                if(Vector.areRotatedRectanglesColliding(tank1Corners, tank2Corners)){ // if they are colliding
                    setX(tank1X - vX); // remove its x speed
                    setY(tank1Y - vY); // remove its y speed
                    break; // exit
                }
            }
        }
    }
    
    /**
     * check for tank collision with wall
     * @param walls walls on the map
     */
    private void resolveWallCollisions(ArrayList<Wall> walls){
        float tankX = getX();
        float tankY = getY();
        int numWalls = walls.size();
        Wall currWall;//stores the current wall that is being checked
        //declare variables for the wall's x and y position to avoid excessive use of getters later
        float currWallX;
        float currWallY;
        for (int wallNum = 0; wallNum < numWalls; wallNum++) { // for each wall in the wall array class
            currWall = walls.get(wallNum); // current wall variable
            if (getX() + BODY_WIDTH > currWall.getX() && currWall.getX() + GameStateHandler.BLOCK_WIDTH > getX()
                    && getY() + BODY_HEIGHT > currWall.getY() && currWall.getY() + GameStateHandler.BLOCK_HEIGHT > getY()) { //if true, there is a collision
                //find x and y overlapto determine which side the player collided with                        
                float overlapX = (BODY_WIDTH / 2 + GameStateHandler.BLOCK_WIDTH / 2) - Math.abs((getX() + BODY_WIDTH / 2) - (currWall.getX() + GameStateHandler.BLOCK_WIDTH / 2));
                float overlapY = (BODY_HEIGHT / 2 + GameStateHandler.BLOCK_HEIGHT / 2) - Math.abs((getY() + BODY_HEIGHT / 2) - (currWall.getY() + GameStateHandler.BLOCK_HEIGHT / 2));

                if (overlapX >= overlapY) {//player hit top or bottom of plat
                    if (getY() > currWall.getY()) { // if tank is overlapping with wall on top
                        setY(getY() + overlapY); // add the overlap (make tank move down)
                    } else {
                        setY(getY() - overlapY); // subtract the overlap (make tank move up)
                    }
                } else {//player hit sides of plat
                    if (getX() < currWall.getX()) { // if tank is overlapping on right side
                        setX(getX() - overlapX); // subtract overlap so that tank is not in wall
                    } else {
                        setX(getX() + overlapX); // add so it moves right and isnt in wall
                    }
                }
            }

        }
       
    }//end resolve wall collisions
    
    /**
     * this method shoots a bullet (returns a bullet to the caller, caller adds it to bullet arrayList)
     * @param imaginary variable created to see if this is a fake bullet designed for the AI, or if its a real bullet that was shot
     * @return a new bullet that was shot out of the turret
     */
    public Bullet shootBullet(boolean imaginary){
        if(this.type == NORMAL_TYPE){ // if normal bullet from yellow tank
            return new Bullet(getCenterX() + TURRET_LENGTH*(float)Math.cos(Math.toRadians(turretAngle)), // create a new bullet directed in the angle of the turret, with the properties assigned to the tank
                getCenterY() + TURRET_LENGTH*(float)Math.sin(Math.toRadians(turretAngle)), turretAngle, false, imaginary, this.bodyColor);
        }else if(this.type == FAST_TYPE){
            return new Bullet(getCenterX() + TURRET_LENGTH*(float)Math.cos(Math.toRadians(turretAngle)), // create a new bullet directed in the angle of the turret, with the properties assigned to the tank
                getCenterY() + TURRET_LENGTH*(float)Math.sin(Math.toRadians(turretAngle)), turretAngle, true, imaginary, this.bodyColor);
        }else{//if it's type is the type that shoots seeking bullets
            return new SeekingBullet(getCenterX() + TURRET_LENGTH*(float)Math.cos(Math.toRadians(turretAngle)), // create a new bullet directed in the angle of the turret, with the properties assigned to the tank
                getCenterY() + TURRET_LENGTH*(float)Math.sin(Math.toRadians(turretAngle)), turretAngle, imaginary, this.bodyColor);
        }
        
    }
    
    // getters and setters
    
    public float getCenterX(){
        return getX() + BODY_WIDTH/2;
    }
    
    public float getCenterY(){
        return getY() + BODY_HEIGHT/2;
    }

    public double getBodyRotation() {
        return bodyAngle;
    }

    public double getTurretRotation() {
        return turretAngle;
    }

    /**
     * 
     * @param x x position of mouse (or player if its the enemy tank)
     * @param y y position of mouse (or player if its the enemy tank)
     */
    protected void pointTurret(double x, double y){
        double dX = x - getCenterX();
        double dY = y - getCenterY();
      //  double hypotenuse = Math.sqrt(dY*dY+dX*dX);
        int newTurretAngle = (int)Math.toDegrees(Math.atan(dY/dX));
        if(dX < 0){
            newTurretAngle = (newTurretAngle + 180) % 360;
        }
        turretAngle = newTurretAngle; // set the new calculated turret angle
    }
    
    /**
     * rotates the body of the tank
     */
    private void rotateBodyToMovement(){
        
        // gets the target angle
        double targetAngle = Math.toDegrees(Math.atan(((double)vY/(double)vX)));
        
        // if moving left
        if (vX < 0) {
            targetAngle += 180; // adjust angle
        }else if(vX >= 0 && vY < 0){ // moving right and up
            targetAngle += 360; // adjust angle
        }
        
        //fix angles > 360 or < 0
        targetAngle = (360 + targetAngle) % 360;
        
        if(targetAngle - bodyAngle > 180){
            targetAngle -= 360;
        }else if(bodyAngle - targetAngle > 180){
            targetAngle += 360;
        }
        
        if(bodyAngle < targetAngle){
            bodyAngle += BODY_ROTATION_SPEED;
            rotatedThisFrame = BODY_ROTATION_SPEED;
        }else if(bodyAngle > targetAngle){
            bodyAngle -= BODY_ROTATION_SPEED;
            rotatedThisFrame = - BODY_ROTATION_SPEED;            
        }
        
    }
    public boolean equals(Tank t){
        //since 2 tanks will never be in the exact same position we only have do compare their positions
        //(and not rotation, color, etc)
        return getX() == t.getX() && getY() == t.getY();
    }
    
    
}
