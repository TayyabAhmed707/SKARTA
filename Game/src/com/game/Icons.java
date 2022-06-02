package com.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Icons implements Animated
{
    private Image image;
    private int xCor;
    private int yCor;
    private int width;
    private int height;

    Icons(String path, int width, int height, int xCor, int yCor)
    {
        this.xCor = xCor;
        this.yCor = yCor;
        this.width = width;
        this.height = height;
        try
        {
            image = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Gets scaled version of Icon
    }

    @Override
    public void render(Graphics g)
    {
        g.drawImage(image, xCor, yCor, null);
    } // Renders The Icon

    @Override
    public void tick()
    {
        yCor -= 50;
    }

    int getYcor()
    {
        return yCor;
    }
    void setYCor(int yCor) {
        this.yCor = yCor;
    }

    void setXCor(int xCor) {
        this.xCor = xCor;
    }
    int getXCor() {return xCor;}
}