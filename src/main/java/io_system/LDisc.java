package io_system;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class LDisc implements Disc {

    private int blockNumber;

    //block size in bytes
    private int blockSize;

    private RandomAccessFile file;

    LDisc(int blockNumber, int blockSize, String filePath) {
        this.blockNumber = blockNumber;
        this.blockSize = blockSize;

        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public byte[] readBlock(int blockIndex) {
        //TODO Implement reading
        return new byte[0];
    }

    @Override
    public void writeBlock(int blockIndex, byte[] block) {
        //TODO Implement writing
    }

    @Override
    public int blockSize() {
        return blockSize;
    }

    @Override
    public int blockNumber() {
        return blockNumber;
    }
}
