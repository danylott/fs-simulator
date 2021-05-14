package file_system;

import components.DirectoryEntry;
import exceptions.OFTException;

public class Oft extends OftInterface {
    @Override
    public int getOftIndex(int _fdIndex) throws OFTException {
        if (_fdIndex < 0) {
            throw new OFTException("File descriptor index " + _fdIndex + " is out of bounds");
        }

        for (int i = 0; i < entriesBuffer.size(); i++) {
            if (entriesBuffer.get(i).fDescIndex == _fdIndex)
                return i;
        }
        throw new OFTException("No open file by descriptor index " + _fdIndex);
    }

    @Override
    public int addFile(int _fdIndex) throws OFTException {
        // if file exists or no free space in oft
        if (oftSize == OftInterface.FDOPENEDLIMIT) {
            throw new OFTException("Open file table if full");
        } else if (getOftIndex(_fdIndex) >= 0) {
            throw new OFTException("File is already open");
        }

        OftEntry newOftEntry = new OftEntry();
        newOftEntry.fDescIndex = _fdIndex;

        for (int i = 0; i < OftInterface.FDOPENEDLIMIT; i++) {
            if (entriesBuffer.get(i).fDescIndex == -1) {
                entriesBuffer.set(i, newOftEntry);
                oftSize++;
                return i;
            }
        }
        throw new IllegalStateException("Could not find empty directory entry");
    }

    @Override
    public OftEntry getFile(int _oftIndex) throws OFTException {
        checkOftIndex(_oftIndex);
        OftEntry file = entriesBuffer.get(_oftIndex);
        if (file.fDescIndex == -1) {
            throw new OFTException("No open file by index " + _oftIndex);
        }
        return file;
    }

    @Override
    public void removeOftEntry(int _oftIndex) throws OFTException {
        checkOftIndex(_oftIndex);
        entriesBuffer.get(_oftIndex).fDescIndex = -1;
        entriesBuffer.get(_oftIndex).blockModified = false;
        entriesBuffer.get(_oftIndex).blockRead = false;
        entriesBuffer.get(_oftIndex).fPos = 0;
        entriesBuffer.get(_oftIndex).readBlockIndex = -1;
        oftSize--;
    }

    @Override
    public int getNumOfOpenFiles() {
        return oftSize;
    }

    @Override
    public int getFDIndex(int _oftIndex) throws OFTException {
        checkOftIndex(_oftIndex);
        OftEntry file = entriesBuffer.get(_oftIndex);
        if (file.fDescIndex == -1) {
            throw new OFTException("No open file by index " + _oftIndex);
        }
        return file.fDescIndex;
    }
}
