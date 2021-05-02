package utils;

import file_system.FSConfig;
import io_system.IOSystemInterface;

public class DiskStream {
    protected byte[] blockBuffer;
    protected IOSystemInterface ios;
    protected int blockIndex;
    protected int shift;
    protected boolean blockRead;

    public DiskStream(IOSystemInterface ios, int blockIndex, int shift) {
        assert 0 <= blockIndex && blockIndex < FSConfig.BLOCKS_NUM;
        assert 0 <= shift && shift < FSConfig.BLOCK_SIZE;

        this.ios = ios;
        this.blockIndex = blockIndex;
        this.shift = shift;
        this.blockRead = false;
    }
}
