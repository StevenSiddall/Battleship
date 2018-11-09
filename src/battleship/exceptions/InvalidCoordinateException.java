package battleship.exceptions;

/**
Programmer: Steven Siddall
 @author Steven Siddall

This an exception class that represents an invalid coordinate. This should be
thrown when a coordinate with x or y element is < 0 or > 9

*/

public class InvalidCoordinateException extends BattleshipException
{
    public InvalidCoordinateException()
    {
        super("Invalid coordinate entered");
    }

    public InvalidCoordinateException(String newMessage)
    {
        super(newMessage);
    }
}
