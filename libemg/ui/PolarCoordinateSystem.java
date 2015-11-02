package rascal.libemg.ui;

import rascal.libemg.Position;

/**
 * A general polar coordinate system for transforming between input coordinate
 * pair radius and theta (bounded by [0, 1] and [-1, 1] respectively) and 
 * screen coordinates.
 */
public class PolarCoordinateSystem extends CoordinateSystem {
    private Position origin;
    
    /**
     * Creates a new polar coordinate system with the origin specified by 
     * origin_x and origin_y fractions of screen width and height,
     * respectively, where the "screen coordinate system" has it's origin at
     * the bottom left and the upper right corner corresponds to [1, 1].
     * @param origin_x : fraction of screen width at which the coordinate
     * system is originated
     * @param origin_y : fraction of screen height at which the coordinate
     * system is originated
     */
    public PolarCoordinateSystem(float origin_x, float origin_y) {
        super(origin_x, origin_y);
        setBounds(-1, 1, 1, -1);
    }
    
    /**
     * Transforms an input position to screen coordinates based on the
     * dimensions of the screen.
     * @param input : input position -- first coordinate is assumed to be the
     * radius [0, 1], second coordinate is assumed to be theta [-1, 1].
     * @param width : width of the screen
     * @param height : height of the screen
     * @return the transformed position in screen coordinates (pixels)
     */
    public Position transform(Position input, float width, float height) {
        Position output = new Position();
        
        float bound_x = Math.max(origin.getQ1(), 1-origin.getQ2()) * width;
        float bound_y = Math.max(origin.getQ1(), 1-origin.getQ2()) * height;
        float bound_r = Math.min(bound_x, bound_y);
        
        output.setQ1(origin.getQ1()*width + 
                (input.getQ1()*bound_r)*(float)Math.cos(input.getQ2()*Math.PI));
        output.setQ2(origin.getQ2()*height +
                (input.getQ1()*bound_r)*(float)Math.sin(input.getQ2()*Math.PI));
        
        return output;
    }
}
