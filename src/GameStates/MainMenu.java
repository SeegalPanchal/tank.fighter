/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The main menu game state.
 */
package GameStates;

import GameObjects.Button;
import Resources.ResourceGetter;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import tankfighter.GameStateHandler;
import static tankfighter.GameStateHandler.FRAME_WIDTH;

public class MainMenu extends GameState{
    
    private final String[] buttonTexts = {"Play Game", "Instructions", "Highscores", "Credits", "Exit"};
    final public static int BUTTONS_START_Y = 180;
    final public static int BUTTONS_VERTICAL_SPACING = 40;//vertical spacing between buttons
    
    public MainMenu(){
        super();
        super.setLogoImageFromPath("tankFighterLogo.png");
        resetState();        
       
    }

    @Override
    public int update() {
        return GameStateHandler.MAIN_MENU_STATE;//the game state remains in this state until the "return to menu" button is pressed        
    }

    @Override
    public void draw(Graphics2D g, ImageObserver io) {
            //draw title
            for (Button b : getButtons()) {
                b.drawWithShadow(g);
            }
            //draw logo
            drawLogo(g, io);
            
            //draw tank images
            BufferedImage greenTank = null;
            BufferedImage yellowTank = null;
            greenTank = ResourceGetter.getBufferedImage("greenTankIcon.png");
            yellowTank = ResourceGetter.getBufferedImage("yellowTankIcon.png");
            
  
            
            g.drawImage(greenTank, 520, 250, 250, 250, io);
            g.drawImage(yellowTank, 30, 250, 250, 250, io);            
            
    }

    @Override
    public void resetState() {
        //initialize buttons for each gameState
        for (int i = 0; i < 5; i++) {

            super.addButton(//int x, int y, int width, int height, Color boxColor, String text, int textSize, Color textColor                    
                new Button(FRAME_WIDTH/2 - Button.STANDARD_WIDTH/2, //x
                    BUTTONS_START_Y + i*(Button.STANDARD_HEIGHT + BUTTONS_VERTICAL_SPACING),//y
                    Button.STANDARD_WIDTH,//width
                    Button.STANDARD_HEIGHT,//height
                    Button.STANDARD_BOX_COLOR,//color of rectangle around text                    
                    buttonTexts[i],//text (String)
                    Button.STANDARD_FONT_SIZE,//font size
                    Button.STANDARD_TEXT_COLOR//color of actual text
                )
                    
            );
        }//end creating buttons
    }
    
    
    //returns the new gameState to go to (ex. if "Instructions")
    public int mousePressed(int[] pos){
        String currButtonText = getMenuButtonPressed(pos);
        if(currButtonText.equals("Play Game")){
            return 3;
        }else if(currButtonText.equals("Instructions")){
            return 4;
        }else if(currButtonText.equals("Highscores")){
            return 2;
        }else if(currButtonText.equals("Credits")){
            return 0;
        }else if(currButtonText.equals("Exit")){
            return -1;
        }
        //if the user did not press any of the buttons, stay on the main menu
        return 5;
    }
    /**
     * Gets the button number of the button pressed on the main menu screen
     * @return the number of the button (ex. 0 = play game), -1 if none are pressed
     */
    private String getMenuButtonPressed(int[] mousePos){
        for (Button b : getButtons()) {
            if(b.isInBounds(mousePos)){
                return b.getText();
            }
        }
        return "";
    }
}
