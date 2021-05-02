package exceptions;

import file_system.FSConfig;

public class TooLongFileNameException extends Exception {
    public TooLongFileNameException(String fileName) {
        super("File name " + fileName + " is over character limit: " + FSConfig.MAX_FILENAME_LEN);
    }
}
