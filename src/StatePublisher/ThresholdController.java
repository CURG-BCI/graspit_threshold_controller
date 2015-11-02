package StatePublisher;

import java.util.Random;

import rascal.libemg.proc.Util;

public class ThresholdController {
    
    public enum InputState {
        TRANSITIONING,
        LOW,
        MED,
        HIGH
    }
    
    public static final long TRANSITION_DELAY_DEFAULT = 750 * 1000000;
    public static final float LOW_THRESHOLD_DEFAULT = 0.15f;
    public static final float HIGH_THRESHOLD_DEFAULT = 0.42f;
    public static final float FORWARD_INCREMENT_MAX_DEFAULT = 0.1f;
    public static final float FORWARD_INCREMENT_SLOW_DEFAULT = 0.05f;
    public static final float ROTATION_INCREMENT_DEFAULT = (float)Math.PI/16;
    public static final float ATANH07 = 0.867300577f;
    
    private float x;
    private float y;
    private float theta;
    
    private float lowThreshold = LOW_THRESHOLD_DEFAULT;
    private float highThreshold = HIGH_THRESHOLD_DEFAULT;
    private float forwardIncrement = FORWARD_INCREMENT_MAX_DEFAULT;
    private float forwardSlow = FORWARD_INCREMENT_SLOW_DEFAULT;
    private float rotationIncrement = ROTATION_INCREMENT_DEFAULT;
    private long transitionDelay = TRANSITION_DELAY_DEFAULT;
    
    private InputState mode = InputState.LOW;
    private long timestamp;
    private long transitionTimestamp;
    private float prevVal;
    
    public ThresholdController(float lowThreshold, float highThreshold,
            float forwardIncrement, float rotationIncrement, float forwardSlow) {
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.forwardIncrement = forwardIncrement;
        this.rotationIncrement = rotationIncrement;
        
        reset();
    }
    
    public void update(float val) {
        timestamp = System.nanoTime();
        
        if (val < lowThreshold) {
            mode = InputState.LOW;
        }
        else if (val < highThreshold) {
            if (prevVal < lowThreshold || prevVal > highThreshold) {
                transitionTimestamp = timestamp;
                mode = InputState.TRANSITIONING;
            }
            
            if (mode != InputState.MED &&
                    timestamp - transitionTimestamp > transitionDelay) {
                mode = InputState.MED;
            }
        }
        else {
            mode = InputState.HIGH;
        }
        
        move(val);
        prevVal = val;
    }
    
    private void move(float val) {
        switch (mode) {
        case LOW:
        	System.out.println("Low: Rotate -" +rotationIncrement);
            // rotate counter-clockwise
            rotate(rotationIncrement);
            break;
        case TRANSITIONING:
        	System.out.println("Transition");
            // do nothing
            break;
        case MED:
        	System.out.println("Medium: Forward Slow -"+forwardSlow );
            // move forward at constant speed
            forward(0);
            break;
        case HIGH:
            // go forward
        	System.out.println("High: Forward Fast -"+forwardSlow + forwardIncrement*(val*val) );
            forward((val-highThreshold)/(1-highThreshold));
            break;
        }
    }
    
    private void rotate(float speed) {
        theta = Util.wrapAngle(theta + speed);
    }
    
    private void forward(float val) {
        if (val > 1) {
            val = 1;
        }
        if (val < 0) {
            val = 0;
        }
        
        // parabolic profile with an offset
        val = forwardSlow + forwardIncrement*(val*val);
        
        x = clipCoord(x + val*(float)Math.cos(theta));
        y = clipCoord(y + val*(float)Math.sin(theta));
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getTheta() {
        return theta;
    }
    
    public void reset() {
        x = 0;
        y = 0;
        theta = 0;
    }
    
    public void resetRandAngle() {
        reset();
        Random r = new Random();
        theta = 2*Util.FPI*r.nextFloat();
    }
    
    private static float clipCoord(float coord) {
        if (coord < -1) {
            coord = -1;
        }
        if (coord > 1) {
            coord = 1;
        }
        
        return coord;
    }
    
    public InputState getInputState() {
        return mode;
    }
    
    public void setLowThreshold(float threshold) {
        lowThreshold = threshold;
    }
    
    public float getLowThreshold() {
        return lowThreshold;
    }
    
    public void setHighThreshold(float threshold) {
        highThreshold = threshold;
    }
    
    public float getHighThreshold() {
        return highThreshold;
    }
    
    public void setRotationIncrement(float val) {
        rotationIncrement = val;
    }
    
    public float getRotationIncrement() {
        return rotationIncrement;
    }
    
    public void setForwardIncrement(float val) {
        forwardIncrement = val;
    }
    
    public float getForwardIncrement() {
        return forwardIncrement;
    }
    
    public void setForwardSlow(float val) {
        forwardSlow = val;
    }
    
    public float getForwardSlow() {
        return forwardSlow;
    }
    
    public void setTransitionDelay(long millis) {
        transitionDelay = millis * 1000000;
    }
    
    public long getTransitionDelay() {
        return transitionDelay / 1000000;
    }
}
