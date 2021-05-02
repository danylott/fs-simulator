package exceptions;

import file_system.FSConfig;

public class FileSizeExceededException extends Exception {
    public FileSizeExceededException() {
        super("File cannot be larger then " + FSConfig.BLOCKS_PER_FILE * FSConfig.BLOCK_SIZE + " bytes");
    }
}
