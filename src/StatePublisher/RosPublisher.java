package StatePublisher;


import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.sound.sampled.LineUnavailableException;

import StatePublisher.ThresholdController;
import rascal.libemg.EMGSensor;
import rascal.libemg.EMGSensor.OnReadListener;
import rascal.libemg.proc.MovingAverageFilter;
import rascal.libemg.proc.Util;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RosPublisher extends JFrame implements OnReadListener{
	MovingAverageFilter avgFilter;
	EMGSensor sensor;
	ThresholdController controller;
	private static  float LOW_THRESHOLD = 0.15f;
    private static  float HIGH_THRESHOLD = 0.42f;
    private static  float FORWARD_INCREMENT_MAX = 0.35f;
    private static  float FORWARD_SLOW_DEFAULT = 0.05f;
    private static  float ROTATION_INCREMENT_MAX = 0.3f;
    private static long TRANSITION_DELAY_DEFAULT = 1;
    private static long TRANSITION_DELAY_MAX = 3000;
    private static final int CURSOR_UPDATE_RATE = 16;
    private static String title;
    
    private JSlider calibrationSlider;
    private JSlider lowThresholdSlider;
    private JSlider highThresholdSlider;
    private JSlider rotationSpeedSlider;
    private JSlider forwardSpeedMaxSlider;
    private JSlider forwardSpeedSlowSlider;
    static float  calibration=0.3f;
    
    
    JFrame ui;
    

	  
	  JLabel calibrationLabel;
	  JLabel lowThresholdLabel;
	  JLabel highThresholdLabel;
	  JLabel rotationSpeedLabel;
	  JLabel forwardSpeedMaxLabel;
	  JLabel forwardSpeedSlowLabel;
	  JLabel stateLabel;
	  JButton connectButton;
	
public RosPublisher() throws LineUnavailableException
	{ 
    	System.out.println("Initializing");
    	  avgFilter = new MovingAverageFilter(8);
    	  sensor=new EMGSensor(16);
    	  sensor.addOnReadListener(this);
    	  controller=new ThresholdController(LOW_THRESHOLD,HIGH_THRESHOLD,FORWARD_INCREMENT_MAX,ROTATION_INCREMENT_MAX, FORWARD_SLOW_DEFAULT);

	}

public void startPublisher() throws UnknownHostException, IOException, LineUnavailableException
{
	  initUI();
	  //checkConnection();
	  sensor.start();	
}
	
public void initUI()
{
	  ui=new JFrame("Cursor Control");
	  
	   calibrationLabel=new JLabel("Calibration: " +(int)(calibration*100) ,JLabel.CENTER);
	   lowThresholdLabel=new JLabel("Low Threshold: "+(int)(LOW_THRESHOLD*100),JLabel.CENTER);
	  highThresholdLabel=new JLabel("High Threshold: "+(int)(HIGH_THRESHOLD*100),JLabel.CENTER);
	   rotationSpeedLabel=new JLabel("Rotation Speed: "+(int)(ROTATION_INCREMENT_MAX*100),JLabel.CENTER);
	   forwardSpeedMaxLabel=new JLabel("Forward Speed Max: "+(int)(FORWARD_INCREMENT_MAX*100),JLabel.CENTER);
	  forwardSpeedSlowLabel=new JLabel("Forward Speed Slow: "+(int)(FORWARD_SLOW_DEFAULT*100 ),JLabel.CENTER);
	  

	  
	  stateLabel=new JLabel("State", JLabel.CENTER);
	  
	  
	  
      calibrationSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(calibration*100));
	  calibrationSlider.setMinorTickSpacing(5);
	  calibrationSlider.setMajorTickSpacing(25);
	  calibrationSlider.setPaintTicks(true);
	  calibrationSlider.setPaintLabels(true);
	  calibrationSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  calibration=calibrationSlider.getValue()/100f;
	    	  calibrationLabel.setText("Calibration: "+calibrationSlider.getValue());
	         stateLabel.setText(""+controller.getInputState());
	       
	    	  System.out.println(calibration);
	        }
	      });
	  
	  lowThresholdSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(LOW_THRESHOLD *100));
	  lowThresholdSlider.setMinorTickSpacing(5);
	  lowThresholdSlider.setMajorTickSpacing(25);
	  lowThresholdSlider.setPaintTicks(true);
	  lowThresholdSlider.setPaintLabels(true);
	  lowThresholdSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setLowThreshold(Math.abs((float)lowThresholdSlider.getValue()/100));
	    	  lowThresholdLabel.setText("Low Threshold: "+lowThresholdSlider.getValue());

	    	  //System.out.println(controller.lo+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  
	  highThresholdSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(HIGH_THRESHOLD*100));
	  highThresholdSlider.setMinorTickSpacing(5);
	  highThresholdSlider.setMajorTickSpacing(25);
	  highThresholdSlider.setPaintTicks(true);
	  highThresholdSlider.setPaintLabels(true);
	  highThresholdSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	 controller.setHighThreshold(Math.abs((float)highThresholdSlider.getValue()/100));
	    	 highThresholdLabel.setText("High Threshold: "+highThresholdSlider.getValue());

	    	  //System.out.println(HIGH_THRESHOLD+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  rotationSpeedSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(ROTATION_INCREMENT_MAX*100));
	  rotationSpeedSlider.setMinorTickSpacing(5);
	  rotationSpeedSlider.setMajorTickSpacing(25);
	  rotationSpeedSlider.setPaintTicks(true);
	  rotationSpeedSlider.setPaintLabels(true);
	  rotationSpeedSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setRotationIncrement(mapRotationSpeed(Math.abs((float)rotationSpeedSlider.getValue()/100)));
	    	  rotationSpeedLabel.setText("Rotation Speed: "+rotationSpeedSlider.getValue());

	    	  //System.out.println(ROTATION_INCREMENT_MAX+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  
	  forwardSpeedMaxSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(FORWARD_INCREMENT_MAX*100));
	  forwardSpeedMaxSlider.setMinorTickSpacing(5);
	  forwardSpeedMaxSlider.setMajorTickSpacing(25);
	  forwardSpeedMaxSlider.setPaintTicks(true);
	  forwardSpeedMaxSlider.setPaintLabels(true);
	  forwardSpeedMaxSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setForwardIncrement(mapForwardSpeed(Math.abs((float)forwardSpeedMaxSlider.getValue()/100)));
	    	  forwardSpeedMaxLabel.setText("Forward Speed Max: "+forwardSpeedMaxSlider.getValue());

	    	 // System.out.println(FORWARD_INCREMENT_MAX+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  forwardSpeedSlowSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(FORWARD_SLOW_DEFAULT*100));
	  forwardSpeedSlowSlider.setMinorTickSpacing(5);
	  forwardSpeedSlowSlider.setMajorTickSpacing(25);
	  forwardSpeedSlowSlider.setPaintTicks(true);
	  forwardSpeedSlowSlider.setPaintLabels(true);
	  forwardSpeedSlowSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setForwardSlow(mapForwardSpeed(Math.abs((float)forwardSpeedSlowSlider.getValue()/100)));
	    	  forwardSpeedSlowLabel.setText("Forward Speed Slow: "+forwardSpeedSlowSlider.getValue());

	    	 // System.out.println(FORWARD_SLOW_DEFAULT+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  //JSlider TransitionDelaySlider=new JSlider(0,100,(int)(calibration*100));
	  connectButton=new JButton("Connect");
	  
	  connectButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	        	  if(controller.initSocketConnections())
	        	  {
	        		  ui.setTitle("Cursor Control [Real Time]");
	        	  }
	        	  else
	        	  {
	        		  ui.setTitle("Cursor Control [Debug]");
	        	  }
	             
	          }          
	       });

	  JPanel calibrationPanel=new JPanel();
	  calibrationPanel.setLayout(new GridLayout(2,1,0,0));
	  JPanel lowThresholdPanel=new JPanel();
	  lowThresholdPanel.setLayout(new GridLayout(2,1,0,0));
	  JPanel highThresholdPanel=new JPanel();
	  highThresholdPanel.setLayout(new GridLayout(2,1,0,0));
	  JPanel rotationPanel=new JPanel();
	  rotationPanel.setLayout(new GridLayout(2,1,0,0));
	  JPanel forwardSpeedMaxPanel=new JPanel();
	  forwardSpeedMaxPanel.setLayout(new GridLayout(2,1,0,0));
	  JPanel forwardSpeedSlowPanel=new JPanel();
	  forwardSpeedSlowPanel.setLayout(new GridLayout(2,1,0,0));
	  JPanel statePanel=new JPanel();
	  statePanel.setLayout(new GridLayout(1,2,0,0));
	  
	  
	  JPanel uiPanel=new JPanel();
	  
	  calibrationPanel.add(calibrationLabel);
	 // calibrationPanel.add(Box.createHorizontalStrut(50));
	  calibrationPanel.add(calibrationSlider);
	  calibrationPanel.setBackground(Color.orange);
	  
	  lowThresholdPanel.add(lowThresholdLabel);
	  //lowThresholdPanel.add(Box.createHorizontalStrut(30));
	  lowThresholdPanel.add(lowThresholdSlider);
	  lowThresholdPanel.setBackground(Color.orange);
	  
	  highThresholdPanel.add(highThresholdLabel);
	  //highThresholdPanel.add(Box.createHorizontalStrut(30));
	  highThresholdPanel.add(highThresholdSlider);
	  
	  highThresholdPanel.setBackground(Color.orange);
	  
	  rotationPanel.add(rotationSpeedLabel);
	  
	  rotationPanel.add(rotationSpeedSlider);
	  rotationPanel.setBackground(Color.orange);
	  
	  forwardSpeedMaxPanel.add(forwardSpeedMaxLabel);
	  forwardSpeedMaxPanel.add(forwardSpeedMaxSlider);
	  forwardSpeedMaxPanel.setBackground(Color.orange);
	  
	  forwardSpeedSlowPanel.add(forwardSpeedSlowLabel);
	  forwardSpeedSlowPanel.add(forwardSpeedSlowSlider);
	  forwardSpeedSlowPanel.setBackground(Color.orange);
	  
	  statePanel.add(stateLabel);
	  statePanel.add(connectButton);
	  
	  uiPanel.setLayout(new GridLayout(7,1));
	  uiPanel.add(calibrationPanel);
	  uiPanel.add(lowThresholdPanel);
	  uiPanel.add(highThresholdPanel);
	  uiPanel.add(rotationPanel);
	  uiPanel.add(forwardSpeedMaxPanel);
	  uiPanel.add(forwardSpeedSlowPanel);
	  uiPanel.add(statePanel);
	  System.out.println("Adding Panel");
	  ui.add(uiPanel);
	  ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  ui.setSize(400,600);
	  //ui.pack();
	  ui.setVisible(true);
	  
	  

 
	  }
    
    private static float mapForwardSpeed(float val) {
        return val * (1f/CURSOR_UPDATE_RATE);
    }
    
    private static float mapRotationSpeed(float val) {
        return val * (2*Util.FPI/CURSOR_UPDATE_RATE);
    }

	
    @Override
    public void onRead(float[] sensorData) {
        // process raw input on the recording thread
    	stateLabel.setText(""+controller.getInputState());
        final float val = Util.rms(sensorData);
        System.out.println("In onRead : " + val);
        onUpdate(val);
        try {
			controller.update(val);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(!controller.connected)
        {
       	 ui.setTitle("Cursor Control [Debug]");
        }
    }
    
    public void onUpdate(float val)
    {   
    	val = avgFilter.update(val / calibration);
    	System.out.println("val: "+val);
    	
    	
    }
    
    public void checkConnection() throws UnknownHostException, IOException, LineUnavailableException
    {
	  if(controller.initSocketConnections())
	  {
		  ui.setTitle("Cursor Control [Real Time]");
	  }
	  else
	  {
		  ui.setTitle("Cursor Control [Debug]");
	  }
	  
    	
    }
	public static void main(String args[]) throws LineUnavailableException, UnknownHostException, IOException
	{ RosPublisher rospub=new RosPublisher();
	rospub.startPublisher();
	}
	

}