package rascal.libemg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple class for keeping track of trials throughout a session.
 */
public class ExperimentSession {
    /** Maximum number of times a target can be seen in a row */
    private static final int MAX_REPEAT = 3;
    /** Maximum number of times the randomization can run */
    private static final int MAX_ITERS = 100;
    
    private int numTrials;
    private int numTargets;
    private int currentTrial = 1;
    private List<Integer> targetSequence = new ArrayList<Integer>();
    private int numIters = 0;
    private int[][] successes;
    
    /**
     * Initializes the session with the indices of the targets in randomized
     * order that will be used throughout the session. Make sure numTrials is
     * evenly divisible by numTargets.
     * @param numTrials : the number of trials in the session
     * @param numTargets : the number of targets to generate indices for
     */
    public ExperimentSession(int numTrials, int numTargets) {
        this.numTrials = numTrials;
        this.numTargets = numTargets;
        
        successes = new int[numTargets][2];
        
        initTargetSequence();
    }
    
    /**
     * Generates a list of target indices and randomizes it for the session.
     * Also checks to make sure there aren't too many of the same target in 
     * consecutive trials. Filtering lists with too many of the same target 
     * in a row means this method is applied recursively, so a maximum number
     * of iterations is enforced.
     */
    private void initTargetSequence() {
        numIters++;
        
        // build list of target indices with an entry for each trial
        for (int t = 0; t < numTargets; t++) {
            for (int i = 0; i < numTrials/numTargets; i++) {
                targetSequence.add(t);
            }
        }
        
        // shuffle the list
        Collections.shuffle(targetSequence);
        
        // check if there are too many repeated targets and if so, try again
        if (checkRepeats(targetSequence, MAX_REPEAT) && numIters < MAX_ITERS) {
            targetSequence.clear();
            initTargetSequence();
        }
    }
    
    /**
     * Checks if a list of numbers has any elements that are repeated in
     * maxRepeat consecutive positions in the list. 
     * @param list : the list of numbers to check for repeats
     * @param maxRepeat : maximum number of times the element can appear in
     * consecutive positions in the list
     * @return true if an element is repeated maxRepeat times, false otherwise
     */
    private static boolean checkRepeats(List<Integer> list, int maxRepeat) {
        boolean tooManyRepeats = true;
        
        for (int i = 0; i < list.size() - (maxRepeat); i++) {
            tooManyRepeats = true;
            for (int j = 1; j < maxRepeat+1; j++) {
                if (list.get(i+j) != list.get(i)) {
                    tooManyRepeats = false;
                    break;
                }
            }

            // if we fall out of the small loop without having set the flag to
            // false, we have at least maxRepeat repeats
            if (tooManyRepeats) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Unimplemented.
     */
    public void startTrial() {
    }
    
    /**
     * Increments the trial counter and updates the accuracy values.
     * @param success : true if the trial was successful, false otherwise
     */
    public void endTrial(boolean success) {
        successes[getCurrentTarget()][0] += success ? 1 : 0;
        successes[getCurrentTarget()][1] += 1;
        currentTrial++;
    }
    
    /**
     * Unimplemented.
     */
    public void restartTrial() {

    }
    
    /**
     * Gets the current trial index (1-indexed!).
     * @return the current trial number
     */
    public int getCurrentTrial() {
        return currentTrial;
    }
    
    /**
     * Gets the current target index (0-indexed).
     * @return the current goal target index
     */
    public int getCurrentTarget() {
        return targetSequence.get(currentTrial-1);
    }
    
    /**
     * Gets the accuracy (successes divided by attempts) for each target based
     * on the successes reported at the end of each trial in endTrial().
     * @param target : the target to get the accuracy for
     * @return the accuracy (0 to 1)
     */
    public float getAccuracy(int target) {
        return successes[target][0] / (float)successes[target][1];
    }
    
    /**
     * Gets the overall accuracy (successes divided by attempts) for all
     * targets.
     * @return the average accuracy over all targets
     */
    public float getAverageAccuracy() {
        int successCount = 0;
        int attemptCount = 0;
        for (int i = 0; i < numTargets; i++) {
            successCount += successes[i][0];
            attemptCount += successes[i][1];
        }
        
        return successCount / (float)attemptCount;
    }
}
