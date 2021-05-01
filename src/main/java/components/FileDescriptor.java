package components;

import file_system.FSConfig;

public class FileDescriptor {
    public int fileLength = -1;
    public int[] blockArray;

    public FileDescriptor() {
        blockArray = new int[FSConfig.BLOCKS_PER_FILE];
    }
    public FileDescriptor(int fileLength, int[] blockArray) {
        assert blockArray.length == FSConfig.BLOCKS_PER_FILE;

        this.fileLength = fileLength;
        this.blockArray = blockArray;
    }
}
