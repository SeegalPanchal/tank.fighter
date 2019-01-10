/*
    Seegal, Ian, Daniel
    June 12th 2017
    This is a vector class that defines and creates vectors with direction. It was created to mathematically simplify
    collision between tanks. Because tanks can be on an angle, they have a direction, as such, this class along with
    it's methods such as dot product, projection, etc.. are used in collision.
 */
package GameObjects;

public class Vector {
    private float x;
    private float y;

    // x and y of the vector
    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    //note: the corners of the rectangles in the arrays must be in order
    //(they cannot go corner to opposite corner)
    /**
     * 
     * @param r1 vector of one tanks side
     * @param r2 vector of other tanks side
     * @return if they are colliding (true if they are, false if they are not)
     */
    public static boolean areRotatedRectanglesColliding(Vector[] r1, Vector[] r2){
        //the axes we will be projecting the rectangles onto
        Vector[] axes = new Vector[4];
        //axes normal to r1's sides
        axes[0] = r1[0].subtract(r1[1]);
        axes[1] = r1[1].subtract(r1[2]);
        //axes normal to r2's sides        
        axes[2] = r2[0].subtract(r2[1]);
        axes[3] = r2[1].subtract(r2[2]);
        
        for (Vector axis : axes) { // for every vector in axes
            Vector[] r1Proj = new Vector[4];
            Vector[] r2Proj = new Vector[4];
            
            // project every vector in the two parameter array's onto the current axis (vector from axes)
            for (int i = 0; i < 4; i++) {
                r1Proj[i] = r1[i].projectOnto(axis);
                r2Proj[i] = r2[i].projectOnto(axis);
            }
            
            // find the shortest and longest vector projections in the two arrays
            Vector r1Shortest = shortestParallelVector(r1Proj);
            Vector r1Longest = longestParallelVector(r1Proj);
            Vector r2Shortest = shortestParallelVector(r2Proj);            
            Vector r2Longest = longestParallelVector(r2Proj);
            
            // if the longest projection from r1 is less than the shortest projection from r2
            // but if the x's are the same, check that for y
            if(r1Longest.x < r2Shortest.x || (r1Longest.x == r2Shortest.x && r1Longest.y < r2Shortest.y)){
                return false; // they aren't colliding
            }
            // if the longest projection from r2 is less than the shortest projection from r1
            // but if the x's are the same, check that for y
            if(r2Longest.x < r1Shortest.x || (r2Longest.x == r1Shortest.x && r2Longest.y < r1Shortest.y)){
                return false; // they arent colliding
            }
            
        }
        return true; // they are
    }
    
    /**
     * 
     * @param v array of vectors
     * @return the longest vector in the array of vectors
     */
    public static Vector longestParallelVector(Vector[] v){
        int index = 0;
        float longestX = v[0].x;
        for (int i = 1; i < v.length; i++) { // loop through array
            if(v[i].x > longestX){ // if the x at current index is greater than last x that was really long
                longestX = v[i].x; // set the new longest
                index = i; // set the new index
            }else if(v[i].x == longestX){ // if the x value is the same as the previous longest
                if(v[i].y > v[index].y){ // check the y value
                    index = i; // set the new index
                }
            }
        }
        return v[index]; // return the index of the longest vector in the array
    }
    
    /**
     * 
     * @param v takes an array of vectors
     * @return finds and returns the shortest vector in the array
     */
    public static Vector shortestParallelVector(Vector[] v){
        int index = 0;
        float shortestX = v[0].x;
        for (int i = 1; i < v.length; i++) { // loop through array
            if(v[i].x < shortestX){ // find shortest x value
                shortestX = v[i].x;
                index = i; // set the index to be returned
            }else if(v[i].x == shortestX){ // if two X values are the same, compare the y's
                if(v[i].y < v[index].y){ // if the y is smaller
                    index = i; // set the index to be returned to the new vector
                }
            }
        }
        return v[index]; // return the index of the shortest vector
    }
    
    // subtract two vectors
    public Vector subtract(Vector v){
        return new Vector(x - v.x, y - v.y);
    }
    
    // multiple the vector by a scalar multiple (increase size of vector)
    public Vector multiplyByScalar(float s){
        return new Vector(x*s, y*s);
    }
    
    // project a vector
    public Vector projectOnto(Vector v){
        return v.multiplyByScalar(this.dotProduct(v) / (float)(Math.pow(v.getMagnitude(), 2)));
    }
    
    /**
     * 
     * @param v takes a vector
     * @return the dot product (VectA*VectB*CosAngle)
     */
    public float dotProduct(Vector v){
        return x*v.x + y*v.y;
    }
    
    /**
     * returns the size of the vector, without direction
     * @return the magnitude of the vector
     */
    public float getMagnitude(){
        return (float)Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    
    
    
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    @Override
    public String toString(){
        return "(" + x + ", " + y + ")";
    }
}
