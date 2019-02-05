package ci;

public class CloneException extends Exception {
    public CloneException() { super(); }
    public CloneException(String message) { super(message); }
    public CloneException(String message, Throwable cause) { super(message, cause); }
    public CloneException(Throwable cause) { super(cause); }
}