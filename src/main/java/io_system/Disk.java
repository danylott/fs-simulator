package io_system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Objects;

public class Disk extends IOSystemInterface {
    private static final int MAXBLOCKSIZE = 0; //128
    private static final int MAXBLOCKNUM = 0; //128
    protected int blocksNum;
    protected int blockLen;
    protected byte[][] lDisk;

    public Disk() {
        blocksNum = 0;
        blockLen = 0;
    }

    protected boolean checkConfig() {
        return blockLen > 0 && blockLen <= MAXBLOCKSIZE
                && blocksNum > 0 && blocksNum <= MAXBLOCKNUM;
    }

    public void init(int blocksLength, int blocksNumber, String systemStatePath) throws IOException {
        this.blocksNum = blocksNumber;
        this.blockLen = blocksLength;

        if(!checkConfig())
            System.out.println("ERROR in checkConfig(Disk)!!!");

        init(systemStatePath);
    }

    // N:::CleanUp when end

    @Override
    public byte[] read_block(int i, byte[] data) {
        assert i > 0 && i < this.blocksNum && data != null;

        data = Arrays.copyOf(lDisk[i], lDisk.length);
        return data;
    }

    @Override
    public void write_block(int i, byte[] data) {
        assert i > 0 && i < this.blocksNum && data != null;

        lDisk[i] = Arrays.copyOf(data, data.length);
    }

    @Override
    protected void saveSystemState() throws IOException {
        RandomAccessFile file = new RandomAccessFile(this.systemStatePath, "rw");

        // Q:::Check Opened?

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

        // Q:::Check Opened?

        int offset = 0;
        for (int i = 0; i < this.blocksNum; i++){
            file.read(lDisk[i], offset, this.blockLen);
            offset += this.blockLen;
        }

        file.close();
    }
}
