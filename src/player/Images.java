package player;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import layers.ingame.InGameLayer;
import tiles.ScaleTool;

public class Images {

	private BufferedImage[][] sprites;
	private InGameLayer gameLayer;
	private static BufferedImage fullHeart;
	private static BufferedImage emptyHeart;

	public Images(InGameLayer game, EntityType entityType)
	{
		this.gameLayer = game;
		sprites = new BufferedImage[Direction.values().length][2];
		loadImages(entityType);
	}

	private void loadImages(EntityType entityType)
	{
		ScaleTool u = new ScaleTool();
		try {

			if(entityType == EntityType.PIRATE)
			{
				int Width = gameLayer.getTileSize();
				int Height = gameLayer.getTileSize();
				for(int i = 1; i < 9; i++)
				{
					BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/player/Pirate_" + i + ".png"));
					sprites[(i - 1)/2][((i - 1) % 2)] = u.scaleImage(image, Width, Height);
					// sprites 0 0 = Pirate 1
					// sprites 0 1 = Pirate 2 itd 1,2,3,4,5,6,7
				}
			}
			else if(entityType == EntityType.PLANTTHING)
			{
				int Width = gameLayer.getTileSize();
				int Height = gameLayer.getTileSize();
				for(int i = 0; i < 8; i++)
				{
					BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/player/PlantThing.png"));
					sprites[i / 2][(i % 2)] = u.scaleImage(image, Width, Height);
				}
			}
			else
			{
				for(Direction dir : Direction.values())
				{

					for(int i = 0; i < 2; i++)
					{

						int Width = gameLayer.getTileSize();
						int Height = gameLayer.getTileSize();
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
