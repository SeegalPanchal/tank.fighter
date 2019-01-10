/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The JPanel object which we draw our game on
 */
package tankfighter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class DrawingSurface extends JPanel implements ActionListener, MouseMotionListener, MouseListener{
    
    //the MainGame object which processes most of what we will be drawing and updating in the game
    GameStateHandler game;
    //a timer to allow us to update the game every frame
    Timer timer;
    public DrawingSurface(){
        //create the MainGame
        game = new GameStateHandler();
        //create a keyListener
        addKeyListener(new TAdapter());
        //create a MouseMotionListener (so we always know its x and y position)
        addMouseMotionListener(this);
        //create a MouseListener (so we know when the left-click is pressed, indicating a button press or a shot from the player)
        addMouseListener(this);
        //focus on the JPanel so the user does not have to click it when the program starts
        setFocusable(true);
        setBackground(Color.WHITE);
        //initialize and start the Timer
        timer = new Timer(10, this);//delay per frame (ms). default: 10
        timer.start();
    }
    
    //does the actual drawing
    private void doDrawing(Graphics g) {
        //the Graphics2D class is the class that handles all the drawing
        //must be casted from older Graphics class in order to have access to some newer methods
        Graphics2D g2d = (Graphics2D) g;
        game.drawGame(g2d, this);
        
        
    }
    //tests for key clicks
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            game.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            game.keyPressed(e);
        }
    }
     
    
    @Override    
    public void mouseMoved(MouseEvent e) {
        //get the position of the mouse
       int[] pos = {e.getX(), e.getY()};
       game.mouseMoved(pos);
    }
    
    //we don't actually need this event lisetener but it is necessary because we implement the interface
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    
    //we don't actually need this event lisetener but it is necessary because we implement the interface
    @Override
    public void mouseClicked(MouseEvent e){
    }
    
    @Override
    public void mousePressed(MouseEvent e){
        game.mousePressed();
    }
    
    //we don't actually need this event lisetener but it is necessary because we implement the interface
    @Override
    public void mouseReleased(MouseEvent e){
    }
    
    //we don't actually need this event lisetener but it is necessary because we implement the interface
    @Override
    public void mouseExited(MouseEvent e){   
    }
    
    //we don't actually need this event lisetener but it is necessary because we implement the interface
    @Override
    public void mouseEntered(MouseEvent e){        
    }
    
    //overrides paintComponent in JPanel class
    //performs custom painting
    @Override
    public void paintComponent(Graphics g) {        
        super.paintComponent(g);//does the necessary work to prepare the panel for drawing
        
        doDrawing(g);
    }
    
    //when our timer hits the next iteration, update the game and redraw it
    @Override
    public void actionPerformed(ActionEvent e){
        game.update();
        repaint();
    }
    
    
    
}
