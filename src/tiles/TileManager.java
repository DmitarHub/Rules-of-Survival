package tiles;

import java.io.InputStream;
import java.util.Random;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import game.Game;

public class TileManager {

	private Tile[] tiles;
	private final int numOfTiles = TileFileName.values().length;
	private int[][] map;
	private Game game;
	private final int numberOfMaps = 3;

	public TileManager(Game game)
	{
		this.game = game;
		setMap(new int[game.getColumnNumber()][game.getRowNumber()]);
		tiles = new Tile[numOfTiles];
		loadTiles();
		Random rand = new Random();
		int map = rand.nextInt(numberOfMaps);
		map++;
		loadMap("/maps/mapa" + map + ".txt");
	}

	private void loadTiles()
	{
		Utool u = new Utool();
        try {
            TileFileName[] names = TileFileName.values();
            for (int i = 0; i < names.length; i++) {
                tiles[i] = new Tile();
                String filename = "/tilesV2/" + names[i].name() + ".png";
                BufferedImage original = ImageIO.read(getClass().getResourceAsStream(filename));

                tiles[i].setImage(original);
                tiles[i].setImage(u.scaleImage(original, game.getTileSize(), game.getTileSize()));
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


			for(int i = 0; i < game.getColumnNumber(); i++)
			{
				String line = bufferedReader.readLine();
				String row[] = line.split(" ");
				for(int j = 0; j < game.getRowNumber(); j++)
				{
					int num = Integer.parseInt(row[j]);

					getMap()[i][j] = num;

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
		while(row < game.getRowNumber())
		{
			int screenX = game.getTileSize() * col;
			int screenY = game.getTileSize() * row;
			int tileNumber = getMap()[row][col];

			if (tileNumber >= 0 && tileNumber < tiles.length && tiles[tileNumber] != null) {
			    g.drawImage(tiles[tileNumber].getImage(), screenX, screenY, null);
			}


			col++;
			if(col == game.getColumnNumber())
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
