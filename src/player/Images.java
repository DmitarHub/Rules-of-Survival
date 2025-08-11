package player;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import tiles.Utool;

public class Images {

	private BufferedImage[][] sprites;
	private Game game;
	private static BufferedImage fullHeart;
	private static BufferedImage emptyHeart;

	public Images(Game game, EntityType entityType)
	{
		this.game = game;
		sprites = new BufferedImage[Direction.values().length][2];
		loadImages(entityType);
	}

	private void loadImages(EntityType entityType)
	{
		Utool u = new Utool();
		try {

			for(Direction dir : Direction.values())
			{

				for(int i = 0; i < 2; i++)
				{
					int Width = game.getTileSize();
					int Height = game.getTileSize();
					String filePath = "";
					switch(entityType)
					{
					case PLAYER:
						filePath = String.format("/player/Player_%s_%d.png", dir.name(), i + 1 );
						break;
					case ENEMY:
						filePath = String.format("/player/Enemy_%s_%d.png", dir.name(), i + 1 );
						break;
					case FROZEN:
						filePath = String.format("/player/Frozen_%s_%d.png", dir.name(), i + 1 );
						break;
					case STRIKING:
						filePath = String.format("/player/Striking_%s_%d.png", dir.name(), i + 1 );
						switch(dir)
						{
						case UP -> Height += Height;
						case DOWN -> Height += Height;
						case LEFT -> Width += Width;
						case RIGHT -> Width += Width;
						}
						break;
					}

					BufferedImage image = ImageIO.read(getClass().getResourceAsStream(filePath));
					sprites[dir.ordinal()][i] = u.scaleImage(image, Width, Height);
				}
			}
			emptyHeart = ImageIO.read(getClass().getResourceAsStream("/ui/emptyHeart.png"));
			fullHeart = ImageIO.read(getClass().getResourceAsStream("/ui/fullHeart.png"));

		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getSprite(Direction dir, int index)
	{
		return sprites[dir.ordinal()][index];
	}

	public BufferedImage getFullHeart()
	{
		return fullHeart;
	}

	public BufferedImage getEmptyHeart()
	{
		return emptyHeart;
	}



}
