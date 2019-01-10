/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * Game Over state, after the player finishes their run
 */
package GameStates;

import GameObjects.Button;
import Resources.ResourceGetter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import tankfighter.GameStateHandler;
import static tankfighter.GameStateHandler.FRAME_WIDTH;

public class GameOver extends GameState{
    private boolean beatGame;
    private String playerName;
    private int lastLevel;//the player lost at this level (displeyed on Game Over screen and recorded to highscores)
    
    
    public GameOver(){
        super();  
        resetState();
    }

    @Override
    public int update() {
        return GameStateHandler.GAME_OVER_STATE;//the game state remains in this state until the "return to menu" button is pressed
    }

    @Override
    public void draw(Graphics2D g, ImageObserver io) {
        super.drawLogo(g, io);
        
        
        //draw "YOU GOT TO LEVEL #" text
        getButtons().get(0).draw(g);
        
        //draw "Choose a Name", "Record Score" and "Return to Main Menu" buttons
        for (int i = 1; i < 4; i++) {
            getButtons().get(i).drawWithShadow(g);
        }
    }
    
    @Override
    public void resetState() {
        //create buttons
        super.addButton(new Button(FRAME_WIDTH/2 - Button.STANDARD_WIDTH/2, 200, 
                Button.STANDARD_WIDTH, Button.STANDARD_HEIGHT,
                new Color(0, 0, 0, 0), "",
                Button.STANDARD_FONT_SIZE, Color.BLACK));
        super.addButton(new Button(FRAME_WIDTH/2 - Button.STANDARD_WIDTH/2, 250, 
                Button.STANDARD_WIDTH, Button.STANDARD_HEIGHT,
                Button.STANDARD_BOX_COLOR, "Choose a Name",
                Button.STANDARD_FONT_SIZE, Button.STANDARD_TEXT_COLOR));
        super.addButton(new Button(FRAME_WIDTH/2 - Button.STANDARD_WIDTH/2, 320, 
                Button.STANDARD_WIDTH, Button.STANDARD_HEIGHT,
                Button.STANDARD_BOX_COLOR, "Record Score",
                Button.STANDARD_FONT_SIZE, Button.STANDARD_TEXT_COLOR));
        addReturnToMenuButton();     
        
        super.setLogoImageFromPath("gameOverLogo.png");      
        
        getButtons().get(0).setText("YOU GOT TO LEVEL " + lastLevel);
        getButtons().get(1).setText("Choose a Name");
    }
    
    public int mousePressed(int[] mousePos, int score, Highscores highscores){
        //if the user pressed "return to main menu" then return 5
        if(getButtons().get(3).isInBounds(mousePos)) return 5;
        //if they pressed "enter name here"
        if(getButtons().get(1).isInBounds(mousePos)){
            playerName = JOptionPane.showInputDialog("Enter Your Name Below");
            if(playerName == null){
                playerName = "UNNAMED";
            }
            getButtons().get(1).setText(playerName);
        }
        //if they pressed "Record Score"
        else if(getButtons().get(2).isInBounds(mousePos)){
            //JOptionPane.showMessageDialog(null, "recording score");
            //we need to check if the name is null again just in case they do not ever press "Choose a Name"
            if(playerName == null){
                playerName = "UNNAMED";
            }
            writeToHighscores(playerName, lastLevel);
            //let the highscores page know the current player's name and score
            //(so it can be displayed and highlighted in the highscores page)
            highscores.setRecentPlayerName(playerName);
            highscores.setRecentPlayerScore(score);

            //tell the GameStatesHandler to go to highscores page
            
            return 2;

        }
        //if the user does not wish to change gameStates, then remain on the GameOver screen
        return 1;
        
    }
    private void writeToHighscores(String line, int score) {
        ResourceGetter.writeToHighscores(line, score);
    }

    public void setLastLevel(int lastLevel) {
        this.lastLevel = lastLevel;
    }

    public void setBeatGame(boolean beatGame) {
        this.beatGame = beatGame;
    }
    
    
}
