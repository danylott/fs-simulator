package io_system;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Disk extends IOSystemInterface {
    protected int blocksNum;
    protected int blockLen;
    protected byte[][] lDisk;

    public Disk(int blocksNumber, int blocksLength) {
        this.blocksNum = blocksNumber;
        this.blockLen = blocksLength;
        lDisk = new byte[blocksNumber][blocksLength];
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
    public int blockLen() {
        return blockLen;
    }

    @Override
    public int blocksNum() {
        return blocksNum;
    }

    @Override
    protected void saveSystemState() throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.systemStatePath, "rw");
        file.seek(0);

        for (int i = 0; i < this.blocksNum; i++){
            file.write(lDisk[i]);
        }

        file.close();
    }

    @Override
    protected void restoreSystemState() throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.systemStatePath, "rw");
        file.seek(0);

        for (int i = 0; i < this.blocksNum; i++){
            file.read(lDisk[i]);
        }

        file.close();
    }
}
