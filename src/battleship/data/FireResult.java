package battleship.data;

/*
Programmer: Steven Siddall

This class represents a set of information about the result of a battleship
shot.

Possible Results:
0: Error. Something went wrong such as the target already having been shot at
1: Miss. The shot did not hit any ships.
2: Hit. The shot hit a ship but did not sink it
3: Sink. The shot hit a ship and sunk it

Instance variables:
sunkShip: Reference to the ship that was sunk if the result is 3. Other wise null
result: integer representation of the result as described above
target: The location that was shot at

*/

public class FireResult
{
    public static final int ERROR = 0;
    public static final int MISS = 1;
    public static final int HIT = 2;
    public static final int SINK = 3;

    private Ship sunkShip;
    private int result;
    private Coord target;

    /**
    Default constructor
    */
    public FireResult()
    {
        sunkShip = null;
        result = 0;
        target = new Coord();
    }

    /**
    Full constructor
    @param sunkShip The ship that was sunk if the result is 3
    @param result Integer representation of the result
    @param target Location that was shot at
    */
    public FireResult(Ship sunkShip, int result, Coord target)
    {
        this.sunkShip = sunkShip;
        this.result = result;
        this.target = target;
    }

    public void setSunkShip(Ship sunkShip)
    {
        this.sunkShip = sunkShip;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public void setTarget(Coord target)
    {
        this.target = target;
    }

    public Ship getSunkShip()
    {
        return sunkShip;
    }

    public int getResult()
    {
        return result;
    }

    public Coord getTarget()
    {
        return target;
    }

    public int getTargetX()
    {
        return target.getX();
    }

    public int getTargetY()
    {
        return target.getY();
    }
}
