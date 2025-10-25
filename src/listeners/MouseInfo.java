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
    public void mouseClicked(MouseEvent e) 
    {
    	List<Layer> temp =  game.getCurrentLayers();
    	for(Layer l: temp)
    	{
    		l.onEvent(new Event(DifferentEvents.CLICKED, e.getPoint()));
    	}
    }


    @Override
    public void mouseMoved(MouseEvent e)
    {
    	List<Layer> temp =  game.getCurrentLayers();
    	for(Layer l: temp)
    	{
    		l.onEvent(new Event(DifferentEvents.MOVED, e.getPoint()));
    	}

    }
}
