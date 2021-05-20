package utils;

import io_system.IOSystemInterface;

import static java.lang.Math.min;

public class DiskReader extends DiskStream {
    public DiskReader(IOSystemInterface ios, int blockIndex, int shift) {
        super(ios, blockIndex, shift);
    }

    public void clear() {
        blockRead = false;
    }

    public byte[] read(int bytes) {
        byte[] res = new byte[bytes];
        int resIndex = 0;

        while (bytes > 0) {
            if (!blockRead) {
                //Buffer is outdated, read new block
                blockBuffer = ios.read_block(blockIndex);
                blockRead = true;
            }
            int readLen = min(ios.blockLen() - shift, bytes); //Bytes to read from current block;
            System.arraycopy(blockBuffer, shift, res, resIndex, readLen);
            shift = (shift + readLen) % ios.blockLen();
            bytes -= readLen;
            resIndex += readLen;
            //If we are here, some date were read (while condition), so shift==0 means reaching of the block boundary.
            if(shift == 0) {
                //End of the current block, move to new block
                ++blockIndex;
                blockRead = false;
            }
        }
        return res;
    }
}
