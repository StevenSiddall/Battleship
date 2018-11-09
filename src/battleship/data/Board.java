package battleship.data;


import battleship.exceptions.InvalidCoordinateException;
import java.text.DecimalFormat;

/**
Programmer: Steven Siddall

This class represents a board for a single player. Contains a two dimensional
array of cells
 A board has letters going up and down and numbers going across
*/

public class Board
{
    /**2 dimensional array of cells to represent board*/
    private Cell[][] cells;

    /**
     * default constructor. Creates a board of cells 10x10 and gives them coordinates
     */
    public Board()
    {
        cells = new Cell[10][10];
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                try
                {
                    cells[x][y] = new Cell(x,y);
                }
                catch(InvalidCoordinateException e) //should never happen since coord values are hardcoded
                {
                    System.out.println("Coordinate error in board default constructor");
                    System.exit(1);
                }
            }
        }
    }
    
    /**
    copy constructor
    @param original original board that we are copying
    */
    public Board(Board original)
    {
        cells = new Cell[10][10];
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                try
                {
                        cells[x][y] = new Cell(original.getCell(x, y));
                }
                catch(InvalidCoordinateException e) //should never happen since coord values are hardcoded
                {
                        System.out.println("Coordinate error in board default constructor");
                        System.exit(1);
                }
            }
        }
    }

    /**
     * Returns a deep copy of the cell at the specified coordinates
     * @param x x value for the desired cell
     * @param y y value for the desired cell
     * @return a deep copy of the requested cell
     */
    public Cell getCell(int x, int y) throws InvalidCoordinateException
    {
        if(!checkBounds(x,y))
        {
            return null;
        }
        return cells[x][y];
    }

    /**
     * Sets the value of the cell at the specified coordinates. Can be any value
     * @param x x coordinate of the desired cell
     * @param y y coordinate of the desired cell
     * @param value new point value for this cell
     * @return true if the coordinates were within bounds and the value was set
     */
    public boolean setCellValue(int x, int y, double value)
    {
        if(!checkBounds(x,y))
        {
            return false;
        }
        cells[x][y].setValue(value);
        return true;
    }

    /**
     * Sets the state of the cell at the specified coordinates to a state from 0 to 3 inclusive
     * @param x x coordinate of the target cell
     * @param y y coordinate of the target cell
     * @param state new state of the cell. Must be from 0 to 3 inclusive
     * @return true if the coordinates and state were valid and the state was successfully set
     */
    public boolean setCellState(int x, int y, int state)
    {
        if(!checkBounds(x,y))
        {
            return false;
        }
        return cells[x][y].setState(state);
    }

    /**
     * Variation of a toString that only returns the point values in a grid representation of the board
     * @return a string containing all point values in a grid representation
     */
    public String toStringValues()
    {
        DecimalFormat df = new DecimalFormat("00.00");
        String returnStr = "";
        for(int i = 0; i < 10; i++)
        {
            returnStr += "\t";
            returnStr += (i+1);
        }
        
        for(int y = 0; y < 10; y++)
        {
            returnStr += "\n\n\n";
            char rowChar = (char) (y+65);
            returnStr += rowChar;
            for(int x = 0; x < 10; x++)
            {
                returnStr += "\t";   
                returnStr += df.format(cells[x][y].getValue());
            }
        }
        return returnStr;
    }

    /**
     * Variation of a toStirng that only returns the integer version of the coordinates in a grid representation of the board
     * @return a string containing the integer version of the coordinates in a grid representation of the board
     */
    public String toStringIntCoords()
    {
        String returnStr = "";
        for(int i = 0; i < 10; i++)
        {
            returnStr += "\t";
            returnStr += (i+1);
        }
        
        for(int y = 0; y < 10; y++)
        {
            returnStr += "\n\n\n";
            char rowChar = (char) (y+65);
            returnStr += rowChar;
            for(int x = 0; x < 10; x++)
            {
                returnStr += "\t";
                returnStr += cells[x][y].getXCoord();
                returnStr += "," + cells[x][y].getYCoord();
            }
        }
        return returnStr;
    }

    /**
     * Variation of a toString that only returns the battleship form coordinates in a grid representation of the board
     * @return a string containing the battleship form coordinates in a grid representation of the board
     */
    public String toStringBShipCoords()
    {
        String returnStr = "";
        for(int i = 0; i < 10; i++)
        {
            returnStr += "\t";
            returnStr += (i+1);
        }
        
        for(int y = 0; y < 10; y++)
        {
            returnStr += "\n\n\n\n";
            char rowChar = (char) (y+65);
            returnStr += rowChar;
            for(int x = 0; x < 10; x++)
            {
                returnStr += "\t";
                returnStr += cells[x][y].getBShipCoords();
            }
        }
        return returnStr;
    }

    /**
     * Variation of a toString that only returns the states of the cells in a grid representation of the board
     * @return a string containing the states of the cells in a grid representation of the board
     */
    public String toStringStates()
    {
        String returnStr = "";
        for(int i = 0; i < 10; i++)
        {
            returnStr += "\t";
            returnStr += (i+1);
        }
        
        for(int y = 0; y < 10; y++)
        {
            returnStr += "\n\n\n";
            char rowChar = (char) (y+65);
            returnStr += rowChar;
            for(int x = 0; x < 10; x++)
            {
                String addStr = "";
                returnStr += "\t";
                switch(cells[x][y].getState())
                {
                    case Cell.HIT:
                        addStr = "X";
                        break;
                    case Cell.MISS:
                        addStr = "*";
                        break;
                    case Cell.UNTOUCHED:
                        addStr = "O";
                        break;
                    case Cell.SUNKEN_SHIP:
                        addStr = "#";
                        break;
                }
                
                returnStr += addStr;
            }
        }
        return returnStr;
    }

    /**
     * Represents the board with all cell information in a list form
     */
    public String toString()
    {
        String returnStr = "";
        for(int i = 0; i < 10; i++)
        {
            returnStr += "\t";
            returnStr += (i+1);
        }
        
        for(int y = 0; y < 10; y++)
        {
            returnStr += "\n\n\n\n\n\n";
            char rowChar = (char) (y+65);
            returnStr += rowChar;
            for(int x = 0; x < 10; x++)
            {
                returnStr += cells[x][y].toString();
                returnStr += "\n";
            }
        }
        return returnStr;
    }

    /**
     * Checks if this board is the same as another including all cell info
     * @param otherBoard board to compare this one to
     * @return true if they are equal
     */
    public boolean equals(Board otherBoard) throws InvalidCoordinateException
    {
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                if(!cells[x][y].equals(otherBoard.getCell(x,y)))
                {
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Helper method to check if the coordinates are from 0 to 9 inclusive
     * @param x x coordinate to check
     * @param y y coordinate to check
     * @return true if they are in bounds
     */
    private boolean checkBounds(int x, int y)
    {
        if(x < 0 || x > 9 || y < 0 || y > 9)
        {
            return false;
        }
        return true;
    }
}
