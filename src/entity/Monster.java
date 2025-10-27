package entity;

import java.util.ArrayList;
import java.util.List;

import layers.ingame.InGameLayer;

public class Monster extends Enemy {
	
	private Player chased;
	private boolean chasingPlayer = false;
	private PathFinder pathFinder;
	private final int randomMoveDelay = 150;
	private int randomMoveCounter = 0;
	
	private int pathRecalcDelay = 15;
	private int pathRecalcCounter = 0;
	
	private int pauseDelay = 60;
	private int pauseCounter = 0;
	private List<int[]> path = new ArrayList<>();
	
	private boolean paused = false;

	public Monster(InGameLayer game, int x, int y, int speed, EntityType entityType, int healthPoints,
			List<Player> players) {
		super(game, x, y, speed, entityType, healthPoints, players);
		this.pathFinder = new PathFinder(game, speed);
	}
	
	@Override
	public void update()
	{
		updateSpawning();
		updateInvincible();
		updatePaused();
		if(paused) return;
		if(chasingPlayer)
		{
	        if (getDistanceToPlayer(chased) > gameLayer.getTileSize() * 4) {
	            chasingPlayer = false;
	        } else {
	            followPathToPlayer();
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
	
	private void followPathToPlayer() {
	    if (pathRecalcCounter-- <= 0) {
	        path = pathFinder.findPath(y, x, chased.getY(), chased.getX());
	        pathRecalcCounter = pathRecalcDelay;
	    }
	    if (path.size() > 1) {
	        int[] next = path.get(path.size() - 2);

            int nextRow = next[0];
            int nextCol = next[1];

            if (nextRow < y) setDirection(Direction.UP);
            else if (nextRow > y) setDirection(Direction.DOWN);
            else if (nextCol < x) setDirection(Direction.LEFT);
            else if (nextCol > x) setDirection(Direction.RIGHT);

            collisionCheck();

            move();
	    }
	}
	
	private void randomMove()
	{
		if(randomMoveCounter <= 0)
		{
			changeToRandomDirection();
			randomMoveCounter = randomMoveDelay;
		}
		else {
			randomMoveCounter--;
		}

		collisionCheck();

		move();

		if(isCollisionOn())
		{
			changeToRandomDirection();
			randomMoveCounter = randomMoveDelay;
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
	
	public void pauseForABit()
	{
		paused = true;
	}
	
	private void updatePaused()
	{
		if(++pauseCounter > pauseDelay)
		{
			pauseCounter = 0;
			paused = false;
		}
	}

}
