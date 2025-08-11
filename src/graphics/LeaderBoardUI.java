package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import game.Game;
import game.GameState;
import listeners.KeyInputs;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import game.Entries;

public class LeaderBoardUI {

	private Game game;
	private final int width;
	private final int height;
	private KeyInputs keyInputs;

	private final String top10 = "TOP 10 SCORES";
	private final String back = "Back";
	private final int ten = 10;
	private PriorityQueue<Entries> leaderBoardEntries =  new PriorityQueue<>((a, b) -> Integer.compare(b.getFinalscore(), a.getFinalscore()));

	private final String fileName = "/leaderboard.txt";

    private final Color backgroundColor = new Color(0xAEECEF);
    private final Color titleColor = new Color(0x1D3557);
    private final Color textColor = new Color(0x1D3557);
    private final Color highlightColor = new Color(0x1AA557);
	private final Color accentColor = new Color(0x103557);

    private final Color firstPlace = new Color(0xFFD700);
    private final Color secondPlace = new Color(0xC0C0C0);
    private final Color thirdPlace = new Color(0xCD7F32);

    private Font titleFont = new Font("SansSerif", Font.BOLD, 50);
    private Font entryFont = new Font("SansSerif", Font.PLAIN, 36);
    private Font backFont = new Font("SansSerif", Font.PLAIN, 42);

    private boolean enterReleased = true;

    private final int lineWidth = 3;

    private final String externalFile = "externalLeaderBoard.txt";
    private int titleX;
    private final int rankX;
    private final int nameX;
    private final int scoreX;
    private final int bigMargin = 50;
    private final int lineHeight = 40;
    private final int startY;

	public LeaderBoardUI(Game game, int width, int height, KeyInputs keyInputs)
	{
		this.game = game;
		this.width = width;
		this.height = height;
		this.keyInputs = keyInputs;
		this.rankX = width / 6;
		this.nameX = width / 6 + bigMargin;
		this.scoreX = width - width / 6;
		this.startY = height / 4;
		initialize();
	}

	public void initialize()
	{
		File file = new File(externalFile);

		if(!file.exists())
		{
			 try (InputStream is = getClass().getResourceAsStream(fileName);
					 OutputStream os = new FileOutputStream(file))
			 {
				 if(is == null)
				 {
					 System.out.println("Resource " + fileName + " not found!");
					 return;
				 }

				 byte[] buffer = new byte[1024];
				 int lenght;
				 while((lenght = is.read(buffer)) > 0)
				 {
					 os.write(buffer, 0, lenght);
				 }

			 } catch(IOException e)
			 {
				 System.out.println("Error copying leaderboard resource!");
			 }
		}

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;
			while(( line = bufferedReader.readLine()) != null)
			{
				String[] entries = line.split(";");
	            for (String entry : entries)
	            {
	                entry = entry.trim();
	                if (!entry.isEmpty())
	                {
	                    String[] parts = entry.split(" ");
	                    if (parts.length == 2)
	                    {
	                        String name = parts[0].trim();
	                        int score = Integer.parseInt(parts[1].trim());
	                        leaderBoardEntries.offer(new Entries(name, score));
	                    }
	                }
	            }
			}
			bufferedReader.close();

		} catch (IOException e) {
			System.out.println("Error while opening file!");
		}
	}


	public void update()
	{
	    if (keyInputs.isEnter())
	    {
	        if (enterReleased)
	        {
	        	game.setGameState(GameState.MENU);
	        	game.getMenuUI().setup();
	            enterReleased = false;
	        }
	    } else
	    {

	        enterReleased = true;
	    }
	}

	public void setup()
	{
		enterReleased = false;
	}

	public void addEntry(String name, int finalscore)
	{
		leaderBoardEntries.offer(new Entries(name, finalscore));
		saveFile();
	}

	private void saveFile()
	{
		try {
			FileWriter fileWriter = new FileWriter(externalFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			List<Entries> sortedEntries = new ArrayList<>(leaderBoardEntries);
		    sortedEntries.sort((a, b) -> Integer.compare(b.getFinalscore(), a.getFinalscore()));
            for (int i = 0; i < sortedEntries.size(); i++)
            {
                Entries entry = sortedEntries.get(i);
                bufferedWriter.write(entry.getName() + " " + entry.getFinalscore());
                if (i != sortedEntries.size() - 1)
                {
                	bufferedWriter.write("; ");
                }
            }
            bufferedWriter.newLine();
            bufferedWriter.close();
		} catch (IOException e)
		{
			System.out.println("Error saving leaderboard!");
		}
	}

	public void draw(Graphics2D drawing)
	{
	    drawing.setColor(backgroundColor);
	    drawing.fillRect(0, 0, width, height);

	    drawing.setColor(titleColor);
	    drawing.setFont(titleFont);
	    titleX = centerString(drawing, top10);
	    drawing.drawString(top10, titleX, height / 6);

	    drawing.setFont(entryFont);

	    int i = 0;
	    for (Entries entry : leaderBoardEntries)
	    {

	        String rankStr = (i + 1) + ".";
	        drawing.setColor(textColor);
	        drawing.drawString(rankStr, rankX, startY + i * lineHeight);

	        drawing.setColor(textColor);
	        drawing.drawString(entry.getName(), nameX, startY + i * lineHeight);

	        String scoreStr = String.valueOf(entry.getFinalscore());
	        int scoreWidth = (int) drawing.getFontMetrics().getStringBounds(scoreStr, drawing).getWidth();
	        drawing.drawString(scoreStr, scoreX - scoreWidth, startY + i * lineHeight);

	        if (i == 0)
	        {
	            drawing.setColor(firstPlace);
	        } else if (i == 1)
	        {
	            drawing.setColor(secondPlace);

	        } else if (i == 2)
	        {
	            drawing.setColor(thirdPlace);

	        }
            drawing.drawString(rankStr, rankX, startY + i * lineHeight);
            drawing.drawString(entry.getName(), nameX, startY + i * lineHeight);
            drawing.drawString(scoreStr, scoreX - scoreWidth, startY + i * lineHeight);
	        i++;
	        if(i == ten) break;
	    }

	    drawing.setFont(backFont);
	    drawing.setColor(highlightColor);

	    int backX = centerString(drawing, back);
	    int backY = height - 100;
	    drawing.drawString(back, backX, backY);

	    drawing.setColor(accentColor);
	    drawing.fillRect(backX, backY + 10, (int) drawing.getFontMetrics().getStringBounds(back, drawing).getWidth(), lineWidth);
	}

	private int centerString(Graphics2D draw, String string)
	{
        int length = (int) draw.getFontMetrics().getStringBounds(string, draw).getWidth();
        return width / 2 - length / 2;
    }

	public int getWidth()
	{
		return width;
	}


	public int getHeight()
	{
		return height;
	}


}
