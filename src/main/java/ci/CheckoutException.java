package ci;

public class CheckoutException extends Exception {
    public CheckoutException() { super(); }
    public CheckoutException(String me) { super(message); }
    public CheckoutException(String message, Throwable cause) { super(message, cause); }
    public CheckoutException(Throwable cause) { super(cause); }
}
