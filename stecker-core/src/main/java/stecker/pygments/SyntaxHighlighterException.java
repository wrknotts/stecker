package stecker.pygments;

/**
 * Exception thrown when an error occurs while highlighting source code.
 */
public class SyntaxHighlighterException extends RuntimeException {

    private static final long serialVersionUID = -5323677014327799033L;

    /**
     * Creates a new SyntaxHighlighterException object.
     */
    public SyntaxHighlighterException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public SyntaxHighlighterException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public SyntaxHighlighterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param cause the cause
     */
    public SyntaxHighlighterException(Throwable cause) {
        super(cause);
    }
}
