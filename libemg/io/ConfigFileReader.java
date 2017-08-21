package rascal.libemg.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * A generic class for reading config files. Preferably, it should be used to 
 * read files written by ConfigFileWriter. Files written by hand will work as
 * well assuming the format is consistent with the output of 
 * ConfigFileWriter.
 */
public class ConfigFileReader {
    private String basePath;
    private String fileName;
    private Map<String, String> dataMap = new HashMap<String, String>();
    
    /**
     * Initializes the reader with a base path (just folders, make sure a
     * trailing "/" is at the end) and file name.
     * @param basePath : path to file
     * @param fileName : the file name (with extension)
     */
    public ConfigFileReader(String basePath, String fileName) {
        this.basePath = basePath;
        this.fileName = fileName;
    }
    
    /**
     * Reads the file specified in the constructor and puts each line into a
     * key/value map which can be queried with getEntry().
     * @throws FileNotFoundException
     */
    public void parseFile() throws FileNotFoundException {
        File file = new File(basePath + fileName);
        if (file.exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            String line;
            String[] tokens;
            try {
                while ((line = reader.readLine()) != null) {
                    tokens = line.split(ConfigFileWriter.DELIMITER);
                    
                    if (tokens.length < 2) {
                        Log.e("ConfigFileReader", "Line in file has missing value.");
                    }
                    else {
                        dataMap.put(tokens[0], tokens[1]);
                    }
                }
                
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            throw new FileNotFoundException();
        }
    }
    
    /**
     * Tries to find the value corresponding to the given key. 
     * @param key : the left hand argument on the config file line
     * @return the value if found, null if not found
     */
    public String getEntry(String key) {
        return dataMap.get(key);
    }
}
