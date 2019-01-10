/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The instructions menu.
 */
package GameStates;

import Resources.ResourceGetter;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import tankfighter.GameStateHandler;

public class Instructions extends GameState{
    
    private BufferedImage instructionsImage;//used to hold the instructions image

    public Instructions(){
        super();
        super.setLogoImageFromPath("instructionsLogo.png");                
        resetState();        
    }

    @Override
    public int update() {
        return GameStateHandler.INSTRUCTIONS_STATE;//the game state remains in this state until the "return to menu" button is pressed

    }

    @Override
    public void draw(Graphics2D g, ImageObserver io) {
        super.drawLogo(g, io);
        getButtons().get(0).drawWithShadow(g);
        super.drawLogo(g, io);
        //loading instructions from an image
        instructionsImage = ResourceGetter.getBufferedImage("instructions.png");

        g.drawImage(instructionsImage, 40, 155, 700, 350, null);//draws the image of the instructions
        
        getButtons().get(0).drawWithShadow(g);//loads "Return to Main Menu" logo
    }
    
    @Override
    public void resetState() {
        addReturnToMenuButton();            
    }
    
}
