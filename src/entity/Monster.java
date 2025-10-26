package entity;

import java.util.List;
import java.util.Random;

import layers.ingame.InGameLayer;

public class Monster extends Enemy {
	
	private Player chased;
	private boolean chasingPlayer = false;
	private PathFinder pathFinder;

	private final int randomMoveDelay = 60;
	private int randomMoveCounter = 0;

	public Monster(InGameLayer game, int x, int y, int speed, EntityType entityType, int healthPoints,
			List<Player> players) {
		super(game, x, y, speed, entityType, healthPoints, players);
		this.pathFinder = new PathFinder(game);
	}
	
	@Override
	public void update()
	{
		updateSpawning();
		updateInvincible();
		if(chasingPlayer)
		{
			int currentRow = y / gameLayer.getTileSize();
	        int currentCol = x / gameLayer.getTileSize();

	        int playerRow = chased.getY() / gameLayer.getTileSize();
	        int playerCol = chased.getX() / gameLayer.getTileSize();

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
			int minDistance = Integer.MAX_VALUE;
			for(Player p : players)
			{
				double distanceToPlayer = getDistanceToPlayer(p);
				if(distanceToPlayer <= gameLayer.getTileSize() * 2 && minDistance > (int)distanceToPlayer) 
				{
					chasingPlayer = true;
					chased = p;
					minDistance = (int)distanceToPlayer;
				}
			}

		}
		animationCounter();
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
