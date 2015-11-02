package rascal.libemg.proc;

import rascal.libemg.Position;

/**
 * Implements dual band pass filters with pass bands of 80-100 Hz and
 * 130-150 Hz for a 4 kHz input signal. Output is a Position with each element
 * set to the power of the input signal after being filtered by each of the
 * bandpass filters. The filters were designed using the butter() function in
 * MATLAB/Octave, with the filter order N = 2 and a sample rate of 4 kHz. The
 * call would look something like:
 * <p>
 * {@code
 * [b1, a1] = butter(2, [80/(4000/2), 100/(4000/2)]);}
 */
public class BandFilter {

    private double[] xv1, xv2, yv1, yv2;
    private Position output;
    
    // passband_x: 80-100 Hz
    private static final double[] b1 = {
            0.000241359049040274,
            0.0,
           -0.000482718098080548,
            0.0,
            0.000241359049040274
    };
    private static final double[] a1 = {
            1.0,
           -3.916599218986691,
            5.790978455131096,
           -3.830543025785111,
            0.956543676511202
    };

    // passband_y: 130-150 Hz
    private static final double[] b2 = {
            0.000241359049035160,
            0.0,
           -0.000482718098070319,
            0,
            0.000241359049035160
    };
    private static final double[] a2 = {
            1.0,
           -3.860791404137975,
            5.682455564664910,
           -3.775961429864676,
            0.956543676511206
    };

    /**
     * Initializes input and output arrays for the two band filters.
     */
    public BandFilter() {
        xv1 = new double[b1.length];
        xv2 = new double[b2.length];
        yv1 = new double[a1.length];
        yv2 = new double[a2.length];
        
        output = new Position();
    }

    /**
     * Filters the input data array with two different filters, outputting
     * the signal power (sum of squares) left after filtering.
     * @param data : input data to filter
     * @return Position representing the power remaining after filtering with
     * each of the filters.
     */
    public Position filter(float[] data) {
        output.setQ1(0);
        output.setQ2(0);

        for(int n = 0; n < data.length; n++){
            
            // shift previous data
            for (int i = 1; i < xv1.length; i++){
                xv1[i-1] = xv1[i];
                xv2[i-1] = xv2[i];
            }
            for (int i = 1; i < yv1.length; i++) {
                yv1[i-1] = yv1[i];
                yv2[i-1] = yv2[i];
            }
            
            // set new input data
            xv1[xv1.length-1] = data[n];
            xv2[xv2.length-1] = data[n];
            
            // run the filter
            yv1[yv1.length-1] = 0;
            yv2[yv2.length-1] = 0;
            for (int i = 0; i < xv1.length; i++) {
                yv1[yv1.length-1] += b1[i]*xv1[xv1.length-1-i];
                yv2[yv2.length-1] += b2[i]*xv2[xv2.length-1-i];
            }
            for (int i = 1; i < yv1.length; i++) {
                yv1[yv1.length-1] -= a1[i]*yv1[yv1.length-1-i];
                yv2[yv2.length-1] -= a2[i]*yv2[yv2.length-1-i];
            }
            yv1[yv1.length-1] /= a1[0];
            yv2[yv2.length-1] /= a2[0];
            
            output.translate(
                    (float)(yv1[yv1.length-1] * yv1[yv1.length-1]),
                    (float)(yv2[yv2.length-1] * yv2[yv2.length-1]));
        }
        
        return output;
    }
}
