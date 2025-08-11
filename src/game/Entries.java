package game;

public class Entries {

	private String name;
	private int finalscore;


	public Entries(String name, int finalscore)
	{
		this.name = name;
		this.finalscore = finalscore;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public int getFinalscore()
	{
		return finalscore;
	}


	public void setFinalscore(int finalscore)
	{
		this.finalscore = finalscore;
	}


}
