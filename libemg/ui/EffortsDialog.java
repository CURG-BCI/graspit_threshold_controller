package rascal.libemg.ui;

import rascal.libemg.Participant;
import rascal.libemg.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * A Dialog that pops up a window for changing the effort values for the
 * current participant. A saved change takes effect immediately upon returning
 * to the BCIView and is saved to the participants info files. Does not
 * deactivate the BCIView. Note that to display the dialog, you should use the
 * show(Participant p) method, not show() directly.
 */
public class EffortsDialog extends Dialog {

    private SeekBar effortXSeekBar;
    private SeekBar effortYSeekBar;
    private TextView effortXText;
    private TextView effortYText;
    private TextView effortXLabel;
    private TextView effortYLabel;
    private Button effortsSaveButton;
    
    /**
     * Creates a new EffortsDialog, initializing the sliders and other view
     * elements.
     * @param context : activity context (needed by Dialog)
     */
    public EffortsDialog(Context context) {
        super(context);
        
        setContentView(R.layout.efforts_dialog);
        setTitle("Change Efforts");
        
        // get all the view elements
        effortXSeekBar = (SeekBar) findViewById(R.id.effort_x_seekbar);
        effortYSeekBar = (SeekBar) findViewById(R.id.effort_y_seekbar);
        effortXText = (TextView) findViewById(R.id.effort_x_text);
        effortYText = (TextView) findViewById(R.id.effort_y_text);
        effortXLabel = (TextView) findViewById(R.id.effort_x_label);
        effortYLabel = (TextView) findViewById(R.id.effort_y_label);
        effortsSaveButton = (Button) findViewById(R.id.efforts_save_button);  
    }
    
    /**
     * Shows the dialog above the current view. 
     * @param p : the Participant whose efforts are get/set
     */
    public void show(Participant p) {
        super.show();
        final Participant participant = p;
        
        // on moving sliders, change effort text values and update actual participant effort values
        effortXSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int prog, boolean fromUser) {
                effortXText.setText("X = " + sb.getProgress());
                participant.setEffort1(effortXSeekBar.getProgress());
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}    
        });
        effortYSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar sb, int prog, boolean fromUser) {
                effortYText.setText("Y = " + sb.getProgress());
                participant.setEffort2(effortYSeekBar.getProgress());
            }
            public void onStartTrackingTouch(SeekBar sb) {}
            public void onStopTrackingTouch(SeekBar sb) {}    
        });
        
        // set seekbar progress to current effort values
        effortXSeekBar.setProgress((int)participant.getEffort1());
        effortYSeekBar.setProgress((int)participant.getEffort2());
        
        // on clicking OK, save new effort values to file and dismiss the dialog
        effortsSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // save efforts and exit dialog
                participant.setEfforts(effortXSeekBar.getProgress(), effortYSeekBar.getProgress());
                participant.saveData();
                dismiss();
            }
        });
    }
    
    /**
     * Sets the text labels next to the effort adjustment sliders in the
     * dialog. Has no influence on effort values or the way they are used.
     * @param xlabel : the label for band 1 (aka x)
     * @param ylabel : the label for band 2 (aka y)
     */
    public void setLabels(String xlabel, String ylabel) {
        effortXLabel.setText(xlabel);
        effortYLabel.setText(ylabel);
    }
}
