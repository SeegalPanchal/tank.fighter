/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The credits screen state
 */
package GameStates;

import Resources.ResourceGetter;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import tankfighter.GameStateHandler;

public class Credits extends GameState{
    
    private BufferedImage credit;//used to hold the image of the credits
    
    public Credits(){
        super();//gets GameState
        super.setLogoImageFromPath("creditsLogo.png");//loads the "Credits" logo
        resetState();
    }

    @Override
    public int update() {
        return GameStateHandler.CREDITS_STATE;//the game state remains in this state until the "return to menu" button is pressed
    }

    @Override
    public void draw(Graphics2D g, ImageObserver io) {
        super.drawLogo(g, io);
        //sets credit to the image of the credits
        credit = ResourceGetter.getBufferedImage("credits.png");
        g.drawImage(credit, 0, 40, null);//loads the credits onto the screen
        getButtons().get(0).drawWithShadow(g);//loads "Return to Main Menu" logo
        
    }
    
    @Override
    public void resetState() {
        getButtons().clear();//clears the loaded buttons
        addReturnToMenuButton();//loads the "Return to Main Menu" button
    }
    
}
