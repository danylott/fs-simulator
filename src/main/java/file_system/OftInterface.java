package file_system;

import exceptions.OFTException;

public abstract class OftInterface {


    protected int oftSize = 0;
    protected OftEntry[] entriesBuffer = new OftEntry[FSConfig.MAX_OPEN_FILES];

    public OftInterface() {
        for(int i = 0; i < FSConfig.MAX_OPEN_FILES; ++i) {
            entriesBuffer[i] = new OftEntry();
        }
    }

    protected void checkOftIndex(int _oftIndex) throws OFTException {
        if (_oftIndex < 0 || _oftIndex >= FSConfig.MAX_OPEN_FILES) {
            throw new OFTException("File index " + _oftIndex + " is out of bounds");
        }
    }

    public abstract int getOftIndex(int _fdIndex) throws OFTException;

    public abstract int addFile(int _fdIndex) throws OFTException;

    public abstract OftEntry getFile(int _oftIndex) throws OFTException;

    public abstract void removeOftEntry(int _oftIndex) throws OFTException;

    public abstract int getNumOfOpenFiles();

    public abstract int getFDIndex(int _oftIndex) throws OFTException;
}
