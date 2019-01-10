/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * The highscores screen
 */
package GameStates;

import GameObjects.Button;
import GameObjects.Score;
import Resources.ResourceGetter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import tankfighter.GameStateHandler;

public class Highscores extends GameState{
    
    private String recentPlayerName;
    private int recentPlayerScore;
    
    //names of the highest scorers
    private Button[] top5nameButtons  = new Button[6];
    private Button[] top5scoreButtons = new Button[6];
    
    
    //ann ArrayList of all the highscores
    private ArrayList<Score> allscores = new ArrayList<>();
    private Score filler = new Score("Player",0);
    final private Color transparent = new Color(0, 0, 0, 0);
    final private Color semiTransparentGray = new Color(100, 100, 100, 80);
    
    public Highscores(){
        super();
        setLogoImageFromPath("highscoresLogo.png");
        resetState();
    }
    
    public void quickSort(ArrayList<Score> n, int left, int right) {
        int i = left;
        int j = right;
        int pivot = n.get((left+right)/2).getScore(); // middle value
        Score temp = new Score("",0);
        while (i < j) {
            while (n.get(i).getScore() > pivot) {
                i++;
            }
            while (n.get(j).getScore() < pivot) {
                j--;
            }
            if (i <= j) {
                temp = n.get(i); // temp = left number
                n.set(i, n.get(j)); // left number turns into right number
                n.set(j, temp); // right number = previous left numbers (swap complete)
                i++; // move index right
                j--; // move index left
            }            
        }
        if (left < j) {
            quickSort(n, left, j);
        }
        if (right > i) {
            quickSort(n, i, right);
        }       
    }


    
    public void readHighscores(){
        //clear the highscores so if the user leaves highscores and then returns then the scores are not doubled
        allscores.clear();
        
        //variables used for holding values when the datafile is read
        String name;
        int score;
        
        //System.out.println(System.getProperty("user.home") + "\\TankFighter_Highscores" + "\\highscores.txt");
        Scanner s = ResourceGetter.getHighscoresScanner();
        //boolean eof = false;
       // s.nextLine();
        while(s.hasNextLine()){
            name = s.nextLine();
            score = Integer.parseInt(s.nextLine());
            allscores.add(new Score(name,score));//add score to the ArrayList

        }

        //need to add 5 filler scores, so the game can 
        //load 5 highscores even if nobody played the game
        for(int i = 0; i < 5; i++){
            allscores.add(filler);
        }

        //sort the highscores in descending order
        quickSort(allscores, 0, allscores.size()-1);
         
    }
    

    @Override
    public int update() {
        return GameStateHandler.HIGHSCORES_STATE;//the game state remains in this state until the "return to menu" button is pressed

    }

    @Override
    public void draw(Graphics2D g, ImageObserver io) {
        super.drawLogo(g, io);
        for (Button nameButton : top5nameButtons) {
            nameButton.draw(g);
        }
        for (Button scoreButton : top5scoreButtons) {
            scoreButton.draw(g);
        }
        getButtons().get(0).drawWithShadow(g);
    }
    
    @Override
    /**
     * Resets the state, including:
     *      -> recalculates the ArrayList of highscores
     *      -> recalculates the top 5 score names and scores
     *      -> if the last player is not in the top 5, place them in 5th place
     *      -> Highlights the last player's name and score
     */
    public void resetState() {
        //read and sort all the highscores
        readHighscores();
        
        //load the top 5 highscores into the top 5 names and scores arrays
        //the first element of each array will be the title
        top5nameButtons[0] = new Button(100,180,200,30,transparent,"Name",36,Color.BLACK);
        top5scoreButtons[0] = new Button(500,180,200,30,transparent,"Score",36,Color.BLACK);
        
        boolean playerInTop5 = false;
        String currName;
        int currScore;
        int currRank = 1;
        for (int i =  1; i < 6; i++) {
            
            currName = allscores.get(i-1).getName();
            currScore = allscores.get(i-1).getScore();
            if(i > 1 && currScore < allscores.get(i-2).getScore()){
                currRank++;
            }
            top5nameButtons[i] = new Button (150,200 + 50*i,275,30,transparent,currRank + ". " + currName,24,Color.BLACK, Button.LEFT_LOCATION);
            top5scoreButtons[i] = new Button(425,200 + 50*i,200,30,transparent,"" + currScore,24,Color.BLACK, Button.RIGHT_LOCATION);
            
            //if the player is in the top 5, highlight their score
            if(recentPlayerName != null && recentPlayerName.equals(currName) && recentPlayerScore == currScore){
                playerInTop5 = true;
                top5nameButtons[i].setBoxColor(semiTransparentGray);
                top5scoreButtons[i].setBoxColor(semiTransparentGray);
            }
        }
        
        //if the player is not in the top 5 scores, display their name at the bottom of the list with their rank
        if(recentPlayerName != null && !playerInTop5){
            //get the player's rank
            currRank = 1;//counter for getting their rank
            for (int i = 0; i < allscores.size(); i++) {
                
                //if the checked score is not the current player's score, add 1 to the counter
                if(i > 0 && allscores.get(i).getScore() < allscores.get(i-1).getScore()){
                    currRank++;
                }
                
                //if the checked score is the current player's score, stop searching
                if(allscores.get(i).getScore() == recentPlayerScore){
                    break;
                }
            }
            
            //write the current player's score to the scoreboard, along with their rank
            top5nameButtons[5] = new Button (150, 450, 275,30,semiTransparentGray, currRank + ". " + recentPlayerName,24,Color.BLACK, Button.LEFT_LOCATION);
            top5scoreButtons[5] = new Button(425, 450, 200,30,semiTransparentGray,"" + recentPlayerScore,24,Color.BLACK, Button.RIGHT_LOCATION);
        }
        addReturnToMenuButton();
    }
    
    
    //setters
    public void setRecentPlayerName(String name) {
        this.recentPlayerName = name;
    }

    public void setRecentPlayerScore(int score) {
        this.recentPlayerScore = score;
    }

    
}
