package battleship.data;

import battleship.exceptions.InvalidShipSizeException;
import battleship.exceptions.InvalidShipPlacementException;
import battleship.exceptions.InvalidCoordinateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
Programmer: Steven Siddall

This class represents a single player in a battleship game with their own name,
board, ships, etc. A player's board is the board that records all previous
shots that THIS PLAYER has fired AT THE OTHER PLAYER. This player object's ships,
however, are this player's own ships in the game.

Ship Identifiers:
0: Carrier. Takes up 5 cells
1: Battleship. Takes up 4 cells
2: Cruiser. Takes up 3 cells
3: Submarine. Takes up 3 cells
4: Patrol Boat. Takes up 2 cells
*/

public class Player
{
    /**the name of the player*/
    private String name;
    
    /**board specific to this player's ships and shots*/
    private Board board;
    
    /**If the player is a human this is the computers recommendation for
    where to shoot next if the programmer chooses to implement recommendations.
    If this player is a computer then this is where the computer will shoot
    next*/
    private Coord nextRecommendation;
    
    /**keeps track of the longest opponent ship still afloat*/
    private int maxOpponentShipLength;
    
    /**used in salvo mode. represents the number of shots left for this player
    during this turn.*/
    private int remainingShots;

    /**this player's ships*/
    private Ship patrolBoat;
    private Ship submarine;
    private Ship cruiser;
    private Ship battleship;
    private Ship carrier;

    /**array containing references to each ship*/
    private Ship[] ships;

    //constants for point calculation formulas:
    //***MOST OF THESE ARE NOT CURRENTLY BEING USED***

    /**denominator value for 'a' term of point formula for hit cells*/
    private static double A_DENOM = 1.9;
    /**denominator value for 'b' term of point formula for hit cells*/
    private static double B_DENOM = 10;
    /**value for 'c' term of point formula for hit cells*/
    private static double BASE_VALUE_HIT = 20.0;
    /**base value for point formula for untouched cells*/
    private static double BASE_VALUE_UNTOUCHED = 5;
    /**multiplier for diminishing point value of untouched cells*/
    private static double DIMINISHING_MULTIPLIER = 1;

    //constants for identifying ships
    private static final int CARRIER = 0;
    private static final int BATTLESHIP = 1;
    private static final int CRUISER = 2;
    private static final int SUBMARINE = 3;
    private static final int PATROL_BOAT = 4;


    /**
     * Default constructor. Warning: Each ship's cells will not be initialized
     * @throws InvalidCoordinateException
     */
    public Player()
    {
        name = "No Name";
        board = new Board();
        nextRecommendation = new Coord();
        maxOpponentShipLength = 0;

        patrolBoat = new Ship(2,true,"Patrol Boat");
        submarine = new Ship(3,true,"Submarine");
        cruiser = new Ship(3,true,"Cruiser");
        battleship = new Ship(4,true,"Battleship");
        carrier = new Ship(5,true,"Carrier");

        ships = new Ship[5];
        ships[0] = patrolBoat;
        ships[1] = submarine;
        ships[2] = cruiser;
        ships[3] = battleship;
        ships[4] = carrier;

        updateRecommendation();
    }

    /**
     * constructor that takes a name for the player. Warning: Each ship's cells will not be initialized
     * @param name the name of the player
     * @throws InvalidCoordinateException
     */
    public Player(String name)
    {
        if(name == null)
        {
            this.name = "No Name";
        }
        else
        {
            this.name = name;
        }
        board = new Board();
        nextRecommendation = new Coord();
        maxOpponentShipLength = 5;
        remainingShots = 1;

        patrolBoat = new Ship(2,true,"Patrol Boat");
        submarine = new Ship(3,true,"Submarine");
        cruiser = new Ship(3,true,"Cruiser");
        battleship = new Ship(4,true,"Battleship");
        carrier = new Ship(5,true,"Carrier");

        ships = new Ship[5];
        ships[0] = patrolBoat;
        ships[1] = submarine;
        ships[2] = cruiser;
        ships[3] = battleship;
        ships[4] = carrier;

        updateRecommendation();
    }

    /**
     * Constructor that takes cell arrays for each ship and a player name
     * @param name name of the player
     * @param patrolBoatCells cell array representing the patrol boat's cells
     * @param submarineCells cell array representing the sub's cells
     * @param cruiserCells cell array representing the cruiser's cells
     * @param battleshipCells cell array representing the battleship's cells
     * @param carrierCells cell array representing the carrier's cells
     * @throws InvalidShipSizeException thrown if any of the cell arrays are the incorrect size
     * @throws InvalidShipPlacementException thrown if any of the ships cells are in invalid locations
     */
    public Player(String name, Cell[] patrolBoatCells,
                               Cell[] submarineCells,
                               Cell[] cruiserCells,
                               Cell[] battleshipCells,
                               Cell[] carrierCells) throws InvalidShipSizeException,
                                                           InvalidShipPlacementException
    {
        if(name == null)
        {
            this.name = "No Name";
        }
        else
        {
            this.name = name;
        }
        board = new Board();
        nextRecommendation = new Coord();
        maxOpponentShipLength = 5;
        remainingShots = 1;

        patrolBoat = new Ship(2,true,"Patrol Boat");
        submarine = new Ship(3,true,"Submarine");
        cruiser = new Ship(3,true,"Cruiser");
        battleship = new Ship(4,true,"Battleship");
        carrier = new Ship(5,true,"Carrier");

        initializeShips(patrolBoatCells, submarineCells, cruiserCells, battleshipCells, carrierCells);
        
        ships = new Ship[5];
        ships[0] = patrolBoat;
        ships[1] = submarine;
        ships[2] = cruiser;
        ships[3] = battleship;
        ships[4] = carrier;
        
        updateRecommendation();
    }

    /**
     * Master method for initializing all of the ships' coordinates
     * @param patrolBoatCells cells that the patrol boat is on
     * @param submarineCells cells that the submarine is on
     * @param cruiserCells cells that the cruiser is on
     * @param battleshipCells cells that the battleship is on
     * @param carrierCells cells that the carrier is on
     * @throws InvalidShipSizeException throws if any of the cell arrays are an incorrect size
     */
    public void initializeShips(Cell[] patrolBoatCells,
                                Cell[] submarineCells,
                                Cell[] cruiserCells,
                                Cell[] battleshipCells,
                                Cell[] carrierCells) throws InvalidShipSizeException,
                                                            InvalidShipPlacementException
    {
        this.initializePatrolBoat(patrolBoatCells);
        this.initializeSubmarine(submarineCells);
        this.initializeCruiser(cruiserCells);
        this.initializeBattleship(battleshipCells);
        this.initializeCarrier(carrierCells);
    }

    /**
     * Method for individually setting the cells of the patrol boat
     * @param cells cells that the patrol boat is on
     * @throws InvalidShipSizeException throws if any of the cell arrays are an incorrect size
     */
    public void initializePatrolBoat(Cell[] cells) throws InvalidShipSizeException, InvalidShipPlacementException
    {
        if(cells == null)
        {
            patrolBoat = null;
            ships[0] = null;
            return;
        }
        //check if these cells overlap with the other ships
        for(int i = 0; i < cells.length; i++)
        {
            if(carrier != null && carrier.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with carrier");
            }
            else if(battleship != null && battleship.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with battleship");
            }
            else if(cruiser != null && cruiser.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with cruiser");
            }
            else if(submarine != null && submarine.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with submarine");
            }
        }
        patrolBoat = new Ship(2,true,"Patrol Boat");
        patrolBoat.setCells(cells);
        ships[0] = patrolBoat;
    }

    /**
     * Method for individually setting the cells of the submarine
     * @param cells cells that the submarine is on
     * @throws InvalidShipSizeException throws if any of the cell arrays are an incorrect size
     */
    public void initializeSubmarine(Cell[] cells) throws InvalidShipSizeException, InvalidShipPlacementException
    {
        if(cells == null)
        {
            submarine = null;
            ships[1] = null;
            return;
        }
        
        //check if these cells overlap with the other ships
        for(int i = 0; i < cells.length; i++)
        {
            if(carrier != null && carrier.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with carrier");
            }
            else if(battleship != null && battleship.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with battleship");
            }
            else if(cruiser != null && cruiser.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with cruiser");
            }
            else if(patrolBoat != null && patrolBoat.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with patrol boat");
            }
        }
        submarine = new Ship(3,true,"Submarine");
        submarine.setCells(cells);
        ships[1] = submarine;
    }

    /**
     * Method for individually setting the cells of the cruiser
     * @param cells cells that the cruiser is on
     * @throws InvalidShipSizeException throws if any of the cell arrays are an incorrect size
     */
    public void initializeCruiser(Cell[] cells) throws InvalidShipSizeException, InvalidShipPlacementException
    {
        if(cells == null)
        {
            cruiser = null;
            ships[2] = null;
            return;
        }
        //check if these cells overlap with the other ships
        for(int i = 0; i < cells.length; i++)
        {
            if(carrier != null && carrier.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with carrier");
            }
            else if(battleship != null && battleship.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with battleship");
            }
            else if(patrolBoat != null && patrolBoat.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with patrol boat");
            }
            else if(submarine != null && submarine.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with submarine");
            }
        }
        cruiser = new Ship(3,true,"Cruiser");
        cruiser.setCells(cells);
        ships[2] = cruiser;
    }

    /**
     * Method for individually setting the cells of the battleship
     * @param cells cells that the battleship is on
     * @throws InvalidShipSizeException throws if any of the cell arrays are an incorrect size
     */
    public void initializeBattleship(Cell[] cells) throws InvalidShipSizeException, InvalidShipPlacementException
    {
        if(cells == null)
        {
            battleship = null;
            ships[3] = null;
            return;
        }
        //check if these cells overlap with the other ships
        for(int i = 0; i < cells.length; i++)
        {
            if(carrier != null && carrier.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with carrier");
            }
            else if(patrolBoat != null && patrolBoat.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with patrol boat");
            }
            else if(cruiser != null && cruiser.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with cruiser");
            }
            else if(submarine != null && submarine.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with submarine");
            }
        }
        battleship = new Ship(4,true,"Battleship");
        battleship.setCells(cells);
        ships[3] = battleship;
    }

    /**
     * Method for individually setting the cells of the carrier
     * @param cells cells that the carrier is on
     * @throws InvalidShipSizeException throws if any of the cell arrays are an incorrect size
     */
    public void initializeCarrier(Cell[] cells) throws InvalidShipSizeException, InvalidShipPlacementException
    {
        if(cells == null)
        {
            carrier = null;
            ships[4] = null;
            return;
        }
        //check if these cells overlap with the other ships
        for(int i = 0; i < cells.length; i++)
        {
            if(patrolBoat != null && patrolBoat.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with patrol boat");
            }
            else if(battleship != null && battleship.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with battleship");
            }
            else if(cruiser != null && cruiser.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with cruiser");
            }
            else if(submarine != null && submarine.hasCell(cells[i].getCoords()))
            {
                throw new InvalidShipPlacementException("Invalid Ship Placement: patrol boat overlaps with submarine");
            }
        }
        carrier = new Ship(5,true,"Carrier");
        carrier.setCells(cells);
        ships[4] = carrier;
    }

    /**
     * setter for the player's name
     * @param newName the name of the player
     */
    public void setName(String newName)
    {
        if(newName == null)
        {
            name = "";
        }
        else
        {
            name = newName;
        }
    }

    /**
     * getter for the player's name
     * @return the name of the player
     */
    public String getName()
    {
        return name;
    }
    
    public Board getBoard()
    {
        return new Board(board);
    }

    /**
     * setter for the size of the largest opponent ship that is still afloat
     * @param newLength the number of cells that the largest enemy ship occupies
     */
    public void setMaxOpponentShipLength(int newLength)
    {
        if(newLength < 0)
        {
            maxOpponentShipLength = 0;
        }
        else
        {
            maxOpponentShipLength = newLength;
        }
    }

    /**
     * figures out the size of the largest ship that this player still has afloat
     * @return the number of cells that this player's largest floating ship occupies
     */
    public int getMaxShipLength()
    {
        int maxLength = 0;
        for(int i = 0; i < 5; i++)
        {
            if(ships[i].isAfloat())
            {
                maxLength = ships[i].getSize();
            }
        }
        return maxLength;
    }

    /**
     * Checks point values for each cell on the board and chooses the next recommendation
     * based on the one with the highest value. If there are several tied for the highest
     * value, it chooses one of them randomly
     */
    public void updateRecommendation()
    {
        recalculateTotalPoints();

        double highestValue = 0;
        ArrayList<Coord> tiedValues = new ArrayList<Coord>();

        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                try
                {
                    if(board.getCell(x, y).getValue() > highestValue)
                    {
                        highestValue = board.getCell(x, y).getValue();
                        tiedValues.clear();
                        tiedValues.add(new Coord(x,y));
                    }
                    else if(board.getCell(x, y).getValue() == highestValue)
                    {
                        tiedValues.add(new Coord(x,y));
                    }
                }
                catch(InvalidCoordinateException e) //should never happen since coord values are hardcoded
                {
                    System.out.println("Coordinate Error in update recommendation method.");
                    System.exit(1);
                }
            }
        }

        //randomly choose a recommendation from the list of tied highest cells
        int numTies = tiedValues.size();
        Random rng = new Random();
        int randomIndex = rng.nextInt(numTies);
        nextRecommendation = new Coord(tiedValues.get(randomIndex));
    }

    /**
     * gets the next coordinate that the computer recommends to shoot at.
     * updateRecommendation should be called before this method
     * @return the recommended coordinate to shoot at
     */
    public Coord getRecommendation()
    {
        return nextRecommendation;
    }

    /**
     * checks if all of this player's ships have been sunk
     * @return true if all ships have been sunk, false otherwise
     */
    public boolean allShipsSunk()
    {
        for(int i = 0; i < 5; i++)
        {
            if(ships[i].isAfloat())
            {
                return false;
            }
        }

        return true;
    }

    /***
     * checks if this player has a ship based on its unique ID
     * @param id the ID number of the ship in question
     * @return true if this player has a ship with that ID, false otherwise
     */
    public boolean hasShip(int id)
    {
        for(int i = 0; i < 5; i++)
        {
            if(ships[i].getID() == id)
            {
                return true;
            }
        }

        return false;
    }

    /**
    Determines if the player has a ship at a specific coordinate
    @param coord the location where we are checking for a ship
    @return the ship at the specified coordinate. null if no ship was found
    */
    public Ship getShipAt(Coord coord)
    {
        for(int i = 0; i < ships.length; i++)
        {
            if(ships[i] != null && ships[i].hasCell(coord))
            {
                return ships[i];
            }
        }
        
        return null;
    }
    
    /**
     * returns a deep copy of the ship with the given ID number
     * @param id the ID of the ship to return
     * @return the ship with the given ID, null if this player doesn't have that ship
     */
    public Ship getShip(int id)
    {
        for(int i = 0; i < 5; i++)
        {
            if(ships[i].getID() == id)
            {
                return new Ship(ships[i]);
            }
        }

            return null;
    }
    
    /**
    gets an array of deep copies of all ships
    @return an array of deep copies of all ships
    */
    public Ship[] getAllShips()
    {
        Ship[] shipsCopy = {getCarrier(),
                            getBattleship(),
                            getCruiser(),
                            getSubmarine(),
                            getPatrolBoat()};
        return shipsCopy;
    }

    public Ship getCarrier()
    {
        return new Ship(carrier);
    }

    public Ship getBattleship()
    {
        return new Ship(battleship);
    }

    public Ship getCruiser()
    {
        return new Ship(cruiser);
    }

    public Ship getSubmarine()
    {
        return new Ship(submarine);
    }

    public Ship getPatrolBoat()
    {
        return new Ship(patrolBoat);
    }
    
    public int getNumShipsAfloat()
    {
        int numShipsAfloat = 0;
        
        if(carrier.isAfloat())
        {
            numShipsAfloat++;
        }   
        if(battleship.isAfloat())
        {
            numShipsAfloat++;
        }
        if(cruiser.isAfloat())
        {
            numShipsAfloat++;
        }
        if(submarine.isAfloat())
        {
            numShipsAfloat++;
        }
        if(patrolBoat.isAfloat())
        {
            numShipsAfloat++;
        }
        
        return numShipsAfloat;
    }

    /**
     * checks if a shot at the given coordinates hits any of this player's ships
     * and builds a FireResult object reflecting the results
     * @param coords the coordinates that are being shot at
     * @return a FireResult object representing the result of the shot
     * @throws InvalidCoordinateException thrown if the given coordinates are not valid
     */
    public FireResult fireAt(Coord coords, Player shooter) throws InvalidCoordinateException
    {
        FireResult result = new FireResult();
        result.setTarget(new Coord(coords));
        Cell targetCell = board.getCell(coords.getX(), coords.getY());
        shooter.setNumShots(shooter.getNumShots() - 1);
        
        //check if shot it a hit
        for(int i = 0; i < 5; i++)
        {
            if(ships[i].hitCell(coords.getX(), coords.getY()))
            {
                if(ships[i].isAfloat())
                {
                    result.setResult(FireResult.HIT);
                }
                else
                {
                    result.setResult(FireResult.SINK);
                    result.setSunkShip(new Ship(ships[i]));
                    ships[i].setAfloat(false);
                }
                shooter.processFireResult(result);
                return result;
            }
        }

        //must have been a miss
        result.setResult(FireResult.MISS);
        shooter.processFireResult(result);
        shooter.setMaxOpponentShipLength(this.getMaxShipLength());
        return result;
    }

    /**
     * After firing at another player, this method is called with the returned fire result
     * from the other player. This method changes the board to reflect the results of that shot
     * @param result
     */
    private void processFireResult(FireResult result)
    {
        int status = result.getResult();
        switch(status)
        {
        case FireResult.MISS: 	
            board.setCellState(result.getTargetX(), result.getTargetY(), Cell.MISS);
            break;
        case FireResult.HIT:	
            board.setCellState(result.getTargetX(), result.getTargetY(), Cell.HIT);
            break;
        case FireResult.SINK:	
            Ship sunkShip = result.getSunkShip();
            for(Cell cell: sunkShip.getCells())
            {
                board.setCellState(cell.getXCoord(), cell.getYCoord(), Cell.SUNKEN_SHIP);
            }
            break;
        }

    }
    
    /**
    sets the number of remaining shots for this player
    @param newNumShots the number of shots this player will have. must be non-negative
    */
    public void setNumShots(int newNumShots)
    {
        if(newNumShots >= 0)
        {
            remainingShots = newNumShots;
        }
        else
        {
            remainingShots = 0;
        }
    }
    
    /**
    replenishes the number of shots for this player based on the game mode
    @param gameMode if MODE_SALVO: player gets as many shots as they have ships afloat. 
                    if MDOE_CLASSIC: player always gets 1
    */
    public void replenishShots(int gameMode)
    {
        if(gameMode == 0)
        {
            this.setNumShots(1);
        }
        else if(gameMode == 1)
        {
            this.setNumShots(getNumShipsAfloat());
        }
    }
    
    /**
    gets the number of shots remaining for this player this turn
    @return the number of shots this player has for this turn
    */
    public int getNumShots()
    {
        return remainingShots;
    }

    /**
     * Calls the Board toStringValues for this player's board
     * @return a string representing the point values of every cell on the board
     */
    public String boardToStringValues()
    {
        return board.toStringValues();
    }

    /**
     * calls the Board toStringIntCoords for this player's board
     * @return a string representing the integer coordinates of every cell on the board
     */
    public String boardToStringIntCoords()
    {
        return board.toStringIntCoords();
    }

    /**
     * calls the Board toStringBShipCooords for this player's board
     * @return a string representing the battleship coordinates of every cell on the board
     */
    public String boardToStringBShipCoords()
    {
        return board.toStringBShipCoords();
    }

    /**
     * calls the Board toStringStates for this player's board
     * @return a string representing the states of each cell on the board
     */
    public String boardToStringStates()
    {
        return board.toStringStates();
    }

    /**
     * calls the Board toString for this player's board
     * @return a string representing the board
     */
    public String boardToString()
    {
        return board.toString();
    }

    public String boardToStringShips()
    {
        String returnStr = "";

        for(int y = 0; y < 10; y++)
        {
            returnStr += "\n\n\n";
            for(int x = 0; x < 10; x++)
            {
                boolean hasShip = false;
                for(int i = 0; i < 5; i++)
                {
                    Coord coord = null;
                    try
                    {
                        coord = new Coord(x,y);
                    }
                    catch(InvalidCoordinateException e)
                    {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }

                    if(ships[i].hasCell(coord))
                    {
                        hasShip = true;
                        returnStr += "X\t";
                        break;
                    }
                }

                if(!hasShip)
                {
                    returnStr += "O\t";
                }
            }
        }

        return returnStr;
    }

    /**
     * Generates a random arrangement of ships for this player
     */
    public void generateRandomShipPlacement()
    {
        //make sure all ships are reset to null
        carrier = null;
        battleship = null;
        cruiser = null;
        submarine = null;
        patrolBoat = null;

        placeShip(CARRIER, 5);
        ships[4] = carrier;
        carrier.setName("Carrier");

        placeShip(BATTLESHIP, 4);
        ships[3] = battleship;
        battleship.setName("Battleship");

        placeShip(CRUISER, 3);
        ships[2] = cruiser;
        cruiser.setName("Cruiser");

        placeShip(SUBMARINE, 3);
        ships[1] = submarine;
        submarine.setName("Submarine");

        placeShip(PATROL_BOAT, 2);
        ships[0] = patrolBoat;
        patrolBoat.setName("Patrol Boat");
    }

    private void placeShip(int ship, int shipSize)
    {
        Random rng = new Random();
        boolean placedSuccessfully = false;
        Coord currentCoordinate = new Coord();

        //we repeat this loop every time we haven't found a valid location for the ship
        while(!placedSuccessfully)
        {
            try
            {
                currentCoordinate.setX(rng.nextInt(10));
                currentCoordinate.setY(rng.nextInt(10));
            }
            catch(InvalidCoordinateException e)
            {
                System.out.println("Error: random board generated invalid coordinate");
                System.exit(1);
            }

            //we want to try orienting the ship up, down, left, and right in a random order
            int up = rng.nextInt();
            int down = rng.nextInt();
            int left = rng.nextInt();
            int right = rng.nextInt();
            int[] order = {up, down, left, right};
            Arrays.sort(order);

            boolean triedUp = false;
            boolean triedDown = false;
            boolean triedLeft = false;
            boolean triedRight = false;

            //call the methods in random order
            for(int i = 0; i < 4; i++)
            {
                if(order[i] == up && !triedUp)
                {
                    triedUp = true;
                    if(tryOrientationUp(ship, shipSize, currentCoordinate))
                    {
                            //placed successfully
                            placedSuccessfully = true;
                            break;
                    }
                }
                else if(order[i] == down && !triedDown)
                {
                    triedDown = true;
                    if(tryOrientationDown(ship, shipSize, currentCoordinate))
                    {
                            placedSuccessfully = true;
                            break;
                    }
                }
                else if(order[i] == left && !triedLeft)
                {
                    triedLeft = true;
                    if(tryOrientationLeft(ship, shipSize, currentCoordinate))
                    {
                            placedSuccessfully = true;
                            break;
                    }
                }
                else if(order[i] == right && !triedRight)
                {
                    triedRight = true;
                    if(tryOrientationRight(ship, shipSize, currentCoordinate))
                    {
                            placedSuccessfully = true;
                            break;
                    }
                }
            }
        }
    }

    private boolean tryOrientationUp(int ship, int shipSize, Coord coord)
    {
        for(int i = 0; i < shipSize; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                Coord currentCoord = null;
                try
                {
                    currentCoord = new Coord(coord.getX(), coord.getY() - i);
                }
                catch(InvalidCoordinateException e)
                {
                    //went out of bounds
                    return false;
                }

                if(ships[j] == null)
                {
                    continue;
                }
                else if(ships[j].hasCell(currentCoord))
                {
                    return false;
                }
            }
        }

        //didn't go out of bounds and didn't intersect with any other ships so we place the ship
        Cell[] newShipCells = new Cell[shipSize];
        Ship thisShip = new Ship(shipSize);

        for(int i = 0; i < shipSize; i++)
        {
            Cell nextCell = null;
            try
            {
                nextCell = board.getCell(coord.getX(), coord.getY() - i);
            }
            catch(InvalidCoordinateException e)
            {
                System.out.println("Error: tryOrientationUp generated an invalid coordinate.");
                System.out.println(e.getMessage());
                System.exit(1);
            }
            newShipCells[i] = new Cell(nextCell);
        }

        try
        {
            thisShip.setCells(newShipCells);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        switch(ship)
        {
        case CARRIER:		
            carrier = new Ship(thisShip);
            break;
        case BATTLESHIP:	
            battleship = new Ship(thisShip);
            break;
        case CRUISER:		
            cruiser = new Ship(thisShip);
            break;
        case SUBMARINE:		
            submarine = new Ship(thisShip);
            break;
        case PATROL_BOAT:	
            patrolBoat = new Ship(thisShip);
            break;
        }

        return true;
    }

    private boolean tryOrientationDown(int ship, int shipSize, Coord coord)
    {
        for(int i = 0; i < shipSize; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                Coord currentCoord = null;
                try
                {
                    currentCoord = new Coord(coord.getX(), coord.getY() + i);
                }
                catch(InvalidCoordinateException e)
                {
                    //went out of bounds
                    return false;
                }

                if(ships[j] == null)
                {
                    continue;
                }
                else if(ships[j].hasCell(currentCoord))
                {
                    return false;
                }
            }
        }

        //didn't go out of bounds and didn't intersect with any other ships so we place the ship
        Cell[] newShipCells = new Cell[shipSize];
        Ship thisShip = new Ship(shipSize);
        for(int i = 0; i < shipSize; i++)
        {
            Cell nextCell = null;
            try
            {
                nextCell = board.getCell(coord.getX(), coord.getY() + i);
            }
            catch(InvalidCoordinateException e)
            {
                System.out.println("Error: tryOrientationDown generated an invalid coordinate.");
                System.out.println(e.getMessage());
                System.exit(1);
            }
            newShipCells[i] = new Cell(nextCell);
        }

        try
        {
            thisShip.setCells(newShipCells);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }


        switch(ship)
        {
        case CARRIER:		
            carrier = new Ship(thisShip);
            break;
        case BATTLESHIP:	
            battleship = new Ship(thisShip);
            break;
        case CRUISER:		
            cruiser = new Ship(thisShip);
            break;
        case SUBMARINE:		
            submarine = new Ship(thisShip);
            break;
        case PATROL_BOAT:	
            patrolBoat = new Ship(thisShip);
            break;
        }

        return true;
    }

    private boolean tryOrientationLeft(int ship, int shipSize, Coord coord)
    {
        for(int i = 0; i < shipSize; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                Coord currentCoord = null;
                try
                {
                    currentCoord = new Coord(coord.getX() - i, coord.getY());
                }
                catch(InvalidCoordinateException e)
                {
                    //went out of bounds
                    return false;
                }

                if(ships[j] == null)
                {
                    continue;
                }
                else if(ships[j].hasCell(currentCoord))
                {
                    return false;
                }
            }
        }

        //didn't go out of bounds and didn't intersect with any other ships so we place the ship
        Cell[] newShipCells = new Cell[shipSize];
        Ship thisShip = new Ship(shipSize);
        for(int i = 0; i < shipSize; i++)
        {
            Cell nextCell = null;
            try
            {
                nextCell = board.getCell(coord.getX() - i, coord.getY());
            }
            catch(InvalidCoordinateException e)
            {
                System.out.println("Error: tryOrientationLeft generated an invalid coordinate.");
                System.out.println(e.getMessage());
                System.exit(1);
            }
            newShipCells[i] = new Cell(nextCell);
        }

        try
        {
            thisShip.setCells(newShipCells);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }


        switch(ship)
        {
        case CARRIER:		
            carrier = new Ship(thisShip);
            break;
        case BATTLESHIP:	
            battleship = new Ship(thisShip);
            break;
        case CRUISER:		
            cruiser = new Ship(thisShip);
            break;
        case SUBMARINE:		
            submarine = new Ship(thisShip);
            break;
        case PATROL_BOAT:	
            patrolBoat = new Ship(thisShip);
            break;
        }

        return true;
    }

    private boolean tryOrientationRight(int ship, int shipSize, Coord coord)
    {
        for(int i = 0; i < shipSize; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                Coord currentCoord = null;
                try
                {
                    currentCoord = new Coord(coord.getX() + i, coord.getY());
                }
                catch(InvalidCoordinateException e)
                {
                    //went out of bounds
                    return false;
                }

                if(ships[j] == null)
                {
                    continue;
                }
                else if(ships[j].hasCell(currentCoord))
                {
                    return false;
                }
            }
        }

        //didn't go out of bounds and didn't intersect with any other ships so we place the ship
        Cell[] newShipCells = new Cell[shipSize];
        Ship thisShip = new Ship(shipSize);
        for(int i = 0; i < shipSize; i++)
        {
            Cell nextCell = null;
            try
            {
                nextCell = board.getCell(coord.getX() + i, coord.getY());
            }
            catch(InvalidCoordinateException e)
            {
                System.out.println("Error: tryOrientationRight generated an invalid coordinate.");
                System.out.println(e.getMessage());
                System.exit(1);
            }
            newShipCells[i] = new Cell(nextCell);
        }

        try
        {
            thisShip.setCells(newShipCells);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        switch(ship)
        {
        case CARRIER:		
            carrier = new Ship(thisShip);
            break;
        case BATTLESHIP:	
            battleship = new Ship(thisShip);
            break;
        case CRUISER:		
            cruiser = new Ship(thisShip);
            break;
        case SUBMARINE:		
            submarine = new Ship(thisShip);
            break;
        case PATROL_BOAT:	
            patrolBoat = new Ship(thisShip);
            break;
        }

        return true;
    }

    /**
     * helper method to calculate the point values for every cell on the board
     */
    private void recalculateTotalPoints()
    {
        if(allShipsSunk())
        {
            nextRecommendation = new Coord();
            return;
        }

        //recalculate points for each cell in the board
        for(int y = 0; y < 10; y++)
        {
            for(int x = 0; x < 10; x++)
            {
                double newPoints = 0;
                try
                {
                    newPoints += calcPointsUp(board.getCell(x, y));
                    newPoints += calcPointsDown(board.getCell(x, y));
                    newPoints += calcPointsLeft(board.getCell(x, y));
                    newPoints += calcPointsRight(board.getCell(x, y));
                }
                catch(InvalidCoordinateException e) //should never happen since coordinate values are hardcoded
                {
                    System.out.println("Coordinate Error in recalculate method");
                    System.exit(1);
                }
                board.setCellValue(x, y, newPoints);
            }
        }
    }

    /**
     * this method will calculate the current cell's point value only for the adjacent cells to the north.
     * calcPointsDown, calcPointsLeft, and calcPointsRight work almost identically
     * @param currentCell the cell that we are assigning a point value to
     * @return the point value for the adjacent cells to the north
     */
    private double calcPointsUp(Cell cell) throws InvalidCoordinateException
    {
        double upPoints = 0; //running total to return
        int maxLength = maxOpponentShipLength - 1; //decremented to account for counter starting at 0
        int counter = 0; //for making sure we don't go over the max length

        //only untouched cells can have point values
        if(cell.getState() != Cell.UNTOUCHED)
        {
            return 0;
        }

        //loop for checking adjacent cells above currentCell
        //y is decremented b/c y = 0 is the top row of the board
        //loop's condition checks if we go out of bounds of the board
        //more specific break conditions are checked inside the loop
        for(int y = cell.getYCoord(); y >= 0; y--)
        {
            Cell currentCell = board.getCell(cell.getXCoord(), y);
            int currentState = currentCell.getState();

            //make sure we don't check past the necessary number of cells
            if(counter > maxLength)
            {
                break;
            }
            //make sure we stop if we encounter a sunken ship or missed shot
            else if(currentState == Cell.MISS || currentState == Cell.SUNKEN_SHIP)
            {
                break;
            }
            //add a small amount of points for an untouched adjacent cell
            else if(currentState == Cell.UNTOUCHED)
            {
                upPoints += BASE_VALUE_UNTOUCHED - counter*DIMINISHING_MULTIPLIER;
                counter++;
            }
            //add a large amount of points for an adjacent hit
            // uses formula: -x^2/a - x/b + c
            // where x = counter, a = A_DENOM, b = B_DENOM, c = BASE_VALUE
            else if(currentState == Cell.HIT)
            {
                //upPoints += -(counter*counter)/A_DENOM - counter/B_DENOM + BASE_VALUE_HIT;
                upPoints += 30 - (5*counter);
                counter++;
            }
        }

        return upPoints;
    }

    /**
     * this method will calculate the current cell's point value only for the adjacent cells to the south.
     * @param currentCell the cell that we are assigning a point value to
     * @return the point value for the adjacent cells to the south
     */
    private double calcPointsDown(Cell cell) throws InvalidCoordinateException
    {
        double downPoints = 0;
        int maxLength = maxOpponentShipLength - 1;
        int counter = 0;

        if(cell.getState() != Cell.UNTOUCHED)
        {
            return 0;
        }

        for(int y = cell.getYCoord(); y <= 9; y++)
        {
            Cell currentCell = board.getCell(cell.getXCoord(), y);
            int currentState = currentCell.getState();

            //make sure we don't check past the necessary number of cells
            if(counter > maxLength)
            {
                break;
            }
            //make sure we stop if we encounter a sunken ship or missed shot
            else if(currentState == Cell.MISS || currentState == Cell.SUNKEN_SHIP)
            {
                break;
            }
            //add a small amount of points for an untouched adjacent cell
            else if(currentState == Cell.UNTOUCHED)
            {
                downPoints += BASE_VALUE_UNTOUCHED - counter*DIMINISHING_MULTIPLIER;
                counter++;
            }
            //add a large amount of points for an adjacent hit
            // uses formula: -x^2/a - x/b + c
            // where x = counter, a = A_DENOM, b = B_DENOM, c = BASE_VALUE
            else if(currentState == Cell.HIT)
            {
                //downPoints += -(counter*counter)/A_DENOM - counter/B_DENOM + BASE_VALUE_HIT;
                downPoints += 30 - (5*counter);
                counter++;
            }
        }

        return downPoints;
    }

    /**
     * this method will calculate the current cell's point value only for the adjacent cells to the west.
     * @param currentCell the cell that we are assigning a point value to
     * @return the point value for the adjacent cells to the west
     */
    private double calcPointsLeft(Cell cell) throws InvalidCoordinateException
    {
        double leftPoints = 0;
        int maxLength = maxOpponentShipLength - 1;
        int counter = 0;

        if(cell.getState() != Cell.UNTOUCHED)
        {
            return 0;
        }

        for(int x = cell.getXCoord(); x >= 0; x--)
        {
            Cell currentCell = board.getCell(x, cell.getYCoord());
            int currentState = currentCell.getState();

            //make sure we don't check past the necessary number of cells
            if(counter > maxLength)
            {
                break;
            }
            //make sure we stop if we encounter a sunken ship or missed shot
            else if(currentState == Cell.MISS || currentState == Cell.SUNKEN_SHIP)
            {
                break;
            }
            //add a small amount of points for an untouched adjacent cell
            else if(currentState == Cell.UNTOUCHED)
            {
                leftPoints += BASE_VALUE_UNTOUCHED - counter*DIMINISHING_MULTIPLIER;
                counter++;
            }
            //add a large amount of points for an adjacent hit
            // uses formula: -x^2/a - x/b + c
            // where x = counter, a = A_DENOM, b = B_DENOM, c = BASE_VALUE
            else if(currentState == Cell.HIT)
            {
                //leftPoints += -(counter*counter)/A_DENOM - counter/B_DENOM + BASE_VALUE_HIT;
                leftPoints += 30 - (5*counter);
                counter++;
            }
        }

        return leftPoints;
    }

    /**
     * this method will calculate the current cell's point value only for the adjacent cells to the east.
     * @param currentCell the cell that we are assigning a point value to
     * @return the point value for the adjacent cells to the east
     */
    private double calcPointsRight(Cell cell) throws InvalidCoordinateException
    {
        double rightPoints = 0;
        int maxLength = maxOpponentShipLength - 1;
        int counter = 0;

        if(cell.getState() != Cell.UNTOUCHED)
        {
            return 0;
        }

        for(int x = cell.getXCoord(); x <= 9; x++)
        {
            Cell currentCell = board.getCell(x, cell.getYCoord());
            int currentState = currentCell.getState();

            //make sure we don't check past the necessary number of cells
            if(counter > maxLength)
            {
                break;
            }
            //make sure we stop if we encounter a sunken ship or missed shot
            else if(currentState == Cell.MISS || currentState == Cell.SUNKEN_SHIP)
            {
                break;
            }
            //add a small amount of points for an untouched adjacent cell
            else if(currentState == Cell.UNTOUCHED)
            {
                rightPoints += BASE_VALUE_UNTOUCHED - counter*DIMINISHING_MULTIPLIER;
                counter++;
            }
            //add a large amount of points for an adjacent hit
            // uses formula: -x^2/a - x/b + c
            // where x = counter, a = A_DENOM, b = B_DENOM, c = BASE_VALUE
            else if(currentState == Cell.HIT)
            {
                //rightPoints += -(counter*counter)/A_DENOM - counter/B_DENOM + BASE_VALUE_HIT;
                rightPoints += 30 - (5*counter);
                counter++;
            }
        }

        return rightPoints;
    }
}
