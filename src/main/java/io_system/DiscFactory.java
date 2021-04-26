package io_system;

public class DiscFactory {

    public static Disc createDisc(int blockNumber, int blockSize, String filePath) {

        LDisc disc = new LDisc(blockNumber, blockSize, filePath);

        //Fill file with empty blocks
        byte[] zeros = new byte[blockSize];
        for(int i = 0; i < blockNumber; ++i) {
            disc.writeBlock(i, zeros);
        }

        //TODO Create and initialize bitmap

        return disc;
    }
}
