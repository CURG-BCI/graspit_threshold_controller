package rascal.libemg.ui;

import rascal.libemg.Calibration;
import rascal.libemg.Position;
import rascal.libemg.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A dialog for manually typing in calibration values as an alternative to
 * performing the contractions in CalibrateActivity. Note that all of the 
 * calibration values are the same for each of the calibrations when this is
 * used instead of the normal calibration method.
 */
public class ManualCalibrationDialog extends Dialog {
    private EditText calBand1EditText;
    private EditText calBand2EditText;
    private Button calibrationSaveButton;
    
    /**
     * Creates a new ManualCalibrationDialog, initializing the view elements.
     * @param context : activity context (needed by Dialog)
     */
    public ManualCalibrationDialog(Context context) {
        super(context);
        
        setContentView(R.layout.manualcalibration_dialog);
        setTitle("Enter calibration values");
        
        // get all the view elements
        calBand1EditText = (EditText) findViewById(R.id.cal_band1_edittext);
        calBand2EditText = (EditText) findViewById(R.id.cal_band2_edittext);
        calibrationSaveButton = (Button) findViewById(R.id.calibration_save_button);  
    }
    
    /**
     * Shows the dialog above the current view. You should add an 
     * onDismissListener to the dialog to handle the case that bad input is
     * entered into the EditTexts and the dialog is dismissed.
     * @param calibration : the Calibration to set the values for.
     */
    public void show(Calibration calibration) {
        super.show();
        final Calibration cal = calibration;

        calibrationSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                float cb1, cb2;
                try {
                    cb1 = Float.parseFloat(calBand1EditText.getText().toString());
                    cb2 = Float.parseFloat(calBand2EditText.getText().toString());
                
                    Position p = new Position(cb1, cb2);
                    for (int i = 0; i < 3; i++) {
                        cal.addCalibrationValue(p);
                    }
                    
                    dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), 
                            "Input not formatted correctly", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
