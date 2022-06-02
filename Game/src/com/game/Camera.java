package com.game;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Camera extends Canvas implements Runnable
{
    Handler handler;
    private ControlsMenu controlsMenu;//the panel that shows all the controls of the game
    static final long timeDelay = Key.timeDelayBetweenFrames;//variable to determine the amount of delay that occurs between each run of the thread's while loop
    private HUD headsUpDisplay;//display that shows stats such as life and current character on the display
    Thread graphicsThread;
    private int ticks = 0;//each tick is 25 milli-seconds (amount of delay between each run of the graphicsThread's while loop), used to count time to transition from loading screen
    private Icons transition;//loading screen
    Icons fade;//fade effect for when character dies
    private Icons mainMenubackground;
    private Icons credits; // Final Ending Credits for the game
    private int xcor;//x coordinate according to camera's supposed position on the foreground array
    private boolean running;//used to determine whether graphicsThread is running or not
    private Icons healthBar;


    Camera(Handler handler)
    {
        //all objects and variables are initialized
        credits = new Icons("/credits.png", 1920,1080, 0, Key.screenHeight);
        healthBar = new Icons("/healthbarenemy.png", 200,30, 500, 50);
        mainMenubackground = new Icons("/MENU BACKGROUND.png", 1920,1080, 0, 0);
        transition = new Icons("/transition.png", 1920, 1080, 0, 0);
        fade = new Icons("/fade.png", 1920, 2160, 0, Key.screenHeight); // Height was Key.screenHeight
        controlsMenu = new ControlsMenu();
        setBackground(Color.black);
        headsUpDisplay = new HUD();
        this.handler = handler;
        this.addKeyListener(new KeyboardListener(handler.players, handler.mainMenu, handler.pauseMenu));//key listener added to take inputs through the Keyboard
    }


    synchronized void start()
    {
        graphicsThread = new Thread(this);
        running = true;
        graphicsThread.start();
    }

    @Override
    public void run()
    {
        while (running)
        {
            long time = System.currentTimeMillis();//records time at the beginning of iteration to calculate how long it took to run the loop
            render();
            time = System.currentTimeMillis() - time;//calculates time taken to run the render method
            if (time > timeDelay)
            {
                time = timeDelay;//if the time taken to run render is greater than the time allowed by delay variable, make the thread sleep for 0 milli seconds
            }
            try {
                Thread.sleep(timeDelay - time);//makes sure the total time taken to run through single iteration of loop is not less than what the delay variable allows
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();//graphics object used to draw all the objects on screen

        //calls different mathods based on what state the game is in
        switch(Key.gameState)
        {
            case 0: // Game is being played
            {
                renderGame(g);
                tick();
                break;
            }
            case 1: // On the Main Menu Screen Before Game Runs
            {
                handler.mainMenu.render(g);
                break;
            }
            case 2: //the user is viewing the controls through the main menu
            {
                mainMenubackground.render(g);
                controlsMenu.render(g);
                break;
            }
            case 3: // On PauseMenu
            {
                renderGame(g);
                handler.pauseMenu.render(g);
                break;
            }
            case 4: //the user is viewing the controls through the pause menu
            {
                renderGame(g);
                controlsMenu.render(g);
                break;
            }
            case 5: // game is being loaded
            {
                ticks++;
                transition.render(g);
                if (ticks >= 10)//loading screen lasts 10 ticks
                {
                    ticks = 0;
                    Key.gameState = 0;
                }
                break;
            }
            case 6: //level is being reset after player dies
            {
                renderGame(g);
                if (fade.getYcor() == Key.screenHeight)
                {
                    MusicPlayer.playSound(Player.deathSoundFile, -10, 0);
                }
                fade.render(g);
                fade.tick();
                if (fade.getYcor() < - Key.screenHeight * 2)
                {
                    fade.setYCor(Key.screenHeight);
                    Key.gameState = 0;
                }
                break;
            }
            case 7: //main menu is being loaded
            {
                ticks++;
                transition.render(g);
                if (ticks >= 10)//loading screen lasts 10 ticks
                {
                    ticks = 0;
                    Key.gameState = 1;
                }
                break;
            }
            case 8: // Credits are being shown
            {
                mainMenubackground.render(g);
                if(credits.getYcor() <= 0) // Credits have rolled all the way up
                {
                    ticks++;
                    credits.setYCor(0);
                }
                else
                {
                    credits.setYCor(credits.getYcor() - 3); // Roll up Credits
                }

                credits.render(g);
                if (ticks >= 100)//Credits stay at top for 100 ticks
                {
                    ticks = 0;
                    Key.gameState = 7;
                    credits.setYCor(Key.screenHeight);
                }
                break;
            }

        }

        g.dispose();
        bs.show();
    }

    void renderGame(Graphics g)
    {
        //background and player are rendered
        handler.background.render(xcor, g);
        handler.players[Player.currentCharacter].render(g);

        //nested loop to render enemies on screen
        for (int row = 0; row < handler.foreground.length; row++)//iterates through the foreground array vertically
        {
            for (int column = 0; column < handler.foreground[0].length; column++)//iterates horizontally
            {
                if (handler.foreground[row][column] >= 1225)
                {
                    handler.bosses[handler.foreground[row][column] - 1225].updateDisplayCoordinates(xcor);
                    handler.bosses[handler.foreground[row][column] - 1225].render(g);
                }
                else if (handler.foreground[row][column] >= 25)
                {
                    handler.enemies[handler.foreground[row][column] - 25].updateDisplayCoordinates(xcor);
                    handler.enemies[handler.foreground[row][column] - 25].renderWithCheck(g);
                }
            }
        }

        //nested for loop to determine which platform needs to be rendered on screen
        for (int row = 0; row < handler.foreground.length; row++)
        {
            for (int column = xcor / Key.platformWidth; column < (Key.screenWidth + xcor) / Key.platformWidth + 1; column++)//iterates horizontally, starting from the x coordinate of the camera up to the width of the screen
            {
                if (handler.foreground[row][column] > 0 && handler.foreground[row][column] < 25)
                {
                    Platform.render(handler.foreground[row][column], column * Platform.platformWidth - xcor, row * Platform.platformHeight, g);
                }
            }
        }
        if(Key.bossFight)
        {
            healthBar.setXCor(500);
            for(int i = 0; i < Boss.life; i++)
            {
                healthBar.render(g);
                healthBar.setXCor(healthBar.getXCor() + 200);
            }
        }
        headsUpDisplay.render(g);//heads up display rendered on top of everything else
    }

    void tick()
    {
        //frames are updated to create an animation like effect in the tick method
        //only player and enemies need to be updated through this method
        //enemies have projectiles that need to tick, but those methods are called as an extension to the tick method of the enemy through the enemy's tick method
        //this is done so the projectiles don't remain on screen once the enemy controlling them dies, also to save space on the foreground array as it would get too crowded if there are too many projectiles on screen
        handler.players[Player.currentCharacter].tick();
        for (int[] row : handler.foreground)
        {
            for (int entry : row)
            {
                if (entry >= 1225)
                {
                    handler.bosses[entry - 1225].tick();
                }
                else if (entry >= 25)
                {
                    handler.enemies[entry - 25].tick();
                }
            }
        }
        headsUpDisplay.tick();
    }

    //camera is shifted left or right based on the player's movements
    void shift(int horizontalSpeed)
    {
        xcor += horizontalSpeed;
    }

    int getXcor()
    {
        return xcor;
    }
    void setXcor(int xcor)
    {
        this.xcor = xcor;
    }
}