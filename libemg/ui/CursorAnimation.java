package rascal.libemg.ui;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

/**
 * Simple extension of Animation for showing a Cursor move smoothly from
 * one point to the next, though it is not necessarily bound to the Cursor
 * class in any way. When startAnimation is called on a View, this class
 * fires a callback (see {@link CursorAnimationInterface}) each time the 
 * interpolator gives a new position. You can then update the view with
 * the new position and invalidate it.
 */
public class CursorAnimation extends Animation {
    private float from_x, from_y, to_x, to_y;
    private OnCursorAnimationUpdateListener listener;
    
    /**
     * Initializes the CursorAnimation with a default LinearInterpolator.
     */
    public CursorAnimation() {
        super();
        setInterpolator(new LinearInterpolator());
    }
    
    /**
     * Registers a callback to be invoked when the animation produces a new
     * position.
     * @param listener : the callback to run 
     */
    public void setAnimationUpdateListener(OnCursorAnimationUpdateListener listener) {
        this.listener = listener;
    }
    
    /**
     * Sets the two points for the cursor to animate between.
     * @param from_x : starting point x coordinate
     * @param from_y : starting point y coordinate
     * @param to_x : ending point x coordinate
     * @param to_y : ending point y coordinate
     */
    public void setPositions(float from_x, float from_y, float to_x, float to_y) {
        this.from_x = from_x;
        this.from_y = from_y;
        this.to_x = to_x;
        this.to_y = to_y;
    }
    
    @Override
    public void applyTransformation(float interpolatedTime, Transformation t) {
        listener.onCursorAnimationUpdate(
                from_x + (to_x - from_x)*interpolatedTime,
                from_y + (to_y - from_y)*interpolatedTime);
    }
    
    /**
     * Interface for callback invoked when the animation produces a new 
     * position.
     */
    public interface OnCursorAnimationUpdateListener {
        
        /**
         * Callback that is invoked when the animation produces a new position.
         * @param x : the x coordinate of the new position
         * @param y : the y coordinate of the new position
         */
        public void onCursorAnimationUpdate(float x, float y);
    }
}
