package rascal.libemg.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * A fairly generalized utility class for logging data to CSV files. The file
 * is placed in external storage under a directory of the caller's choosing. 
 * The file name is generated according to the standard agreed upon in RASCAL.
 * After initialization, logging should take place in sets of calls to 
 * {@link #log(String)}, each set followed by a call to {@link #finishRow()}. 
 * When you are ready to write the data to file, call {@link #flush()}. If you 
 * have logged some data that you do not wish to write to file, you can call
 * {@link #clear()}. Always remember to call {@link #close()} when you're done 
 * with the instance of the CSVLogger.
 */
public class CSVLogger {
    private String fileName;
    private String basePath;
    private BufferedWriter writer;
    private boolean initialized = false;
    private ArrayList<ArrayList<String>> buffer = new ArrayList<ArrayList<String>>();
    private ArrayList<String> bufferLine = new ArrayList<String>();
    
    /**
     * Constructs a new CSVLogger with filename
     * 'experimentId_subjectId_timeStamp_fileType.csv', placed in external
     * storage under the 'experimentId/fileType/' subdirectory. The file is not
     * created by this constructor. It is only created when 
     * {@link #initialize(String...)} is called.
     * @param fileType : the type of log file (typically "Trace" or "Summary")
     * @param experimentId : the experiment ID (used in file name and path)
     * @param subjectId : the ID of the currently signed in Participant.
     * @param timeStamp : a time stamp (from System.currentTimeMillis()) to use
     * for the file name (it is not taken automatically so you can provide the
     * same value to mulitple CSVLoggers for consistency)
     */
    public CSVLogger(String fileType, String experimentId, 
            String subjectId, long timeStamp) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.US);
        String timeString = sdf.format(new Date(timeStamp));
        
        fileName = experimentId + "_" +
                   subjectId + "_" +
                   timeString + "_" +
                   fileType +
                   ".csv";
        
        basePath = Environment.getExternalStorageDirectory() + "/" + 
                   experimentId + "/" + 
                   fileType + "/";
    }
    
    /**
     * Initializes the CSVLogger by creating the file in external storage and
     * writing the header row, which is determined by the set of strings passed
     * in. It is up to you to ensure that the number of calls to 
     * {@link #log(String)} between calls to {@link #finishRow()} match the
     * number of parameters passed in here.
     * @param columns : a set of strings that will become the column headers of
     * the CSV file
     */
    public void initialize(String... columns) {
        File dir = new File(basePath);
        dir.mkdirs();
        
        try {
            writer = new BufferedWriter(new FileWriter(basePath + fileName));
        } catch (IOException e) {
            Log.e("Logger", "Could not create file writer.");
            e.printStackTrace();
        }
        
        try {
            for (int i = 0; i < columns.length - 1; i++) {
                writer.write(columns[i] + ",");
            }
            writer.write(columns[columns.length-1]);
            writer.newLine();
        } catch (IOException e) {
            Log.e("Logger", "Could not initialize file header.");
            e.printStackTrace();
        }
        
        initialized = true;
    }
    
    /**
     * Returns whether or not the file associated with this CSVLogger has been
     * created (i.e. whether or not {@link #initialize(String...)} has
     * been called).
     * @return true if the file has been created, false if not
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Appends an item to the CSV file. Note that commas are automatically
     * added, so only pass in what you want to appear in the file.
     * @param item : the string to add to the file
     */
    public void log(String item) {
        bufferLine.add(item);
    }
    
    /**
     * Casts the double to a String and calls {@link #log(String)}.
     * @param item : the item to add to file
     */
    public void log(double item) {
        log(Double.toString(item));
    }
    
    /**
     * Casts the float to a String and calls {@link #log(String)}.
     * @param item : the item to add to file
     */
    public void log(float item) {
        log(Float.toString(item));
    }
    
    /**
     * Casts the int to a String and calls {@link #log(String)}.
     * @param item : the item to add to file
     */
    public void log(int item) {
        log(Integer.toString(item));
    }
    
    /**
     * Casts the long to a String and calls {@link #log(String)}.
     * @param item : the item to add to file
     */
    public void log(long item) {
        log(Long.toString(item));
    }

    /**
     * Lets the CSVLogger know to stop writing to this row and start another.
     */
    public void finishRow() {
        ArrayList<String> copy = new ArrayList<String>(bufferLine);
        buffer.add(copy);
        bufferLine.clear();
    }
    
    /**
     * Flushes the buffered data to file. 
     */
    public void flush() {
        try{
            for (ArrayList<String> line : buffer) {
                for (int i = 0; i < line.size(); i++) {
                    writer.write(line.get(i));
                    
                    if (i < line.size() - 1) {
                        writer.write(',');
                    }
                }
                
                writer.newLine();
            }
            
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        clear();
    }
    
    /**
     * Clears the buffer in case all calls to log() back to the most recent
     * flush() needs to be removed (i.e. not written to disk).
     */
    public void clear() {
        buffer.clear();
    }
    
    /**
     * Flushes the CSVLogger's buffer to file and releases it. Do not use an
     * instance of CSVLogger after calling this method on it.
     */
    public void close() {
        if (writer != null) {
            flush();
            
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        initialized = false;
        writer = null;
    }
    
    /**
     * Forces the Android media scanner to update for the file created by this
     * instance of CSVLogger. 
     * @param context : Activity context to call sendBroadcast on
     */
    public void runScanner(Context context) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
                Uri.parse("file://" + basePath + fileName))); 
    }
}