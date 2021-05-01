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

    protected void init(String systemStatePath) throws IOException {
        this.systemStatePath = systemStatePath;

        if(fileExists(systemStatePath)) {
            restoreSystemState();
        }
    }

    protected void cleanup() throws IOException {
        if(fileExists(systemStatePath)) {
            saveSystemState();
        }
    }

    /*
     * Copies block number i from disk into a given byte array.
     */
    public abstract byte[] read_block(int i, byte[] data);

    /*
     * Writes given data into block number i on the disk.
     */
    public abstract void write_block(int i, byte[] data);

    /*
     * Changes system state file and saves the system.
     */
    public void SaveNewSystemState(String filename) throws IOException {
        systemStatePath = filename;
        saveSystemState();
    }
}
