package graphics;

public enum MenuOptions {
	START_GAME("START GAME"),
	LEADERBOARD("LEADERBOARD"),
	EXIT("EXIT");

	private final String name;

	MenuOptions(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}
