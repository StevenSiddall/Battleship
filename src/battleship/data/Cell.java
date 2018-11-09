package battleship.data;

import battleship.exceptions.InvalidCoordinateException;


/**
Programmer: Steven Siddall

Class representing a single cell (square) in a game of battleship

Cell states:
0: Untouched. This cell has not been shot at yet
1: Miss. This cell has been shot at but did not contain a ship
2: Hit. This cell was shot at and contained a ship
3. Sunken Ship. This cell contains a ship that was sunk
*/
public class Cell
{
    /**constant for cell state*/
    public static final int UNTOUCHED = 0;
    /**constant for cell state*/
    public static final int MISS = 1;
    /**constant for cell state*/
    public static final int HIT = 2;
    /**constant for cell state*/
    public static final int SUNKEN_SHIP = 3;

    /**Coordinates of the cell*/
    private Coord coords;
    /*Indicates if the cell is untouched, a miss, a hit, or a former hit now sunken ship- 0, 1, 2, 3 respectively*/
    private int state;
    /*indicates the point value for the likelihood of a ship being present*/
    private double value;

    /**
     * Default constructor. Initializes coords to 0,0
     */
    public Cell() throws InvalidCoordinateException
    {
        coords = new Coord(0,0);
        state = UNTOUCHED;
        value = 0;
    }

    /**
     * Full constructor which takes the x and y coordinates
     * @param x x-coordinate of new cell. Must be from 0 to 9 inclusive
     * @param y y-coordinate of new cell. Mist be from 0 to 9 inclusive
     */
    public Cell(int x, int y) throws InvalidCoordinateException
    {
        coords = new Coord(x,y);
        state = UNTOUCHED;
        value = 0;
    }

    /**
     * Copy constructor
     * @param otherCell cell to make this one a copy of
     */
    public Cell(Cell otherCell)
    {
        if(otherCell == null)
        {
            try
            {
                coords = new Coord(0,0);
            }
            catch (InvalidCoordinateException e) //should never happen since coords are hardcoded
            {
                System.out.println("Coordinate error in Cell copy constructor.");
                System.exit(1);
            }
            state = UNTOUCHED;
            value = 0;
        }
        else
        {
            try
            {
                coords = new Coord(otherCell.getXCoord(),otherCell.getYCoord());
            }
            catch (InvalidCoordinateException e) ////should never happen since other cell has good coords
            {
                System.out.println("Coordinate error in Cell copy constructor.");
                System.exit(1);
            }
            state = otherCell.getState();
            value = otherCell.getValue();
        }
    }

    /**
     * Gets the x coordinate of the cell
     * @return the x coordinate of the cell
     */
    public int getXCoord()
    {
        return coords.getX();
    }

    /**
     * Gets the y coordinate of the cell
     * @return the y coordinate of the cell
     */
    public int getYCoord()
    {
        return coords.getY();
    }

    /**
     * Gets the state of the cell
     * @return the state of the cell. 0 = untouched, 1 = miss, 2 = hit, 3 = sunken ship
     */
    public int getState()
    {
        return state;
    }

    /**
     * Gets the point value of the cell indicating the likelihood of a ship being present
     * @return the point value of the cell indicating the likelihood of a ship being present
     */
    public double getValue()
    {
        return value;
    }

    /**
     * Gets the coordinates of the cell in the from of an int array
     * @return an int[] with index 0 being the x coordinate and index 1 being the y coordinate
     */
    public Coord getCoords()
    {
        return new Coord(coords);
    }

    /**
     * Gets a string of the coordinates of this cell in the for of battleship coordinates
     * @return a string representing the coordinates of this cell in battleship form
     */
    public String getBShipCoords()
    {
        return translateCoords();
    }

    /**
     * Sets the x coordinate of the cell
     * @param x the new x coordinate of the cell. Must be from 0 to 9 inclusive
     * @return true if the coordinate was successfully set
     */
    public void setXCoord(int x) throws InvalidCoordinateException
    {
        coords.setX(x);
    }

    /**
     * Sets the y coordinate of the cell
     * @param y the new y coordinate of the cell. Must be 0 to 9 inclusive
     * @return true if the coordinate was successfully set
     */
    public void setYCoord(int y) throws InvalidCoordinateException
    {
        coords.setY(y);
    }

    /**
     * Sets both x and y coordinates
     * @param x the new x coordinate. Must be from 0 to 9 inclusive
     * @param y the new y coordinate. Must be from 0 to 9 inclusive
     * @return true if both were set successfully
     */
    public void setCoords(int x, int y) throws InvalidCoordinateException
    {
        coords.setX(x);
        coords.setY(y);
    }

    /**
     * Translates from int coords to battleship coords. For example from 0,0 to A1
     * @return the string of coordinates in battlehsip form
     */
    private String translateCoords()
    {
        return coords.bShipCoords();
    }

    /**
     * Sets the state of the cell as long as it is from 0 to 3 inclusive
     * @param newState the new state of the cell
     * @return true if the state was successfully set
     */
    public boolean setState(int newState)
    {
        if(newState >= UNTOUCHED && newState <= SUNKEN_SHIP)
        {
            state = newState;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets the value of the cell which indicates the likelihood of a ship being present
     * @param newValue the new point value for the cell
     */
    public void setValue(double newValue)
    {
        value = newValue;
    }

    /**
     * Represents this cell in the from of a string
     */
    public String toString()
    {
        String returnStr = "";
        returnStr += "Integer Coordinates: " + coords.toStringParanthetical() + "\n";
        returnStr += "Battleship Coordinates: " + translateCoords() + "\n";
        returnStr += "State: " + state + "\n";
        returnStr += "Point Value: " + value + "\n";
        return returnStr;
    }

    /**
     * Represents this cell in the form of a string
     * @param otherCell cell that we are comparing this one to
     * @return true if all instance variables are equal
     */
    public boolean equals(Cell otherCell)
    {
        if(otherCell.getXCoord() != coords.getX())
        {
            return false;
        }
        else if(otherCell.getYCoord() != coords.getY())
        {
            return false;
        }
        else if(otherCell.getState() != state)
        {
            return false;
        }
        else if(otherCell.getValue() != value)
        {
            return false;
        }

        return true;
    }
}
