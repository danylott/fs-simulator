package io_system;

public interface Disc {

    public byte[] readBlock(int blockIndex);

    public void writeBlock(int blockIndex, byte[] block);

    public int blockSize();

    public int blockNumber();
}
