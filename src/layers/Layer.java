package layers;

import java.awt.Graphics2D;

import listeners.Event;

public abstract class Layer {
	
	public abstract void onUpdate();
	
	public abstract void onEvent(Event e);
	
	public abstract void onRender(Graphics2D draw);
	
	public abstract <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition);
}
