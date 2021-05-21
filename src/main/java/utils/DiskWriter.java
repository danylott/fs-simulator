package utils;

import io_system.IOSystemInterface;

public class DiskWriter extends DiskStream {
    public DiskWriter(IOSystemInterface ios, int blockIndex, int shift) {
        super(ios, blockIndex, shift);
    }

    public void write(byte[] data) {
        int dataIndex = 0;
        while (dataIndex < data.length) {
            if(!blockRead) {
                //Buffer is outdated, read new block
                blockBuffer = ios.readBlock(blockIndex);
                blockRead = true;
            }
            int writeLen = Integer.min(ios.blockLen() - shift, data.length - dataIndex);
            System.out.flush();
            System.arraycopy(data, dataIndex, blockBuffer, shift, writeLen);
            shift = (shift + writeLen) % ios.blockLen();
            dataIndex += writeLen;
            ios.writeBlock(blockIndex, blockBuffer);
            //If we are here, some date were written (while condition), so shift==0 means reaching of the block boundary.
            if(shift == 0) {
                //End of the current block, move to new block
                ++blockIndex;
                blockRead = false;
            }
        }
    }
}
