package StatePublisher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PlotPanel extends JPanel {

	private double percent= 0.3;
    private double lowThreshold= 0.5;
    private double highThreshold = 0.75;

    public PlotPanel() {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //border rectangle
        drawRect(g, 10, 10, 380, 60, Color.BLACK, false);
        
        //low threshold
        drawRect(g, 10, 10, (int) (380*lowThreshold), 60, new Color(255, 10,10, 30 ), true);
        
        //high threshold
        drawRect(g, 10, 10, (int) (380*highThreshold), 60, new Color(255, 10,10, 30 ), true);
        
        //current value
        drawRect(g, 10, 10, (int) (380*percent), 60, new Color(255, 10,10, 30 ), true);        
    }
    
    protected void drawRect(Graphics g, int x,int y,int w,int h, Color c, boolean fill)
    {
    	g.setColor(c);
    	if(fill)
    	{
    		g.fillRect(x, y, w, h);
    	}
    	else{
    		g.drawRect(x, y, w, h);
    	}
    }
    
	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getLowThreshold() {
		return lowThreshold;
	}

	public void setLowThreshold(double lowThreshold) {
		this.lowThreshold = lowThreshold;
	}

	public double getHighThreshold() {
		return highThreshold;
	}

	public void setHighThreshold(double highThreshold) {
		this.highThreshold = highThreshold;
	}


}