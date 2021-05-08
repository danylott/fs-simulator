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

    public boolean cleanup() throws IOException {
        if(fileExists(systemStatePath)) {
            saveSystemState();
            return true;
        } else {
            return false;
        }
    }

    /*
     * Copies block number i from disk.
     */
    public abstract byte[] read_block(int i);

    /*
     * Writes given data into block number i on the disk.
     */
    public abstract void write_block(int i, byte[] data);

    /*
     * Changes system state file and saves the system.
     */
    public void saveNewSystemState(String filename) throws IOException {
        systemStatePath = filename;
        saveSystemState();
    }
}
