package messages;

public class InvalidMessageException extends Exception{
    
    /**
     * For serializable implementation
     */
    private static final long serialVersionUID = 1L;
    
    private String message;

    /**
     * Invalid Message Exception Constructor
     * @param message message containing exception
     */
    public InvalidMessageException(String message) {
        this.message = message;
    }

    /**
     * Getter for message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for message
     * @param message message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
