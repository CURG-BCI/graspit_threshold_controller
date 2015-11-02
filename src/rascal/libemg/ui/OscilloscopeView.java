package rascal.libemg.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class OscilloscopeView extends View {

    // max value that read will be (for normalizing graph height)
    public static final float MAX_READ_HEIGHT = 1;
    
    // paint to be used for the x-axis of the graph
    private Paint midline_paint;
    // paint to be used for the data lines of the graph
    private Paint graph_paint;
    // paint used for text
    private Paint text_paint;
    // MicTestView's instance of the data (will be passed in to be graphed)
    public float[] audioData;
    
    private int height, mid_height;
    private int width, mid_width;
    private int i;
    private float tick_y;
    private String tick_label;
    boolean first = true;
    
    public OscilloscopeView(Context context) {
        super(context);
        
        // define x-axis color and line width
        midline_paint = new Paint();
        midline_paint.setColor(Color.BLACK);
        midline_paint.setStrokeWidth(2);
        
        // define graph lines' color and width
        graph_paint = new Paint();
        graph_paint.setColor(Color.RED);
        graph_paint.setStrokeWidth(1);
        
        // define text paint
        text_paint = new Paint();
        text_paint.setColor(Color.BLACK);
        text_paint.setTextSize(20);
    }
    
    // function that defines what is to be done when view becomes invalid (needs to be redrawn)
    @Override 
    public void onDraw(Canvas canvas) {
        
        // calculate pixel value for x-axis
        if (first) {
            height = getHeight();
            width = getWidth();
            mid_height = height/2;
            mid_width = width/2;
            
            first = false;
        }
        
        
        // reset canvas
        canvas.drawColor(Color.WHITE);
        
        // draw midline
        canvas.drawLine(0, mid_height, width, mid_height, midline_paint);
    
        // draw y-axis
        canvas.drawLine(getWidth()/2, 0, getWidth()/2, getHeight(), midline_paint);
        //canvas.drawText("max y = " + MAX_READ_HEIGHT, getWidth()/2 + 10 , 15, text_paint);
        
        for (i = 0; i < 5; i++) {
            tick_y = mid_height - i*mid_height/4;
            tick_label = Float.toString(i*MAX_READ_HEIGHT/4);
            
            canvas.drawLine(mid_width-10, tick_y, mid_width+10, tick_y, midline_paint);
            canvas.drawText(tick_label, mid_width+15, tick_y+10, midline_paint);
        }

        for (i = 1; i < 5; i++) {
            tick_y = mid_height + i*mid_height/4;
            tick_label = "-" + Float.toString(i*MAX_READ_HEIGHT/4);
            
            canvas.drawLine(mid_width-10, tick_y, mid_width+10, tick_y, midline_paint);
            canvas.drawText(tick_label, mid_width+15, tick_y-10, midline_paint);
        }
    
        // draw a line in between each data point in audioData
        if (audioData != null) {
            for(i = 1; i < audioData.length; i++) {
                    canvas.drawLine((width/(float)audioData.length)*(i-1), 
                                    calcHeight(audioData[i-1], mid_height),
                                    (width/(float)audioData.length)*i, 
                                    calcHeight(audioData[i], mid_height), 
                                    graph_paint);
            }
        }
    }
    
    public void setAudioData(float[] data) {
        audioData = data;
        postInvalidate();
    }
    
    
    // calculates the pixel value that a data point should be drawn at
    private int calcHeight(float data, float mid_height) {
            return (int)(-1*((data/MAX_READ_HEIGHT) * mid_height) + mid_height);
    }
}
