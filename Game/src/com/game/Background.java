package com.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Background
{
    private int absoluteXcor;
    private Image image;

    Background(){ }

    void render(int cameraXcor, Graphics g)
    {
        int xcor = absoluteXcor - cameraXcor;

        g.drawImage(image, xcor, 0, null);
    }

    void move(int horizontalSpeed)
    {
        absoluteXcor += horizontalSpeed;
        if(absoluteXcor < 0)
        {
            absoluteXcor = 0;
        }
    }

    int calculateSpeed(int cameraXcor, int characterSpeed)
    {
        int backgroundDistanceFromEdge;
        int cameraDistanceFromEdge;
        double backgroundSpeed;

        if (characterSpeed >= 0) // Moving towards right
        {
            backgroundDistanceFromEdge = (Key.levelArrayColumns * Key.platformWidth) - Key.backgroundWidth - absoluteXcor; // distance from right Limit of Background
            cameraDistanceFromEdge = (Key.levelArrayColumns * Key.platformWidth) - Key.screenWidth - cameraXcor; // distance from right Limit of Camera
        }
        else // Moving towards Left
        {
            backgroundDistanceFromEdge = absoluteXcor; // distance from Left Limit of Background
            cameraDistanceFromEdge = cameraXcor; // distance from Left Limit of Camera
        }

        backgroundSpeed = (backgroundDistanceFromEdge * 1.0) / cameraDistanceFromEdge * characterSpeed;

        return (int)backgroundSpeed;
    }

    void reset (String path, int xcor)
    {
        absoluteXcor = xcor;
        try
        {
            image = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        image = image.getScaledInstance(Key.backgroundWidth, Key.backgroundHeight, Image.SCALE_SMOOTH);
    }

    int getAbsoluteXcor()
    {
        return absoluteXcor;
    }
}