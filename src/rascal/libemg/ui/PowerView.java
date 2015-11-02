package rascal.libemg.ui;

import rascal.libemg.EMGSensor;
import rascal.libemg.Position;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class PowerView extends View {
    
    //paints used for the x and y power bars 
    private Paint good_paint;
    private Paint bad_paint;

    //paint used to draw boxes for powers
    private Paint black_paint;
    private Paint text_paint;
    
    private Rect r1, r2;
    
    private int height;
    private float point_x, point_y;
    
    private BarAnimation barAnimation;
    
    private boolean first = true;

    public PowerView(Context context) {
        super(context);
        
        good_paint = new Paint();
        good_paint.setColor(Color.BLUE);
        good_paint.setStrokeWidth(2);
        
        bad_paint = new Paint();
        bad_paint.setColor(Color.RED);
        bad_paint.setStrokeWidth(2);
        
        black_paint = new Paint();
        black_paint.setColor(Color.BLACK);
        black_paint.setStrokeWidth(2);
        
        text_paint = new Paint();
        text_paint.setColor(Color.BLACK);
        text_paint.setTextSize(20);
        text_paint.setAntiAlias(true);
            
        r1 = new Rect();
        r2 = new Rect();
        
        barAnimation = 
                new BarAnimation();
        barAnimation.setInterpolator(new LinearInterpolator());
        barAnimation.setDuration(1000/EMGSensor.DEFAULT_UPDATE_RATE);
        
        setFocusable(false);
    }
    
    
    
    // function that defines what is to be done when view becomes invalid (needs to be redrawn)
    @Override 
    protected void onDraw(Canvas canvas) {

        if (first) {
            height = getHeight();
            first = false;
        }
        
        // reset canvas
        canvas.drawColor(Color.WHITE);
        
        // draw labels
        canvas.drawText("Power X", getWidth()/4 - 40, 25, text_paint);
        canvas.drawText("(80 - 100 Hz)", getWidth()/4 - 45, 42, text_paint);
        
        canvas.drawText("Power Y", 3*getWidth()/4 - 40, 25, text_paint);
        canvas.drawText("(130-150 Hz)", 3*getWidth()/4 - 45, 42, text_paint);
        
        // draw lines for x box
        canvas.drawLine(25, 50, getWidth()/2 - 25, 50, black_paint);        
        canvas.drawLine(25, 50, 25, getHeight() - 25, black_paint);     
        canvas.drawLine(getWidth()/2 - 25, 50, getWidth()/2 - 25, getHeight() - 25, black_paint);       
        canvas.drawLine(25, getHeight() - 25, getWidth()/2 - 25, getHeight() - 25, black_paint);
        
        
        // draw lines for y box
        canvas.drawLine(25 + getWidth()/2, 50, getWidth() - 25,   50, black_paint);     
        canvas.drawLine(25 + getWidth()/2, 50, 25 + getWidth()/2, getHeight() - 25, black_paint);       
        canvas.drawLine(getWidth() - 25,   50, getWidth() - 25,   getHeight() - 25, black_paint);       
        canvas.drawLine(25 + getWidth()/2, getHeight() - 25, getWidth() - 25,   getHeight() - 25, black_paint);
        
        
        // draw rectangles for power bars
        if( point_x < getHeight() - 77) {
            r1.left = 26; 
            r1.top = (int) (getHeight() - 26 - point_x);
            r1.right = getWidth()/2 - 26;       
            r1.bottom = getHeight() - 26;
                
            canvas.drawRect(r1, good_paint);
        }
        else {
            r1.left = 26; 
            r1.top = 51;
            r1.right = getWidth()/2 - 26;       
            r1.bottom = getHeight() - 26;
            canvas.drawRect(r1, bad_paint);
        }
        
        if( point_y < getHeight() - 77) {
            r2.left = getWidth()/2 + 26; 
            r2.top = (int) (getHeight() - 26 - point_y);
            r2.right = getWidth() - 26;     
            r2.bottom = getHeight() - 26;
        
            canvas.drawRect(r2, good_paint);
        }
        else {
            r2.left = getWidth()/2 + 26; 
            r2.top = 51;
            r2.right = getWidth() - 26;     
            r2.bottom = getHeight() - 26;
        
            canvas.drawRect(r2, bad_paint);
        }       
    }
    
    public void update(Position pos) {
        barAnimation.setPositions(
                point_x, point_y, 
                pos.getQ1()*(height-75), pos.getQ2()*(height-75));
        startAnimation(barAnimation);
    }

    private void setIntermediatePosition(float x, float y) {
        point_x = x;
        point_y = y;
        invalidate();
    }
    
    public class BarAnimation extends Animation {
        private float from_x, from_y, to_x, to_y;
        
        public BarAnimation() {
            super();
        }
        
        public void setPositions(float from_x, float from_y, float to_x, float to_y) {
            this.from_x = from_x;
            this.from_y = from_y;
            this.to_x = to_x;
            this.to_y = to_y;
        }
        
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            setIntermediatePosition(
                    from_x + (to_x - from_x)*interpolatedTime,
                    from_y + (to_y - from_y)*interpolatedTime);
        }
    }
}
