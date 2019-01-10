/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * A class that handles the reading of the levels from the data file
 * and converting the text into Level objects
 */
package tankfighter;
import GameObjects.Wall;
import GameObjects.Player;
import GameObjects.Enemy;
import GameObjects.Tank;
import Resources.ResourceGetter;
import java.io.*;
import java.util.ArrayList;

public class LevelReader {
    
    public LevelReader(){
    }
    /**
     * Loads the data file into Level objects
     * @return an ArrayList of all the Levels
     */
    public ArrayList<Level> getLevels(){
        //create empty arraylist
        ArrayList<Level> levels = new ArrayList<>();
        
        //use try/catch to catch IO errors
        try{
            //create BufferedReader
            BufferedReader br = ResourceGetter.getBufferedReader("levels.txt", true);
            //the first line in the file is a number representing the number of levels
            int numLevels = Integer.parseInt(br.readLine());
            //for each level
            for (int level = 0; level < numLevels; level++) {
                //this is not the real position/rotation of the player but it is necessary to initialise it "just in case' it is not in the level text from the data file
                Player player = new Player(0, 0, 0);
                ArrayList<Wall> walls = new ArrayList<>();
                ArrayList<Enemy> enemies = new ArrayList<>();
                
                //for each row
                for (int row = 0; row < GameStateHandler.FRAME_HEIGHT_IN_BLOCKS; row++) {
                    //split the string into individual characters
                    String[] characters = br.readLine().split("");
                    //the integers after each line of 16 characters represents the rotations of the tanks on that line
                    //(0 = 0 degrees, 1 = 90 degrees, 2 = 180 degrees, 3 = 270 degrees)
                    //since there can be multiple tanks on one row we have to have a variable to store which one we are on
                    //this variable is currTankIndex
                    int currTankAngleIndex = 0;
                    //for each character in the row
                    for (int character = 0; character < 16; character++) {
                        //convert the 1-character String into a char
                        char currChar = characters[character].charAt(0);
                        
                        //get the current position in pixels
                        int pixelRow = row*(GameStateHandler.BLOCK_HEIGHT) + 50;
                        int pixelCol = character*(GameStateHandler.BLOCK_WIDTH);
                        
                        //switch statement to do something depending on the character
                        switch(currChar){
                            
                            case '-'://wall
                                walls.add(new Wall(pixelCol, pixelRow));
                                break;
                            case 'B'://breakable wall
                                walls.add(new Wall(pixelCol, pixelRow, true));
                                break;
                            case 'E'://enemy
                                enemies.add(new Enemy(pixelCol, pixelRow, 90*Integer.parseInt(characters[16 + currTankAngleIndex])));
                                currTankAngleIndex++;
                                break;
                            case 'F'://enemy that shoots fast bullet
                                enemies.add(new Enemy(pixelCol, pixelRow, 90*Integer.parseInt(characters[16 + currTankAngleIndex]), Tank.FAST_TYPE));
                                currTankAngleIndex++;                                
                                break;
                            case 'S'://enemy that shoots seeking bullet
                                enemies.add(new Enemy(pixelCol, pixelRow, 90*Integer.parseInt(characters[16 + currTankAngleIndex]), Tank.SEEKING_TYPE));
                                currTankAngleIndex++;                                
                                break;
                            case 'P'://player
                                player = new Player(pixelCol, pixelRow, 90*Integer.parseInt(characters[16 + currTankAngleIndex]));
                                currTankAngleIndex++;                                
                                break;
                        }
                    }
                    
                }//end for each row
                
                //after the Level is read and the objects stored, use them to create a Level
                levels.add(new Level(player, enemies, walls));
            }//end for each level
            
        }catch(IOException e){
            System.out.println("Error: " + e);
        }
        
        return levels;
    }
}
