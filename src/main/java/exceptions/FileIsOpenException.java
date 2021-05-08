package exceptions;

public class FileIsOpenException extends Exception {
    public FileIsOpenException(String fileName) {
        super("File + " + fileName + " is open");
    }
}
