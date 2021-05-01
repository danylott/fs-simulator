package exceptions;

public class FileAlreadyExistsException extends Exception {
    public FileAlreadyExistsException(String filename) {
        super("File with this name already exists: " + filename);
    }
}
