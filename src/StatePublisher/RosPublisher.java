package StatePublisher;

import java.awt.GridLayout;

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
	private static  float LOW_THRESHOLD = 0.20f;
    private static  float HIGH_THRESHOLD = 0.42f;
    private static  float FORWARD_INCREMENT_MAX = 0.35f;
    private static  float FORWARD_SLOW_DEFAULT = 0.05f;
    private static  float ROTATION_INCREMENT_MAX = 0.3f;
    private static long TRANSITION_DELAY_DEFAULT = 1;
    private static long TRANSITION_DELAY_MAX = 3000;
    private static final int CURSOR_UPDATE_RATE = 16;
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
	  
	  
	  JButton test= new JButton("Click");
	  final JSlider calibrationSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)calibration*100);
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
	  
	  final JSlider lowThresholdSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)LOW_THRESHOLD *100);
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
	  
	  
	  final JSlider highThresholdSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)HIGH_THRESHOLD*100);
	  highThresholdSlider.setMinorTickSpacing(10);
	  highThresholdSlider.setMajorTickSpacing(25);
	  highThresholdSlider.setPaintTicks(true);
	  highThresholdSlider.setPaintLabels(true);
	  highThresholdSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	 controller.setHighThreshold(Math.abs(highThresholdSlider.getValue()/100));
	    	  System.out.println(HIGH_THRESHOLD+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  final JSlider rotationSpeedSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)ROTATION_INCREMENT_MAX*100);
	  rotationSpeedSlider.setMinorTickSpacing(10);
	  rotationSpeedSlider.setMajorTickSpacing(25);
	  rotationSpeedSlider.setPaintTicks(true);
	  rotationSpeedSlider.setPaintLabels(true);
	  rotationSpeedSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setRotationIncrement(mapRotationSpeed(Math.abs((float)rotationSpeedSlider.getValue()/100)));
	    	  System.out.println(ROTATION_INCREMENT_MAX+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  
	  final JSlider ForwardSpeedMaxSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)FORWARD_INCREMENT_MAX*100);
	  ForwardSpeedMaxSlider.setMinorTickSpacing(10);
	  ForwardSpeedMaxSlider.setMajorTickSpacing(25);
	  ForwardSpeedMaxSlider.setPaintTicks(true);
	  ForwardSpeedMaxSlider.setPaintLabels(true);
	  ForwardSpeedMaxSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setForwardIncrement(mapForwardSpeed(Math.abs(ForwardSpeedMaxSlider.getValue()/100)));
	    	  System.out.println(FORWARD_INCREMENT_MAX+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
	  final JSlider ForwardSpeedSlowSlider=new JSlider(JSlider.HORIZONTAL,0,100,(int)FORWARD_SLOW_DEFAULT*100);
	  ForwardSpeedSlowSlider.setMinorTickSpacing(10);
	  ForwardSpeedSlowSlider.setMajorTickSpacing(25);
	  ForwardSpeedSlowSlider.setPaintTicks(true);
	  ForwardSpeedSlowSlider.setPaintLabels(true);
	  ForwardSpeedSlowSlider.addChangeListener(new ChangeListener() {
	      public void stateChanged(ChangeEvent e) {
	    	  controller.setForwardSlow(mapForwardSpeed(Math.abs(ForwardSpeedSlowSlider.getValue()/100)));
	    	  
	    	  System.out.println(FORWARD_SLOW_DEFAULT+" !!!!!!!!!!!!!!!!!");
	        }
	      });
	  
//	  JSlider TransitionDelaySlider=new JSlider(0,100,(int)calibration*100);
//	  calibrationSlider.setMinorTickSpacing(10);
//	  calibrationSlider.setMajorTickSpacing(25);
//	  calibrationSlider.setPaintTicks(true);
//	  calibrationSlider.setPaintLabels(true);
	  
	  JPanel uiPanel=new JPanel();
	  uiPanel.setLayout(new GridLayout(2,3));
	  uiPanel.add(calibrationSlider);
	  uiPanel.add(lowThresholdSlider);
	  uiPanel.add(highThresholdSlider);
	  uiPanel.add(rotationSpeedSlider);
	  uiPanel.add(ForwardSpeedMaxSlider);
	  uiPanel.add(ForwardSpeedSlowSlider);
	  //uiPanel.add(TransitionDelaySlider);
	  System.out.println("Adding Panel");
	  ui.add(uiPanel);
	  //pack();
	 //ui.setLayout(null);
	  
	  ui.setSize(640,480);
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
