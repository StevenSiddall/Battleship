package battleship.exceptions;

/*
Programmer: Steven Siddall

This is an exception class that represents an invalid ship placement.
Should be throws when someone tries to place a ship on a board when the whole
ship does not fit or overlaps with another ship

*/

public class InvalidShipPlacementException extends BattleshipException
{
	public InvalidShipPlacementException()
	{
		super();
	}
	
	public InvalidShipPlacementException(String newMessage)
	{
		super(newMessage);
	}
}
