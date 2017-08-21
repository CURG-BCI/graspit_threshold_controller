package rascal.libemg.ui;

/**
 * Utility class for calculating intersections between shapes. An example use
 * case is to check whether or not a circular cursor is inside a circular
 * target. 
 */
public class Intersection {
    
    /**
     * Checks if one circle intersects another circle. Units are arbitrary,
     * but both circles are assumed to have the same units and scale.
     * @param x1 : x coordinate of first circle
     * @param y1 : y coordinate of first circle
     * @param r1 : radius of first circle
     * @param x2 : x coordinate of second circle
     * @param y2 : y coordinate of second circle
     * @param r2 : radius of second circle
     * @return whether or not the circles are intersecting
     */
    public static boolean isCircleInCircle(
            float x1, float y1, float r1, 
            float x2, float y2, float r2) {
        if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) < r1 + r2) {
            return true;
        }
        else {
            return false;
        }
    }
}
