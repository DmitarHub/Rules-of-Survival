package layers.mapmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
	
	private Font optionFont = new Font("SansSerif", Font.PLAIN, 40);
	
	private final Color backgroundColor = new Color(0xAEECEF);
	private final Color optionChosenColorAll = new Color(0x003366);
	private final Color optionChosenColor = new Color(0xFF7043);

	private BasicStroke rectStroke = new BasicStroke(3f);

	private String currentTile = "Current";
	
	private final int columnCount = 18;
	private final int rowCount = 14;
	private final int columnCountAll = 11;
	private final int rowCountAll = 3;
	
	private final int mapWidth = columnCount * tileSize;
	private final int mapHeight = rowCount * tileSize;
	
	private boolean isPaused = false;

	private boolean[][] hoveredTile = new boolean[rowCount][columnCount];
	private Tile[][] mapTiles = new Tile[rowCount][columnCount];
	
	private final int numOfTiles = TileFileName.values().length;
	private Tile[] allTiles = new Tile[numOfTiles];
	private boolean[][] hoveredTileAll = new boolean[rowCount][columnCount];
	
	private int startXAllTiles = tileSize * 6;
	private int startYAllTiles = rowCount * tileSize + tileSize/2;
	
	private int selectedX = 3 * tileSize;
	private int selectedY = ( rowCount + 1 ) * tileSize;
	
	private BufferedImage emptyTile;
	private BufferedImage selectedTile;
	private final int selectedTileSize = tileSize * 2;
	
	private int lastRow = -1, lastColumn = -1;
	private int lastRowAll = -1, lastColumnAll = -1;
	
	private ScaleTool u = new ScaleTool();
	
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
		drawAll(draw, false);
		drawAll(draw, true);
		drawCurrentSelected(draw);
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
			if(lastColumnAll >= 0 && lastRowAll >= 0) hoveredTileAll[lastRowAll][lastColumnAll] = false;
			lastRowAll = -1;
			lastColumnAll = -1;
			int column = (int)mousePoint.getX() / tileSize;
			int row = (int)mousePoint.getY() / tileSize;
			if(code == DifferentEvents.MOVED)
			{

				hoveredTile[row][column] = true;
				if((column != lastColumn || row != lastRow) && lastColumn >= 0 && lastRow >= 0) hoveredTile[lastRow][lastColumn] = false;
				lastRow = row;
				lastColumn = column;
			}
			else if(code == DifferentEvents.LEFTCLICK) mapTiles[row][column].setImage(selectedTile);
			else if(code == DifferentEvents.RIGHTCLICK) mapTiles[row][column].setImage(emptyTile);
		}
		else
		{
			if(lastColumn >= 0 && lastRow >= 0) hoveredTile[lastRow][lastColumn] = false;
			lastRow = -1;
			lastColumn = -1;
			int column = (int)(mousePoint.getX() - startXAllTiles) / tileSize;
			int row = (int)(mousePoint.getY() - startYAllTiles) / tileSize;
			if(row < 0 || column < 0) return; // Nismo u zoni 
			if(code == DifferentEvents.MOVED)
			{
				hoveredTileAll[row][column] = true;
				if((column != lastColumnAll || row != lastRowAll) && lastColumnAll >= 0 && lastRowAll >= 0) hoveredTileAll[lastRowAll][lastColumnAll] = false;
				lastRowAll = row;
				lastColumnAll = column;
			}
			else if(code == DifferentEvents.LEFTCLICK)
			{
				selectedTile = (BufferedImage)allTiles[row * columnCountAll + column].getImage();
			}
		}
	}
	
	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) {
		
	}
	
	private void drawCurrentSelected(Graphics2D draw)
	{
		draw.setColor(optionChosenColorAll);
		draw.setFont(optionFont);
		draw.drawString(currentTile, 5, selectedY + tileSize + 10);
		draw.drawImage(u.scaleImage(selectedTile, selectedTileSize, selectedTileSize), selectedX, selectedY, null);
		draw.drawRect(selectedX, selectedY, selectedTileSize, selectedTileSize);
	}
	
	private void drawAll(Graphics2D draw, boolean hovered)
	{
		int col = 0;
		int row = 0;
		while(row < rowCountAll)
		{
			if(hovered && hoveredTileAll[row][col])
			{
				int screenX = tileSize * col + startXAllTiles;
				int screenY = tileSize * row + startYAllTiles;
				Stroke oldStroke = draw.getStroke();
				draw.setColor(optionChosenColor);
				draw.setStroke(rectStroke);
				draw.drawRect(screenX, screenY, tileSize, tileSize);
				draw.setStroke(oldStroke);
			}
			else if(!hovered)
			{
				int screenX = tileSize * col + startXAllTiles;
				int screenY = tileSize * row + startYAllTiles;
				draw.drawImage(allTiles[row * columnCountAll + col].getImage(), screenX, screenY, null);
				draw.setColor(optionChosenColorAll);
				draw.drawRect(screenX, screenY, tileSize, tileSize);
			}
			col++;
			if(col == columnCountAll)
			{
				col = 0;
				row++;
			}
		}
	}
	
	private void loadEmptyTiles()
	{
        try {
            emptyTile = u.scaleImage(ImageIO.read(getClass().getResourceAsStream("/mapmaker/emptyTile.png")), tileSize, tileSize);
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
        selectedTile = (BufferedImage)allTiles[0].getImage();
	}
	
	private void togglePaused() { isPaused = !isPaused; }
}
