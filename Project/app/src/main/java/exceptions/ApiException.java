package exceptions;
public class ApiException extends RuntimeException {

    private final int httpCode;

    public ApiException(String message) {
        this(message, 0);
    }

    public ApiException(String message, int httpCode) {
        super(message);
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String what() {
        return super.getMessage();
    }
}
