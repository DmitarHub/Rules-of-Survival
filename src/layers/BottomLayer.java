package layers;

import java.awt.Graphics2D;

import listeners.Event;

public class BottomLayer extends Layer {

	protected final static int originalTileSize = 32;
	protected final int columnNumber = 18;
	protected final int rowNumber = 18;

	protected final double scale = 1.5;
	protected final int tileSize =(int)((double)originalTileSize * scale);

	protected int width = tileSize * columnNumber;
	protected int height = tileSize * rowNumber;
	
	
	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRender(Graphics2D draw) {
		// TODO Auto-generated method stub
	}

	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onEvent(Event e)
	{

	}
	
    protected int centerString(Graphics2D g, String str) {
        int length = (int) g.getFontMetrics().getStringBounds(str, g).getWidth();
        return width / 2 - length / 2;
    }
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getColumnNumber() { return columnNumber; }
	public int getRowNumber() { return rowNumber; }
	public int getTileSize() { return tileSize; }

}
