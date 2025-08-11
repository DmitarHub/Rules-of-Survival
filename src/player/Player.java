package player;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import game.Game;
import listeners.KeyInputs;

public class Player extends Entity{


	private KeyInputs keyInputs;

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

	private boolean swordStrike = false;
	private final int swordStrikeDuration = 15;
	private int swordStrikeCounter = 0;
	private final int initialStrike = 3;
	private final int attackWidth = 40;
	private final int attackHeight = 40;

	public Player(Game game, KeyInputs keyInputs, int x, int y, int speed, EntityType entityType, int healthPoints)
	{
		super(game, x, y, speed, entityType, healthPoints);
		this.keyInputs = keyInputs;
		this.frozenImages = new Images(game, EntityType.FROZEN);
		this.strikingImages = new Images(game, EntityType.STRIKING);
	}



	@Override
	public void update()
	{
		updateInvincible();
		updateDisarming();
		updateDisarmed();
		updateTeleporting();
		updateFreezingAndFrozen();

		if(!frozen)
		{
			updateMovement();
		}

	}

	public void setup()
	{
		healthPoints = maxHealthPoints;
		freezing = false;
		freezeCounter = 0;
		frozen = false;
		teleporting = false;
		teleportCounter = 0;
		disarmed = false;
		disarmedCounter = 0;
		disarming = false;
		swordStrike = false;
		swordStrikeCounter = 0;
		invincible = true;
		invincibleCounter = 0;
	}

	private void updateInvincible()
	{
		if(invincible)
		{
			invincibleCounter++;
			if(invincibleCounter >= invincibleTimer)
			{
				invincibleCounter = 0;
				invincible = false;
			}
		}
	}

	private void updateDisarming()
	{
	    if (disarming) {
	        disarmedCounter++;
	        if (disarmedCounter >= disarmedDuration)
	        {
	            disarming = false;
	            disarmedCounter = 0;
	        }
	    }
	}

	private void updateDisarmed()
	{
	    if (disarmed && !disarming) {
	        disarmedCounter++;
	        if (disarmedCounter >= disarmedTimer)
	        {
	            disarming = true;

	            disarmedCounter = 0;
	        }
	    }
	}

	private void updateTeleporting()
	{
	    if (teleporting) {
	        teleportCounter++;
	        if (teleportCounter >= teleportTimer)
	        {
	            randomTeleport();
	            teleportCounter = 0;
	        }
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
	    } else if (freezing) {
	        freezeCounter++;
	        if (freezeCounter >= freezeTimer)
	        {
	            frozen = true;
	            freezeCounter = 0;
	        }
	    }
	}

	private void updateMovement()
	{
		if(!swordStrike)
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
		        if (keyInputs.isDown()) setDirection(Direction.DOWN);
		        if (keyInputs.isUp()) setDirection(Direction.UP);
		        if (keyInputs.isLeft()) setDirection(Direction.LEFT);
		        if (keyInputs.isRight()) setDirection(Direction.RIGHT);

		        collisionCheck();
		        move();
		        animationCounter();
		    }
		}
		else {
			swordStrike();
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

			game.checkCollisionEnemy();
		}
		else if(swordStrikeCounter > swordStrikeDuration)
		{
			swordStrike = false;
			swordStrikeCounter = 0;
		}
	}

	public void drawFrozen(Graphics2D graphics2D)
	{
		BufferedImage image = frozenImages.getSprite(direction, spriteNumber - 1);
		graphics2D.drawImage(image, x, y, null);

	}

	public void drawStrike(Graphics2D graphics2D)
	{
		BufferedImage image = strikingImages.getSprite(direction, spriteNumber - 1);
		int tempX = x;
		int tempY = y;
		switch(direction)
		{
			case UP: tempY -= game.getTileSize(); break;
			case LEFT: tempX -= game.getTileSize(); break;
			default: break;
		}
		graphics2D.drawImage(image, tempX, tempY, null);

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
			game.gameOver();
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
		keyInputs.reverse(reverse);
	}

	public void setRandomTeleport(boolean teleport)
	{
		teleporting = teleport;
	}


	public void randomTeleport()
	{
		this.x = game.getRandom().nextInt(game.getRowNumber());
		this.y = game.getRandom().nextInt(game.getColumnNumber());

		while(game.getCollisionMap()[y][x])
		{
			x = game.getRandom().nextInt(game.getRowNumber());
			y = game.getRandom().nextInt(game.getColumnNumber());

		}

		x *= game.getTileSize();
		y *= game.getTileSize();
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

	public KeyInputs getKeyInputs()
	{
		return keyInputs;
	}
}
