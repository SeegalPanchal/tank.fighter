/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The class which creates the game frame (JFrame)
 */
package tankfighter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame{
    //constructor
    public GameFrame() {
        //create the User interface
        initUI();
    }
    
    //create the custom JFrame
    private void initUI() {
        //set title of the JFrame
        setTitle("Tank Fighter");
        
        //add a custom JPanel to draw on
        add(new DrawingSurface());
        //set the size of the window
        setSize(816, 689); //inner frame is 800 x 650
        //tell the JFrame what to do when closed
        //this is important if our application has multiple windows
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                //instantiate the main window
                GameFrame windowFrame = new GameFrame();
                //make sure it can be seen
                windowFrame.setVisible(true);
            }
        });
    }
}
