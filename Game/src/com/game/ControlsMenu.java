package com.game;
import java.awt.*;

public class ControlsMenu
{
    private Icons basicControlsIcon;
    private Icons ahsanControlsIcon;
    private Icons saroshControlsIcon;
    private Icons tayyabControlsIcon;
    private Icons goBackIcon;

    ControlsMenu()
    {
        //Initialising Icons: Giving them appropriate coordinates, widths and heights
        goBackIcon = new Icons("/esc.png",350, 100, 25,900);
        basicControlsIcon = new Icons ("/Controls1.png",650, 475, 400,50);
        ahsanControlsIcon = new Icons ("/ControlsAhsan.png",650, 475, 1100,50);
        saroshControlsIcon = new Icons ("/ControlsSarosh.png",650, 475, 400,550);
        tayyabControlsIcon = new Icons ("/ControlsTayyab.png",650, 475, 1100,550);
    }

    void render(Graphics g)
    {
        //Rendering every Icon
        goBackIcon.render(g);
        basicControlsIcon.render(g);
        ahsanControlsIcon.render(g);
        saroshControlsIcon.render(g);
        tayyabControlsIcon.render(g);
    }
}