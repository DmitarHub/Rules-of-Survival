package graphics;

public enum GameOverOptions {
	NEW_GAME("NEW GAME"),
	LEADERBOARD("LEADERBOARD"),
	MAIN_MENU("MAIN MENU"),
	EXIT("EXIT");

	private String name;

	GameOverOptions(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;

	}
}
