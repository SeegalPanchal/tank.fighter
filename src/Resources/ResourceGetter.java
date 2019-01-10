/*
 * Ian Liu / Seegal Panchal / Daniel Peng
 * June 13, 2017
 * 
 * A class which handles reading, writing and accessing from resources.
 */
package Resources;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Formatter;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class ResourceGetter {
    
    private static Formatter hsFormatter = null;
    
    public static Scanner getHighscoresScanner(){
        File f2 = new File(ResourceGetter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        f2 = new File((f2.getParent() + "\\TankFighter_Highscores.txt").replace("%20", " "));
        Scanner s = null;
        if(!f2.exists()){
            try{
                f2.createNewFile();
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "error creating file");
            }
        }
        try{
            s = new Scanner(f2);
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "error with reading highscores");
        }
        return s;
    }
    
    //returns a BufferedReader from a filepath
    public static BufferedReader getBufferedReader(String location, boolean gettingLevels) {
        //look right beside the class
        InputStream in;
        if(!gettingLevels){//if getting highscores
            in = new ByteArrayInputStream(location.getBytes());//ResourceGetter.class.getResourceAsStream(location);
        }else{
            in = ResourceGetter.class.getResourceAsStream(location);
        }
        //System.out.println(location);
        //convert stream to reader
        InputStreamReader isr = new InputStreamReader(in);
        //convert the reader to a buffered reader
        return new BufferedReader(isr);
    }
    
    //returns a BufferedImage from a filepath
    public static BufferedImage getBufferedImage(String filepath){
        URL url = ResourceGetter.class.getResource(filepath);
        try{
            return ImageIO.read(url);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "image error " + filepath);
        }
        return null;
    }
    
    
    
    //writes a name and score to the highscores
    public static void writeToHighscores(String name, int score){
        //saveHighscore(ResourceGetter.class.getResource("highscores.txt"), name, score);
        File f = new File(System.getProperty("user.home") + "\\TankFighter_Highscores.txt");
        File f2 = new File(ResourceGetter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        f2 = new File((f2.getParent() + "\\TankFighter_Highscores.txt").replace("%20", " "));
        
        if(!f2.exists()){
            try{
                f2.createNewFile();
            }catch(IOException e){
                System.out.println(e);
            }
            
        }
        
        try{
            PrintWriter pw = new PrintWriter(new FileWriter(f2, true));
            pw.append(name + "\n" + score + "\n");
            pw.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }
     
    private static void saveHighscore(URL resourceStream, String name, int score){
        //JOptionPane.showMessageDialog(null, "starting to save highscore");
        File folder = new File(System.getProperty("user.home"), "TankFighter_Highscores");
        if(!folder.exists() && !folder.mkdirs()) {
           //failed to create the folder, probably exit
           throw new RuntimeException("Failed to create save directory.");
        }

        File myFile = new File(folder, "highscores.txt");
        if(myFile.exists()){
            try{

                final PrintWriter pw = new PrintWriter(myFile);
                System.out.println(pw);
                pw.append(name + "\n");
                pw.append("" + score + "\n");
                pw.close();
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        /*File file = null;
        BufferedWriter bw = null;
        try {
            file = new File(resourceStream.toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(ResourceGetter.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error writing to highscores");
        }
        JOptionPane.showMessageDialog(null, "finished getting file");
        try {
            FileWriter fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);

            bw.write(name + "\n");
            bw.write("" + score + "\n");
            JOptionPane.showMessageDialog(null, "saving to highscore");

            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error getting Buffered Writer (highscores)");
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error in closing the BufferedWriter" + ex);
            }
        }*/
    }
      
   
    
    
}
