/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * Used to hold a score + player name for searching and sorting highscores
 */

package GameObjects;


public class Score {
    
    private String name;//player name
    private int score;//player score
    
    //constructor
    public Score(){
        name = "";
        score = -1;
    }
    
    //constructor given name and score
    public Score(String n, int s){
        name = n;
        score = s;
    }

    //getters
    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }
    public void setScore(int score) {
        this.score = score;
    }
    
    
}
