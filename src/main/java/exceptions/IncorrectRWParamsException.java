package exceptions;

public class IncorrectRWParamsException extends Exception {
    public IncorrectRWParamsException(int bytes, int fdIndex) {
        super("Bytes <= 0 (bytes: " + bytes + ") , or fdIndex = -1 (fdIndex " + fdIndex + ")");
    }
}
