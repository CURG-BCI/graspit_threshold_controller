package rascal.libemg.proc;

/**
 * Implements a simple moving average filter which is meant to be updated point
 * by point (sample by sample).
 */
public class MovingAverageFilter {
    
    private float[] inputs;
    private int m;
    private float out;
    
    /**
     * Creates a moving average filter of length m. The length determines
     * the number of past inputs to use in calculating the output.
     * @param m : number of input samples to use in the filter
     */
    public MovingAverageFilter(int m) {
        this.m = m;
        
        inputs = new float[m];
    }
    
    /**
     * Updates the filter by adding a new input and returning the current
     * output.
     * @param x : new input value
     * @return Current filtered output.
     */
    public float update(float x) {
        for (int i = 0; i < m-1; i++) {
            inputs[i] = inputs[i+1];
        }
        inputs[m-1] = x;
        
        out = 0;
        for (int i = 0; i < m; i++) {
            out += inputs[i];
        }
        out /= m;
        
        return out;
    }
}