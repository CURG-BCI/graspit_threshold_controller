package rascal.libemg.proc;

import java.util.List;

public class Util {
    /** Float representation of pi. Convenient! */
    public static final float FPI = (float)Math.PI;
    
    /**
     * Wraps an angle so it is in the [0, 2pi] range (inclusive). For example,
     * (3*pi -> pi, -pi/2 -> 3*pi/2, etc.)
     * 
     * @param angle : the angle to wrap
     * @return The corresponding angle in the [0, 2pi] range.
     */
    public static float wrapAngle(float angle) {
        if (angle < 0) {
            angle = wrapAngle(angle + 2*FPI);
        }
        
        if (angle > 2*FPI) {
            angle = wrapAngle(angle - 2*FPI);
        }
        
        return angle;
    }
    
    /**
     * Unimplemented!
     */
    public static float interpolateAngle(float from, float to, float amount) {
        return to;
    }
    
    /**
     * Calculates the root-mean-square value of the input array.
     * @param data : data to calculate the RMS value for.
     * @return The RMS value.
     */
    public static float rms(float[] data) {
        float out = 0;
        
        for (int i = 0; i < data.length; i++) {
            out += data[i]*data[i];
        }
        out = (float)Math.sqrt(out/data.length);
        
        return out;
    }

    /**
     * Convenience function for calculating the base-2 log.
     * @param num : the value to compute the base-2 log of.
     * @return The base-2 log of the input.
     */
    public static float log2(float num) {
        return (float)(Math.log(num) / Math.log(2.0f));
    }
    
    /**
     * Computes the arithmetic mean of the input list.
     * @param list : list of numbers to compute the mean over.
     * @return The mean of the input values.
     */
    public static float mean(List<Float> list) {
        if (list.isEmpty()) {
            return 0;
        }
        
        float mean = 0;
        for (Float f: list) {
            mean += f;
        }
        mean /= list.size();
        return mean;
    }
}
