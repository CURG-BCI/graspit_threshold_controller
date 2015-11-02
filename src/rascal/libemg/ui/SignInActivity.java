package rascal.libemg.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rascal.libemg.Participant;
import rascal.libemg.Participant.ParticipantNotFoundException;
import rascal.libemg.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A single Activity that takes care of creating participants, setting their
 * information, and signing them in.
 */
public class SignInActivity extends Activity implements OnClickListener {
    private Participant participant;
    private ListView participantList;
    private String[] list;
    private Button sessionDecrement, sessionIncrement;
    private AlertDialog newParicipantDialog;
    
    // new participant dialog layout elements
    private EditText npId, npAge, npMuscle, npExperiment, npDevice;
    private AutoCompleteTextView npType;
    private RadioButton npMale, npRight;
    
    // selected participant layout elements
    private TextView spTvHeading, spTvAge, spTvGender, spTvHand, spTvMuscle, 
                     spTvExperiment, spTvSession, spTvDevice, spTvType;
    private Button selectButton;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        
        spTvHeading = (TextView) findViewById(R.id.sp_heading);
        spTvType = (TextView) findViewById(R.id.sp_type);
        spTvAge = (TextView) findViewById(R.id.sp_age);
        spTvGender = (TextView) findViewById(R.id.sp_gender);
        spTvHand = (TextView) findViewById(R.id.sp_hand);
        spTvMuscle = (TextView) findViewById(R.id.sp_muscle);
        spTvExperiment = (TextView) findViewById(R.id.sp_experiment);
        spTvDevice = (TextView) findViewById(R.id.sp_device);
        spTvSession = (TextView) findViewById(R.id.sp_session);
        
        sessionDecrement = (Button) findViewById(R.id.button_sessiondec);
        sessionDecrement.setOnClickListener(this);
        sessionIncrement = (Button) findViewById(R.id.button_sessioninc);
        sessionIncrement.setOnClickListener(this);
        selectButton = (Button) findViewById(R.id.button_select);
        selectButton.setOnClickListener(this);
        
        getParticipantList();
        participantList = (ListView) findViewById(R.id.list_currentparticipants);
        if (list != null) {
            participantList.setAdapter(new CustomListAdapter(this, list));
        }
        participantList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int arg2, long arg3) {
                setSelectedParticipant(list[arg2]);
            }
        });
        participantList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteParticipant(position);
                return true;
            }
        });
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(null).setTitle("New Participant");
        View dialogView = (View) LayoutInflater.from(this).inflate(R.layout.newparticipant_dialog, null);
        npId = (EditText) dialogView.findViewById(R.id.new_id);
        npType = (AutoCompleteTextView) dialogView.findViewById(R.id.new_type);
        npAge = (EditText) dialogView.findViewById(R.id.new_age);
        npMuscle = (EditText) dialogView.findViewById(R.id.new_muscle);
        npExperiment = (EditText) dialogView.findViewById(R.id.new_experiment);
        npDevice = (EditText) dialogView.findViewById(R.id.new_device);
        npMale = (RadioButton) dialogView.findViewById(R.id.new_male);
        npRight = (RadioButton) dialogView.findViewById(R.id.new_right);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Participant.TYPE_LIST);
        npType.setAdapter(adapter);
        
        builder.setView(dialogView);
        builder.setPositiveButton("Save",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int dialogId) {
                Participant newP = new Participant();
                newP.setId(npId.getText().toString());
                newP.setType(npType.getText().toString());
                newP.setAge(npAge.getText().toString());
                newP.setGender((npMale.isChecked() ? "male" : "female"));
                newP.setHand((npRight.isChecked() ? "right" : "left"));
                newP.setMuscle(npMuscle.getText().toString());
                newP.setExperiment(npExperiment.getText().toString());
                newP.setDevice(npDevice.getText().toString());
                newP.setSession("1");
                
                newP.saveInfo();
                
                if (list == null) {
                    list = new String[1];
                    list[0] = newP.getId();
                }
                else if (!Arrays.asList(list).contains(newP.getId())) {
                    String[] tempIds = list.clone();
                    list = new String[tempIds.length + 1];
                    for (int i = 0; i < tempIds.length; i++) {
                        list[i] = tempIds[i];
                    }
                    list[tempIds.length] = newP.getId();
                }
                Arrays.sort(list);
                participantList.setAdapter(new CustomListAdapter(getApplicationContext(), list));
                setSelectedParticipant(newP.getId());
            }
        });
        builder.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        newParicipantDialog = builder.create();
    }
    
    private void deleteParticipant(int position) {
        final int deletePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(null).setTitle("Delete participant " + list[position] + "?");
        
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int dialogId) {
                deleteParticipantFile(list[deletePosition]);
                List<String> tempList = new ArrayList<String>(Arrays.asList(list));
                tempList.remove(deletePosition);
                list = tempList.toArray(new String[0]);
                participantList.setAdapter(new CustomListAdapter(getApplicationContext(), list));
                
                if (list.length > 0) {
                    setSelectedParticipant(list[0]);
                }
            }
        });
        
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        
        builder.show();
    }
    
    private void deleteParticipantFile(String id) {
        Participant p = new Participant();
        p.setId(id);
        p.deleteFiles();
    }
    
    private void setSelectedParticipant(String id) {
        participant = new Participant();
        participant.setId(id);
        try {
            participant.loadInfo();
        } catch (ParticipantNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load participant log in data", 
                    Toast.LENGTH_LONG).show();
        }
        
        spTvHeading.setText("Participant " + participant.getId());
        spTvType.setText("type: " + participant.getType());
        spTvAge.setText("age: " + participant.getAge());
        spTvGender.setText("gender: " + participant.getGender());
        spTvHand.setText("hand: " + participant.getHand());
        spTvMuscle.setText("muscle: " + participant.getMuscle());
        spTvExperiment.setText("experiment: " + participant.getExperiment());
        spTvSession.setText(participant.getSession());
        spTvDevice.setText("device: " + participant.getDevice());
    }
    
    private void getParticipantList() {
        File root = new File(Environment.getExternalStorageDirectory() + 
                "/" + Participant.DIR_SIGNIN_FILES + "/");
        File[] files = root.listFiles();
        if (files == null) {
            return;
        }
        String path, id;
        ArrayList<String> idList = new ArrayList<String>();
        int count = 0;
        
        for (File file : files) {
            path = file.getName();
            id = path.substring(path.indexOf('_')+1, path.indexOf('.'));
            idList.add(id);
            count++;
        }
        
        list = new String[count];
        for (int i = 0; i < count; i++) {
            list[i] = idList.get(i);
        }
        Arrays.sort(list);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signin, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int item = menuItem.getItemId();
        
        if (item == R.id.menu_newparticipant) {
            newParicipantDialog.show();
        }
        return true;
    }
    
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_sessiondec) {
            if (participant!= null) {
                int session = Integer.parseInt(spTvSession.getText().toString());
                session--;
                
                String sessionStr = Integer.toString(session);
                participant.setSession(sessionStr);
                spTvSession.setText(sessionStr);
            }
        }
        else if (id == R.id.button_sessioninc) {
            if (participant != null) {
                int session = Integer.parseInt(spTvSession.getText().toString());
                session++;
                
                String sessionStr = Integer.toString(session);
                participant.setSession(sessionStr);
                spTvSession.setText(sessionStr);
            }
        } 
        else if (id == R.id.button_select) {
            if (participant != null) {
                participant.saveInfo(); // in case session number has changed
                participant.setSignedIn();
            }
            finish();
        }
    }


    public class CustomListAdapter extends BaseAdapter implements ListAdapter{

        private Context mContext;
        private String[] list;

        public CustomListAdapter(Context context, String[] titles) {
            mContext = context;
            list = titles;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomView cv;
            if (convertView == null) {
                cv = new CustomView(mContext,""+list[position]);
            } 
            else {
                cv = (CustomView) convertView;
                cv.setTitle(list[position]);
            }
            return cv;
        }
    }

    private class CustomView extends LinearLayout {
        private TextView mTitle;
        
        public CustomView(Context context, String itemName) {
            super(context);
            this.setOrientation(HORIZONTAL);

            mTitle = new TextView(context);
            mTitle.setTextColor(Color.BLACK);
            mTitle.setText(itemName);

            mTitle.setTextSize(25);

            addView(mTitle, new LinearLayout.LayoutParams(200, LayoutParams.WRAP_CONTENT));
        }
       
        public void setTitle(String title) {
            mTitle.setText(title);
         }
     }
}
