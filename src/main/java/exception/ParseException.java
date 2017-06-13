package exception;

/**
 * Created by Administrator on 2017/6/9.
 */
public class ParseException extends RuntimeException{
    /**
     * Default ParseException.
     */
    public ParseException() {
    }

    /**
     * ParseException with message.
     *
     * @param message The exception message.
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * ParseException with throwable.
     *
     * @param cause The Throwable object.
     */
    public ParseException(Throwable cause) {
        super(cause);
    }

    /**
     * ParseException with message and throwable.
     *
     * @param message The exception message.
     * @param cause The Throwable object.
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
