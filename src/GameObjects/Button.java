/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * A class which can create, draw, and test if a certain position is within a button.
 * a Button consists of a box around the button and the text inside of the button.
 */
package GameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

public class Button{
    
    //final class variables
    //these are the standard sizes and colors for a button in the game
    final public static int STANDARD_WIDTH = 200;
    final public static int STANDARD_HEIGHT = 50;
    final public static int STANDARD_FONT_SIZE = 20;
    final public static Color STANDARD_BOX_COLOR =  new Color(40, 120, 10);
    final public static Color STANDARD_TEXT_COLOR = Color.WHITE;
    //class variables showing which integers correspond to which locations so you don't have to remember them
    final public static int CENTER_LOCATION = 0;
    final public static int LEFT_LOCATION = 1;
    final public static int RIGHT_LOCATION = 2;
    
    private int x, y;//coordinates of top-left of the box around the text
    final private int width, height, textSize;//width and height are the dimensions of the box (not the text)
    private String text;
    private Color boxColor, textColor;
    private int textLocation;//if the text should be at the left, center or right of the surrounding box
    
    //constructor
    public Button(int x, int y, int width, int height, Color boxColor, String text, int textSize, Color textColor){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.boxColor = boxColor;
        this.text = text;
        this.textSize = textSize;
        this.textColor = textColor;
        this.textLocation = CENTER_LOCATION;
    }
     public Button(int x, int y, int width, int height, Color boxColor, String text, int textSize, Color textColor, int textLocation){
        this(x, y, width, height, boxColor, text, textSize, textColor);
        this.textLocation = textLocation;
    }
    
     //getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public String getText() {
        return text;
    }

    //setters
    public void setBoxColor(Color boxColor) {
        this.boxColor = boxColor;
    }
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Draws the button with a black "shadow" to the bottom right of the actual button position
     * @param g the Graphics2D object to draw with
     */
    public void drawWithShadow(Graphics2D g){
        //draw shadow
        g.setColor(Color.BLACK);
        g.fillRect(x + 5, y + 5, width, height);
        //darw actual button
        draw(g);
    }
    /**
     * Draws the button
     * @param g the Graphics2D object to draw with    
     */
    public void draw(Graphics2D g) {
        //draw the box surrounding the text
        g.setColor(boxColor);
        g.fillRect(x, y, width, height);
        
        //draw the text
        //create Font
        Font f = new Font("sansSerif", Font.PLAIN, textSize);
        //next we get some measurements about the text to allow us to place the text in the exact center of the box
        //get line metrics to know the height of the font
        LineMetrics lm = f.getLineMetrics(text, g.getFontRenderContext());
        //get rectangular bounding box of the text
        Rectangle2D textBound = f.getStringBounds(text, g.getFontRenderContext());
        //get width of text
        double textWidth = textBound.getWidth();
        //get height of text
        double textHeight = lm.getHeight();
        //get final x position where we will draw the text
        double fontX;
        if(textLocation == CENTER_LOCATION){
            fontX = x + (width/2) - (textWidth/2);
        }else if(textLocation == LEFT_LOCATION){
            //the leading is the space between the baseline of a line to the next line of text
            //this will be approximately the length of one character
            //We use this number x 5 to determine how much spacing there is between the start of the box and the start of the text
            double leadingSpace = lm.getLeading()*5;
            fontX = x + leadingSpace;
        }else{//if textLocation == RIGHT_LOCATION
            double leadingSpace = lm.getLeading()*5;
            fontX = x + width - leadingSpace - textWidth;
        }
        //get final y position where we will draw the text        
        double fontBaselineY = y + (height/2) + (textHeight/2) - lm.getDescent();
        g.setFont(f);
        g.setColor(textColor);
        //draw the text
        g.drawString(text, (int)fontX, (int)fontBaselineY);
    }
    /**
     * Checks whether or not a certain position is within the bounds of the button.
     * Used to check if the user has clicked the button given the mouse position.
     * @param pos the position to check (x, y)
     * @return true if position pos is within the button, false otherwise
     */
    public boolean isInBounds(int[] pos){
        return pos[0] > x && pos[0] < x + width && pos[1] > y && pos[1] < y + height;
        
    }
    //creates a clone of the text
    public Button clone(){
        return new Button(x, y, width, height, boxColor, text, textSize, textColor);
    
    }
}
