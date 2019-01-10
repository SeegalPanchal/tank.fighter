/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The class for an enemy Tank. Has the update method (takes the state of the game, 
    evaluates how the enemy should respond, then activates the most optimal response
    e.g. shoot player, move out of way of bullet, etc...
 */
package GameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import tankfighter.Node;
import tankfighter.Pathfinding;

public class Enemy extends Tank{
    private int timeUntilShoot;//time before the enemy shoots their next bullet
    final private static int SHOOT_DELAY = 300;//frames of delay between each shot (default: 400)
    final private static float MAX_SPEED = (float)1.25;//maximum speed
    
    //constructor
    public Enemy(float x, float y, int angle) {
        super(x, y, angle);
        this.maxSpeed = MAX_SPEED;
        this.bodyColor = Color.YELLOW;
        this.turretColor = bodyColor.darker();
        //generate a random timeUntilShoot so all the enemies don't shoot at the same time
        timeUntilShoot = (int)(Math.random()*SHOOT_DELAY);
    }
    //constructor with the type (of Bullet) parameter (normal, seeking, fast)
    public Enemy(float x, float y, int angle, int type) {
        
        super(x, y, angle, type);
        
        if(type == Tank.FAST_TYPE){
            this.bodyColor = new Color(70, 70, 210);//light navy blue
        }else if(type == Tank.SEEKING_TYPE){
            this.bodyColor = Color.RED;
        }else{
            this.bodyColor = Color.YELLOW;            
        }
        this.turretColor = bodyColor.darker();
        timeUntilShoot = (int)(Math.random()*SHOOT_DELAY);   
        this.maxSpeed = MAX_SPEED;
    }
    
    //draws the enemy
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
    }
   
    public boolean update(Player player, ArrayList<Wall> walls, ArrayList<Bullet> bullets, ArrayList<Enemy> enemies, Node[][] map){
        
        
        //point turret at player
        pointTurret(player.getCenterX(), player.getCenterY());
        //decrease the counter so its is closer to shooting time
        timeUntilShoot--;
        //check if its time to shoot a bullet
        if(timeUntilShoot <= 0){
            if(hasClearPath(player, walls, map)){
                timeUntilShoot = SHOOT_DELAY;
                bullets.add(shootBullet(false));//false means the bullet is not imaginary
            }
            
        }
        //if the enemy is in a position where its x and y coordinates 
        //are very close to mulptiples of 50, we recalculate the path. 
        //this is because recalculating the path every frame is very taxing and unnecessary.
        //However we cannot recalculate only when the x and y values are exact multiples of 50
        //because when it is moving diagonally it will never reach an exact multiple.
        if(    ((getX() + vX)%50 < maxSpeed || (getX() + vX)%50 > 50 - maxSpeed) //if the x position + x velocity is very close to 50
             &&((getY() + vY)%50 < maxSpeed || (getY() + vY)%50 > 50 - maxSpeed) ){//if the y position + y velocity is very close to 50
            
            //set x and y to the exact multiples of 50
            setX(50*(Math.round(getX()/50)));
            setY(50*(Math.round(getY()/50)));
            //recalculate the path
            return recalculatePath(player, walls, bullets, enemies, map);
        }
        return super.update(walls, bullets, enemies, player);
    }
    
    public boolean recalculatePath(Player player, ArrayList<Wall> walls, ArrayList<Bullet> bullets, ArrayList<Enemy> enemies, Node[][] map){
        // copy of the map that we can change without affecting normal map
        Node[][] mapCopy = new Node[16][12];
        // manually set nodes
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 12; j++) {
                if(map[i][j] == null){
                    mapCopy[i][j] = null;
                }else{
                    mapCopy[i][j] = map[i][j].clone();
                }
            }
        }
        
        
        // for each bullet in the bullets arraylist
        for (Bullet bullet : bullets) {
            // get the x and y of the bullets
            int x = (int)(bullet.getX()/50.0);
            int y = (int)(bullet.getY()/50.0);
            mapCopy[x][y] = null; // bullet cell
            if (x+1 < 16) { // if there is space right of bullet
                mapCopy[x+1][y] = null; // right of bullet
            }
            if (x-1 >= 0) { // if there is space left of bullet
                mapCopy[x-1][y] = null; // left of bullet
            }
            if (y+1 < 12) { // if thers is space below bullet
                mapCopy[x][y+1] = null; // below bullet
            }
            if (y-1 >= 0) { // if there is space above bullet
                mapCopy[x][y-1] = null; // top of bullet 
            }

            // if right is available
            if (x+1 < 16) {
                // check if top and bottom are available as well
                if (y+1 < 12) {
                    // set them to null
                    mapCopy[x+1][y+1] = null; // bottom right
                }
                if (y-1 >= 0) {
                    mapCopy[x+1][y-1] = null; // top right
                }
            }
            // if left is available
            if (x-1 >= 0) {
                // check if top and bottom are available as well
                if (y+1 < 12) {
                    // set them to null
                    mapCopy[x-1][y+1] = null; // bottom left
                }
                if (y-1 >= 0) {
                    mapCopy[x-1][y-1] = null; // top left
                }
            }
                
        }
        
        // create a pathfinder
        Pathfinding q = new Pathfinding((int)getX(), (int)getY(), 
                (int)player.getX(), (int)player.getY(), mapCopy);


        // use the pathfinder to find a path to target:(player)
        q.findPath();
        ArrayList<Node> path = q.getPath(); // get the path it found

        
        int pathSize = path.size(); // store the size of the path
        
        // if pathSize is 0, meaning there is no path
        if (pathSize == 0) {
            // get a random location
            int x = (int) (Math.random() * 16);
            int y = (int) (Math.random() * 12);
            // if this random location is null on the map-grid
            // loop until you x and y positions that aren't null
            while (mapCopy[x][y] == null) {
                x = (int) (Math.random() * 16);
                y = (int) (Math.random() * 12);
            }
            // create a new pathfinder with the new target location
            q = new Pathfinding((int)getX(), (int)getY(), 
                x*50, y*50, mapCopy);
            // find the new path
            q.findPath();
            // get the new path
            path = q.getPath();
            // change the path size to the new size
            pathSize = path.size();
        }
        
        // the next x and y location to go to on the path found from the pathfinder
        // currently stores the enemies x and y position
        int nextX = (int)getX();
        int nextY = (int)getY();
        // if pathsize is 2 or more
        // doesnt work with 1 because its right next to the target
        if(pathSize >= 2){
            // get the next x and y positions on the map
            nextX = path.get(pathSize - 2).getX();
            nextY = path.get(pathSize - 2).getY();
        }

        // if you are below the next node on the path, move up
        if(nextY < getY()){
            vY = -maxSpeed;
        }else if(nextY > getY()){ // if you are above, mode up
            vY = maxSpeed;
        }else{ // just dont move in the y direction if the next node is to the right of left
            vY = 0;
        }
        if(nextX < getX()){ // if you are right of desired node
            vX = -maxSpeed; // move left
        }else if(nextX > getX()){ // if you are left of desired node
            vX = maxSpeed; // move right
        }else{ // you are above or below desired node
            vX = 0; // dont move in x direction
        }
        // update the tank
        return super.update(walls, bullets, enemies, player);
    }
    
    //clones the Enemy
    public Enemy clone(){
        return new Enemy(getX(), getY(), bodyAngle, type);
    }
    
    /**
     * 
     * @param player get the player object (contains all player info)
     * @param walls get the walls on the map (arrayList)
     * @param map get a grid of the level (the map split into 16*12 nodes)
     * @return true if bullet hits the player, if it doesn't, then don't shoot
     */
    public boolean hasClearPath(Player player, ArrayList<Wall> walls, Node[][] map){
        Bullet imaginaryBullet = this.shootBullet(true);//create imaginary bullet, true means its imaginary
        ArrayList<Bullet> imaginaryBulletList = new ArrayList<>(); // create an imaginary bullet list (doesnt do dmg)
        imaginaryBulletList.add(imaginaryBullet); // add it to the imaginary bullet list 
        Enemy imaginaryEnemy = this.clone(); // fake enemy
        ArrayList<Enemy> imaginaryEnemyList = new ArrayList<>(); // create fake enemy list
        imaginaryEnemyList.add(imaginaryEnemy); // add to fake enemy list
        
        ArrayList<Bullet> empty = new ArrayList<>(); // no bullets
        
        while(true){ // loop until imaginary bullet hits player or imaginary bullet does not exist anymore or imaginary enemy dies
            
            imaginaryEnemy.isCollidingBullet(imaginaryBulletList);//we have to call this to remove the bullet if it hits the tank
            if(imaginaryBullet.update(walls) && imaginaryEnemy.recalculatePath(player, walls, empty, imaginaryEnemyList, map)){//if the bullet exists
                // if the bullet hits
                if(player.isCollidingBullet(imaginaryBulletList)){
                    return true; // has a clear path, return true, which shoots bullet
                }
            }else{
                return false; // bullet hits the enemy again (it hit the shooter again) or doesnt exist, therefore dont shoot
            }
        }
    }
}
