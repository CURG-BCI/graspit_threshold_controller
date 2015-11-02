package rascal.libemg.io;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

/**
 * Provides a mechanism for writing raw EMG data to a file. The resulting file
 * is largely dependent on the configuration of EMGSensor. Currently, a file
 * written with this class can be opened in a program like Audacity using
 * Import -> Raw Data..., then choosing Signed 16 bit PCM, Little-endian, 1
 * Channel (Mono), 4000 Hz. Don't forget to call {@link #close()} when done.
 */
public class PCMWriter {
    private File pcmfile;
    private DataOutputStream writer;
    
    /**
     * Initializes the PCMWriter by creating the file at the specified path.
     * The first part of the path should usually be obtained using
     * {@code Environment.getExternalStorageDirectory()}.
     * @param filePath : the full path of the file (including name)
     */
    public PCMWriter(String filePath) {
        pcmfile = new File(filePath);
        
        try {
            if (pcmfile.exists()) {
                pcmfile.delete();
                pcmfile.createNewFile();
            }
            else {
                pcmfile.createNewFile();
            }
        } catch (IOException e) {
            Log.e("EMGSensor", "Couldn't create PCM file");
        }
        
        try {
            writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(pcmfile)));
        } catch (FileNotFoundException e) {
            Log.e("PCMLogger", "Couldn't create output stream");
        }
    }
    
    /**
     * Appends a set of EMG signal samples to the file. The data should be
     * floats between -1 and +1 (i.e. it should not be raw PCM data, but the 
     * output of EMGSensor).
     * @param data : array of samples to be written to file
     */
    public void write(float[] data) {
        short samp;
        if (writer != null) {
            try {
                for (int i = 0; i < data.length; i++) {
                    samp = (short)(data[i] * 32767);
                    writer.write(samp >> 0);
                    writer.write(samp >> 8);
                }
            } catch (IOException e) {
                Log.e("PCMLogger", "Failed to write audio data to file");
            }
        }
    }
    
    /**
     * Flushes the PCMWriter's buffer to file and releases it. Do not use an 
     * instance of PCMWriter after calling this method on it.
     */
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        writer = null;
    }
}
