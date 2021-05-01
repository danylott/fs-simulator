package file_system;

/*
 * Here you can find size of classes when written on the disc and other useful numbers.
 */
public class FSConfig {
    //ALL SIZES ARE IN BYTES

    public static final int BLOCKS_NUM = 64;
    public static final int BLOCK_SIZE = 64;
    public static final int BLOCKS_PER_FILE = 3;
    public static final int FILE_DESCRIPTOR_SIZE = (BLOCKS_PER_FILE + 1) * Integer.BYTES;

    /*
     * Maximum possible number of 3-block files on 64*64 disk is 19.
     * With 3 64-bytes blocks for all 18 directory entries, optimal
     * filename length is 6.7 bytes (chars).
     */
    public static final int MAX_FILENAME_LEN = 6;
    public static final int MAX_FILES_NUM = 19;
    public static final int MAX_FILES_IN_DIR = 18;
    public static final int DIRECTORY_ENTRY_SIZE = MAX_FILENAME_LEN + Integer.BYTES;

    static {
        //So descriptors don't cross block bounds
        assert BLOCK_SIZE % BLOCKS_NUM == 0;
        assert BLOCK_SIZE % FILE_DESCRIPTOR_SIZE == 0;
    }
}
