package tiles;

public class PathNode {

	private int gCost = 0;
	private int hCost = 0;
	private int fCost = 0;

	private int row, column;

	private PathNode parent;

	public PathNode(int row, int column)
	{
		this.row = row;
		this.column = column;
	}


	public int getgCost()
	{
		return gCost;
	}

	public void setgCost(int gCost)
	{
		this.gCost = gCost;
	}

	public int gethCost()
	{
		return hCost;
	}

	public void sethCost(int hCost)
	{
		this.hCost = hCost;
	}

	public int getfCost()
	{
		return fCost;
	}

	public void setfCost(int fCost)
	{
		this.fCost = fCost;
	}

	public int getColumn()
	{
		return column;
	}

	public void setColumn(int column)
	{
		this.column = column;
	}

	public PathNode getParent()
	{
		return parent;
	}

	public void setParent(PathNode parent)
	{
		this.parent = parent;
	}

	public void setRow(int row)
	{
		this.row = row;
	}

	public int getRow()
	{
		return row;
	}


}
