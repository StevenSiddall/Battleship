package battleship.exceptions;

/**
Programmer: Steven Siddall
 @author Steven Siddall

This is an exception class for the Battleship game.
This type of exception should be thrown when a ship or array of cells of invalid
size is passed to another method

*/
public class InvalidShipSizeException extends BattleshipException
{
	public InvalidShipSizeException()
	{
		super("Error: Invalid Ship Size");
	}
	
	public InvalidShipSizeException(String newMessage)
	{
		super(newMessage);
	}
}
