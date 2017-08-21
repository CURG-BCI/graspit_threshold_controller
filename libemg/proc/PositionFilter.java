package rascal.libemg.proc;

import rascal.libemg.Position;

/**
 * A true filter in the sense that it outputs values at the same rate as the
 * input is given to it. The purpose is to further smooth the values coming
 * out of BandFilter. This particular one is a FIR filter with a 0.5Hz cutoff
 * designed using firpm in Matlab (remez in Octave).
 */
public class PositionFilter {
    // f = 4, cutoff = 0.5, order = 5, transition = +/- 0.05
    private static final double[] b = {
        0.150793,
        0.258509,
        0.224878,
        0.224878,
        0.258509,
        0.150793
    };

    public Position output;
    private int i;
    
    // arrays to store low-pass filter points
    private static float inputsX[];
    private static float inputsY[];
    
    /**
     * Sets up the input and output arrays, output position, and takes the
     * the current Participant for normalizing the output to MVC.
     * @param part : the currently signed in Participant
     */
    public PositionFilter() {
        inputsX = new float[b.length];
        inputsY = new float[b.length];
        
        output = new Position();
    }

    /**
     * Runs the input through the filter and sets the output Position pos.
     */
    public Position filter(float x, float y) {
        // shift input buffer over
        for(i = 1; i < b.length; i++) {
            inputsX[i-1] = inputsX[i];
            inputsY[i-1] = inputsY[i];
        }
        
        inputsX[b.length-1] = x;
        inputsY[b.length-1] = y;
        
        // calculate output based on filter coefficients and previous inputs
        output.setQ1(0);
        output.setQ2(0);
        for(i = 0; i < b.length; i++) {
            output.translate(
                    (float)(b[i]*inputsX[b.length-1-i]),
                    (float)(b[i]*inputsY[b.length-1-i]));
        }
        
        return output;
    }
}
