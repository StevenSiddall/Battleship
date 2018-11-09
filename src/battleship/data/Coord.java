package battleship.data;

import battleship.exceptions.InvalidCoordinateException;


/**
Programmer: Steven Siddall
 @author Steven Siddall

Class representing a set of x and y coordinates
*/
public class Coord
{
    /**X value of coordinates*/
    private int xValue;
    /**Y value of coordinates*/
    private int yValue;

    /**
     * Full constructor that takes x and y values
     * @param x x location
     * @param y y location
     */
    public Coord(int x, int y) throws InvalidCoordinateException
    {
        setX(x);
        setY(y);
    }
    
    /**
    Constructor that takes in parameters in the form of battleship coordinates
    with the y value being a letter
    @param x the x location
    @param rowChar letter representation of the y location
    @throws InvalidCoordinateException 
    */
    public Coord(int x, char rowChar) throws InvalidCoordinateException
    {
        setX(x);
        setY(rowChar);
    }

    /**
     * Default constructor initializes x and y to 0
     */
    public Coord()
    {
        xValue = 0;
        yValue = 0;
    }

    /**
     * Copy constructor
     * @param otherCoord coordinate to copy
     */
    public Coord(Coord otherCoord)
    {
        if(otherCoord != null)
        {
            this.xValue = otherCoord.getX();
            this.yValue = otherCoord.getY();
        }
        else
        {
            xValue = 0;
            yValue = 0;
        }
    }

    public void setX(int x) throws InvalidCoordinateException
    {
        if(x < 0 || x > 9)
        {
            xValue = 0;
            throw new InvalidCoordinateException("Invalid X Coordinate: " + x);
        }
        xValue = x;
    }

    public void setY(int y) throws InvalidCoordinateException
    {
        if(y < 0 || y > 9)
        {
            yValue = 0;
            throw new InvalidCoordinateException("Invalid Y Coordinate: " + y);
        }
        yValue = y;
    }
    
    /**
    Setter for the y coordinate that takes a letter as input
    @param y letter representation of the y coordinate
    @throws InvalidCoordinateException 
    */
    public void setY(char y) throws InvalidCoordinateException
    {
        y = Character.toUpperCase(y);
        if(y < 65 || y > 74)
        {
            throw new InvalidCoordinateException("Invalid Y Coordinate: " + y);
        }
        else
        {
            yValue = y - 65;
        }
    }

    public int getX()
    {
        return xValue;
    }

    public int getY()
    {
        return yValue;
    }

    /**
     * Creates a string that represents this coord as battleship coordinates i.e. "A1".
     * Will only work if this coord's y value is from 0 to 9 inclusive
     * @return a string representing this coord as battleship coordinates
     */
    public String bShipCoords()
    {
        String finalCoords = "";
        switch(yValue)
        {
        case 0:
            finalCoords += "A";
            break;
        case 1:
            finalCoords += "B";
            break;
        case 2:
            finalCoords += "C";
            break;
        case 3:
            finalCoords += "D";
            break;
        case 4:
            finalCoords += "E";
            break;
        case 5:
            finalCoords += "F";
            break;
        case 6:
            finalCoords += "G";
            break;
        case 7:
            finalCoords += "H";
            break;
        case 8:
            finalCoords += "I";
            break;
        case 9:
            finalCoords += "J";
            break;
        default:
            finalCoords += ("ERROR INVALID Y COORDINATE: " + yValue);
        }

        finalCoords += (xValue + 1);
        return finalCoords;
    }

    /**
     * Represents the coord in the form: (1,1)
     * @return
     */
    public String toStringParanthetical()
    {
        return "(" + xValue + "," + yValue + ")";
    }

    public String toString()
    {
        return "X Value: " + xValue + "\nY Value: " + yValue;
    }

    public boolean equals(Coord otherCoord)
    {
        if(otherCoord.getX() != xValue)
        {
            return false;
        }
        else if(otherCoord.getY() != yValue)
        {
            return false;
        }
        return true;
    }
}
