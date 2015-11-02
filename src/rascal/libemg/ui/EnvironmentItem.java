package rascal.libemg.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import rascal.libemg.Position;
import rascal.libemg.ui.CoordinateSystem.Dimension;

public class EnvironmentItem {
    private CoordinateSystem coord;
    private Position normPosition;
    private Position screenPosition;
    private float screenWidth = 0;
    private float screenHeight = 0;
    private float normRadius;
    private float radius;
    private Paint paint;
    private boolean visible;
    
    /**
     * Constructs an item with the specified size and color.
     * @param coord : a coordinate system with respect to which the cursor is
     * drawn on screen
     * @param normRadius : specify as a percentage of screen width/height
     * @param color : use the Color class to generate a color code 
     * (ex. Color.RED or Color.rgb(0.5, 0.2, 0.3))
     */
    public EnvironmentItem(CoordinateSystem coord, float radius, int color) {
        this.coord = coord;
        this.normRadius = radius;
        
        this.normPosition = new Position();
        this.screenPosition = new Position();
        
        this.visible = true;
        
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
    }
    
    /**
     * Draws the item to the provided canvas.
     * @param canvas : the canvas to draw to
     */
    public void draw(Canvas canvas) {
        if (visible) {
            canvas.drawCircle(
                    screenPosition.getQ1(), 
                    screenHeight - screenPosition.getQ2(),
                    radius, 
                    paint);
        }
    }
    

    
    /**
     * Sets the width of the screen that the item is drawn in for converting
     * between normalized and screen positions. This is updated by
     * {@link CursorEnvironment} when it changes size, so it does not normally
     * need to be called directly.
     * @param width : width of the screen
     * @param height : height of the screen
     */
    public void setScreenDimensions(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
        
        setPosition(normPosition.getQ1(), normPosition.getQ2());
        setNormRadius(normRadius);
    }
    
    /**
     * Sets the normalized radius of the item, as a fraction of screen width.
     * @param radius : normalized radius
     */
    public void setNormRadius(float radius) {
        this.normRadius = radius;
        this.radius = coord.scale(radius, getScreenWidth(), Dimension.WIDTH);
    }
    
    /**
     * Sets the radius in screen coordinates (pixels).
     * @param radius : the radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
        this.normRadius = coord.iscale(radius, getScreenWidth(), Dimension.WIDTH);
    }
    
    /**
     * Gets the radius of the item on the screen.
     * @return the radius in units of pixels
     */
    public float getRadius() {
        return radius;
    }
    
    /**
     * Gets the normalized radius, as a fraction of screen width.
     * @return the radius
     */
    public float getNormRadius() {
        return normRadius;
    }
    
    /**
     * Sets the position of the cursor, where the coordinates are meaningful
     * in the context of this item's given coordinate system.
     * @param c1 : the first generalized coordinate
     * @param c2 : the second generalized coordinate
     */
    public void setPosition(float c1, float c2) {
        normPosition.setQ1(c1);
        normPosition.setQ2(c2);
        
        screenPosition = coord.transform(normPosition, screenWidth, screenHeight);
    }
    
    /**
     * Gets the x position of the item on the screen.
     * @return x coordinate in units of pixels positive to the right from the
     * left edge of the Canvas this is drawn on
     */
    public float getX() {
        return screenPosition.getQ1();
    }
    
    /**
     * Gets the y position of the itme on the screen.
     * @return y coordinate in units of pixels positive up from the bottom edge
     * of the Canvas this is drawn on
     */
    public float getY() {
        return screenPosition.getQ2();
    }
    
    /**
     * Gets the normalized x position of the item in the given coordinate
     * system.
     * @return x position in the coordinate system
     */
    public float getNormX() {
        return normPosition.getQ1();
    }
    
    /**
     * Gets the normalized y position of the item in the given coordinate
     * system.
     * @return y position in the coordinate system
     */
    public float getNormY() {
        return normPosition.getQ2();
    }
    
    /**
     * Gets the width dimension (pixels) of the View that this item is drawn
     * on.
     * @return the screen width
     */
    public float getScreenWidth() {
        return screenWidth;
    }
    
    /**
     * Gets the height dimension (pixels) of the View that this item is drawn
     * on.
     * @return the screen height
     */
    public float getScreenHeight() {
        return screenHeight;
    }
    
    public void setCoordinateSystem(CoordinateSystem coord) {
        this.coord = coord;
    }
    
    public CoordinateSystem getCoordinateSystem() {
        return coord;
    }
    
    /**
     * Sets if the item should be drawn or not.
     * @param visible : true if item should be drawn
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Returns whether or not the item is currently visible (drawn).
     * @return true if item is visible
     */
    public boolean isVisible() {
        return visible;
    }
    
    public Paint getPaint() {
        return paint;
    }
    
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
