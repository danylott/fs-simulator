package exceptions;

import file_system.FSConfig;

public class FileNotFoundException extends Exception {
    public FileNotFoundException() {
        super("File not found");
    }
}
