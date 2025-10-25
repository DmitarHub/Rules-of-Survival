package player;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import layers.ingame.InGameLayer;

public class RocketMarker {
	
	private int x, y;
    private int life = 60;
    private BufferedImage fireSprite;
    private BufferedImage[] numbers = new BufferedImage[3];
    private int tileSize;
    private InGameLayer gameLayer;
    private int damage = 3;
	private int countdown = 180;
	private boolean countingDown = true;
	private Rectangle bounds = new Rectangle();
	private Pirate owner;

    public RocketMarker(int x, int y, int tileSize, InGameLayer gameLayer, Pirate owner) {
        this.x = x;
        this.y = y;
        this.owner = owner;
        this.tileSize = tileSize;
        this.gameLayer = gameLayer;
        bounds.setBounds(x, y, tileSize, tileSize);

        try {
			fireSprite = ImageIO.read(getClass().getResourceAsStream("/player/Pirate_9.png"));
			for(int i = 0; i < 3; i++)
			{
				numbers[i] = ImageIO.read(getClass().getResourceAsStream("/player/"+ (i + 1) + ".png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void update() {
    	if(countingDown)
    	{
    		if(--countdown <= 0)
    		{
    			countingDown = false;
    		}
    	}
    	else
    	{
            life--;
            if(life <= 0) 
            {
            	gameLayer.getToBeRemoved().add(this);
            }
    	}

    }

    public boolean isExpired() {
        return life <= 0;
    }

    public void draw(Graphics2D g) {
    	if(countingDown)
    	{
    		if(countdown > 120)  g.drawImage(numbers[2], x, y, tileSize, tileSize, null);
    		else if(countdown > 60)  g.drawImage(numbers[1], x, y, tileSize, tileSize, null);
    		else g.drawImage(numbers[0], x, y, tileSize, tileSize, null);
    	}
    	else
    	{
    		g.drawImage(fireSprite, x, y, tileSize, tileSize, null);
    	}
       
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getTileSize() { return tileSize; } 
    public int getDamage() { return damage; }
    public Rectangle getBounds() { return bounds; }
    public boolean isCountingDown() { return countingDown; }
    public Pirate getOwner() { return owner; }
}
