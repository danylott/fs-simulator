package file_system;

public class FSConfig {
    public static final int BLOCKS_NUM = 64;
    public static final int BLOCK_SIZE = 64;
    public static final int BLOCKS_PER_FILE = 3;
    public static final int FILE_DESCRIPTOR_SIZE = (BLOCKS_PER_FILE + 1) * Integer.BYTES;
}
