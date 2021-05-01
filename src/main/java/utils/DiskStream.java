package utils;

import io_system.IOSystemInterface;

public class DiskStream {
    protected char[] blockBuffer;
    protected IOSystemInterface ios;
    protected int blockIndex;
    protected int shift;
    protected boolean blockRead;

    public DiskStream(IOSystemInterface ios, int blockIndex, int shift) {
        this.ios = ios;
        this.blockIndex = blockIndex;
        this.shift = shift;
        this.blockRead = false;
    }
}
