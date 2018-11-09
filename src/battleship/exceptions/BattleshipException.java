package battleship.exceptions;

/**
Programmer: Steven Siddall

This is the super class for all battleship exceptions. This specific type of
exception should never be thrown. We use it when we want to catch all types of 
battleship exceptions at once
 */
public class BattleshipException extends Exception
{
    public BattleshipException()
    {
        super("Battleship Exception");
    }
    
    public BattleshipException(String message)
    {
        super(message);
    }
}
