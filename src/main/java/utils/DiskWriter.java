package utils;

import io_system.IOSystemInterface;

public class DiskWriter extends DiskStream {
    public DiskWriter(IOSystemInterface ios, int blockIndex, int shift) {
        super(ios, blockIndex, shift);
    }

    public void flush() {
        if (blockRead) {
            ios.write_block(blockIndex, blockBuffer);
            blockRead = false;
        }
    }

    public void write(byte[] readFrom, int bytes) {

    }
}
