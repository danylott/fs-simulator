package file_system;

import exceptions.OFTException;

public class Oft extends OftInterface {
    @Override
    public int getOftIndex(int fdIndex) throws OFTException {
        if (fdIndex < 0) {
            throw new OFTException("File descriptor index " + fdIndex + " is out of bounds");
        }

        for (int i = 0; i < entriesBuffer.length; i++) {
            if (entriesBuffer[i].fDescIndex == fdIndex)
                return i;
        }
        return -1;
    }

    @Override
    public int addFile(int fdIndex) throws OFTException {
        // if file exists or no free space in oft
        if (oftSize == FSConfig.MAX_OPEN_FILES) {
            throw new OFTException("Open file table if full");
        } else if (getOftIndex(fdIndex) >= 0) {
            throw new OFTException("File is already open");
        }

        OftEntry newOftEntry = new OftEntry();
        newOftEntry.fDescIndex = fdIndex;

        for (int i = 0; i < FSConfig.MAX_OPEN_FILES; i++) {
            if (entriesBuffer[i].fDescIndex == -1) {
                entriesBuffer[i] = newOftEntry;
                oftSize++;
                return i;
            }
        }
        throw new IllegalStateException("Could not find empty directory entry");
    }

    @Override
    public OftEntry getFile(int oftIndex) throws OFTException {
        checkOftIndex(oftIndex);
        OftEntry file = entriesBuffer[oftIndex];
        if (file.fDescIndex == -1) {
            throw new OFTException("No open file by index " + oftIndex);
        }
        return file;
    }

    @Override
    public void removeOftEntry(int oftIndex) throws OFTException {
        checkOftIndex(oftIndex);
        entriesBuffer[oftIndex].fDescIndex = -1;
        entriesBuffer[oftIndex].blockModified = false;
        entriesBuffer[oftIndex].fPos = 0;
        entriesBuffer[oftIndex].readBlockIndex = -1;
        oftSize--;
    }
}
