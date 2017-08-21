package rascal.libemg;

/**
 * A pair of float coordinates that can be used for many purposes. For example,
 * the coordinates could be Cartesian (x and y) or polar (r and theta). Here,
 * the coordinate pair is treated as generalized [q1, q2].
 */
public class Position {
    private float q1, q2;
    
    /**
     * Creates a new position with default coordinates [0, 0].
     */
    public Position() {
        this.q1 = 0;
        this.q2 = 0;
    }
    
    /**
     * Creates a new position with specified coordinates [q1, q2].
     * @param q1 : first generalized coordinate
     * @param q2 : second generalized coordinate
     */
    public Position(float q1, float q2) {
        this.q1 = q1;
        this.q2 = q2;
    }
    
    /**
     * Scales the position by dividing each coordinate by the corresponding
     * input argument.
     * @param a1 : scaling factor for first coordinate (q1)
     * @param a2 : scaling factor for second coordinate (q2)
     */
    public void scale(float a1, float a2) {
        q1 = q1/a1;
        q2 = q2/a2;
    }
    
    /**
     * Translates the position by adding to each coordinate the corresponding
     * input argument.
     * @param a1 : translation in the q1 direction
     * @param a2 : translation in the q2 direction
     */
    public void translate(float a1, float a2) {
        q1 += a1;
        q2 += a2;
    }
    
    /**
     * Transforms the Position with a 2x2 matrix. Standard row-column indices
     * are used for the arguments forming the transformation matrix.
     * @param a11 : row 1, column 1
     * @param a12 : row 1, column 2
     * @param a21 : row 2, column 1
     * @param a22 : row 2, column 2
     */
    public void transform(float a11, float a12, float a21, float a22) {
        float temp_x = a11*q1 + a12*q2;
        float temp_y = a21*q1 + a22*q2;
        
        q1 = temp_x;
        q2 = temp_y;
    }
    
    /**
     * Gets the current position's first generalized coordinate.
     * @return q1
     */
    public float getQ1() {
        return q1;
    }
    
    /**
     * Returns the current position's second generalized coordinate.
     * @return q2
     */
    public float getQ2() {
        return q2;
    }
    
    /**
     * Sets the first generalized coordinate of the position.
     * @param q1 : the new value of q1
     */
    public void setQ1(float q1) {
        this.q1 = q1;
    }
    
    /**
     * Sets the second generalized coordinate of the position.
     * @param q2 : the new value of q2
     */
    public void setQ2(float q2) {
        this.q2 = q2;
    }
    
    /**
     * Sets both generalized coordinates by copying the input Position's 
     * coordinates.
     * @param p : input position to copy values from
     */
    public void setPosition(Position p) {
        this.q1 = p.getQ1();
        this.q2 = p.getQ2();
    }
}
