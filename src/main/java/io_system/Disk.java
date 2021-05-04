package io_system;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Disk extends IOSystemInterface {
    protected int blocksNum;
    protected int blockLen;
    protected byte[][] lDisk;

    public Disk() {
        blocksNum = 0;
        blockLen = 0;
    }

    public void init(int blocksLength, int blocksNumber, String systemStatePath) throws IOException {
        this.blocksNum = blocksNumber;
        this.blockLen = blocksLength;
        init(systemStatePath);
    }

    // N:::CleanUp when end

    @Override
    public byte[] read_block(int i) {
        assert i > 0 && i < this.blocksNum;

        return Arrays.copyOf(lDisk[i], lDisk[i].length);
    }

    @Override
    public void write_block(int i, byte[] data) {
        assert i > 0 && i < this.blocksNum && data != null;

        lDisk[i] = Arrays.copyOf(data, data.length);
    }

    @Override
    protected void saveSystemState() throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.systemStatePath, "rw");

        int offset = 0;
        for (int i = 0; i < this.blocksNum; i++){
            file.write(lDisk[i], offset, this.blockLen);
            offset += this.blockLen;
        }

        file.close();
    }

    @Override
    protected void restoreSystemState() throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.systemStatePath, "rw");

        int offset = 0;
        for (int i = 0; i < this.blocksNum; i++){
            file.read(lDisk[i], offset, this.blockLen);
            offset += this.blockLen;
        }

        file.close();
    }
}
