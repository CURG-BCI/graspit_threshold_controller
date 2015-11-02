package rascal.libemg.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * A basic Target class that is just a circle with a few different colors for
 * different states of the target (inactive, active, goal).
 */
public class Target extends EnvironmentItem {
    /** Makes size of ring around goal target this proportion of the target size */
    private static final float RING_MULTIPLIER = 1.5f;
    /** Stroke width of the ring */
    private static final int RING_WIDTH = 10;
    
    private Paint goalPaint;
    private int colorUnselected, colorSelected, colorGoal;
    private boolean active = false; 
    private boolean goal = false;
    private String button_name="";
   private int id=4;
    /**
     * Creates a target with the specified position, size, and colors. The
     * position is defined with respect to the coordinate system origin and the
     * meaning of the two generalized coordinates is determined by the
     * coordinate system itself. The radius is specified as a percentage of
     * screen width/height.
     * @param coord : the coordinate system with respect to which the target 
     * will be drawn
     * @param c1 : first generalized coordinate
     * @param c2 : second generalized coordinate
     * @param radius : normalized radius of the target
     * @param colorU : color when the target is unselected (use Color class)
     * @param colorS : color when the target is selected
     * @param colorG : color of the ring when the target is the goal
     */
    public Target(CoordinateSystem coord, float c1, float c2, float radius, 
            int colorU, int colorS, int colorG) {
        super(coord, radius, colorU);
        
        setPosition(c1, c2);
        
        this.colorUnselected = colorU;
        this.colorSelected = colorS;
        this.colorGoal = colorG;
        
        goalPaint = new Paint();
        goalPaint.setColor(colorGoal);
        goalPaint.setStyle(Style.STROKE);
        goalPaint.setStrokeWidth(RING_WIDTH);
        goalPaint.setAntiAlias(true);
    }
    
    public Target(CoordinateSystem coord, float c1, float c2, float radius, 
            int colorU, int colorS, int colorG,String name,int id) {
        super(coord, radius, colorU);
        
        setPosition(c1, c2);
        
        this.colorUnselected = colorU;
        this.colorSelected = colorS;
        this.colorGoal = colorG;
        this.setButton_name(name);
        this.setActive(true);//new
        this.setId(id);
        this.setGoal(false);
        goalPaint = new Paint();
        goalPaint.setColor(colorGoal);
        goalPaint.setStyle(Style.STROKE);
        goalPaint.setStrokeWidth(RING_WIDTH);
        goalPaint.setAntiAlias(true);
        
        
    }
    
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        { 
        if (isVisible()&&goal&&id>3) {//&& goal
            canvas.drawCircle(
                    getX(),
                    getScreenHeight() - getY(),
                    RING_MULTIPLIER * getRadius(),
                    goalPaint);
        }
          //canvas.drawText("Button", getX(),getScreenHeight()-getY(), goalPaint);
        	
        	Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setTextSize(20f);
            mTextPaint.setFakeBoldText(true);
        	//this ensures X alignment
        	mTextPaint.setTextAlign(Paint.Align.CENTER);

        	// Measure the text rectangle to get the height        
        	Rect result = new Rect();
        	mTextPaint.getTextBounds(this.button_name, 0, this.button_name.length(), result);
        	//take half the height as the offset
        	int yOffset = result.height()/2;

        	//add offset to ensure Y is aligned center
        	canvas.drawText(this.button_name, getX(), getScreenHeight() - getY()+yOffset, mTextPaint);
        	
        }
    }
    
    /**
     * Set whether or not this target should be drawn with "selected" style
     * or normal style.
     * @param activate : true if activated, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
        getPaint().setColor(active ? colorSelected : colorUnselected);
    }
    
    /**
     * Returns whether or not the target is currently activated.
     * @return true if activated, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Set whether or not this target should be drawn with a ring around it to
     * indicate that it is the current "goal" target.
     * @param goal : true if it is the goal, false otherwise
     */
    public void setGoal(boolean goal) {
        this.goal = goal;
    }
    
    /**
     * Returns whether or not the target is currently the goal target.
     * @return true if it is the goal, false otherwise
     */
    public boolean isGoal() {
        return goal;
    }

	public String getButton_name() {
		return button_name;
	}

	public void setButton_name(String button_name) {
		this.button_name = button_name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
