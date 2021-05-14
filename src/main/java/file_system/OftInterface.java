package file_system;

import exceptions.OFTException;

import java.util.ArrayList;

public abstract class OftInterface {
    protected static final int FDOPENEDLIMIT = 128;

    protected int oftSize = 0;
    protected ArrayList<OftEntry> entriesBuffer = new ArrayList<>(FDOPENEDLIMIT);

    protected void checkOftIndex(int _oftIndex) throws OFTException {
        if (_oftIndex < 0 || _oftIndex >= OftInterface.FDOPENEDLIMIT) {
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
