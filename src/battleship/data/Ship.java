package battleship.data;

import battleship.exceptions.InvalidCoordinateException;
import battleship.exceptions.InvalidShipSizeException;
import battleship.exceptions.InvalidShipPlacementException;


public class Ship
{
    /**unique ID that will be assigned to the next ship*/
    private static int nextID = 0;
    /**number of spaces this ship takes up*/
    private int size;
    /**array of cells that this ship is on*/
    private Cell[] cells;
    /**indicate whether or not the ship is afloat*/
    private boolean afloat;
    /**name of this ship*/
    private String name;
    /**unique ID for identifying ships*/
    private int id;

    /**
     * Default constructor. Sets size to 0 and afloat to false
     */
    public Ship()
    {
        size = 0;
        cells = new Cell[size];
        afloat = true;
        name = "";
        id = nextID;
        nextID++;
    }

    public Ship(int newSize)
    {
        if(!setSize(newSize))
        {
                size = 0;
        }
        cells = new Cell[size];
        afloat = true;
        name = "";
        id = nextID;
        nextID++;
    }

    /**
     * Partial constructor. Takes this size and afloat boolean
     * @param size number of spaces the ship takes up
     * @param afloat whether or not the ship is afloat
     */
    public Ship(int size, boolean afloat, String name)
    {
        if(size < 2 || size > 5)
        {
            this.size = 0;
            cells = new Cell[size];
            this.afloat = true;
            this.name = name;
        }
        else
        {
            this.size = size;
            cells = new Cell[size];
            this.afloat = afloat;
            this.name = name;
        }

        id = nextID;
        nextID++;
    }

    /**
     * Full constructor. size of ship and length of cell array must be equivalent
     * @param size number of spaces that this ship takes up. 2 to 5 inclusive
     * @param afloat whether or not the ship is floating
     * @param cells cells that this ship is on. Should be shallow copy with board cells
     */
    public Ship(int size, boolean afloat, String name, Cell[] cells) throws InvalidShipPlacementException
    {
        this.name = name;
        if(size < 2 || size > 5 || size != cells.length)
        {
            this.size = 0;
            this.cells = new Cell[size];
            this.afloat = true;
        }
        else
        {
            this.cells = new Cell[size];
            for(int i = 0; i < size; i++)
            {
                this.cells[i] = cells[i];
            }
            this.size = size;
            this.afloat = afloat;
            checkValidShipPlacement();
        }

        id = nextID;
        nextID++;
    }

    /**
     * Copy constructor
     * @param otherShip ship to make this one a copy of
     */
    public Ship(Ship otherShip)
    {
        if(otherShip == null)
        {
            size = 0;
            cells = new Cell[size];
            afloat = true;
            name = "";
        }
        else
        {
            size = otherShip.getSize();
            afloat = otherShip.isAfloat();
            name = otherShip.getName();
            cells = otherShip.getCells();
        }

        id = nextID;
        nextID++;
    }

    /**
     * Sets the size of the ship and resets the cell[] to the new size
     * @param size new size of the ship. Must be 2 to 5 inclusive
     * @return true if the size was set and cell[] was reset successfully
     */
    public boolean setSize(int size)
    {
        if(size < 2 || size > 5)
        {
            return false;
        }
        this.size = size;
        this.cells = new Cell[size];
        return true;
    }

    /***
     * Sets the array of cells that this ship is on
     * @param cells cells this ship is on. Must be the same length as the size of the ship
     * @return true if all cells were successfully set
     */
    public void setCells(Cell[] cells) throws InvalidShipSizeException, 
                                              InvalidShipPlacementException
    {
        if(cells.length != size)
        {
            throw new InvalidShipSizeException("Error: Invalid Ship Size." + 
                    " Ship: " + name + ". Invalid size: " + cells.length +
                    ". Expected size: " + size + ".");
        }
        for(int i = 0; i < size; i++)
        {
                this.cells[i] = cells[i];
        }
        checkValidShipPlacement();
    }

    public void setAfloat(boolean afloat)
    {
        this.afloat = afloat;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isAfloat()
    {
        return afloat;
    }

    public int getSize()
    {
        return size;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Gets a cell based on its index in the array
     * @param i index of the cell in the array
     * @return deep copy of a cell at that index. Null if the index does 
     * not have a cell
     */
    public Cell getCell(int i)
    {
        if(i >= size || i < 0)
        {
            return null;
        }
        return new Cell(cells[i]);
    }

    /**
     * Gets a cell based on its coordinates
     * @param x the x coordinate of the desired cell
     * @param y the y coordinate of the desired cell
     * @return deep copy of a cell that has the specified coordinates. Null 
     * if no such cell belongs to this ship
     */
    public Cell getCell(int x, int y) throws InvalidCoordinateException
    {
        for(int i = 0; i < size; i++)
        {
            if(cells[i].getXCoord() == x && cells[i].getYCoord() == y)
            {
                return new Cell(cells[i]);
            }
        }
        return null;
    }

    /**
     * Gets the entire array of cells for this ship
     * @return array of deep copies of the entire array of cells for this ship
     */
    public Cell[] getCells()
    {
        Cell[] returnCells = new Cell[size];
        for(int i = 0; i < size; i++)
        {
            returnCells[i] = this.getCell(i);
        }
        return returnCells;
    }

    public int getID()
    {
        return id;
    }

    /**
     * Determines if the specified cell (or an identical copy) is under 
     * this ship
     * @param cell the cell to be checked for this ship
     * @return true if this ship is on this cell
     */
    public boolean hasCell(Cell cell)
    {
        for(int i = 0; i < size; i++)
        {
            if(cells[i] == null)
            {
                continue;
            }
            else if(cells[i].equals(cell))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * determines if this ship is on a cell with the specified coordinates
     * @param coord the coordinate we are seeing if this ship is on
     * @return true if this ship is on that coordinate, false otherwise
     */
    public boolean hasCell(Coord coord)
    {
        for(int i = 0; i < size; i++)
        {
            if(cells[i] == null)
            {
                continue;
            }
            else if(cells[i].getCoords().equals(coord))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * If this ship is on a cell at the specified coords then it sets the 
     * state of the cell to hit.
     * Also checks if that was the last cell that wasn't hit in which case 
     * it sets the ship to sunk.
     * @param x x coordinate of the cell
     * @param y y coordinate of the cell
     * @return true if the cell was a part of the ship
     */
    public boolean hitCell(int x, int y)
    {
        boolean foundCellInShip = false;
        boolean hasRemainingUnhitCells = false;
        for(int i = 0; i < size; i++)
        {
            if(cells[i] == null)
            {
                continue;
            }
            if(cells[i].getXCoord() == x && cells[i].getYCoord() == y)
            {
                foundCellInShip = true;
                cells[i].setState(Cell.HIT);
            }
            if(cells[i].getState() == Cell.UNTOUCHED)
            {
                hasRemainingUnhitCells = true;
            }
        }

        if(!hasRemainingUnhitCells)
        {
            afloat = false;
            for(int i = 0; i < size; i++)
            {
                if(cells[i] == null)
                {
                    continue;
                }
                cells[i].setState(Cell.SUNKEN_SHIP);
            }
        }

        return foundCellInShip;
    }

    public String toString()
    {
        String returnStr = "";
        returnStr += "Name: " + name + "\nIs Afloat: " + afloat + "\nSize: " + size + "\n";
        returnStr += "Cells:\n";
        for(int i = 0; i < size; i++)
        {
            if(cells[i] == null)
            {
                returnStr += "\t" + i+1 + ": null";
            }
            else
            {
                returnStr += "\t" + i+1 + ": " + cells[i].toString();
            }
        }
        return returnStr;
    }

    public boolean equals(Ship otherShip) throws InvalidCoordinateException
    {
        if(name != otherShip.getName() || size != otherShip.getSize() || afloat != otherShip.isAfloat())
        {
            return false;
        }
        for(int i = 0; i < size; i++)
        {
            if(!cells[i].equals(otherShip.getCell(i)))
            {
                    return false;
            }
        }
        return true;
    }

    private void checkValidShipPlacement() throws InvalidShipPlacementException
    {
        //check if array is null
        if(cells == null)
        {
            String errorMsg = "Error: Invalid Ship Placement - cell array is null\nShip Name: " + name;
            throw new InvalidShipPlacementException(errorMsg);
        }

        //check if any cells in array are null
        for(int i = 0; i < size; i++)
        {
            if(cells[i] == null)
            {
                String errorMsg = "Error: Invalid Ship Placement - cell in array is null\nShip Name: " +
                                                        name + "\nCell index: " + i;
                throw new InvalidShipPlacementException(errorMsg);
            }
        }

        //check if cells are oriented vertically or horizontally
        boolean horizontal = true;
        boolean vertical = true;
        int lastX = cells[0].getXCoord();
        int lastY = cells[0].getYCoord();

        for(int i = 1; i < size; i++)
        {
            if(cells[i].getXCoord() != lastX)
            {
                vertical = false;
            }

            if(cells[i].getYCoord() != lastY)
            {
                horizontal = false;
            }

            lastX = cells[i].getXCoord();
            lastY = cells[i].getYCoord();
        }

        if((!vertical && !horizontal) || (vertical && horizontal))
        {
            String errorMsg = "Error: Invalid Ship Placement - Cells are neither vertical nor horizontal" +
                                                    "\nShip Name: " + name;
            throw new InvalidShipPlacementException(errorMsg);
        }

        //check if any cells are duplicates
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                if(j == i)
                {
                    continue;
                }

                if(cells[i].equals(cells[j]))
                {
                    String errorMsg = "Error: Invalid Ship Placement - Cell duplicates found\n" +
                                                            "Ship Name: " + name + "\nDuplicate Indeces: " + i + ", " + j;
                    throw new InvalidShipPlacementException(errorMsg);
                }
            }
        }

        //check if cells are consecutive
        if(vertical)
        {
            lastY = cells[0].getYCoord();
            for(int i = 1; i < size; i++)
            {
                if( (lastY != (cells[i].getYCoord() - 1))
                                && (lastY != cells[i].getYCoord() + 1) )
                {
                    String errorMsg = "Error: Invalid Ship Placement - Cells are not consecutive\n" +
                                                            "Ship Name: " + name + "\nOrientation: vertical";
                    throw new InvalidShipPlacementException(errorMsg);
                }

                lastY = cells[i].getYCoord();
            }
        }
        else //horizontal
        {
            lastX = cells[0].getXCoord();
            for(int i = 1; i < size; i++)
            {
                if( (lastX != (cells[i].getXCoord() - 1))
                                && (lastX != cells[i].getXCoord() + 1) )
                {
                    String errorMsg = "Error: Invalid Ship Placement - Cells are not consecutive\n" +
                                                            "Ship Name: " + name + "\nOrientation: horizontal";
                    throw new InvalidShipPlacementException(errorMsg);
                }

                lastX = cells[i].getXCoord();
            }
        }
    }
}
