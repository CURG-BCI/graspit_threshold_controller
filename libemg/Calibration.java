package rascal.libemg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for calculating and keeping track of calibration values. 
 * Calibration values can be added directly or by adding a set of band power
 * values over time which is processed through a peak detector to obtain the
 * calibration value.
 */
public class Calibration implements Iterable<Position> {
    public static final int NUM_CONTRACTIONS = 3;
    public static final int CONTRACTION_LENGTH_SECONDS = 3;
    
    private List<Position> calList = new ArrayList<Position>();
    private Position calAvg;
    
    /**
     * Initializes the Calibration with zero values for the mean calibration.
     */
    public Calibration() {
        calAvg = new Position(0, 0);
    }
    
    @Override
    public Iterator<Position> iterator() {
        return calList.iterator();
    }
    
    /**
     * Adds a set of samples (usually a set of band power values in time) for
     * calculating a single calibration value which corresponds to a single
     * contraction.
     * @param samples 2xN array of floats where N is the number of samples for
     * each band
     * @return a Position corresponding to the calibration value calculated
     * from the input samples.
     */
    public Position addContraction(float[][] samples) {
        Position cal = calculateCalibrationValue(samples);
        calList.add(cal);
        calAvg.setPosition(calculateMeanContraction(calList));
        
        return cal;
    }
    
    public void addCalibrationValue(Position cal) {
        calList.add(new Position(cal.getQ1(), cal.getQ2()));
        calAvg.setPosition(calculateMeanContraction(calList));
    }
    
    /**
     * Convenience method for calculating the mean of all contraction values
     * added.
     * @return the average calibration value
     */
    public Position getMeanCalibration() {
        return calAvg;
    }
    
    /**
     * Removes all calibration values added and resets the mean to zero.
     */
    public void clear() {
        calList.clear();
        calAvg.setQ1(0);
        calAvg.setQ2(0);
    }
    
    /**
     * Calculates a calibration value by picking the maximum value of the power
     * in each band during a single contraction.
     * @param samples : 2D array of power samples, where the first index is the
     * band and the second index is the sample index during the contraction
     * @return a Position with coordinates set to the max value of each band
     */
    private static Position calculateCalibrationValue(float[][] samples) {
        Position mvc = new Position(0, 0);
        float maxima[] = {0, 0};
            
        for (int i = 0; i < samples.length; i++) {
            if (samples[i][0] > maxima[0]) {
                maxima[0] = samples[i][0];
            }
            
            if (samples[i][1] > maxima[1]) {
                maxima[1] = samples[i][1];
            }
        }
            
        mvc.translate(maxima[0], maxima[1]);
        
        return mvc;
    }
    
    /**
     * Calculates the mean of each coordinate of a list of Positions.
     * @param positionList : list of positions to average over
     * @return the mean of each coordinate
     */
    private static Position calculateMeanContraction(List<Position> positionList) {
        Position mean = new Position(0, 0);
        int N = positionList.size();
        
        if (N < 1) {
            // just return (0, 0) if the list is empty for whatever reason
            return mean;
        }
        
        for (Position p : positionList) {
            mean.translate(p.getQ1(), p.getQ2());
        }
        mean.scale(N, N);
        
        return mean;
    }
}
