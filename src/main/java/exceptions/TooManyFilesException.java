package exceptions;

import file_system.FSConfig;

public class TooManyFilesException extends Exception {
    public TooManyFilesException() {
        super("File count limit reached: " + FSConfig.MAX_FILES_IN_DIR);
    }
}
