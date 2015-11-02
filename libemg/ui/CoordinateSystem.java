package rascal.libemg.ui;

import rascal.libemg.Position;

public abstract class CoordinateSystem {
    
    public enum Dimension {
        WIDTH, HEIGHT
    };
    
    private float leftBound, topBound, rightBound, bottomBound;
    private float width, height;
    private Position origin;
    
    public CoordinateSystem(float origin_x, float origin_y) {
        origin = new Position(origin_x, origin_y);
    }
    
    public abstract Position transform(Position input, float width, float height);
    
    public void setBounds(float left, float top, float right, float bottom) {
        leftBound = left;
        topBound = top;
        rightBound = right;
        bottomBound = bottom;
        
        width = rightBound - leftBound;
        height = topBound - bottomBound;
    }
    
    /**
     * Scales a number (perhaps a dimension) to screen coordinates.
     * @param num : number to scale
     * @param dim : dimension to scale with (either screen width or height)
     * @param dimType : type of dimension that dim is
     * @return the scaled number
     */
    public float scale(float num, float dim, Dimension dimType) {
        float ret = 0;
        switch (dimType) {
        case WIDTH:
            ret = (num/width)*dim;
            break;
        case HEIGHT:
            ret = (num/height)*dim;
            break;
        }
        
        return ret;
    }
    
    /**
     * Inverse scales a number (perhaps a dimension) in screen coordintaes
     * to coorinate system coordinates.
     * @param num : number to scale
     * @param dim : dimension to scale with (either width or height)
     * @param dimType : type of dimension that dim is
     * @return the scaled number
     */
    public float iscale(float num, float dim, Dimension dimType) {
        return scale(num, 1/dim, dimType);
    }
    
    public Position getOrigin() { return origin; }
    
    public float getLeftBound() { return leftBound; }
    
    public float getRightBound() { return rightBound; }
    
    public float getTopBound() { return topBound; }
    
    public float getBottomBound() { return bottomBound; }
    
    public float getWidth() { return width; }
    
    public float getHeight() { return height; }
}
