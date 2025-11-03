package tiles;

import java.io.InputStream;
import java.util.Random;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import layers.ingame.InGameLayer;

public class TileManager {

	private Tile[] tiles;
	private final int numOfTiles = TileFileName.values().length;
	private int[][] map;
	private InGameLayer gameLayer;
	private final int numberOfMaps = 5;
	
	private final int mapColumnNumber;
	private final int mapRowNumber;
	
	private final int tileSize;

	public TileManager(InGameLayer game)
	{
		this.gameLayer = game;
		mapColumnNumber = gameLayer.getMapColumnNumber();
		mapRowNumber = gameLayer.getMapRowNumber();
		map = new int[mapRowNumber][mapColumnNumber];
		tileSize = gameLayer.getTileSize();
		tiles = new Tile[numOfTiles];
		loadTiles();
		Random rand = new Random();
		int mapNum = rand.nextInt(numberOfMaps);
		mapNum++;
		loadMap("/maps/mapa" + mapNum + ".txt");
	}
	

	private void loadTiles()
	{
		ScaleTool u = new ScaleTool();
        try {
            TileFileName[] names = TileFileName.values();
            for (int i = 0; i < names.length; i++) {
                tiles[i] = new Tile();
                String filename = "/tilesV2/" + names[i].name() + ".png";
                BufferedImage original = ImageIO.read(getClass().getResourceAsStream(filename));

                tiles[i].setImage(original);
                tiles[i].setImage(u.scaleImage(original, tileSize, tileSize));
                tiles[i].setName(names[i].name());
                if(names[i].name().contains("Water") || names[i].name().contains("Tree") ||
                   names[i].name().contains("Bush")) tiles[i].setCollision(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private void loadMap(String fileName)
	{
		try
		{
			InputStream inputStream = getClass().getResourceAsStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


			for(int i = 0; i < mapRowNumber; i++)
			{
				String line = bufferedReader.readLine();
				String row[] = line.split(" ");
				for(int j = 0; j < mapColumnNumber; j++)
				{
					int num = Integer.parseInt(row[j]);

					map[i][j] = num;
				}
			}
			bufferedReader.close();

		} catch (Exception e) {
			System.out.println("Didn't find the file!");
		}
	}

	public void draw(Graphics2D g)
	{
		int col = 0, row = 0;
		while(row < mapRowNumber)
		{
			int screenX = tileSize * col;
			int screenY = tileSize * row;
			int tileNumber = map[row][col];

			if (tileNumber >= 0 && tileNumber < tiles.length && tiles[tileNumber] != null) {
			    g.drawImage(tiles[tileNumber].getImage(), screenX, screenY, null);
			}


			col++;
			if(col == mapColumnNumber)
			{
				col = 0;
				row++;
			}
		}

	}

	public int[][] getMap() {
		return map;
	}

	public void setMap(int[][] map) {
		this.map = map;
	}

	public Tile[] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[] tiles) {
		this.tiles = tiles;
	}




}
