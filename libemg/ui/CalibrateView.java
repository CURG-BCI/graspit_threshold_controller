package rascal.libemg.ui;

import rascal.libemg.Position;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CalibrateView extends View {
    
    // paints used for different colors
    private Paint green_paint;
    private Paint red_paint;
    private Paint blue_paint;
    private Paint black_paint;
    private Paint text_paint;
    
    public static final String DEFAULT_STATUS_TEXT = "Press start to calibrate";
    private String statusText = DEFAULT_STATUS_TEXT;
    
    private boolean measuring = false;
    
    private Position in_power;
    
    public static final int TEXT_SIZE = 25;
    
    public CalibrateView(Context context) {
        super(context);
        
        in_power = new Position(0, 0);
        
        // green dot when participant is supposed to flex
        green_paint = new Paint();
        green_paint.setColor(Color.GREEN);
        green_paint.setStrokeWidth(2);
        green_paint.setAntiAlias(true);
        
        // red dot when participant is relaxing
        red_paint = new Paint();
        red_paint.setColor(Color.RED);
        red_paint.setStrokeWidth(2);
        red_paint.setAntiAlias(true);
        
        blue_paint = new Paint();
        blue_paint.setColor(Color.BLUE);
        blue_paint.setStrokeWidth(2);
        
        black_paint = new Paint();
        black_paint.setColor(Color.BLACK);
        black_paint.setStrokeWidth(2);
        
        text_paint = new Paint();
        text_paint.setColor(Color.BLACK);
        text_paint.setTextSize(TEXT_SIZE);
        text_paint.setAntiAlias(true);
        
        setFocusable(true);
    }
    
    @Override 
    protected void onDraw(Canvas canvas) {
        
        // reset canvas
        canvas.drawColor(Color.WHITE);
        
        canvas.drawText("Current power:", TEXT_SIZE, getHeight() - 3*TEXT_SIZE, text_paint);
        canvas.drawText("    band 1 = " + in_power.getQ1(), 
                TEXT_SIZE, getHeight() - 2*TEXT_SIZE, text_paint);
        canvas.drawText("    band 2 = " + in_power.getQ2(), 
                TEXT_SIZE, getHeight() - TEXT_SIZE, text_paint);
        canvas.drawText(statusText, 25, 2*TEXT_SIZE, text_paint);
        
        if (measuring) {
            canvas.drawCircle(getWidth()/2, getHeight()/2, 100, green_paint);
        }
        else {
            canvas.drawCircle(getWidth()/2, getHeight()/2, 90, red_paint);
        }
    }
    
    public void update(Position pos) {
        in_power = pos;
        invalidate();
    }
    
    public void setStatusText(String text) {
        statusText = text;
        invalidate();
    }
    
    public boolean touchGoal(float x, float y){
        if((x > getWidth()/2 - 90 && x < getWidth()/2 + 90) &&
            (y > getHeight()/2 - 90 && y < getHeight()/2 + 90)){
            return true;
        }
        return false;
    }
    
    public void setMeasuring(boolean meas) {
        measuring = meas;
    }
}
