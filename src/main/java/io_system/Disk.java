package io_system;

public class Disk extends IOSystemInterface {
    protected int blocksNum;
    protected int blockLen;
    protected byte[][] lDisk;

    public Disk() {

    }

    protected boolean checkConfig() {

        return false;
    }

    @Override
    protected void saveSystemState() {

    }

    @Override
    protected void restoreSystemState() {

    }

    public void init(int blocksLength, int blocksNumber, String systemStatePath) {

    }

    @Override
    public void read_block(int i, byte[] data) {

    }

    @Override
    public void write_block(int i, byte[] data) {

    }
}
