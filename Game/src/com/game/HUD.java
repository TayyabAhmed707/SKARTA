package com.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUD implements Animated
{
    private BufferedImage[][] selectedCharacter;
    private BufferedImage[][] lifeCounter;
    private BufferedImage[][] powers;

    private final String characterPath = "/HUDCharacters.png";
    private final int selectorWidth = 250;
    private final int selectorHeight = 160;

    private final String lifePath = "/HUDLife.png";
    private final int lifeHeight = 45;

    private final String powerPath = "/HUDPowers.png";
    private final int powerWidth = 55;
    private final int powerHeight = 60;
    static final int maxPowerFrame = 60;
    static int[] powerFrames = {maxPowerFrame, maxPowerFrame, maxPowerFrame};
    private static boolean refilling = false;

    static boolean isRefilling()
    {
        return refilling;
    }
    static void setRefilling(boolean refilling)
    {
        HUD.refilling = refilling;
    }

    HUD ()
    {
        selectedCharacter = HelperFunctions.makeSpritesheet(characterPath, 3, 1, selectorWidth, selectorHeight);
        lifeCounter = HelperFunctions.makeSpritesheet(lifePath, 5, 1, selectorWidth, lifeHeight);
        powers = HelperFunctions.makeSpritesheet(powerPath, 3, 4, powerWidth, powerHeight);
    }

    @Override
    public void render(Graphics g)
    {
        for (int character = 0; character < powers.length; character++)
        {
            g.drawImage(powers[character][powerFrames[character] / (maxPowerFrame / (powers[0].length - 1))], 48 + (powerWidth - 3) * character, 20, null);
        }
        g.drawImage(selectedCharacter[Player.currentCharacter][0], 0, powerHeight, null);
        g.drawImage(lifeCounter[Player.life][0], 0, selectorHeight + powerHeight - 10, null);

    }

    @Override
    public void tick()
    {
        if (powerFrames[2] < maxPowerFrame)
        {
            powerFrames[2]++;
        }
        if (powerFrames[1] < maxPowerFrame && refilling)
        {
            powerFrames[1]++;
        }
        if(powerFrames[1] >= maxPowerFrame)
        {
            refilling = false;
        }
        if(!refilling)
        {
            powerFrames[1] = Sarosh.shieldLife * maxPowerFrame / 3;
        }
    }

    static void reset()
    {
        for(int i = 0; i<3; i++)
        {
            HUD.powerFrames[i] = HUD.maxPowerFrame;
        }
        refilling = false;
    }
}