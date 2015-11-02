package rascal.libemg.ui;

import rascal.libemg.EMGSensor;
import rascal.libemg.EMGSensor.OnReadListener;
import rascal.libemg.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.LinearLayout;

/**
 * A Dialog that pops up to display the "oscilloscope" view for ensuring that
 * the sensor is working properly and is not excessively noisy.
 */
public class SignalDialog extends Dialog {

    private LinearLayout linearLayout;
    private OscilloscopeView micTestView;
    
    private OnReadListener listener;
    
    /**
     * Initializes the dialog by setting up the MicTestView. A call show()
     * will display the dialog in a popup window. Pressing the back button
     * dismisses it.
     * @param context : activity context
     */
    public SignalDialog(Context context) {
        super(context);
        
        setContentView(R.layout.signal_dialog);
        setTitle("Signal Check");
        
        linearLayout = (LinearLayout) findViewById(R.id.check_signal_layout);
        micTestView = new OscilloscopeView(context);
        
        linearLayout.addView(micTestView);
        
        listener = new OnReadListener() {
            @Override
            public void onRead(float[] sensorData) {
                micTestView.setAudioData(sensorData);
                micTestView.postInvalidate();
            }
        };
    }
    
    /**
     * Displays the oscilloscope view in a dialog.
     * @param s : the EMGSensor that will update the MicTestView
     */
    public void show(EMGSensor s) {
        super.show();
        final EMGSensor sensor = s;
        
        sensor.addOnReadListener(listener);
        
        setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                sensor.removeOnReadListener(listener);
            }
        });
    }
}