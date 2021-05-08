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
    public static final int MAX_FILENAME_LEN = 6;
    public static final int BLOCKS_PER_FILE = 3;

    public static int BLOCKS_NUM; //64
    public static int BLOCK_SIZE; //64

    public static int FILE_DESCRIPTOR_SIZE; //16
    public static int DIRECTORY_ENTRY_SIZE; //10
    public static int MAX_FILES_IN_DIR; //19

    public static int RESERVED_BLOCKS_NUM; //6
    public static int FIRST_DATA_BLOCK; //k = 7

    public static void init(int cylNum, int surfNum, int sectNum, int sectLen) {
        BLOCKS_NUM = cylNum * surfNum * sectNum;
        BLOCK_SIZE = sectLen;

        FILE_DESCRIPTOR_SIZE = (BLOCKS_PER_FILE + 1) * Integer.BYTES;
        DIRECTORY_ENTRY_SIZE = MAX_FILENAME_LEN + Integer.BYTES;
        MAX_FILES_IN_DIR = BLOCKS_PER_FILE * BLOCK_SIZE / DIRECTORY_ENTRY_SIZE;

        RESERVED_BLOCKS_NUM = (BLOCKS_NUM + FILE_DESCRIPTOR_SIZE * (MAX_FILES_IN_DIR + 1) + BLOCK_SIZE - 1) / BLOCK_SIZE;
        FIRST_DATA_BLOCK = RESERVED_BLOCKS_NUM + 1;
    }

    static {
        init(4, 2, 8, 64);
    }
}
