package rascal.libemg.ui;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * A generic View for drawing Cursor, Rest, and Target objects. Those objects
 * should be initialized using normalized coordinates with respect to the 
 * bottom left of the screen. All this View does is call those objects' draw() 
 * methods when it is invalidated. There is also a "paused" function which 
 * hides the Cursors, Rests, and Targets.
 */
public class CursorEnvironment extends View {
    
    protected static final int HIT_NONE = -1;
    private List<EnvironmentItem> items = new ArrayList<EnvironmentItem>();
    private boolean showObjects = true;
    
    public CursorEnvironment(Context context) {
        super(context);
    }
    
    public CursorEnvironment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public CursorEnvironment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    /**
     * We override View onMeasure so there is a simple way to make the BCIView
     * square. If you want it to be square, set either a width or height, then
     * make the other dimension 0dip. For example, in a landscape orientation,
     * you might make the height match_parent and the width 0dip. This makes
     * the BCIView have a height that fills the vertical dimension of the
     * screen and a width that is the same as the height.
     */
    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int widthMode = MeasureSpec.getMode(widthSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);

        int width;
        int height;
        
        int wrapContentWidth = 200;
        int wrapContentHeight = 200;
        
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        else if (widthMode == MeasureSpec.AT_MOST) {
            width = widthSize;
        }
        else {
            width = wrapContentWidth;
        }
        
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else if (heightMode == MeasureSpec.AT_MOST) {
            height = heightSize;
        }
        else {
            height = wrapContentHeight;
        }
        
        if (width == 0) {
            width = height;
        }
        if (height == 0) {
            height = width;
        }
        
        setAllElementDimensions(width, height);
        setMeasuredDimension(width, height);
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        if (showObjects) {
            for (EnvironmentItem item : items) {
                item.draw(canvas);
            }
        }
    }
    
    /**
     * Adds an item (cursor, target, etc.) to the environment to draw on its
     * canvas.
     * @param item : item to draw
     */
    public void addItem(EnvironmentItem item) {
        items.add(item);
    }
    
    /**
     * Removes an item from the environment so it will no longer be drawn.
     * @param item : item to remove
     */
    public void removeItem(EnvironmentItem item) {
        items.remove(item);
    }
    
    /**
     * Gives this view's width and height to the elements (Rest, Target, and
     * Cursor) so they can update their non-normalized coordinates.
     * @param width : screen width (pixels)
     * @param height : screen height (pixels)
     */
    private void setAllElementDimensions(int width, int height) {
        for (EnvironmentItem item : items) {
            item.setScreenDimensions(width, height);
        }
    }
    
    /**
     * Hides the Cursor, Rest, and Target objects by not drawing them on
     * subsequent invalidations. Show them again with showObjects().
     */
    public void hideObjects() {
        showObjects = false;
    }
    
    /**
     * Shows the Cursor, Rest, and Target objects as normally by drawing them
     * on each invalidation.
     */
    public void showObjects() {
        showObjects = true;
    }
}