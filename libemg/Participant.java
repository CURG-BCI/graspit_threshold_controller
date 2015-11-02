package rascal.libemg;

import java.io.File;
import java.io.FileNotFoundException;

import rascal.libemg.io.ConfigFileReader;
import rascal.libemg.io.ConfigFileWriter;
import android.os.Environment;

/**
 * A basic class that represents a participant undergoing a BCI experiment. Any
 * data that is customized to or representative of an individual participant is
 * handled here, like retrieving and providing identification information. 
 */
public class Participant {
    /** Key for obtaining user ID */
    public static final String KEY_ID = "subject_id";
    /** Key for obtaining user gender */
    public static final String KEY_GENDER = "gender";
    /** Key for obtaining user type (patient, control, etc.) */
    public static final String KEY_TYPE = "subject_type";
    /** Key for obtaining user age */
    public static final String KEY_AGE = "age";
    /** Key for obtaining user handedness */
    public static final String KEY_HAND = "dominant_hand";
    /** Key for obtaining the muscle the participant is using */
    public static final String KEY_MUSCLE = "muscle";
    /** Key for obtaining the experiment ID the user is participating in */
    public static final String KEY_EXPERIMENT = "experiment";
    /** Key for obtaining the device name the user is using */
    public static final String KEY_DEVICE = "device";
    /** Key for obtaining the session number the user is currently on */
    public static final String KEY_SESSION = "session";
    
    /** Directory in external storage for user files */
    public static final String DIR_USER_FILES = "user_files";
    /** Directory in external storage for signin files (used by SignInActivity) */
    public static final String DIR_SIGNIN_FILES = "signin_data";
    /** Directory in external storage for calibration files */
    public static final String DIR_CALIBRATION_FILES = "calibration";
    
    /** List of the different types of participant */
    public static final String[] TYPE_LIST = new String[] { "control", "patient", "subject" };
    
    /** Minimum value that the effort can be */
    public static final int EFFORT_MIN = 1;
    /** Maximum value that the effort can be */
    public static final int EFFORT_MAX = 100;
    /** Default value for effort */
    public static final int EFFORT_DEFAULT = 30;
    
    /** Minimum value that the speed gain can be */
    public static final float SPEED_GAIN_MIN = 0.01f;
    /** Maximum value that the speed gain can be */
    public static final float SPEED_GAIN_MAX = 0.99f;
    /** Default value for speed gain */
    public static final float SPEED_GAIN_DEFAULT = 0.5f;
    
    private String id;
    private String gender;
    private String session;
    private String muscle;
    private String experiment;
    private String age;
    private String hand;
    private String type;
    private String device;
    private Calibration calibration;
    private int effort_1 = EFFORT_DEFAULT;
    private int effort_2 = EFFORT_DEFAULT;
    private float speedGain = SPEED_GAIN_DEFAULT;
    
    public Participant() {
        calibration = new Calibration();
    }
    
    /**
     * Finds participant who was last signed in to the device.
     * @throws ParticipantNotFoundException if the sign-in file can't be found.
     */
    public void signIn() throws SignInFailedException {
        try {
            readIdFile();
        } catch (FileNotFoundException e) {
            throw new SignInFailedException("Could not sign in participant");
        }
    }
    
    /**
     * Loads the current participant's information (no calibration values or
     * efforts -- see loadData()).
     * @throws ParticipantNotFoundException if the information file can't be 
     * found.
     */
    public void loadInfo() throws ParticipantNotFoundException {
        try {
            readSigninFile();
        } catch (FileNotFoundException e) {
            throw new ParticipantNotFoundException(
                    "Could not find participant's sign in file");
        }
    }
    
    /**
     * Writes the particpiant's current information (ID, age, gender, etc.) to
     * file so that he/she can be signed in later using signIn() and loadInfo().
     */
    public void saveInfo() {
        writeUserFile();
        writeSigninFile();
    }
    
    /**
     * Loads the current participant's calibration data (calibration values,
     * efforts).
     * @throws CalibrationNotFoundException if the calibration file can't be
     * found.
     */
    public void loadData() throws CalibrationNotFoundException {
        try {
            readCalibrationFile();
        } catch (FileNotFoundException e) {
            throw new CalibrationNotFoundException(
                    "Could not find participant's calibration file");
        }
    }
    
    /**
     * Writes the participant's current calibration data (calibration values,
     * efforts) to file so they can be loaded later using signIn() and 
     * loadData().
     */
    public void saveData() {
        writeCalibrationFile();
    }
    
    /**
     * Ensures that this participant (with currently set ID) will be signed in
     * next time signIn() is called.
     */
    public void setSignedIn() {
        writeIdFile();
    }
    
    /**
     * Deletes all of this participant's files. The participant will no longer
     * be able to sign in.
     */
    public void deleteFiles() {
        // delete user file
        File file = new File(
                Environment.getExternalStorageDirectory() + "/" + DIR_USER_FILES + "/" + 
                        "user_" + id + ".txt");
        if (file.exists()) {
            file.delete();
        }
        
        // delete sign in file
        file = new File(
                Environment.getExternalStorageDirectory() + "/" + DIR_SIGNIN_FILES + "/" + 
                        "signin_" + id + ".txt");
        if (file.exists()) {
            file.delete();
        }
        
        // delete calibration file
        file = new File(
                Environment.getExternalStorageDirectory() + "/" + DIR_CALIBRATION_FILES + "/" +
                        "calibration_" + id + ".txt");
        if (file.exists()) {
            file.delete();
        }
    }
    
    private void readIdFile() throws FileNotFoundException {
        ConfigFileReader cfgreader = new ConfigFileReader(
                Environment.getExternalStorageDirectory() + "/",
                "signed_in.txt");
        
        cfgreader.parseFile();
        id = cfgreader.getEntry(KEY_ID);
    }
    
    private void writeIdFile() {
        ConfigFileWriter cfgwriter = new ConfigFileWriter(
                Environment.getExternalStorageDirectory() + "/",
                "signed_in.txt");
        cfgwriter.addEntry(KEY_ID, id);
        cfgwriter.write();
    }
    
    private void writeUserFile() {
        ConfigFileWriter cfgwriter = new ConfigFileWriter(
                Environment.getExternalStorageDirectory() + "/" + DIR_USER_FILES + "/",
                "user_" + id + ".txt");
        cfgwriter.addEntry(KEY_ID, id);
        cfgwriter.addEntry(KEY_GENDER, gender);
        cfgwriter.addEntry(KEY_TYPE, type);
        cfgwriter.addEntry(KEY_AGE, age);
        cfgwriter.addEntry(KEY_HAND, hand);
        cfgwriter.write();
    }
    
    private void writeSigninFile() {
        ConfigFileWriter cfgwriter = new ConfigFileWriter(
                Environment.getExternalStorageDirectory() + "/" + DIR_SIGNIN_FILES + "/",
                "signin_" + id + ".txt");
        cfgwriter.addEntry(KEY_ID, id);
        cfgwriter.addEntry(KEY_TYPE, type);
        cfgwriter.addEntry(KEY_AGE, age);
        cfgwriter.addEntry(KEY_GENDER, gender);
        cfgwriter.addEntry(KEY_HAND, hand);
        cfgwriter.addEntry(KEY_MUSCLE, muscle);
        cfgwriter.addEntry(KEY_EXPERIMENT, experiment);
        cfgwriter.addEntry(KEY_SESSION, session); 
        cfgwriter.addEntry(KEY_DEVICE, device);
        cfgwriter.write();
    }
    
    private void readSigninFile() throws FileNotFoundException {
        ConfigFileReader cfgreader = new ConfigFileReader(
                Environment.getExternalStorageDirectory() + "/" + DIR_SIGNIN_FILES + "/",
                "signin_" + id + ".txt");
        cfgreader.parseFile();
        age = cfgreader.getEntry(KEY_AGE);
        type = cfgreader.getEntry(KEY_TYPE);
        gender = cfgreader.getEntry(KEY_GENDER);
        hand = cfgreader.getEntry(KEY_HAND);
        muscle = cfgreader.getEntry(KEY_MUSCLE);
        experiment = cfgreader.getEntry(KEY_EXPERIMENT);
        session = cfgreader.getEntry(KEY_SESSION);
        device = cfgreader.getEntry(KEY_DEVICE);
    }
    
    
    private void writeCalibrationFile() {
        ConfigFileWriter cfgwriter = new ConfigFileWriter(
                Environment.getExternalStorageDirectory() + "/" + DIR_CALIBRATION_FILES + "/",
                "calibration_" + id + ".txt");
        
        // write effort values
        cfgwriter.addEntry("effort_1", Integer.toString(effort_1));
        cfgwriter.addEntry("effort_2", Integer.toString(effort_2));
        
        // write speed gain value
        cfgwriter.addEntry("speed_gain", Float.toString(speedGain));
        
        // write calibration values
        int i = 1;
        for (Position cal : calibration) {
            cfgwriter.addEntry("calibration" + i + "_b1", Float.toString(cal.getQ1()));
            cfgwriter.addEntry("calibration" + i + "_b2", Float.toString(cal.getQ2()));
            i++;
        }
        
        // write the average calibration value
        Position avg = calibration.getMeanCalibration();
        cfgwriter.addEntry("cal_avg_b1", Float.toString(avg.getQ1()));
        cfgwriter.addEntry("cal_avg_b2", Float.toString(avg.getQ2()));
        
        cfgwriter.write();
    }
    
    private void readCalibrationFile() throws FileNotFoundException {
        ConfigFileReader cfgreader = new ConfigFileReader(
                Environment.getExternalStorageDirectory() + "/" + DIR_CALIBRATION_FILES + "/",
                "calibration_" + id + ".txt");
        cfgreader.parseFile();
        
        // read effort values
        effort_1 = Integer.parseInt(cfgreader.getEntry("effort_1"));
        effort_2 = Integer.parseInt(cfgreader.getEntry("effort_2"));
        
        // read speed gain
        speedGain = Float.parseFloat(cfgreader.getEntry("speed_gain"));
        
        // read calibration values
        float b1;
        float b2;
        calibration.clear();
        for (int i = 1; i <= Calibration.NUM_CONTRACTIONS; i++) {
            b1 = Float.parseFloat(cfgreader.getEntry("calibration" + i + "_b1"));
            b2 = Float.parseFloat(cfgreader.getEntry("calibration" + i + "_b2"));
            calibration.addCalibrationValue(new Position(b1, b2));
        }
        
        // no need to read the average because Calibration class will calculate
        // it based on the values read above
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public void setSession(String session) {
        this.session = session;
    }
    
    public void setMuscle(String muscle) {
        this.muscle = muscle;
    }
    
    public void setExperiment(String experiment) {
        this.experiment = experiment;
    }
    
    public void setAge(String age) {
        this.age = age;
    }
    
    public void setHand(String hand) {
        this.hand = hand;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setDevice(String device) {
        this.device = device;
    }
    
    public void setCalibration(Calibration calibration) {
        this.calibration = calibration;
    }
    
    public void setEfforts(int effort_1, int effort_2) {
        setEffort1(effort_1);
        setEffort2(effort_2);
    }
    
    public void setEffort1(int effort_1) {
        if (effort_1 < EFFORT_MIN) {
            this.effort_1 = EFFORT_MIN;
        }
        else if (effort_1 > EFFORT_MAX) {
            this.effort_1 = EFFORT_MAX;
        }
        else {
            this.effort_1 = effort_1;
        }
    }
        
    public void setEffort2(int effort_2) {
        if (effort_2 < EFFORT_MIN) {
            this.effort_2 = EFFORT_MIN;
        }
        else if (effort_2 > EFFORT_MAX) {
            this.effort_2 = EFFORT_MAX;
        }
        else {
            this.effort_2 = effort_2;
        }
    }
    
    public void setSpeedGain(float speedGain) {
        if (speedGain < SPEED_GAIN_MIN) {
            this.speedGain = SPEED_GAIN_MIN;
        }
        else if (speedGain > SPEED_GAIN_MAX) {
            this.speedGain = SPEED_GAIN_MAX;
        }
        else {
            this.speedGain = speedGain;
        }
    }
    
    public String getId() {
        return id;
    }
    
    public String getGender() {
        return gender;
    }
    
    public String getSession() {
        return session;
    }
    
    public String getMuscle() {
        return muscle;
    }
    
    public String getExperiment() {
        return experiment;
    }
    
    public String getAge() {
        return age;
    }
    
    public String getHand() {
        return hand;
    }
    
    public String getType() {
        return type;
    }
    
    public String getDevice() {
        return device;
    }
    
    public Calibration getCalibration() {
        return calibration;
    }
    
    public Position getCalibrationPosition() {
        return calibration.getMeanCalibration();
    }
    
    public float getSpeedGain() {
        return speedGain;
    }
    
    public int getEffort1() {
        return effort_1;
    }
    
    public int getEffort2() {
        return effort_2;
    }
    
    public class SignInFailedException extends Exception {
        private static final long serialVersionUID = 3659366239674232401L;

        public SignInFailedException() {}
        
        public SignInFailedException(String message) {
            super(message);
        }
    }
    
    public class ParticipantNotFoundException extends Exception {
        private static final long serialVersionUID = 8727653449335522344L;

        public ParticipantNotFoundException() {}
        
        public ParticipantNotFoundException(String message) {
            super(message);
        }
    }
    
    public class CalibrationNotFoundException extends Exception {
        private static final long serialVersionUID = -1808461250132226451L;

        public CalibrationNotFoundException() {}
        
        public CalibrationNotFoundException(String message) {
            super(message);
        }
    }
}