package rascal.libemg.ui;

import rascal.libemg.Participant;
import rascal.libemg.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * A simple Dialog for changing the speed gain parameter of the signed-in
 * Participant manually (EditText).
 */
public class SpeedGainDialog extends Dialog {
    private EditText speedGainEditText;
    private Button speedGainSaveButton;
    
    /**
     * Creates a new SpeedGainDialog, initializing the view elements.
     * @param context : activity context (needed by Dialog)
     */
    public SpeedGainDialog(Context context) {
        super(context);
        
        setContentView(R.layout.speedgain_dialog);
        setTitle("Change Speed Gain");
        
        speedGainEditText = (EditText) findViewById(R.id.speedgain_edittext);
        speedGainSaveButton = (Button) findViewById(R.id.speedgain_save_button);  
    }

    /**
     * Displays the dialog. When the OK button is pressed, the value input to
     * the Dialog by the user is set as the speed gain for the participant and
     * the participant's data is saved to file. The default value for speed gain
     * (see Participant.SPEED_GAIN_DEFAULT) is used in bad input is entered.
     * @param p : the participant to set the speed gain for
     */
    public void show(Participant p) {
        super.show();
        final Participant participant = p;
        
        speedGainEditText.setText(Float.toString(participant.getSpeedGain()));
        speedGainEditText.selectAll();
        
        speedGainSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                float gain = Participant.SPEED_GAIN_DEFAULT;
                try {
                    gain = Float.parseFloat(speedGainEditText.getText().toString());
                } catch(NumberFormatException e) {
                    // leave it as default
                }
                participant.setSpeedGain(gain);
                participant.saveData();
                dismiss();
            }
        });
    }
}