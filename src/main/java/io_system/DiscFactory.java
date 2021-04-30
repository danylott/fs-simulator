package io_system;

public class DiscFactory {

    public static Disc createDisc(int blockNumber, int blockSize, String filePath) {

        LDisc disc = new LDisc(blockNumber, blockSize, filePath);

        //TODO Create and initialize bitmap

        //TODO Create descriptors after bitmap

        //Fill file with empty blocks
        byte[] zeros = new byte[blockSize];
        for(int i = 0; i < blockNumber; ++i) {
            disc.writeBlock(i, zeros);
        }

        /*
         * BITMAP[BlockNumber] | FileDescriptor[k] | BLOCKS [BlockSize][BlockNumber]
         */

        return disc;
    }
}
