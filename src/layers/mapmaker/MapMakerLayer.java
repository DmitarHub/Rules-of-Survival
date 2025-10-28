package layers.mapmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import game.Game;
import layers.BottomLayer;
import layers.Layer;
import listeners.DifferentEvents;
import listeners.Event;
import listeners.KeyInputs;
import tiles.ScaleTool;
import tiles.Tile;
import tiles.TileFileName;

public class MapMakerLayer extends BottomLayer {
	
	private Font optionFont = new Font("SansSerif", Font.PLAIN, 28);
	
	private final Color backgroundColor = new Color(0xAEECEF);
	private final Color cantClickColor = new Color(0x1A6970);
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
	
	private int[] xLeftCoords = {(int)(21.0 / 4 * tileSize), (int)(23.0 / 4 * tileSize), (int)(23.0 / 4 * tileSize)};
	private int[] yLeftCoords = {startYAllTiles + tileSize/2 + tileSize, startYAllTiles + tileSize, startYAllTiles + 2 *tileSize};
	private Polygon leftTriangle = new Polygon(xLeftCoords, yLeftCoords, 3);
	
	private int[] xRightCoords = {(columnCount - 1) * tileSize + tileSize / 4, (columnCount - 1) * tileSize + tileSize / 4, (columnCount - 1) * tileSize + tileSize * 3 / 4};
	private int[] yRightCoords = {startYAllTiles + 2 *tileSize, startYAllTiles + tileSize, startYAllTiles + tileSize/2 + tileSize};
	private Polygon rightTriangle = new Polygon(xRightCoords, yRightCoords, 3);
	
	private int selectedX = 3 * tileSize;
	private int selectedY = ( rowCount + 1 ) * tileSize;
	
	private BufferedImage emptyTile;
	private BufferedImage selectedTile;
	private final int selectedTileSize = tileSize * 2;
	
	private int lastRow = -1, lastColumn = -1;
	private int lastRowAll = -1, lastColumnAll = -1;
	
	private boolean leftClickOn = false;
	private boolean rightClickOn = false;
	
	private int shiftX, shiftY;
	
	private boolean allSelected = false;
	
	private ScaleTool u = new ScaleTool();
	
	private KeyInputs ki;
	
	private boolean hoveredLeftTriangle = false;
	private boolean hoveredRightTriangle = false;
	
	private int pageOffset = 0;
	
	public MapMakerLayer()
	{
		ki = Game.Get().getKeyInputs();
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
		drawMap(draw, false);
		drawMap(draw, true);
		drawAll(draw, false);
		drawAll(draw, true);
		drawCurrentSelected(draw);
		drawTriangles(draw);
	}
	
	private void drawTriangles(Graphics2D draw)
	{
		draw.drawPolygon(leftTriangle);
		if(pageOffset == 0)
		{
			Color oldColor = draw.getColor();
			draw.setColor(cantClickColor);
			draw.fillPolygon(leftTriangle);
			draw.setColor(oldColor);
			draw.drawPolygon(leftTriangle);
		}
		else draw.fillPolygon(leftTriangle);
		draw.drawPolygon(rightTriangle);
		if(pageOffset * (rowCountAll * columnCountAll) + (rowCountAll * columnCountAll)>= allTiles.length)
		{
			Color oldColor = draw.getColor();
			draw.setColor(cantClickColor);
			draw.fillPolygon(rightTriangle);
			draw.setColor(oldColor);
			draw.drawPolygon(rightTriangle);
		}
		else draw.fillPolygon(rightTriangle);
		if(hoveredLeftTriangle) drawStroke(draw, leftTriangle);
		else if(hoveredRightTriangle) drawStroke(draw, rightTriangle);
	}
	
	private void drawStroke(Graphics2D draw, Polygon p)
	{
		Stroke oldStroke = draw.getStroke();
		Color oldColor = draw.getColor();
		draw.setColor(optionChosenColor);
		draw.setStroke(rectStroke);
		draw.drawPolygon(p);
		draw.setStroke(oldStroke);
		draw.setColor(oldColor);
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
		if(isPaused) return;
		if(e.getType() == 0) handleMouseInput(e);
		else handleKeyBoardInput(e);
	}
	
	private void handleMouseInput(Event e)
	{
		DifferentEvents code = e.getCode();
		Point mousePoint = e.getPoint();
		if(mousePoint.getY() < mapHeight)
		{
			if(lastColumnAll >= 0 && lastRowAll >= 0) hoveredTileAll[lastRowAll][lastColumnAll] = false;
			lastRowAll = -1;
			lastColumnAll = -1;
			int column = (int)mousePoint.getX() / tileSize;
			int row = (int)mousePoint.getY() / tileSize;
			if(column < 0 || column > columnCount || row < 0) return;
			if(code == DifferentEvents.MOVED)
			{

				hoveredTile[row][column] = true;
				if((column != lastColumn || row != lastRow) && lastColumn >= 0 && lastRow >= 0) hoveredTile[lastRow][lastColumn] = false;
				lastRow = row;
				lastColumn = column;
			}
			else if(code == DifferentEvents.LEFTCLICKPRESS) 
			{
				if(allSelected)
				{
					setAllTo(selectedTile);
					allSelected = false;
					return;
				}
				mapTiles[row][column].setImage(selectedTile);
				shiftX = (int) mousePoint.getX();
				shiftY = (int) mousePoint.getY();
				leftClickOn = true;
			}
			else if(code == DifferentEvents.RIGHTCLICKPRESS) 
			{
				if(allSelected)
				{
					setAllTo(emptyTile);
					allSelected = false;
					return;
				}
				mapTiles[row][column].setImage(emptyTile);
				rightClickOn = true;
			}
			else if(code == DifferentEvents.LEFTCLICKRELEASE) leftClickOn = false;
			else if(code == DifferentEvents.RIGHTCLICKRELEASE) rightClickOn = false;
			else if(code == DifferentEvents.DRAGGED)
			{
				if(ki.isShift())
				{
					if((Math.abs(mousePoint.getY() - shiftY) - Math.abs(mousePoint.getX() - shiftX)) >= 0) column = (int)shiftX / tileSize;
					else row = (int)shiftY / tileSize;
				}
				hoveredTile[row][column] = true;
				if((column != lastColumn || row != lastRow) && lastColumn >= 0 && lastRow >= 0) hoveredTile[lastRow][lastColumn] = false;
				lastRow = row;
				lastColumn = column;
				if(leftClickOn) mapTiles[row][column].setImage(selectedTile);
				else if(rightClickOn) mapTiles[row][column].setImage(emptyTile);
			}
		}
		else
		{
			if(lastColumn >= 0 && lastRow >= 0) hoveredTile[lastRow][lastColumn] = false;
			lastRow = -1;
			lastColumn = -1;
			if(code == DifferentEvents.MOVED)
			{
				checkHoverTriangles(mousePoint);
				checkHoverAllTiles(mousePoint);
			}
			else if(code == DifferentEvents.LEFTCLICKPRESS)
			{
				checkTrianglesClick(mousePoint);
				checkAllTilesClick(mousePoint);
			}
		}
	}
	
	private void checkHoverTriangles(Point p)
	{
		if(leftTriangle.contains(p) && pageOffset != 0) 
		{
			hoveredLeftTriangle = true;
			hoveredRightTriangle = false;
		}
		else if(rightTriangle.contains(p) && pageOffset * (rowCountAll * columnCountAll) +  (rowCountAll * columnCountAll) < allTiles.length)
		{
			hoveredRightTriangle = true;
			hoveredLeftTriangle = false;
		}
		else
		{
			hoveredLeftTriangle = false;
			hoveredRightTriangle = false;
		}
		
	}
	
	private void checkHoverAllTiles(Point p)
	{
		int column = (int)Math.floor((p.getX() - startXAllTiles) / tileSize);
		int row = (int)Math.floor((p.getY() - startYAllTiles) / tileSize);
		if((row < 0 || column < 0 || row > rowCountAll || column > columnCountAll ) && lastColumnAll >= 0 && lastRowAll >= 0)
		{
			hoveredTileAll[lastRowAll][lastColumnAll] = false;
			return;
		}
		else if(row < 0 || column < 0 || row > rowCountAll || column > columnCountAll) return;
		if(pageOffset * (rowCountAll * columnCountAll) + row * columnCountAll + column >= allTiles.length) 
		{
			hoveredTileAll[lastRowAll][lastColumnAll] = false;
			return;
		}
		hoveredTileAll[row][column] = true;
		if((column != lastColumnAll || row != lastRowAll) && lastColumnAll >= 0 && lastRowAll >= 0) hoveredTileAll[lastRowAll][lastColumnAll] = false;
		lastRowAll = row;
		lastColumnAll = column;
	}
	
	private void checkTrianglesClick(Point p)
	{
		if(leftTriangle.contains(p) && pageOffset != 0) 
		{
			pageOffset--;
		}
		else if(rightTriangle.contains(p))
		{
			if(pageOffset * (rowCountAll * columnCountAll) + (rowCountAll * columnCountAll)>= allTiles.length) return;
			pageOffset++;
		}
	}
	
	private void checkAllTilesClick(Point p)
	{
		int column = (int)Math.floor((p.getX() - startXAllTiles) / tileSize);
		int row = (int)Math.floor((p.getY() - startYAllTiles) / tileSize);
		if(row < 0 || column < 0 || row >= rowCountAll || column >= columnCountAll || pageOffset * (rowCountAll * columnCountAll) + row * columnCountAll + column >= allTiles.length) return; 
		selectedTile = (BufferedImage)allTiles[pageOffset * (rowCountAll * columnCountAll) + row * columnCountAll + column].getImage();
	}
	
	private void handleKeyBoardInput(Event e)
	{
		DifferentEvents code = e.getCode();
		if(code == DifferentEvents.SELECTALL)
		{
			allSelected = true;
		}
	}
	
	@Override
	public <T extends Layer> void transitionTo(Class<T> layerClass, int layerPosition) {
		
	}
	
	private void setAllTo(BufferedImage img)
	{
		
		for(int i = 0; i < rowCount; i++)
		{
			for(int j = 0; j < columnCount; j++)
			{
				mapTiles[i][j].setImage(img);
			}
		}
	}
	
	private void drawCurrentSelected(Graphics2D draw)
	{
		draw.setColor(optionChosenColorAll);
		draw.setFont(optionFont);
		draw.drawString(currentTile, selectedX, selectedY - tileSize / 2 + 10);
		draw.drawImage(u.scaleImage(selectedTile, selectedTileSize, selectedTileSize), selectedX, selectedY, null);
		draw.drawRect(selectedX, selectedY, selectedTileSize, selectedTileSize);
	}
	
	private void drawMap(Graphics2D draw, boolean hovered)
	{
		int col = 0, row = 0;
		while(row < rowCount)
		{
			if(hovered && hoveredTile[row][col])
			{
				int screenX = tileSize * col;
				int screenY = tileSize * row;
				Stroke oldStroke = draw.getStroke();
				draw.setColor(optionChosenColor);
				draw.setStroke(rectStroke);
				draw.drawRect(screenX, screenY, tileSize, tileSize);
				draw.setStroke(oldStroke);
			}
			else if(!hovered)
			{
				int screenX = tileSize * col;
				int screenY = tileSize * row;
				draw.drawImage(mapTiles[row][col].getImage(), screenX, screenY, null);
				draw.setColor(optionChosenColorAll);
				draw.drawRect(screenX, screenY, tileSize, tileSize);
			}

			col++;
			if(col == columnCount)
			{
				col = 0;
				row++;
			}
		}
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
				if(pageOffset * (rowCountAll * columnCountAll) + row * columnCountAll + col < allTiles.length)
				{
					draw.drawImage(allTiles[pageOffset * (rowCountAll * columnCountAll) + row * columnCountAll + col].getImage(), screenX, screenY, null);
				}
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
