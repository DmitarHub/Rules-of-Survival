package listeners;

import game.Game;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import layers.Layer;

public class MouseInfo extends MouseAdapter{
	
	private Game game;
	
	public MouseInfo(Game g)
	{
		game = g;
	}

    @Override
    public void mousePressed(MouseEvent e) 
    {
    	
    	List<Layer> temp =  game.getCurrentLayers();
    	for(Layer l: temp)
    	{
    		l.onEvent(new Event(e.getButton() == MouseEvent.BUTTON1 ? DifferentEvents.LEFTCLICKPRESS : DifferentEvents.RIGHTCLICKPRESS, e.getPoint(), 0));
    	}
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
    	List<Layer> temp =  game.getCurrentLayers();
    	for(Layer l: temp)
    	{
    		l.onEvent(new Event(e.getButton() == MouseEvent.BUTTON1 ? DifferentEvents.LEFTCLICKRELEASE : DifferentEvents.RIGHTCLICKRELEASE, e.getPoint(), 0));
    	}
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
    	List<Layer> temp =  game.getCurrentLayers();
    	for(Layer l: temp)
    	{
    		l.onEvent(new Event(DifferentEvents.DRAGGED, e.getPoint(), 0));
    	}
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    	List<Layer> temp =  game.getCurrentLayers();
    	for(Layer l: temp)
    	{
    		l.onEvent(new Event(DifferentEvents.MOVED, e.getPoint(), 0));
    	}

    }
}
