package layers.mapmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

import game.Game;
import layers.BottomLayer;
import layers.Layer;
import layers.mainmenu.MainMenuLayer;
import listeners.DifferentEvents;
import listeners.Event;
import listeners.KeyInputs;
import tiles.ScaleTool;
import tiles.Tile;
import tiles.TileFileName;

public class MapMakerLayer extends BottomLayer {
	
	private Font optionFont = new Font("SansSerif", Font.PLAIN, 28);
	private Font msgFont = new Font("SansSerif", Font.BOLD, 50);
	
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
	
	private String emptyTileName = "EmptyTile";
	private BufferedImage emptyTile;
	private BufferedImage selectedTile;
	private String selectedName;
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
	
	private Rectangle saveButton = new Rectangle(tileSize/2, rowCount * tileSize + tileSize/2, tileSize * 2, tileSize);
	private Rectangle returnButton = new Rectangle(tileSize/2, (rowCount + 2) * tileSize + tileSize/2, tileSize * 2 , tileSize);
	
	private boolean hoveredSaveButton = false;
	private boolean hoveredReturnButton = false;

	private int newMapIndex = 1;
	
	private Queue<String> msgQueue = new LinkedList<>();
	private boolean destroyLastMessage = false;
	private int msgCounter = 0;
	private final int msgTimer = 60;
	
	public MapMakerLayer()
	{
		ki = Game.Get().getKeyInputs();
		loadEmptyTiles();
		loadTiles();
		File folder = new File("res/maps");
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
        	newMapIndex++;
        }
	}
	
	@Override
	public void onUpdate()
	{
		if(isPaused) return;
		if(destroyLastMessage && msgQueue.size() > 1)
		{
			msgQueue.poll();
			destroyLastMessage = false;
			msgCounter = 0;
		}
		if(!msgQueue.isEmpty() && ++msgCounter >= msgTimer) 
		{
			msgQueue.poll();
			msgCounter = 0;
		}
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
		drawButton(draw, saveButton);
		drawButton(draw, returnButton);
		if(!msgQueue.isEmpty()) drawFirstMsg(draw);
	}
	
	private void drawFirstMsg(Graphics2D draw)
	{
		Color old = draw.getColor();
		draw.setColor(Color.red);
		draw.setFont(msgFont);
		draw.drawString(msgQueue.peek(), centerString(draw, msgQueue.peek()), height/2);
		draw.setColor(old);
	}
	
	private void drawButton(Graphics2D draw, Rectangle button)
	{
		draw.drawRect(button.x, button.y, button.width, button.height);
		draw.fillRect(button.x, button.y, button.width, button.height);
		if((button == saveButton && hoveredSaveButton) || button == returnButton && hoveredReturnButton)
		{
			Stroke oldStroke = draw.getStroke();
			Color oldColor = draw.getColor();
			draw.setColor(optionChosenColor);
			draw.setStroke(rectStroke);
			draw.drawRect(button.x, button.y, button.width, button.height);
			draw.setStroke(oldStroke);
			draw.setColor(oldColor);
		}
		
	    draw.setFont(optionFont);
	    Color old = draw.getColor();
	    draw.setColor(backgroundColor);

	    String text = (button == saveButton) ? "Save" : "Return";

	    FontMetrics fm = draw.getFontMetrics();
	    int textWidth = fm.stringWidth(text);
	    int textHeight = fm.getAscent();

	    int textX = button.x + (button.width - textWidth) / 2;
	    int textY = button.y + (button.height + textHeight) / 2 - 3; 

	    draw.drawString(text, textX, textY);
	    draw.setColor(old);
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
		if(pageOffset * (rowCountAll * columnCountAll) + (rowCountAll * columnCountAll) >= allTiles.length)
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
					setAllTo(selectedTile, selectedName);
					allSelected = false;
					return;
				}
				mapTiles[row][column].setImage(selectedTile);
				mapTiles[row][column].setName(selectedName);
				shiftX = (int) mousePoint.getX();
				shiftY = (int) mousePoint.getY();
				leftClickOn = true;
			}
			else if(code == DifferentEvents.RIGHTCLICKPRESS) 
			{
				if(allSelected)
				{
					setAllTo(emptyTile, emptyTileName);
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
				if(leftClickOn) 
				{
					mapTiles[row][column].setImage(selectedTile);
					mapTiles[row][column].setName(selectedName);
				}
				else if(rightClickOn) 
				{
					mapTiles[row][column].setImage(emptyTile);
					mapTiles[row][column].setName(emptyTileName);
				}
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
				checkHoverButtons(mousePoint);
			}
			else if(code == DifferentEvents.LEFTCLICKPRESS)
			{
				checkTrianglesClick(mousePoint);
				checkAllTilesClick(mousePoint);
				checkButtonsClick(mousePoint);
			}
		}
	}
	
	private void checkHoverButtons(Point p)
	{
		if(saveButton.contains(p))
		{
			hoveredSaveButton = true;
		}
		else if(returnButton.contains(p))
		{
			hoveredReturnButton = true;
		}
		else
		{
			hoveredSaveButton = false;
			hoveredReturnButton = false;
		}
	}
	
	private void checkButtonsClick(Point p)
	{
		if(saveButton.contains(p))
		{
			addToMaps();
		}
		else if(returnButton.contains(p))
		{
			transitionTo(MainMenuLayer.class, 0);
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
			if(pageOffset * (rowCountAll * columnCountAll) + (rowCountAll * columnCountAll) >= allTiles.length) return;
			pageOffset++;
		}
	}
	
	private void checkAllTilesClick(Point p)
	{
		int column = (int)Math.floor((p.getX() - startXAllTiles) / tileSize);
		int row = (int)Math.floor((p.getY() - startYAllTiles) / tileSize);
		if(row < 0 || column < 0 || row >= rowCountAll || column >= columnCountAll || pageOffset * (rowCountAll * columnCountAll) + row * columnCountAll + column >= allTiles.length) return; 
		int index = pageOffset * (rowCountAll * columnCountAll) + row * columnCountAll + column;
		selectedTile = (BufferedImage)allTiles[index].getImage();
		selectedName = allTiles[index].getName();
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
		Game.Get().queueTransition(layerClass, layerPosition);
	}
	
	private void setAllTo(BufferedImage img, String name)
	{
		
		for(int i = 0; i < rowCount; i++)
		{
			for(int j = 0; j < columnCount; j++)
			{
				mapTiles[i][j].setImage(img);
				mapTiles[i][j].setName(name);
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
            		mapTiles[i][j].setName("EmptyTile");
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
        selectedName = allTiles[0].getName();
	}
	
	private void addToMaps()
	{
		for(int i = 0; i < rowCount; i++)
		{
			for(int j = 0; j < columnCount; j++)
			{
				if (mapTiles[i][j].getName().equals(emptyTileName))
				{
					resetErrorMessage("You need to fill in all of the tiles!");
					return;
				}
			}
		}
		
		File folder = new File("res/maps");
		File output = new File(folder, "mapa" + newMapIndex++ + ".txt");
		StringBuilder content = new StringBuilder();
		try (PrintWriter pw = new PrintWriter(new FileWriter(output)))
		{
			for(int i = 0; i < rowCount; i++)
			{
				for(int j = 0; j < columnCount; j++)
				{
					int tileId = getTileId(mapTiles[i][j]);
					content.append(tileId);
					pw.print(tileId);
					if(j < columnCount - 1) 
					{
						pw.print(" ");
						content.append(" ");
					}
				}
				if(i < rowCount - 1) 
				{
					pw.println();
					content.append('\n');
				}
				content.append('\n');
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	private void resetErrorMessage(String msg)
	{
		destroyLastMessage = true;
		msgQueue.offer(msg);
	}
	
	private int getTileId(Tile t)
	{
	    for (int i = 0; i < allTiles.length; i++)
	        if (allTiles[i].getName().equals(t.getName()))
	            return i;

	    return -1;
	}
	
	
	private void togglePaused() { isPaused = !isPaused; }
}
