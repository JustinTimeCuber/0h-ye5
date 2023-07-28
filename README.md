# 0h-ye5
A solver for the mobile game "0h n0" with the ability to solve and generate boards up to 20x20. Not affiliated with 0h n0 or its creator(s) in any way.

## Installation
Download the JAR file and run it with Java 17 or higher. Should work on Windows, Linux, and macOS.

## Usage
Left and right click to alter tile states. Scrolling also works. Hover over a tile and type a number key to set it to a blue tile with that number.
To change the board size, left click (to increase) or right click (to decrease) the button.

## Screenshots
![image](https://github.com/JustinTimeCuber/0h-ye5/assets/46458276/9c5048ea-dfde-4c2a-bc59-a111f0dfb87f)
![image](https://github.com/JustinTimeCuber/0h-ye5/assets/46458276/41bbfaf8-d480-410c-a977-87f6226be279)
![image](https://github.com/JustinTimeCuber/0h-ye5/assets/46458276/fa91477e-8b1b-4e75-95be-c81dd337719f)

## Performance
The generator will sometimes take a few seconds to create a new board, especially at larger sizes. This isn't a huge issue but in the future I'll look into ways to improve this. The solver is much faster, this is because the generator is actually invoking the solver many times to attempt to find solvable boards.
