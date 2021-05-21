package file_system;

/*
 * Here you can find size of classes when written on the disc and other useful numbers.
 */
public class FSConfig {
    //ALL SIZES ARE IN BYTES

    /*
     * Maximum possible number of 3-block files on 64*64 disk is 19.
     * With 3 64-bytes blocks for all 18 directory entries, optimal
     * filename length is 6.7 bytes (chars).
     */
    public static final int MAX_FILENAME_LEN = 4;
    public static final int BLOCKS_PER_FILE = 3;
    public static final int MAX_OPEN_FILES = 128;

    public static final int FILE_DESCRIPTOR_SIZE = (BLOCKS_PER_FILE + 1) * Integer.BYTES; //16
    public static final int DIRECTORY_ENTRY_SIZE = MAX_FILENAME_LEN + Integer.BYTES; //10
}
