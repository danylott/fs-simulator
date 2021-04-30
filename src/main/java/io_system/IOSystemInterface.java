package io_system;

public abstract class IOSystemInterface {
    protected String systemStatePath;

    protected abstract void saveSystemState();

    protected abstract void restoreSystemState();

    protected boolean fileExists(String filePath) {

        //TODO check if file exists
        //Move to util?
        return false;
    }

    protected void init(String systemStatePath) {
        this.systemStatePath = systemStatePath;

        if(fileExists(systemStatePath)) {
            restoreSystemState();
        }
    }

    protected void cleanup() {

        if(fileExists(systemStatePath)) {
            saveSystemState();
        }
    }

    /* Copies block number i from disk into
     * a given byte array. */
    public abstract void read_block(int i, byte[] data);

    /* Writes given data into block number i
     * on the disk. */
    public abstract void write_block(int i, byte[] data);

    public void SaveNewSystemState(String filename) {
        systemStatePath = filename;
        saveSystemState();
    }
}
