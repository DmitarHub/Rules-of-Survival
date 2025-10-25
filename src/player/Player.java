package player;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import game.Game;
import layers.ingame.InGameLayer;
import listeners.Event;
import listeners.KeyInputs;

public class Player extends Entity{

	private boolean freezing;
	private final int freezeTimer = 420;
	private final int freezeDuration = 60;
	private int freezeCounter = 0;
	private boolean frozen;
	private Images frozenImages;
	private Images strikingImages;

	private boolean teleporting;
	private final int teleportTimer = 300;
	private int teleportCounter = 0;

	private boolean disarmed;
	private final int disarmedTimer = 240;
	private final int disarmedDuration = 45;
	private int disarmedCounter = 0;
	private boolean disarming;
	
	private boolean inputsReversed = false;

	private boolean swordStrike = false;
	private final int swordStrikeDuration = 15;
	private int swordStrikeCounter = 0;
	private final int initialStrike = 3;
	private final int attackWidth = 40;
	private final int attackHeight = 40;
	
	private boolean playerOne = false;

	private KeyInputs keyInputs;
	
	private Rectangle bounds = new Rectangle();

	public Player(InGameLayer game,int x, int y, int speed, EntityType entityType, int healthPoints, boolean whichPlayer, KeyInputs keyinputs)
	{
		super(game, x, y, speed, entityType, healthPoints);
		this.keyInputs = keyinputs;
		this.playerOne = whichPlayer;
		this.frozenImages = new Images(game, EntityType.FROZEN);
		this.strikingImages = new Images(game, EntityType.STRIKING);
	}



	@Override
	public void update()
	{
		
		if(invincible) updateInvincible();
		if(disarming) updateDisarming();
		if(disarmed && !disarming) updateDisarmed();
		if(teleporting) updateTeleporting();
		if(frozen || freezing)updateFreezingAndFrozen();
		
		if(!frozen)
		{
			updateMovement();
		}
	}
	
	private void updateMovement()
	{
		if(!swordStrike)
		{
			if(playerOne)
			{
				if (keyInputs != null && (keyInputs.isS() ||
			    		keyInputs.isD() || keyInputs.isA() || keyInputs.isW() || keyInputs.isSpace()))
			    {

			    	if (keyInputs.isSpace() && !frozen && !disarming )
			    	{
			    		swordStrike = true;
			    		swordStrike();
			    		return;
			    	}
			    	if(inputsReversed)
			    	{
				        if (keyInputs.isW()) setDirection(Direction.DOWN);
				        if (keyInputs.isS()) setDirection(Direction.UP);
				        if (keyInputs.isD()) setDirection(Direction.LEFT);
				        if (keyInputs.isA()) setDirection(Direction.RIGHT);
			    	}
			    	else
			    	{
				        if (keyInputs.isS()) setDirection(Direction.DOWN);
				        if (keyInputs.isW()) setDirection(Direction.UP);
				        if (keyInputs.isA()) setDirection(Direction.LEFT);
				        if (keyInputs.isD()) setDirection(Direction.RIGHT);
			    	}

			        collisionCheck();
			        move();
			        animationCounter();
			    }
			}
			else
			{
				if (keyInputs != null && (keyInputs.isDown() ||
			    		keyInputs.isUp() || keyInputs.isLeft() || keyInputs.isRight() || keyInputs.isEnter()))
			    {

			    	if (keyInputs.isEnter() && !frozen && !disarming )
			    	{
			    		swordStrike = true;
			    		swordStrike();
			    		return;
			    	}
			    	if(inputsReversed)
			    	{
				        if (keyInputs.isUp()) setDirection(Direction.DOWN);
				        if (keyInputs.isDown()) setDirection(Direction.UP);
				        if (keyInputs.isRight()) setDirection(Direction.LEFT);
				        if (keyInputs.isLeft()) setDirection(Direction.RIGHT);
			    	}
			    	else
			    	{
				        if (keyInputs.isDown()) setDirection(Direction.DOWN);
				        if (keyInputs.isUp()) setDirection(Direction.UP);
				        if (keyInputs.isLeft()) setDirection(Direction.LEFT);
				        if (keyInputs.isRight()) setDirection(Direction.RIGHT);
			    	}

			        collisionCheck();
			        move();
			        animationCounter();
			    }
			}

		}
		else {
			swordStrike();
		}

	}

	
	@Override
	public void draw(Graphics2D drawing)
	{
		if(frozen)
		{
			BufferedImage image = frozenImages.getSprite(direction, spriteNumber - 1);
			drawing.drawImage(image, x, y, null);
		}
		else if(swordStrike)
		{
			BufferedImage image = strikingImages.getSprite(direction, spriteNumber - 1);
			int tempX = x;
			int tempY = y;
			switch(direction)
			{
				case UP: tempY -= gameLayer.getTileSize(); break;
				case LEFT: tempX -= gameLayer.getTileSize(); break;
				default: break;
			}
			drawing.drawImage(image, tempX, tempY, null);
		}
		else
		{
			BufferedImage image = images.getSprite(direction, spriteNumber - 1);
			drawing.drawImage(image, x, y, null);
		}
		int startingX = x + (gameLayer.getTileSize() / 2) - ((maxHealthPoints * heartSpacing) / 2);
		int startingY = y - yMargin;
		for(int i = 0; i < maxHealthPoints; i++)
		{
			if(i < healthPoints)
			{
				drawing.drawImage(images.getFullHeart(), startingX + i * heartSpacing, startingY, null);
			}
			else {
				drawing.drawImage(images.getEmptyHeart(), startingX + i * heartSpacing, startingY, null);
			}
		}
		String name = "";
		if(playerOne) name = "Player1";
		else name = "Player2";
		drawing.drawString(name, startingX + gameLayer.getTileSize()/2, startingY - yMargin/2);
	}


	private void updateInvincible()
	{
		invincibleCounter++;
		if(invincibleCounter >= invincibleTimer)
		{
			invincibleCounter = 0;
			invincible = false;
		}
	}

	private void updateDisarming()
	{
        disarmedCounter++;
        if (disarmedCounter >= disarmedDuration)
        {
            disarming = false;
            disarmedCounter = 0;
        }
	}

	private void updateDisarmed()
	{
        disarmedCounter++;
        if (disarmedCounter >= disarmedTimer)
        {
            disarming = true;

            disarmedCounter = 0;
        }
	}

	private void updateTeleporting()
	{
        teleportCounter++;
        if (teleportCounter >= teleportTimer)
        {
            randomTeleport();
            teleportCounter = 0;
        }
	}

	private void updateFreezingAndFrozen()
	{
	    if (frozen) {
	        freezeCounter++;
	        if (freezeCounter >= freezeDuration)
	        {
	            frozen = false;
	            freezeCounter = 0;
	        }
	    } else {
	        freezeCounter++;
	        if (freezeCounter >= freezeTimer)
	        {
	            frozen = true;
	            freezeCounter = 0;
	        }
	    }
	}
	

	public void swordStrike()
	{
		swordStrikeCounter++;
		if(swordStrikeCounter <= initialStrike)
		{
			spriteNumber = 1;
		}
		else if(swordStrikeCounter >initialStrike && swordStrikeCounter < swordStrikeDuration)
		{
			spriteNumber = 2;

			gameLayer.checkCollisionEnemy(this);
		}
		else if(swordStrikeCounter > swordStrikeDuration)
		{
			swordStrike = false;
			swordStrikeCounter = 0;
		}
	}




	public void setHP(int hp)
	{
		healthPoints = hp;
	}

	public void loseHP(int amount)
	{
		if(invincible) return;
		healthPoints -= amount;
		if(healthPoints <= 0)
		{
			gameLayer.playerDead(this);
		}
		else {
			invincible = true;
		}
	}

	public boolean isStriking()
	{
		return swordStrike;
	}

	public void reverseInputs(boolean reverse)
	{
		inputsReversed = reverse;
	}

	public void setRandomTeleport(boolean teleport)
	{
		teleporting = teleport;
	}


	public void randomTeleport()
	{
		this.x = gameLayer.getRandom().nextInt(gameLayer.getRowNumber());
		this.y = gameLayer.getRandom().nextInt(gameLayer.getColumnNumber());

		while(gameLayer.getCollisionMap()[y][x])
		{
			x = gameLayer.getRandom().nextInt(gameLayer.getRowNumber());
			y = gameLayer.getRandom().nextInt(gameLayer.getColumnNumber());

		}

		x *= gameLayer.getTileSize();
		y *= gameLayer.getTileSize();
	}

	public void setFreeze(boolean freeze)
	{
		freezing = freeze;
	}

	public boolean isTeleporting() {
		return teleporting;
	}


	public boolean isFreezing() {
		return freezing;
	}


	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public void setDisarmed(boolean disarmed)
	{
		this.disarmed = disarmed;
	}

	public boolean isInvincible()
	{
		return invincible;
	}

	public int getAttackHeight() {
		return attackHeight;
	}



	public int getAttackWidth() {
		return attackWidth;
	}

	public Rectangle getBounds()
	{
		bounds.setBounds(x + hitbox.getOffsetX(), y + hitbox.getOffsetY(), hitbox.getHitboxWidth(), hitbox.getHitboxHeight());
		return bounds;
	}
}
