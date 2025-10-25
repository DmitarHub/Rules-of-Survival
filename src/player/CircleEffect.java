package player;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import layers.ingame.InGameLayer;

public class CircleEffect {

	private Ellipse2D.Double area;
    private double centerX, centerY;
    private double currentRadius = 5;
    private double maxRadius;
    private double speed = 0.5; 
	private InGameLayer gameLayer;
	
	public CircleEffect(InGameLayer gameLayer, int x, int y)
	{
		this.gameLayer = gameLayer;
		double tileSize = gameLayer.getTileSize();
		area = new Ellipse2D.Double(x, y, tileSize * 1.5, tileSize * 1.5);
		gameLayer.getCircleEffects().add(this);
		
        centerX = x + tileSize / 2.0;
        centerY = y + tileSize / 2.0;
        maxRadius = tileSize * 2.5;
	}
	
    public void update() {
        currentRadius += speed;
        if (currentRadius >= maxRadius) {
            currentRadius = 0; 
        }
    }

    public void draw(Graphics2D g2d) {
        if (maxRadius <= 0) return; 
        
        float alpha = (float) (1.0 - (currentRadius / maxRadius));

        alpha = Math.max(0f, Math.min(1f, alpha));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        double diameter = currentRadius * 2;
        double x = centerX - currentRadius;
        double y = centerY - currentRadius;
        Color tempcol = g2d.getColor();
        g2d.setColor(Color.green);
        g2d.draw(new Ellipse2D.Double(x, y, diameter, diameter));
        g2d.fill(new Ellipse2D.Double(x, y, diameter, diameter));


        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(tempcol);
    }

	
	public Rectangle2D getBounds() { return area.getBounds2D(); }
}
