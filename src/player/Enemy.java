package player;

import java.util.List;
import java.util.Random;

import game.Game;
import tiles.PathFinder;

public class Enemy extends Entity{

	private Player player;
	private boolean chasingPlayer = false;
	private PathFinder pathFinder;

	private final int randomMoveDelay = 60;
	private int randomMoveCounter = 0;

	private boolean spawning;
	private final int spawnDuration = 60;
	private int spawnCounter = 0;

	private boolean dead = false;

	public Enemy(Game game, int x, int y, int speed, EntityType entityType, int healthPoints, Player player)
	{
		super(game, x, y, speed, entityType, healthPoints);
		this.player = player;
		this.pathFinder = new PathFinder(this.game);
		this.spawning = true;
	}


	@Override
	public void update()
	{

		updateSpawning();
		updateInvincible();
		if(chasingPlayer)
		{
			int currentRow = y / game.getTileSize();
	        int currentCol = x / game.getTileSize();

	        int playerRow = player.getY() / game.getTileSize();
	        int playerCol = player.getX() / game.getTileSize();

	        List<int[]> path = pathFinder.findPath(currentRow, currentCol, playerRow, playerCol);

	        if (path.size() > 1) {
	            int[] nextStep = path.get(path.size() - 2);

	            int nextRow = nextStep[0];
	            int nextCol = nextStep[1];

	            if (nextRow < currentRow) setDirection(Direction.UP);
	            else if (nextRow > currentRow) setDirection(Direction.DOWN);
	            else if (nextCol < currentCol) setDirection(Direction.LEFT);
	            else if (nextCol > currentCol) setDirection(Direction.RIGHT);

	            collisionCheck();

	            move();
	        } else {
	        	chasingPlayer = false;
	        }
		}
		else {
			randomMove();
			double distanceToPlayer = getDistanceToPlayer();
			if(distanceToPlayer <= game.getTileSize() * 2) chasingPlayer = true;

		}

		animationCounter();
	}

	private void updateSpawning()
	{
		if(spawning)
		{
			spawnCounter++;
			if(spawnCounter >= spawnDuration)
			{

				spawnCounter = 0;
				spawning = false;
			}
		}
	}

	public boolean getSpawning()
	{
		return spawning;
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

	private double getDistanceToPlayer()
	{
		int dx = player.getX() - x;
		int dy = player.getY() - y;
		return Math.sqrt(dx * dx + dy * dy);
	}


	private void randomMove()
	{
		if(randomMoveCounter <= 0)
		{
			pickRandomDirection();
			randomMoveCounter = randomMoveDelay;
		}
		else {
			randomMoveCounter--;
		}

		collisionCheck();

		move();

		if(isCollisionOn())
		{
			pickRandomDirection();
			randomMoveCounter = randomMoveDelay;
		}
	}

	public void takeDamage(int amount)
	{
		if(invincible) return;
		healthPoints -= amount;
		if(healthPoints <= 0)
		{
			this.setDead(true);
		}
		else {
			invincible = true;
		}
	}

	public void setDead(boolean dead)
	{
		this.dead = dead;
	}

	public boolean isDead()
	{
		return dead;
	}

	private void pickRandomDirection()
	{
		int i = new Random().nextInt(4);
		switch(i)
		{
		case 0 -> setDirection(Direction.UP);
		case 1 -> setDirection(Direction.DOWN);
		case 2 -> setDirection(Direction.LEFT);
		case 3 -> setDirection(Direction.RIGHT);
		}
	}


	public void reverseDirection() {
		switch(direction)
		{
		case UP:
			direction = Direction.DOWN;
			break;
		case DOWN:
			direction = Direction.UP;
			break;
		case RIGHT:
			direction = Direction.LEFT;
			break;
		case LEFT:
			direction = Direction.RIGHT;
			break;

		}
	}


	public void handleEnemeyCollision(Entity e2) {

	    double overlapX = (x - e2.getX());
	    double overlapY = (y - e2.getY());

	    if (Math.abs(overlapX) > Math.abs(overlapY)) {
	        if (overlapX > 0) {
	            x += 2;
	            e2.setX(e2.getX() - 2);
	        } else {
	            x -=2;
	            e2.setX(e2.getX() + 2);
	        }
	    } else {
	        if (overlapY > 0) {
	            y += 2;
	            e2.setY(e2.getY() - 2);
	        } else {
	            y -= 2;
	            e2.setY(e2.getY() + 2);
	        }
	    }


	    this.changeToRandomDirection();
	    ((Enemy)e2).changeToRandomDirection();
	}

	public void changeToRandomDirection() {
	    Direction old = this.direction;
	    Direction[] values = Direction.values();
	    Direction newDir;
	    do {
	        newDir = values[(int)(Math.random() * values.length)];
	    } while (newDir == old);

	    this.direction = newDir;
	}

}
