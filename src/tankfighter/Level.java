/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * A class representing a Level.
 */
package tankfighter;

import GameObjects.Wall;
import GameObjects.Player;
import GameObjects.Enemy;
import GameObjects.Bullet;
import GameObjects.SeekingBullet;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Iterator;

public class Level {
    //each Level contains a player, enemies, walls and bullets in the air.
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Wall> walls;
    private ArrayList<Bullet> bullets;
    
    //this map is used for the enemy's pathfinding.
    //it outlines which positions are possible for the enemy to enter
    //ex. the enemy cannot go through a wall
    public Node map[][] = new Node[16][12]; // 12 rows, 16 columns
    
    public long startTime;//the System.currentTimeMillis time of when the level started.
    public float currentTime;//milliseconds since the level started
    
    //the approximate time for a frame to change. this is used to update the logo of the gameState.
    //for example, if the time is between 4000 - approxTimePerFrame (3090) and 4000
    //then we know it is time the change the logo from "Set" to "Fight!"
    private static final int APPROX_TIME_PER_FRAME = 80;
    private static final int INTRO_DELAY = 500;//ms between each logo when the level starts ("Ready", "Set", "Fight") (default: 500)  
    
    //constructor
    public Level(Player player, ArrayList<Enemy> enemies, ArrayList<Wall> walls) {
        this.player = player;
        this.enemies = enemies;
        this.walls = walls;
        startTimer();
        bullets = new ArrayList<>();
        loadMap();
    }
    /**
     * Starts the timer for this level
     */
    public void startTimer(){
        startTime = System.currentTimeMillis();    
    }
    /**
     * Updates the level.
     * @return 1 if the player has completed the level, 2 if the player has died, 0 otherwise
     */
    public int update(){
        currentTime = System.currentTimeMillis() - startTime;
        //at the start of the level it doesn't "update"
        //(when displaying "Ready", "Set", "Fight!"
        //so we just return 0 without doing anything
        if(currentTime < 5*INTRO_DELAY){
            return 0;
        }
        
        if(!player.update(walls, bullets, enemies, player)){//if the player died
            return 2;
        }
        
        //update enemies
        for (int i = 0; i < enemies.size(); i++) {
            if(!enemies.get(i).update(player, walls, bullets, enemies, map)){//if the enemy died
                enemies.remove(i);
            }
        }
        
        //update bullets
        //this loop is used to avoid Concurrent Modification Errors 
        //(when you remove an element of an ArrayList while looping through it)
        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext();) {
            Bullet bullet = iterator.next();
            if(!(bullet instanceof SeekingBullet)){
                if (!bullet.update(walls)) {
                    //if there are any walls broken, we must recalculate the path the enemy must take
                    //because the cells with the broken walls is not a cell the enemy can move into
                    loadMap();
                    // Remove the bullet
                    iterator.remove();
                }
            }else{//if the bullet is a seeking bullet, it passes the player as a parameter in the update method
                if(!   (((SeekingBullet)bullet).update(player, walls)) ){
                    loadMap();
                    iterator.remove();
                }
            }
        }
        
        //if the player has completed the level
        if(enemies.isEmpty())return 1;
        
        //if the player has not died or completed the level return 0
        return 0;
    }
    
    //checks if the logo should change.
    //returns null if it does not change.
    //returns the filepath String for the new logo if it does change
    //this is used when the level starts, because the logo changes from "Ready" to "Set" to "Fight"
    public String checkChangeLogo(){
        //when the level and after "Ready set fight" the logo is set as "Level #"
        if ((currentTime > 0  && currentTime < APPROX_TIME_PER_FRAME)|| (currentTime < 5*INTRO_DELAY && currentTime > 5*INTRO_DELAY - APPROX_TIME_PER_FRAME)) {
            //if the logo is the level logo (ex. "Level 3")
            //then we need to do some extra calculations because the actual number changes.
            //so we return this String. When the InGame detects thisS String, it will do the extra work to resolve this problem.
            return "levelLogo.png";
        } else if (currentTime < 2*INTRO_DELAY && currentTime > 2*INTRO_DELAY - APPROX_TIME_PER_FRAME) {//"ready" logo
            return "readyLogo.png";
        } else if (currentTime < 3*INTRO_DELAY && currentTime > 3*INTRO_DELAY - APPROX_TIME_PER_FRAME) {//"set" logo
            return "setLogo.png";
        } else if (currentTime < 4*INTRO_DELAY && currentTime > 4*INTRO_DELAY - APPROX_TIME_PER_FRAME) {//"fight" logo
            return "fightLogo.png";
        }
        //if the logo does not change return null
        return null;
    }
    //draws the level
    public void drawLevel(Graphics2D g, int levelNumber, ImageObserver io){
        
        //fill top row with wall color
        //(it is where the level text will be written)
        g.setColor(Wall.DEFAULT_COLOR);
        g.fillRect(0, 0, GameStateHandler.FRAME_WIDTH, GameStateHandler.BLOCK_HEIGHT);
        
        //draw the player
        player.draw(g);
        
        //draw each enemy
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        
        //draw each wall
        for (Wall wall : walls) {
            wall.draw(g);
        }
        
        //draw each bullet
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        
    }
    
    //getters
    public Player getPlayer() {
        return player;
    }
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }
    public ArrayList<Wall> getWalls() {
        return walls;
    }
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
    
    //returns a clone of the Level
    public Level clone(){
        ArrayList<Enemy> enemiesClone = new ArrayList<>(enemies.size());
        for (Enemy enemy : enemies) {
            enemiesClone.add(enemy.clone());
        }
        
        ArrayList<Wall> wallsClone = new ArrayList<>(walls.size());        
        for (Wall wall : walls) {
            wallsClone.add(wall.clone());
        }
        return new Level(player.clone(), enemiesClone, wallsClone);
    }
    
    //if a key is pressed call the keyPressed method in the player to let them move
    public void keyPressed(KeyEvent e){
        player.keyPressed(e);
    }
    //if a key is pressed call the keyReleased method in the player to stop them from moving
    public void keyReleased(KeyEvent e){
        player.keyReleased(e);    
    }
    //if mouse is moved call the mouseMoved method in the player to let them aim their turret
    public void mouseMoved(int[] newPos){
        player.mouseMoved(newPos);
    }
    //if mouse is moved call the mousePressed method in the player to let shoot
    public void mousePressed(){
        Bullet b = player.shootBullet();
        //if the player is able to shoot a bullet
        if(b != null) bullets.add(b);
    }
    
    //loads the map used for enemy pathfinding
    public void loadMap() {
        // load the map integer as completely open map
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                map[x][y] = new Node(x*50, (y+1)*50);
            }
        }
        // read through ArrayList of walls, and add the walls to the Map Array
        for (Wall w : walls) {
            int cellX = (int)(w.getX() / 50);
            //subtract 50 because entire game map is shifted down by 50 pixels
            int cellY = (int)((w.getY() - 50) / 50);
            map[cellX][cellY] = null;
            
        }
    }
    
}
