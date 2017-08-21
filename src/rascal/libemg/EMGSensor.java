package rascal.libemg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;


/**
 * Represents an EMG sensor for general purpose use in the BCI environment.
 * The EMGSensor continuously reads in data from the microphone hardware at
 * regular intervals, dumping the data to any OnReadListener(s) added to the
 * list.
 */
public class EMGSensor extends Thread {

    /** Default number of updates (reads) performed per second. */
    public static final int DEFAULT_UPDATE_RATE = 4;
    /** Downsampling factor to use on the 8kHz input data */
    private static final int DOWNSAMPLE_FACTOR = 2;
    
    /*
     * Audio recording configuration.
     */
   private static final int SAMPLE_RATE    = 8000;
   private static final int BUFFER_SIZE = 100000;
    
    private int updateRate;
    private int bufferLength;
    AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
    TargetDataLine microphone;
    //AudioFormat format = new AudioFormat()
    
    
    /*
     * Recording state variables.
     */
    /** Indicates when run() has finished its while loop */
    private boolean doneRunning = false;
    /** Indicates the thread should not do anything */
    private boolean stopped = false;
    
   // private AudioRecord recorder;
    private short[] sensorData;
    private float[] outputData;
    private byte[] byteSensorData;
    private List<OnReadListener> readListeners = new ArrayList<OnReadListener>();

    /**
     * Initializes the sensor by allocating buffer storage and setting up the
     * AudioRecord for reading in microphone data. The update rate is set to
     * the default of 4.
     */
    public EMGSensor() throws LineUnavailableException{
        this.updateRate = DEFAULT_UPDATE_RATE;
        init();
    }
    
    /**
     * Initializes the sensor and sets the updateRate.
     * @param updateRate : the number of times per second to read in the 
     * sensor data
     */
    public EMGSensor(int updateRate) throws LineUnavailableException {
        this.updateRate = updateRate;
        init();
    }
    
    private void init() throws LineUnavailableException {

        bufferLength = SAMPLE_RATE / updateRate;
        byteSensorData=new byte[bufferLength*2];
        sensorData =  new short[bufferLength];
        microphone = AudioSystem.getTargetDataLine(format);
        microphone.open();
        System.out.println("Microphone Init");
       
    }
    
    /**
     * Starts the EMGSensor such that data from the microphone is read in as
     * raw EMG data. Data is read incrementally in chunks until the buffer is
     * full. After each chunk is read, the onRead() callback is triggered, and
     * when the buffer fills, the on onBufferFull() callback is triggered.
     */
    @Override
    public void run() {
        doneRunning = false;
        
        microphone.start();
        
        while (!isInterrupted()) {
            while (stopped) {}
            
            // read data from microphone input buffer
            if (isInterrupted()) { break; }
            microphone.read(byteSensorData, 0, bufferLength);
           
            for(int i=0;i<bufferLength-1;i+=2)
            { ByteBuffer bb = ByteBuffer.allocate(2);
            bb.order(ByteOrder.BIG_ENDIAN);
            bb.put(byteSensorData[i]);
            bb.put(byteSensorData[i+1]);
            short shortVal = bb.getShort(0);
            //System.out.println(shortVal);
           sensorData[i/2]=shortVal;
           }        
            

            // downsample for 4 kHz sample rate and scale so data is in [-1, 1]
            if(isInterrupted()) { break; }
            outputData = downsample(sensorData, DOWNSAMPLE_FACTOR, 32767);
            
            // give the new data to any listeners
            if (isInterrupted()) { break; }
            for (OnReadListener l: readListeners) {
                l.onRead(outputData);
            }
        }
        doneRunning = true;
    }
    
    /**
     * Downsamples the data in an array by a specified integer.
     * @param indata : data to downsample
     * @param M : downsampling factor
     * @param scale : scaling factor for normalization
     * @return downsampled data, with length = indata.length / M
     */
    public static float[] downsample(short[] indata, int M, float scale) {
        float[] outdata = new float[indata.length / M];
        for (int i = 0; i < indata.length/M; i++) {
            outdata[i] = indata[M*i] / scale;
        }
        return outdata;
    }
    
    /**
     * Adds a listener for incoming EMG data. The listener's onRead will be
     * called when new data from the sensor is available.
     * @param listener : the listener to add
     */
    public void addOnReadListener(OnReadListener listener) {
        readListeners.add(listener);
    }
    
    /**
     * Removes a listener so that it will no longer be updated when new sensor
     * data is available.
     * @param listener : the listener to remove
     */
    public void removeOnReadListener(OnReadListener listener) {
        readListeners.remove(listener);
    }
    
    /**
     * Notifies the reading thread to stop and waits for it to do so. The
     * Sensor can be used again by calling restart() method.
     */
    public void pause() {
        stopped = true;
        microphone.stop();
    }
    
    /**
     * Notifies the already-running thread to resume recording and updating
     * listeners.
     */
    public void restart() {
        stopped = false;
       microphone.start();
    }
    
    /**
     * Stops the sensor gracefully then releases its resources. Must create a
     * new EMGSensor object after calling this method as this one can no 
     * longer be used.
     */
    public void kill() {
        stopped = false;
        interrupt();
        while(!doneRunning) {}
        {
            microphone.stop();
        }
       microphone.close();
    }
    
    /**
     * Interface for obtaining normalized (i.e. -1 to 1) samples from the
     * EMGSensor. The callback is invoked at a rate determined by the sensor's
     * update rate.
     */
    public interface OnReadListener {
        
        /**
         * Callback fired from EMGSensor when there is a set of samples
         * to be processed.
         * @param sensorData : the normalized (-1 to 1) samples read by the 
         * EMGSensor
         */
        public void onRead(float[] sensorData);
    }
}
