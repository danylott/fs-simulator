package utils;

import io_system.IOSystemInterface;

public class DiskReader extends DiskStream {
    public DiskReader(IOSystemInterface ios, int blockIndex, int shift) {
        super(ios, blockIndex, shift);
    }

    public void clear() {
        blockRead = false;
    }

    public void read(byte[] writeTo, int bytes) {

    }
}
