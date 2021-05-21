package io_system;

import java.io.File;
import java.io.IOException;

public abstract class IOSystemInterface {
    protected String systemStatePath;

    protected abstract void saveSystemState() throws IOException;

    protected abstract void restoreSystemState() throws IOException;

    protected boolean fileExists(String filePath) {
        //Move to util?
        return new File(filePath).exists();
    }

    public boolean init(String systemStatePath) throws IOException {
        this.systemStatePath = systemStatePath;
        if(fileExists(systemStatePath)) {
            restoreSystemState();
            return true;
        } else {
            return false;
        }
    }

    public void cleanup() throws IOException {
        if(fileExists(systemStatePath)) {
            saveSystemState();
        }
    }

    /*
     * Copies block number i from disk.
     */
    public abstract byte[] readBlock(int i);

    /*
     * Writes given data into block number i on the disk.
     */
    public abstract void writeBlock(int i, byte[] data);

    public abstract int blockLen();

    public abstract int blocksNum();

    /*
     * Changes system state file and saves the system.
     */
    public void saveNewSystemState(String filename) throws IOException {
        systemStatePath = filename;
        saveSystemState();
    }
}
