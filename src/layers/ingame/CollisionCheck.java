package layers.ingame;

import entity.AliveEntity;
import tiles.TileManager;

public class CollisionCheck {

	private InGameLayer gameLayer;
	
	private final int tileSize;
	private TileManager tm;
	
	private final int mapRowNumber;
	private final int mapColumnNumber;

	public CollisionCheck(InGameLayer game)
	{
		this.gameLayer = game;
		tileSize = gameLayer.getTileSize();
		tm = gameLayer.getTileManager();
		mapRowNumber = gameLayer.getMapRowNumber();
		mapColumnNumber = gameLayer.getColumnNumber();
	}

	public void checkTile(AliveEntity entity)
	{
		int left = entity.getX() + entity.getHitbox().getOffsetX();
		int right = entity.getX() + entity.getHitbox().getOffsetX() + entity.getHitbox().getHitboxWidth();
		int top = entity.getY() + entity.getHitbox().getOffsetY();
		int bottom = entity.getY() + entity.getHitbox().getOffsetY() + entity.getHitbox().getHitboxHeight();


		int leftCol = left/tileSize;
		int rightCol = right/tileSize;
		int topRow = top/tileSize;
		int bottomRow = bottom/tileSize;

		switch (entity.getDirection())
		{
        case UP -> topRow = (top - entity.getSpeed()) / tileSize;
        case DOWN -> bottomRow = (bottom + entity.getSpeed()) / tileSize;
        case LEFT -> leftCol = (left - entity.getSpeed()) / tileSize;
        case RIGHT -> rightCol = (right + entity.getSpeed()) / tileSize;
		}

	    int[][] tilePositions = switch (entity.getDirection())
	    		{
	        case UP -> new int[][]{{leftCol, topRow}, {rightCol, topRow}};
	        case DOWN -> new int[][]{{leftCol, bottomRow}, {rightCol, bottomRow}};
	        case LEFT -> new int[][]{{leftCol, topRow}, {leftCol, bottomRow}};
	        case RIGHT -> new int[][]{{rightCol, topRow}, {rightCol, bottomRow}};
	    };

		for (int[] pos : tilePositions)
		{
			if(pos[1] > mapRowNumber || pos[1] < 0 || pos[0] > mapColumnNumber || pos[0] < 0) 
			{
				entity.setCollisionOn(true);
                break;
			}
            int tileNum = tm.getMap()[pos[1]][pos[0]];
            if (tm.getTiles()[tileNum].isCollision())
            {
            	entity.setCollisionOn(true);
                break;
            }
        }

	}

	public boolean checkBlockedTile(int y, int x)
	{
		int row = y / tileSize;
		int column = x / tileSize;
		int tileNum = tm.getMap()[row][column];
		if(tm.getTiles()[tileNum].isCollision()) return true;

		return false;
	}
}
