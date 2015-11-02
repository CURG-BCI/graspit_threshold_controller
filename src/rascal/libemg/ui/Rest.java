package rascal.libemg.ui;

/**
 * A basic rest area that is a colored circle that can be active or inactive 
 * (usually this corresponds to the cursor being inside or outside the rest
 * area).
 */
public class Rest extends EnvironmentItem {
    private int colorActive;
    private int colorInactive;
    private boolean active = false;
    
    /**
     * Constructs a rest area with the specified position, size, and colors. 
     * The position is defined with respect to the coordinate system origin and
     * the meaning of the two generalized coordinates is determined by the
     * coordinate system itself. Typically, the rest area is at the origin
     * of the coordinate system.
     * @param coord : the coordinate system with respect to which the rest area
     * will be drawn
     * @param c1 : the first generalized coordinate
     * @param c2 : the second generalized coordinate
     * @param radius : the normalized radius (percentage of screen
     * width/height)
     * @param colorInactive : color when the rest is inactive (use Color class)
     * @param colorActive : color when the rest is active
     */
    public Rest(CoordinateSystem coord, float c1, float c2, float radius, 
            int colorInactive, int colorActive) {
        super(coord, radius, colorInactive);
        
        setPosition(c1, c2);

        this.colorActive = colorActive;
        this.colorInactive = colorInactive;
    }

    
    /**
     * Sets whether the active or inactive color should be used to draw the 
     * rest area.
     * @param active : true if the active color should be used
     */
    public void setActive(boolean active) {
        this.active = active;
        getPaint().setColor(active ? colorActive : colorInactive);
    }
    
    /**
     * Returns whether or not the rest is currently active.
     * @return true if it is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
}
