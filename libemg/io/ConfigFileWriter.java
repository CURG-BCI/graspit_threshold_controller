package rascal.libemg.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a generic class for writing config files. The format of the file is
 * one entry per line, where each entry consists of a key and a value,
 * separated by a delimiter.
 */
public class ConfigFileWriter {
    /** The delimiter placed between key/value pairs */
    public static final String DELIMITER = "=";

    private String basePath;
    private String fileName;
    private Map<String, String> dataMap = new LinkedHashMap<String, String>();
    
    /**
     * Initializes the config file with a file path and name. Use a trailing
     * "/" at the end of basepath.
     * @param basePath : the path to the file (hint: use 
     * Environment.getExternalStorageDirectory())
     * @param fileName : the name of the file (with desired extension)
     */
    public ConfigFileWriter(String basePath, String fileName) {
        this.basePath = basePath;
        this.fileName = fileName;
    }
    
    /**
     * Adds an entry (i.e. a line) to the config file. This does not write the
     * entry to the file. After you're done adding entries with this method,
     * use write() to write them all to file.
     * @param key : key of the entry (left hand argument)
     * @param value : value of the entry (right hand argument)
     */
    public void addEntry(String key, String value) {
        dataMap.put(key, value);
    }
    
    /**
     * Writes all data to file that has been added with addEntry(). 
     */
    public void write() {
        File dir = new File(basePath);
        dir.mkdirs();
        
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter(new FileWriter(basePath + fileName));
            
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                writer.write(entry.getKey());
                writer.write(DELIMITER);
                if (entry.getValue() == null || entry.getValue().length() == 0) {
                    writer.write("null");
                }
                else {
                    writer.write(entry.getValue());
                }
                writer.newLine();
            }
            
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
