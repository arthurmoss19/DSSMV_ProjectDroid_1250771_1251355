package exceptions;
public class DuplicatedDataException extends RuntimeException {

    public DuplicatedDataException(String message) {
        super(message);
    }

    public String what() {
        return super.getMessage();
    }
}
