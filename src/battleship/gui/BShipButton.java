/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship.gui;
import battleship.data.Coord;
/**
Programmer: Steven Siddall
 @author Steven Siddall

This class represents a single button on the GUI battleship board.

It extends the JavaFX button class so that it can store its own coordinates as well
 */
public class BShipButton extends javafx.scene.control.Button
{
    private Coord coords;
    
    public BShipButton()
    {
        super();
        coords = new Coord();
    }
    
    public BShipButton(Coord newCoords)
    {
        super();
        coords = newCoords;
    }
    
    public Coord getCoords()
    {
        return new Coord(coords);
    }
}