package exceptions;
public class DataConsistencyException extends RuntimeException {

    public DataConsistencyException(String message) {
        super(message);
    }

    public String what() {
        return super.getMessage();
    }
}