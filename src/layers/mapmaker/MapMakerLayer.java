package layers.mapmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import layers.BottomLayer;
import layers.Layer;
import listeners.DifferentEvents;
import listeners.Event;
import tiles.ScaleTool;
import tiles.Tile;
import tiles.TileFileName;

public class MapMakerLayer extends BottomLayer {
	
	private final Color backgroundColor = new Color(0xAEECEF);
	private final Color optionChosenColor = new Color(0xFF7043); // warm orange

	private BasicStroke rectStroke = new BasicStroke(3f);

	
	private final int columnCount = 18;
	private final int rowCount = 14;
	
	private final int mapWidth = columnCount * tileSize;
	private final int mapHeight = rowCount * tileSize;
	
	private boolean isPaused = false;

	private boolean[][] hoveredTile = new boolean[rowCount][columnCount];
	private Tile[][] mapTiles = new Tile[rowCount][columnCount];
	
	private final int numOfTiles = TileFileName.values().length;
	private Tile[] allTiles = new Tile[numOfTiles];
	
	private BufferedImage emptyTile;
	
	private int lastRow = -1, lastColumn = -1;
	
	public MapMakerLayer()
	{
		loadEmptyTiles();
		loadTiles();
	}
	
	@Override
	public void onUpdate()
	{
		if(isPaused) return;
	}
	
	@Override
	public void onRender(Graphics2D draw)
	{
		draw.setColor(backgroundColor);
		draw.fillRect(0, 0, width, height);
		int col = 0, row = 0;
		while(row < rowCount)
		{
			int screenX = tileSize * col;
			int screenY = tileSize * row;
			draw.drawImage(mapTiles[row][col].getImage(), screenX, screenY, null);
			if(hoveredTile[row][col])
			{
				Stroke oldStroke = draw.getStroke();
				draw.setColor(optionChosenColor);
				draw.setStroke(rectStroke);
				draw.drawRect(screenX, screenY, tileSize, tileSize);
				draw.setStroke(oldStroke);
			}
			col++;
			if(col == columnCount)
			{
				col = 0;
				row++;
			}
		}
	}
	
	@Override
	public void onEvent(Event e)
	{
		DifferentEvents code = e.getCode();
		if(code == DifferentEvents.TOGGLEPAUSE)
		{
			togglePaused();
			return;
		}
		Point mousePoint = e.getPoint();
		if(mousePoint.getY() < mapHeight)
		{
			if(code == DifferentEvents.MOVED)
			{
				int column = (int)mousePoint.getX() / tileSize;
				int row = (int)mousePoint.getY() / tileSize;
				hoveredTile[row][column] = true;
				if((column != lastColumn || row != lastRow) && lastColumn >= 0 && lastRow >= 0) hoveredTile[lastRow][lastColumn] = false;
				lastRow = row;
				lastColumn = column;
			}

		}
	}
	
	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) {
		
	}
	
	private void loadEmptyTiles()
	{
		ScaleTool u = new ScaleTool();
        try {
            emptyTile = ImageIO.read(getClass().getResourceAsStream("/mapmaker/emptyTile.png"));
            for(int i = 0; i < rowCount; i++)
            {
            	for(int j = 0; j < columnCount; j++)
            	{
            		mapTiles[i][j] = new Tile();
            		mapTiles[i][j].setImage(u.scaleImage(emptyTile, tileSize, tileSize));
            	}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void loadTiles()
	{
		ScaleTool u = new ScaleTool();
        try {
            TileFileName[] names = TileFileName.values();
            for (int i = 0; i < names.length; i++) {
            	allTiles[i] = new Tile();
                String filename = "/tilesV2/" + names[i].name() + ".png";
                BufferedImage original = ImageIO.read(getClass().getResourceAsStream(filename));

                allTiles[i].setImage(original);
                allTiles[i].setImage(u.scaleImage(original, tileSize, tileSize));
                allTiles[i].setName(names[i].name());
                if(names[i].name().contains("Water") || names[i].name().contains("Tree") ||
                   names[i].name().contains("Bush")) allTiles[i].setCollision(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void togglePaused() { isPaused = !isPaused; }
}
