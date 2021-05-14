package utils;

import io_system.IOSystemInterface;

public class DiskStream {
    protected byte[] blockBuffer;
    protected IOSystemInterface ios;
    protected int blockIndex;
    protected int shift;
    protected boolean blockRead;

    public DiskStream(IOSystemInterface ios, int blockIndex, int shift) {
        assert 0 <= blockIndex && blockIndex < ios.blocksNum();
        assert 0 <= shift && shift < ios.blockLen();

        this.ios = ios;
        this.blockIndex = blockIndex;
        this.shift = shift;
        this.blockRead = false;
    }
}
