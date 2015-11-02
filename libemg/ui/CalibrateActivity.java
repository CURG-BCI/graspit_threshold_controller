package rascal.libemg.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;
import rascal.libemg.Calibration;
import rascal.libemg.EMGSensor;
import rascal.libemg.EMGSensor.OnReadListener;
import rascal.libemg.Participant;
import rascal.libemg.Participant.CalibrationNotFoundException;
import rascal.libemg.Participant.ParticipantNotFoundException;
import rascal.libemg.Participant.SignInFailedException;
import rascal.libemg.Position;
import rascal.libemg.R;
import rascal.libemg.proc.BandFilter;

/**
 * Activity that completely autonomously takes care of everything needed for
 * calibration. Starting this activity will allow the user to go through the
 * calibration process for the currently signed in participant.
 */
public class CalibrateActivity extends Activity implements OnClickListener, OnReadListener {
    private Participant participant;  
    private BandFilter timeDataFilter;
    private EMGSensor sensor;
    
    // variables to keep track of which view to show
    public static final int MIC_TEST_VIEW = 1;
    public static final int CALIBRATE_VIEW = 2;
    public static final int POWER_VIEW = 3;
    private int currentView;
    private ViewFlipper viewFlipper;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    
    private OscilloscopeView micTestView;
    private CalibrateView calibrateView;
    private PowerView powerView;
    
    // calibrate view parameters
    private static final int REST_INTERVAL = 3;     // seconds
    
    private static final int CONTRACT_SAMPLES = 
            Calibration.CONTRACTION_LENGTH_SECONDS * EMGSensor.DEFAULT_UPDATE_RATE;
    private boolean calibrationRunning = false;
    private boolean measuring = false;
    private int measurementSample = 0;
    private int contractionNumber = 0;
    private int restCounts = 0;
    
    private ManualCalibrationDialog manualCalibrationDialog;
    
    private float[][][] calValues = new float[Calibration.NUM_CONTRACTIONS][CONTRACT_SAMPLES][2];
    private Calibration calibration;
    
    
    // handler responsible for keeping track of calibration status and timing
    private Handler measurementCountdownTimer = new Handler();
    private Runnable measurementCountdownRunnable = new Runnable() {
        @Override
        public void run() {
            // set of contractions is finished
            if (contractionNumber == Calibration.NUM_CONTRACTIONS) {
                calibrateView.setMeasuring(false);
                for (int i = 0; i < Calibration.NUM_CONTRACTIONS; i++) {
                    calibration.addContraction(calValues[i]);
                }
                participant.saveData();
                Position cal = calibration.getMeanCalibration();
                calibrateView.setStatusText("Calibration set: " + 
                                            "band 1 = " +  cal.getQ1() + 
                                            " --- " + 
                                            "band 2 = " + cal.getQ2());
                calibrationRunning = false;
                contractionNumber = 0;
            }
            
            // countdown finished, now measure contraction
            else if (restCounts == REST_INTERVAL) {
                calibrateView.setStatusText("measuring");
                calibrateView.setMeasuring(true);
                measuring = true;
                restCounts = 0;
            }
            
            // update countdown dislpay in the view
            else {
                calibrateView.setMeasuring(false);
                calibrateView.setStatusText((REST_INTERVAL - restCounts) + "...");
                measurementCountdownTimer.postDelayed(measurementCountdownRunnable, 1000);
                restCounts++;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.calibrate);

        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // get participant information for calibration settings and logging
        participant = new Participant();
        calibration = new Calibration();
        participant.setCalibration(calibration);
        try {
            participant.signIn();
            participant.loadData();
        } catch (SignInFailedException e) {
            Toast.makeText(this, "No participant is signed in!", Toast.LENGTH_LONG).show();
            finish();
        } catch (CalibrationNotFoundException e) {
            // it's ok if they've never calibrated before
        }
        
        try {
            participant.loadInfo();
        } catch (ParticipantNotFoundException e) {
            Toast.makeText(this, "Could not load participant's info!", Toast.LENGTH_LONG).show();
            finish();
        }

        setTitle("Calibration -- participant " + participant.getId());
        
        // set up flipper for changing views
        viewFlipper = (ViewFlipper)findViewById(R.id.flipper);
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        
        // setup mic test view
        micTestView = new OscilloscopeView(this);
        LinearLayout micTestLayout = (LinearLayout) findViewById(R.id.mictest_root);
        micTestLayout.addView(micTestView, ViewGroup.LayoutParams.MATCH_PARENT, 
                ViewGroup.LayoutParams.MATCH_PARENT);
        
        // setup calibrate view
        calibrateView = new CalibrateView(this);
        LinearLayout calibrateLayout = (LinearLayout) findViewById(R.id.calibrate_root);
        calibrateLayout.addView(calibrateView);
        ((Button) findViewById(R.id.calibrate_start_button)).setOnClickListener(this);
        
        // setup power view
        powerView = new PowerView(this);
        LinearLayout powerLayout = (LinearLayout) findViewById(R.id.power_root);
        powerLayout.addView(powerView);
        
        currentView = MIC_TEST_VIEW;
        
        manualCalibrationDialog = new ManualCalibrationDialog(this);
        manualCalibrationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if (calibration.getMeanCalibration().getQ1() > 0 &&
                    calibration.getMeanCalibration().getQ2() > 0) {
                    participant.saveData();
                }
            }
        });

        // get the sensor ready and running
        timeDataFilter = new BandFilter();
        sensor = new EMGSensor();
        sensor.addOnReadListener(this);
        sensor.start();
    }
    
    @Override
    public void onRead(float[] sensorData) {
        final Position pos;

        switch(currentView) {
        case MIC_TEST_VIEW:
            final float[] data = sensorData;
            
            runOnUiThread(new Runnable() {
                public void run() {
                    micTestView.setAudioData(data);
                    micTestView.invalidate();
                }
            });
            break;
            
        case CALIBRATE_VIEW:
            if (measuring) {
                pos = timeDataFilter.filter(sensorData);
                calValues[contractionNumber][measurementSample][0] = pos.getQ1();
                calValues[contractionNumber][measurementSample][1] = pos.getQ2();
                
                runOnUiThread(new Runnable() {
                    public void run() {
                        calibrateView.update(pos);
                    }
                });
                
                measurementSample++;
                if (measurementSample == CONTRACT_SAMPLES) {
                    measurementSample = 0;
                    measuring = false;
                    contractionNumber++;
                    measurementCountdownTimer.post(measurementCountdownRunnable);
                }
            }
            break;
            
        case POWER_VIEW:
            pos = timeDataFilter.filter(sensorData);
            Log.i("---", "cal x: " + participant.getCalibrationPosition().getQ1());
            pos.scale(
                    participant.getCalibrationPosition().getQ1(), 
                    participant.getCalibrationPosition().getQ2());
            if (pos.getQ1() > 1) { pos.setQ1(1); }
            if (pos.getQ1() < 0) { pos.setQ1(0); }
            if (pos.getQ2() > 1) { pos.setQ2(1); }
            if (pos.getQ2() < 0) { pos.setQ2(0); }
            
            runOnUiThread(new Runnable() {
                public void run() {
                    powerView.update(pos);
                }
            });
            break;
        }
    }
    
    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH || calibrationRunning)
                    return false;
                
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && 
                   Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    
                    //update which view is current view
                    switch(currentView) {
                    case MIC_TEST_VIEW: 
                        currentView = CALIBRATE_VIEW;
                        viewFlipper.setDisplayedChild(1); 
                        break;
                    case CALIBRATE_VIEW:
                        currentView = POWER_VIEW;
                        viewFlipper.setDisplayedChild(2);
                    }    
                }  
                
                // left to right swipe
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && 
                         Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    
                    // update which view is current view
                    switch(currentView) {
                    case POWER_VIEW: 
                        currentView = CALIBRATE_VIEW;
                        viewFlipper.setDisplayedChild(1);
                        break;
                    case CALIBRATE_VIEW:
                        currentView = MIC_TEST_VIEW;
                        viewFlipper.setDisplayedChild(0);
                    }
                }
            } catch (Exception e) {}
            return false;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
           if (gestureDetector.onTouchEvent(ev))
               return true;
           else
               return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calibration, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_manualcalibration) {
            manualCalibrationDialog.show(calibration);
        }
            
        return true;
    }
    
    public void onClick(View v) {
        if (v.getId() == R.id.calibrate_start_button) {
            if (!calibrationRunning) {
                calibrationRunning = true;
                calibrateView.update(new Position(0, 0));
                measurementCountdownTimer.post(measurementCountdownRunnable);
                calibration.clear();
            }
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        sensor.pause();
        measurementCountdownTimer.removeCallbacks(measurementCountdownRunnable);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sensor.restart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    
        sensor.kill();
    }
}
