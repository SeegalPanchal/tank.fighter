/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * Game State when the actual tank game is running
 */
package GameStates;

import Resources.ResourceGetter;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import tankfighter.GameStateHandler;
import tankfighter.Level;
import tankfighter.LevelReader;

public class InGame extends GameState{
    private int numLevels;
    private int currLevelIndex = 0;
    private Level currLevel;
    //initialLevels stores the original state of the Levels.
    //so if you die in the middle of a level, the Level is found in initialLevels 
    //to reset the state of the Level back to the original state
    //(ex. to replace the tanks in their initial positions)
    private ArrayList<Level> initialLevels;
    private ArrayList<Level> levels;
    
    private int livesLeft;
    private boolean beatGame = false;
    
    //the hearts are drawn to represent lives
    private BufferedImage heartImage = null;
    
    //constructor
    public InGame(){
        super();
        super.setLogoImageFromPath("levelLogo.png");   
        resetState();
        
        //load heart/life point image
        heartImage = ResourceGetter.getBufferedImage("heart.png");
    }
    
    
    @Override
    public int update() {
        //levelState = 1 if player has beat the level, 2 if they have died, 0 otherwise
        int levelState = currLevel.update();
        String newLogoPath = currLevel.checkChangeLogo();
        //if the logo should change
        if (newLogoPath != null) {
            if(newLogoPath.equals("levelLogo.png")){
                newLogoPath = "level" + (currLevelIndex + 1) + ".png";
            }
            logoImages.clear();
            logoWidths.clear();
            setLogoImageFromPath(newLogoPath);

        }
        //if they beat the level
        if (levelState == 1) {
            boolean beatGame = nextLevel();
            if(beatGame){
                return GameStateHandler.GAME_OVER_STATE;
                
            }else{//if they are still playing
                return GameStateHandler.IN_GAME_STATE;
            }

        }
        //if they died
        else if(levelState == 2){
            //if they still have lives then subtract 1 from the number of lives
            if(livesLeft > 1){
                livesLeft--;
                //reset the level
                levels.set(currLevelIndex, initialLevels.get(currLevelIndex).clone());
                currLevel = levels.get(currLevelIndex);
                return 3;
            }
            //if they have no more lives then they lose the game
            else{
                //go to "game over" GameState
                return 1;
            }
        }
        //if they did not beat the level or die, stay in the InGame gameState
        return 3;
    }
    private boolean nextLevel(){
        //if there are more levels to play
        if(currLevelIndex < numLevels - 1){
            currLevelIndex++;
            currLevel = levels.get(currLevelIndex);
            currLevel.startTimer();
            return false;
        }
        //otherwise, they beat the game
        beatGame = true;
        return true;
    }
    
    @Override
    public void draw(Graphics2D g, ImageObserver io) {
        currLevel.drawLevel(g, currLevelIndex + 1, io);
        //draw lives
        //x position of heart we are currently drawing
        int currHeartX = 290;
        for (int i = 0; i < livesLeft; i++) {
            currHeartX += 50;
            g.drawImage(heartImage, currHeartX, 615, io);

        }
        //draw logo
        super.drawLogo(g, io);
        
    }
    
    //resets the in-game state
    @Override
    public void resetState() {
        beatGame = false;
        levels = new ArrayList<>();
        livesLeft = 3;
        //create a new LevelReader to re-initialise Levels
        LevelReader lr = new LevelReader();
        initialLevels = lr.getLevels();
        numLevels = initialLevels.size();
        for (Level l : initialLevels) {
            levels.add(l.clone());
        }
        currLevelIndex = 0;
        currLevel = levels.get(0);
        
        //buttons
        addReturnToMenuButton();

    }

    //getters
    public Level getCurrLevel() {
        return currLevel;
    }
    public int getCurrLevelNumber(){
        return currLevelIndex + 1;
    }
    public boolean didBeatGame(){
        return beatGame;
    }
}
