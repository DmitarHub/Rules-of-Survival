package layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import listeners.DifferentEvents;
import listeners.Event;

public class StatsLayer extends Layer {

	private int FPS;
	private Point mousePosition;
	private List<Point> points = new ArrayList<>();
	
	private Font FPSFont = new Font("Sansserif", Font.BOLD, 30);

	private final int xOffset = 10;
	private final int yOffset = 30;
	
	private boolean showing = false;

	@Override
	public void onRender(Graphics2D drawing) 
	{
		if(showing)
		{
			drawFPS(drawing);
			
			drawMousePosition(drawing);
			
			drawPoints(drawing);
		}
	}
	
	@Override
	public void onEvent(Event e)
	{
		if(e.getCode() == DifferentEvents.MOVED)
		{
			mousePosition = e.getPoint();
		}
		else if(e.getCode() == DifferentEvents.LEFTCLICK)
		{
			points.add(e.getPoint());
		}
	}
	
	public void drawFPS(Graphics2D drawing)
	{
		String text = "FPS: " + FPS;
		drawing.setColor(Color.black);
		drawing.setFont(FPSFont);
		drawing.drawString(text, xOffset, yOffset);
	}
	
	public void drawMousePosition(Graphics2D drawing)
	{
		String pos = "MOUSE POSITION: X: " + mousePosition.getX() + " Y: " + mousePosition.getY();
		drawing.setColor(Color.black);
		drawing.setFont(FPSFont);
		drawing.drawString(pos, xOffset, yOffset * 2);
	}
	
	public void drawPoints(Graphics2D drawing)
	{
		drawing.setColor(Color.black);
		drawing.setFont(FPSFont);
		int y = yOffset*3;
		for(Point p: points)
		{
			drawing.drawString("X: " + p.getX() + " Y: " + p.getY(), xOffset, y);
			y += yOffset;
		}
	}

	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) 
	{
		
	}
	

	@Override
	public void onUpdate() 
	{


	}
	
	public void toggleShow()
	{
		showing = !showing;
	}
	
	public void setFPS(int value) { FPS = value; }

}
