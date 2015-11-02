package rascal.libemg.ui;

import rascal.libemg.Position;

/**
 * A general Cartesian coordinate system with a specified origin and the normal
 * conventions -- positive x to the right, positive y up.
 */
public class CartesianCoordinateSystem extends CoordinateSystem {

    /**
     * Creates a new Cartesian coordinate system with the origin specified
     * by origin_x and origin_y fractions of screen width and height, 
     * respectively, where the "screen coordinate system" has it's origin at
     * the bottom left and the upper right corner corresponds to [1, 1].
     * @param origin_x : fraction of screen width at which the coordinate
     * system is originated
     * @param origin_y : fraction of screen height at which the coordinate
     * system is originated
     */
    public CartesianCoordinateSystem(float origin_x, float origin_y) {
        super(origin_x, origin_y);
        setBounds(-1, 1, 1, -1);
    }
    
    /**
     * Transforms an input position to screen coordinates based on the 
     * dimensions of the screen (i.e. the View this coordinate system is being
     * used for).
     * @param position : position to transform to screen coordinates
     * @param width : width of the screen
     * @param height : height of the screen
     * @return the transformed position
     */
    public Position transform(Position input, float width, float height) {
        Position output = new Position();
        
        output.setQ1((getOrigin().getQ1() + input.getQ1()/getWidth())*width);
        output.setQ2((getOrigin().getQ2() + input.getQ2()/getHeight())*height);
//        float bound_x = Math.max(getOrigin().getQ1(), 1-getOrigin().getQ1()) * width;
//        float bound_y = Math.max(getOrigin().getQ2(), 1-getOrigin().getQ2()) * height;
//        
//        output.setQ1(origin.getQ1()*width + input.getQ1()*bound_x);
//        output.setQ2(origin.getQ2()*height + input.getQ2()*bound_y);
//        
        return output;
    }
}
