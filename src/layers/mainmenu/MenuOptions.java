package layers.mainmenu;

public enum MenuOptions {
	ONE_PLAYER("ONE PLAYER"),
	TWO_PLAYERS("TWO PLAYERS"),
	LEADERBOARD("LEADERBOARD"),
	CONTROLS("CONTROLS"),
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
