package StatePublisher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.sound.sampled.LineUnavailableException;

import StatePublisher.ThresholdController;
import rascal.libemg.EMGSensor;
import rascal.libemg.EMGSensor.OnReadListener;
import rascal.libemg.proc.MovingAverageFilter;
import rascal.libemg.proc.Util;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
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
    
    private JSlider calibrationSlider;
    private JSlider lowThresholdSlider;
    private JSlider highThresholdSlider;
    private JSlider rotationSpeedSlider;
    private JSlider forwardSpeedMaxSlider;
    private JSlider forwardSpeedSlowSlider;
    static float  calibration=0.3f;
    JFrame ui;
    
	
    public RosPublisher() throws LineUnavailableException
	{ 
    	System.out.println("Initializing");
    	  avgFilter = new MovingAverageFilter(8);
    	  sensor=new EMGSensor(16);
    	  sensor.addOnReadListener(this);
    	  controller=new ThresholdController(LOW_THRESHOLD,HIGH_THRESHOLD,FORWARD_INCREMENT_MAX,ROTATION_INCREMENT_MAX, FORWARD_SLOW_DEFAULT);
	
	  
	  ui=new JFrame();
	  
	  JLabel calibrationLabel=new JLabel("Calibration",JLabel.CENTER);
	  JLabel lowThresholdLabel=new JLabel("Low Threshold",JLabel.CENTER);
	  JLabel highThresholdLabel=new JLabel("High Threshold",JLabel.CENTER);
	  JLabel rotationSpeedLabel=new JLabel("Rotation Speed",JLabel.CENTER);
	  JLabel forwardSpeedMaxLabel=new JLabel("Forward Speed Max",JLabel.CENTER);
	  JLabel forwardSpeedSlowLabel=new JLabel("Forward Speed Slow",JLabel.CENTER);
      calibrationSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(calibration*100));
	  calibrationSlider.setMinorTickSpacing(10);
	  calibrationSlider.setMajorTickSpacing(25);
	  calibrationSlider.setPaintTicks(true);
	  calibrationSlider.setPaintLabels(true);
	  calibrationSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  calibration=calibrationSlider.getValue()/100f;
	  
	    	  System.out.println(calibration+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  lowThresholdSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(LOW_THRESHOLD *100));
	  lowThresholdSlider.setMinorTickSpacing(10);
	  lowThresholdSlider.setMajorTickSpacing(25);
	  lowThresholdSlider.setPaintTicks(true);
	  lowThresholdSlider.setPaintLabels(true);
	  lowThresholdSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setLowThreshold(Math.abs(lowThresholdSlider.getValue()/100));
	    	  System.out.println(LOW_THRESHOLD+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  
	  highThresholdSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(HIGH_THRESHOLD*100));
	  highThresholdSlider.setMinorTickSpacing(5);
	  highThresholdSlider.setMajorTickSpacing(25);
	  highThresholdSlider.setPaintTicks(true);
	  highThresholdSlider.setPaintLabels(true);
	  highThresholdSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	 controller.setHighThreshold(Math.abs(highThresholdSlider.getValue()/100));
	    	  System.out.println(HIGH_THRESHOLD+" !!!!!!!!!!!!!!!!!");
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
	    	  System.out.println(ROTATION_INCREMENT_MAX+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  
	  forwardSpeedMaxSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(FORWARD_INCREMENT_MAX*100));
	  forwardSpeedMaxSlider.setMinorTickSpacing(10);
	  forwardSpeedMaxSlider.setMajorTickSpacing(25);
	  forwardSpeedMaxSlider.setPaintTicks(true);
	  forwardSpeedMaxSlider.setPaintLabels(true);
	  forwardSpeedMaxSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setForwardIncrement(mapForwardSpeed(Math.abs(forwardSpeedMaxSlider.getValue()/100)));
	    	  System.out.println(FORWARD_INCREMENT_MAX+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  forwardSpeedSlowSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)(FORWARD_SLOW_DEFAULT*100));
	  forwardSpeedSlowSlider.setMinorTickSpacing(10);
	  forwardSpeedSlowSlider.setMajorTickSpacing(25);
	  forwardSpeedSlowSlider.setPaintTicks(true);
	  forwardSpeedSlowSlider.setPaintLabels(true);
	  forwardSpeedSlowSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setForwardSlow(mapForwardSpeed(Math.abs(forwardSpeedSlowSlider.getValue()/100)));
	    	  
	    	  System.out.println(FORWARD_SLOW_DEFAULT+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  JSlider TransitionDelaySlider=new JSlider(0,100,(int)calibration*100);
	  calibrationSlider.setMinorTickSpacing(10);
	  calibrationSlider.setMajorTickSpacing(25);
	  calibrationSlider.setPaintTicks(true);
	  calibrationSlider.setPaintLabels(true);
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
	  
	  uiPanel.setLayout(new GridLayout(6,1));
	  uiPanel.add(calibrationPanel);
	  uiPanel.add(lowThresholdPanel);
	  uiPanel.add(highThresholdPanel);
	  uiPanel.add(rotationPanel);
	  uiPanel.add(forwardSpeedMaxPanel);
	  uiPanel.add(forwardSpeedSlowPanel);
	  //uiPanel.add(TransitionDelaySlider);
	  System.out.println("Adding Panel");
	  ui.add(uiPanel);
	  //pack();
	 //ui.setLayout(null);
	  ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  ui.setSize(320,480);
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
    	
        final float val = Util.rms(sensorData);
        System.out.println("In onRead : " + val);
        onUpdate(val);
        controller.update(val);
        
    }
    
    public void onUpdate(float val)
    {   
    	val = avgFilter.update(val / calibration);
    	System.out.println("val: "+val);
    	
    	
    }
	public static void main(String args[]) throws LineUnavailableException
	{ RosPublisher rospub=new RosPublisher();
	  System.out.println("Starting Sensor");
	  rospub.sensor.start();
	  //rospub.ui.setVisible(true);
	  
	 
	  
	  
	}
	

}
