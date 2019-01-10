/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The superclass for all states of the game:
 *      -> credits
 *      -> game over
 *      -> highscores
 *      -> in-game
 *      -> instructions
 *      -> main menu
 * 
 * All states have a set of Button(s) and a logo (the fancy text displayed at the top of the screen).
 */
package GameStates;

import GameObjects.Button;
import Resources.ResourceGetter;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import tankfighter.GameStateHandler;
import static tankfighter.GameStateHandler.FRAME_HEIGHT;
import static tankfighter.GameStateHandler.FRAME_WIDTH;

public abstract class GameState {
    //ArrayList of Buttons in the state
    private ArrayList<Button> buttons;
    //the image of the logo
    private BufferedImage logoImage;
    
    //if the logo is a set of combined images (ex. "Level 12" is "Level", "1", and "2" combined)
    //then we use an ArrayList to store the images instead of one single image
    protected ArrayList<BufferedImage> logoImages;
    protected ArrayList<Integer> logoWidths;
    
    //the width of the 
    private int logoWidth = 0;
    
    //a standard height for the logo
    public final static int STANDARD_LOGO_HEIGHT = 125;
    
    //constructor
    //initialises arraylist of buttons
    public GameState(){
        buttons = new ArrayList<>();
        logoImages = new ArrayList<>();
        logoWidths = new ArrayList<>();
    }
    
    //abstract methods (must be overridden in subclasses)
    public abstract void draw(Graphics2D g, ImageObserver io);

    public abstract int update();

    public abstract void resetState();

    //draws the logo
    public void drawLogo(Graphics2D g, ImageObserver io) {

        //if the logoWidth is zero, it means the logo has not been scaled yet.
        if (logoWidth == 0) {
            //if not already scaled, scale the logo to the appropriate size
            int oWidth = logoImage.getWidth(io);//original width
            int oHeight = logoImage.getHeight(io);//original height
            //scale proportionally so that height ends up being 150;
            double ratio = (double) oHeight / (double) STANDARD_LOGO_HEIGHT;
            logoWidth = (int) ((double) oWidth / ratio);
        }

        //if the player is in the InGame state, the logo is drawn at y = 0 to make room to draw the level
        //otherwise it is drawn at y = 40
        int y = 40;
        if (this instanceof InGame) {//if the current gameState is the InGame state
            y = 0;
        }
        //draw the logo
        g.drawImage(logoImage, GameStateHandler.FRAME_WIDTH / 2 - logoWidth / 2, y, logoWidth, STANDARD_LOGO_HEIGHT, io);
    }

    //gets the arraylist of Buttons
    public ArrayList<Button> getButtons() {
        return buttons;
    }
    
    //adds a Button to the ArrayList of Buttons
    public void addButton(Button button){
        buttons.add(button);
    }
    
    //since most of the gameStates require this button
    //we can make a method specifically used for adding it
    public void addReturnToMenuButton(){
        buttons.add(
            new Button(FRAME_WIDTH/2 - Button.STANDARD_WIDTH/2, FRAME_HEIGHT - 70,
            Button.STANDARD_WIDTH, Button.STANDARD_HEIGHT,
            Button.STANDARD_BOX_COLOR, "Back to Main Menu",
            Button.STANDARD_FONT_SIZE, Button.STANDARD_TEXT_COLOR)
        );
                        
    }
    
    //sets the logo image to a certain BufferedImage
    public void setLogoImage(BufferedImage logoImage) {
        this.logoImage = logoImage;
        //reset the width to 0 so that the draw method recalculates the new width of the logo
        logoWidth = 0;
    }
    
    //sets the logo image from a filepath
    public void setLogoImageFromPath(String filepath){
        setLogoImage(imageFromPath(filepath));
    }
    
    //creates a BufferedImage from a String filepath.
    public BufferedImage imageFromPath(String filepath){
        return ResourceGetter.getBufferedImage(filepath);
    }
}
