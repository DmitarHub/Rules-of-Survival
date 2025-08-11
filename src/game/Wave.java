package game;

public class Wave {

	private int numberOfEnemies;

	public Wave(int number)
	{
		this.numberOfEnemies = number;
	}

	public int getNumberOfEnemies()
	{
		return this.numberOfEnemies;
	}

	public void setNumberOfEnemies(int number)
	{
		numberOfEnemies += number;
	}
}
