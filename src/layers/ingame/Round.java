package layers.ingame;

import java.util.List;

public class Round {

	private List<Wave> waves;
	private int currentWave = 0;

	public List<Wave> getWaves()
	{
		return waves;
	}

	public void setWaves(List<Wave> waves)
	{
		this.waves = waves;
	}

	public void incrementWave()
	{
		currentWave++;
	}

	public int getCurrentWaveIndex()
	{
		return currentWave;
	}

	public void setCurrentWaveIndex(int i)
	{
		currentWave = i;
	}
}
