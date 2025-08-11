package tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.Game;

import java.util.HashSet;

public class PathFinder {


	private final int maxDepth = 200;
	private Game game;

	public PathFinder(Game game)
	{
		this.game = game;
	}

	public List<int[]> findPath(int startRow, int startCol, int goalRow, int goalCol)
	{
		int heuristic = getHeuristic(startRow, startCol, goalRow, goalCol);
		List<int[]> path = new ArrayList<>();

		while(true)
		{
			HashSet<String> visited = new HashSet<>();
			int searchResult = search(new PathNode(startRow, startCol),
					goalCol, goalRow, 0, heuristic, visited, path, maxDepth);
			if(searchResult == -1)
			{
				Collections.reverse(path);
				return path;
			}
			else if(searchResult == Integer.MAX_VALUE)
			{
				return Collections.emptyList();
			}
			heuristic = searchResult;
		}
	}


	private int getHeuristic(int startRow, int startCol, int goalRow, int goalCol)
	{
		return Math.abs(startCol - goalCol) + Math.abs(startRow - goalRow);
	}

	private int search(PathNode pathNode, int goalCol, int goalRow, int gCost,
			int threshold, HashSet<String> visited, List<int[]> path, int maxDepth)
	{


		if(pathNode.getColumn() == goalCol && pathNode.getRow() == goalRow)
		{
			path.add(new int[]{pathNode.getRow(), pathNode.getColumn()});
			return -1;
		}

		pathNode.setgCost(gCost);
		pathNode.sethCost(getHeuristic(pathNode.getRow(), pathNode.getColumn(), goalRow, goalCol));
		pathNode.setfCost(pathNode.getgCost() + pathNode.gethCost());

		if(pathNode.getgCost() > maxDepth) return Integer.MAX_VALUE;

		if(pathNode.getfCost() > threshold)
		{
			return pathNode.getfCost();
		}

		visited.add(pathNode.getRow() + ", " + pathNode.getColumn());

		int min = Integer.MAX_VALUE;

		for(int[] directions : getDirections())
		{
				int newRow = pathNode.getRow() + directions[0];
				int newCol = pathNode.getColumn() + directions[1];
				if(game.getCollisionCheck().checkBlockedTile(newRow, newCol)) continue;
				if(visited.contains(newRow + ", " + newCol)) continue;
				PathNode child = new PathNode(newRow, newCol);

				int searchResult = search(child, goalCol, goalRow, gCost + 1, threshold, visited, path, maxDepth);

				if(searchResult == -1)
				{
					path.add(new int[]{child.getRow(), child.getColumn()});
					return -1;
				}
				if(min > searchResult) min = searchResult;
		}

		return min;
	}

	private int[][] getDirections()
	{
		return new int[][] {{0,-1},{0,1},{1,0},{-1,0}};
	}

}
