/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * A class that handles all the various states that the game can be in (ex. main menu, highscores, in-game, etc)
 */
package tankfighter;
import GameStates.GameState;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import GameStates.*;
import Resources.ResourceGetter;

public class GameStateHandler {
    
    //final class variables
    final public static int FRAME_WIDTH = 800;
    final public static int FRAME_HEIGHT = 600;
    final public static int FRAME_WIDTH_IN_BLOCKS = 16;
    final public static int FRAME_HEIGHT_IN_BLOCKS = 12;
    final public static int BLOCK_WIDTH = FRAME_WIDTH/FRAME_WIDTH_IN_BLOCKS;
    final public static int BLOCK_HEIGHT = FRAME_HEIGHT/FRAME_HEIGHT_IN_BLOCKS;
    //create static variables for the gameState integers 
    //so we do not have to remember which number corresponds to which state
    final public static int CREDITS_STATE = 0;
    final public static int GAME_OVER_STATE = 1;
    final public static int HIGHSCORES_STATE = 2;
    final public static int IN_GAME_STATE = 3;
    final public static int INSTRUCTIONS_STATE = 4;
    final public static int MAIN_MENU_STATE = 5;
    
    //array of GameStates
    private GameState[] gameStates;
    private GameState currGameState;
    //the index of the current game state in the gameStates array
    private int currGameStateIndex;
    
    private int[] mousePos;//[x, y]
    
    //constructor
    //initialises variables
    public GameStateHandler(){
        gameStates = new GameState[6];
        //We are putting them in alphabetical order so it is easy to know which index is which GameState 
        //(by looking at projects sidebar)
        gameStates[0] = new Credits();
        gameStates[1] = new GameOver();
        gameStates[2] = new Highscores();
        gameStates[3] = new InGame();
        gameStates[4] = new Instructions();
        gameStates[5] = new MainMenu();
        
        //set the current state as the main menu state
        initGameState(5);
        
    }
    
    //initialises a GameState given the index of the state in the gameStates array
    private void initGameState(int state){
        if(state == -1){//-1 represents that the user clicked "exit"
            //exit the program
            System.exit(0);
        }else{
            //set the current idex to the new state and the current state to the new one
            currGameStateIndex = state;
            currGameState = gameStates[currGameStateIndex];
            
            //if we are moving to the gameOver screen then we must set the last level to the current level
            //so the GameOver state knows which level the player got to
            if(currGameStateIndex == GAME_OVER_STATE){
                int lastLevel = ((InGame)gameStates[IN_GAME_STATE]).getCurrLevelNumber();
                ((GameOver)currGameState).setLastLevel(lastLevel);
            }
            //reset the state of the new state
            currGameState.resetState();
        }
    }
    
    //updates the current state
    public void update(){
        int nextState = currGameState.update();//each update method in the gameState returns the index of the new state we are moving to
        if(nextState != currGameStateIndex){//if the game state changed
            //initialise the next game state
            initGameState(nextState);
        }
        //if the player beat the game then we want the "Victory" logo to be displayed instead of the 
        //"game over" logo, so we have to check if the player beat the game
        if(nextState == GAME_OVER_STATE && ((InGame)gameStates[IN_GAME_STATE]).didBeatGame()){
            currGameState.setLogoImageFromPath("logos\\victoryLogo.png");
        }
    }
    
    //draws the game
    public void drawGame(Graphics2D g, ImageObserver io){
        //draw background texture
        BufferedImage image = ResourceGetter.getBufferedImage("groundTexture.jpg");

        //add 50 because the full frame height is 50 pixels longer than the actual level frame
        //(the top 50 pixels in the Level contains the logo)
        g.drawImage(image, 0, 0, FRAME_WIDTH, FRAME_HEIGHT + 50, io);
        
        //draw the current game state
        gameStates[currGameStateIndex].draw(g, io);
        
    }
    
    //KEY AND MOUSE EVENT HANDLING METHODS
    public void keyPressed(KeyEvent e){
        //it only matters if a key is pressed if the user is in the in-game state
        if(currGameStateIndex == IN_GAME_STATE){//if in-game
            ((InGame)currGameState).getCurrLevel().keyPressed(e);
        }
    }
    public void keyReleased(KeyEvent e){
        //it only matters if a key is released if the user is in the in-game state
        if(currGameStateIndex == IN_GAME_STATE){//if in-game
            ((InGame)currGameState).getCurrLevel().keyReleased(e);
        }
    }
    public void mouseMoved(int[] newPos){
        mousePos = newPos;
        //if in the in-game state, aim the turret.
        if(currGameStateIndex == IN_GAME_STATE){//if in-game
            ((InGame)currGameState).getCurrLevel().mouseMoved(newPos);
        }
    }
    
    //if the mouse is pressed we have to check if any buttons are pressed
    public void mousePressed(){
        if(currGameStateIndex == MAIN_MENU_STATE){//the main menu has many buttons so we have a separate method in the class to handle mouse presses
            int nextState = ((MainMenu)gameStates[MAIN_MENU_STATE]).mousePressed(mousePos);
            if(nextState != MAIN_MENU_STATE){//if the gameState changed, initialise the new gameState
                initGameState(nextState);
            }
        }else if(currGameStateIndex == IN_GAME_STATE){//if in-game
            ((InGame)currGameState).getCurrLevel().mousePressed();
        }else if(currGameStateIndex == GAME_OVER_STATE){//the game over state has many buttons so we have a separate method in the class to handle mouse presses
            int nextState = ((GameOver)gameStates[GAME_OVER_STATE]).mousePressed(mousePos, ((InGame)gameStates[IN_GAME_STATE]).getCurrLevelNumber(), (Highscores)gameStates[HIGHSCORES_STATE]); 
            if(nextState != GAME_OVER_STATE){//if the gameState changed, initialise the new gameState
                initGameState(nextState);
            }
        }else{//if the only button is the "return to main menu" button, then check if it is pressed
            if((currGameState).getButtons().get(0).isInBounds(mousePos)){
                //if they are going from the highscores page to the main menu page
                //then we have to set the currPlayerName and currPlayerScore to null
                //this is because we don't want the last player's name to be highlighted when they go to the main menu
                //and then return to the highscores page
                if(currGameStateIndex == HIGHSCORES_STATE){
                    ((Highscores)currGameState).setRecentPlayerName(null);
                    ((Highscores)currGameState).setRecentPlayerScore(-1);                    
                }
                //initialise the main menu state
                initGameState(MAIN_MENU_STATE);
                
            }
        }
        
    }
    
}
