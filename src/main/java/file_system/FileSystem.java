package file_system;

import auxiliary.Pair;
import components.DirectoryEntry;
import components.FileDescriptor;
import io_system.IOSystemInterface;
import exceptions.*;
import utils.*;

import java.security.KeyPair;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

public class FileSystem {
    private IOSystemInterface ios;
    private OftInterface oft;

    private FileDescriptor getDescriptor(int index) {
        //calculate descriptor position in LDisk
        int offset = FSConfig.BLOCKS_NUM + FSConfig.FILE_DESCRIPTOR_SIZE * index;
        int blockIndex = offset / FSConfig.BLOCK_SIZE;
        int blockOffset = offset % FSConfig.BLOCK_SIZE;

        DiskReader reader = new DiskReader(ios, blockIndex, blockOffset);
        return FileDescriptor.formByteArray(reader.read(FSConfig.FILE_DESCRIPTOR_SIZE));
    }

    private BitSet readBitMap() {
        BitSet bitMap = new BitSet();
        DiskReader reader = new DiskReader(ios, 0, 0);
        byte[] bytes = reader.read(FSConfig.BLOCKS_NUM);
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bitMap.set(i);
            }
        }
        return bitMap;
    }

    private void writeBitMap(BitSet bitMap) {
        assert bitMap.length() == FSConfig.BLOCKS_NUM;

        DiskWriter writer = new DiskWriter(ios, 0, 0);
        writer.write(bitMap.toByteArray());
    }

    /*
     * Ensures that given number of bytes can be written in file.
     * Allocates new blocks if needed.
     * Does NOT write file descriptor on disk.
     */
    private void reserveBytesForFile(FileDescriptor fd, int bytes) throws FileSizeExceededException, NotEnoughFreeBlocksException {
        //check if file can store requested bytes
        if (fd.fileLength + bytes > FSConfig.BLOCK_SIZE * FSConfig.BLOCKS_PER_FILE) {
            throw new FileSizeExceededException();
        }
        int occupiedNum = (fd.fileLength + FSConfig.BLOCK_SIZE - 1) / FSConfig.BLOCK_SIZE;
        int freeBytes = occupiedNum * FSConfig.BLOCK_SIZE - fd.fileLength;
        int toAllocateNum = (bytes - freeBytes + FSConfig.BLOCK_SIZE - 1) / FSConfig.BLOCK_SIZE;
        if (toAllocateNum == 0) {
            //Nothing to allocate
            return;
        }
        BitSet bitMap = readBitMap();
        List<Integer> freeBlocks = new LinkedList<>();
        for (int i = FSConfig.FIRST_DATA_BLOCK; i < FSConfig.BLOCKS_NUM; ++i) {
            if (!bitMap.get(i)) {
                freeBlocks.add(i);
                if (freeBlocks.size() == toAllocateNum) {
                    break;
                }
            }
        }
        if (freeBlocks.size() < toAllocateNum) {
            throw new NotEnoughFreeBlocksException(toAllocateNum, freeBlocks.size());
        }
        for (int blockIndex : freeBlocks) {
            fd.blockArray[occupiedNum++] = blockIndex;
            bitMap.set(blockIndex);
        }
        writeBitMap(bitMap);
    }

    private void writeToFile(OftEntry entry, FileDescriptor fd, byte[] data) throws FileSizeExceededException {
        if (data.length + entry.fPos > FSConfig.BLOCK_SIZE * FSConfig.BLOCKS_PER_FILE) {
            throw new FileSizeExceededException();
        }

        int bytesWritten = 0;
        int blockIndex = entry.fPos / FSConfig.BLOCK_SIZE;
        int blockOffset = entry.fPos % FSConfig.BLOCK_SIZE;

        //TODO finish
    }

    public void create(String fileName) throws TooManyFilesException, TooLongFileNameException, FileAlreadyExistsException, NoFreeDescriptorException {
        FileDescriptor dirDescriptor = getDescriptor(0);
        if (fileName.length() > FSConfig.MAX_FILENAME_LEN) {
            throw new TooLongFileNameException(fileName);
        } else if (dirDescriptor.fileLength / FSConfig.DIRECTORY_ENTRY_SIZE >= FSConfig.MAX_FILES_IN_DIR) {
            throw new TooManyFilesException();
        } //TODO Check if file already exists.

        //Calculate offset to read file descriptors
        int offset = FSConfig.BLOCKS_NUM + FSConfig.FILE_DESCRIPTOR_SIZE;
        int blockIndex = offset / FSConfig.BLOCK_SIZE;
        int blockOffset = offset % FSConfig.BLOCK_SIZE;
        DiskReader reader = new DiskReader(ios, blockIndex, blockOffset);

        //Find free descriptor
        FileDescriptor fd = null;
        int fdIndex = -1;
        for (int i = 0; i < FSConfig.MAX_FILES_IN_DIR; ++i) {
            fd = FileDescriptor.formByteArray(reader.read(FSConfig.FILE_DESCRIPTOR_SIZE));
            if(fd.fileLength < 0) {
                fdIndex = i + 1;
                fd.fileLength = 0;
                break;
            }
        }
        if (fdIndex == -1) {
            throw new NoFreeDescriptorException();
        }

        //Create directory entry
        DirectoryEntry de = new DirectoryEntry(fdIndex, fileName);
        OftEntry dir = oft.getFile(0);
        try {
            writeToFile(dir, dirDescriptor, DirectoryEntry.asByteArray(de));
        } catch (FileSizeExceededException e) {
            throw new TooManyFilesException();
        }

        //Write file descriptor on disk
        offset = FSConfig.BLOCKS_NUM + FSConfig.FILE_DESCRIPTOR_SIZE * fdIndex;
        blockIndex = offset / FSConfig.BLOCK_SIZE;
        blockOffset = offset % FSConfig.BLOCK_SIZE;
        DiskWriter writer = new DiskWriter(ios, blockIndex, blockOffset);
        writer.write(FileDescriptor.asByteArray(fd));
    }

    public void destroy(String fileName) {

    }

    public int _lSeek(OftEntry fileEntry, FileDescriptor fDesc, int pos) {
        if (fileEntry != null){
            if (pos < 0 || pos > fDesc.fileLength) {
                return -1;
            }
            return 1;
        }
        else {
            return -1;
        }
    }

    public Pair findFileInDirectory(String fileName) {
        // function return file directory with idx or
        // file directory with idx = -1 if not found
        OftEntry dirOftEntry = oft.getFile(0);
        FileDescriptor dirFd = getDescriptor(0);
        if(_lSeek(dirOftEntry, dirFd, 0) == 1)
            dirOftEntry.fPos = 0;
        int numOFFilesInDir = dirFd.fileLength / FSConfig.FILE_DESCRIPTOR_SIZE;
        int dirEntryIdx = 0;
        for (int i = 0; i < numOFFilesInDir; i++) {
            DirectoryEntry curDirEntry = null;
            // must rerutn curDirEntry
            // readFromFile(dirOftEntry, dirFd, curDirEntry, FSConfig.FILE_DESCRIPTOR_SIZE);
            if (curDirEntry.filename.equals(fileName)){
                dirEntryIdx = i;
                Pair<DirectoryEntry, Integer> file = new Pair<>(curDirEntry, dirEntryIdx);
                return file;
            }
        }
        Pair<DirectoryEntry, Integer> file = new Pair<>(new DirectoryEntry(), -1);
        return file;
    }

    public int open(String fileName) {
        // returns -1 if file with such fileName not found
        // or oftIndex if file found
        Pair<DirectoryEntry, Integer> file = findFileInDirectory(fileName);
        if (file.second == -1) {
            return -1;
        }
        int fdIndex = file.first.fdIndex;
        int oftIndex = oft.addFile(fdIndex);
        return oftIndex;
    }

    public int close(int oftIndex) throws FileNotFoundException {
        int fdIndex = oft.getFDIndexByOftIndex(oftIndex);
        if (fdIndex == -1) {
            return -1;
        }
        FileDescriptor fd = getDescriptor(fdIndex);
        OftEntry oftEntry = oft.getFile(oftIndex);
        if (oftEntry.blockModified) {
            ios.write_block(fd.blockArray[oftEntry.fPos/FSConfig.BLOCK_SIZE], oftEntry.readWriteBuffer);
        }
        oft.removeOftEntry(oftIndex);
        return 1;
    }
}
