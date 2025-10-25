# Rules of Survival 
Rules of Survival is a Java-based survival game where you try to survive as many rounds as possible while facing debuffs from 3 randomly selected rules each round.
## Project Features
* WASD Controls
* NPC interaction
* FPS/Stats toggle
* 5 Rounds with 4 Waves each 
* Collision detection
* Tiled maps
* Two players
* Layered system
* Pause with Esc
* Mouse controls

## Instalation Guide
Follow these steps to get Rules of Survival up and running on your computer
## Prerequisites
* Java Development Kit 8 or higher
1. Clone the repository
- git clone https://github.com/DmitarHub/Rules-of-Survival.git
2. Change the directory
- cd Rules-of-Survival
3. Make a bin folder
- mkdir -p bin or (Windows CMD) mkdir bin
4. Move files from res folder
- cp -r res/. bin/
5. Change to source folder
- cd src
- dir /s /b *.java > sources.txt
6. Compile  
- javac -d ..\bin @sources.txt
7. Run the game
- cd ..
- java -cp bin game.Main

## How to use the project
- In the Main menu using your mouse choose the option you want
- Check the controls tab for control information
- In the Game over screen you first need to enter a name ( max 12 characters ) and press enter to be able to move on
- In Any window you can use CTRL + F to show current FPS and Mouse information

## Preview
![MainMenu](https://github.com/DmitarHub/Rules-of-Survival/raw/main/images/MainMenu.PNG)
![Controls](https://github.com/DmitarHub/Rules-of-Survival/raw/main/images/Controls.PNG)
![Pause](https://github.com/DmitarHub/Rules-of-Survival/raw/main/images/Pause.PNG)
![RuleSelection](https://github.com/DmitarHub/Rules-of-Survival/raw/main/images/RuleSelectionScreenshot.PNG)
![InGame](https://github.com/DmitarHub/Rules-of-Survival/raw/main/images/InGameScreenshot.PNG)
![LeaderBoard](https://github.com/DmitarHub/Rules-of-Survival/raw/main/images/LeaderBoardScreenshot.PNG)
![GameOver](https://github.com/DmitarHub/Rules-of-Survival/raw/main/images/GameOverScreenshot.PNG)

