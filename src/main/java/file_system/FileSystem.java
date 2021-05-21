package file_system;

import auxiliary.*;
import components.*;
import io_system.*;
import exceptions.*;
import utils.*;

import java.io.IOException;
import java.util.*;

public class FileSystem {
    public static int MAX_FILES_IN_DIR;
    public static int RESERVED_BLOCKS_NUM;
    public static int FIRST_DATA_BLOCK; //k

    private IOSystemInterface ios;
    private OftInterface oft;
    private Map<Integer, FileDescriptor> fdCache; //Stores descriptors of open files by fdIndex
    private BitSet bitMap;


    /*
     * Use of uninitialized file system will likely cause NullptrException;
     *
     * Initializes filesystem with given specs, tries to restore disk from given file.
     * Sets up values in FSConfig. (encapsulation? who cares)
     * // So the good solution is to have local FSConfig object here, and pass it wherever it is needed (like DiskStream, OFT, components, etc). Too much code for such a small project.
     *
     * @return: 1 if disk was restored, 2 - if the new was initialized.
     */
    public int init(int cylNum, int surfNum, int sectNum, int sectLen, String fileName) throws IOException, OFTException {
        //Saving old disk
        if(ios != null) {
            try {
                ios.cleanup();
            } catch (IOException ignored) {
                //Saving old disk is not a primary task;
            }
        }

        //Initializing file system
        ios = new Disk(cylNum * surfNum * sectNum, sectLen);
        oft = new Oft();
        bitMap = new BitSet(ios.blocksNum());
        fdCache = new HashMap<>();
        MAX_FILES_IN_DIR = FSConfig.BLOCKS_PER_FILE * ios.blockLen() / FSConfig.DIRECTORY_ENTRY_SIZE;
        RESERVED_BLOCKS_NUM = (ios.blocksNum() + FSConfig.FILE_DESCRIPTOR_SIZE * (MAX_FILES_IN_DIR + 1) + ios.blockLen() - 1) / ios.blockLen();
        FIRST_DATA_BLOCK = RESERVED_BLOCKS_NUM + 1;

        //Trying to restore disk from file
        if (ios.init(fileName)) {
            //Could read disk from file, nice
            //Open root directory
            oft.addFile(0);
            fdCache.put(0, getDescriptor(0));
            //Read bitmap
            DiskReader reader = new DiskReader(ios, 0, 0);
            byte[] bytes = reader.read(ios.blocksNum());
            for (int i = 0; i < bytes.length * 8; i++) {
                if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                    bitMap.set(i);
                }
            }
            return 1;
        } else {
            //Initializing new disk
            //Reserve system blocks
            for(int i = 0; i < RESERVED_BLOCKS_NUM; ++i) {
                bitMap.set(i);
            }
            writeBitMap();

            //Creating file descriptors
            DiskWriter writer = new DiskWriter(ios, ios.blocksNum() / ios.blockLen(), ios.blocksNum() % ios.blockLen());
            //Directory descriptor
            FileDescriptor dirDescriptor = new FileDescriptor();
            dirDescriptor.fileLength = 0;
            writer.write(FileDescriptor.asByteArray(dirDescriptor));
            //Empty file descriptors
            for (int i = 0; i < MAX_FILES_IN_DIR; ++i)
                writer.write(FileDescriptor.asByteArray(new FileDescriptor()));
            writer.flush();

            //Open root directory
            oft.addFile(0);
            fdCache.put(0, dirDescriptor);
            return 2;
        }
    }

    public void save(String fileName) throws IOException {
        closeAll();
        ios.saveNewSystemState(fileName);
    }

    private FileDescriptor getDescriptor(int fdIndex) {
        if (fdCache.containsKey(fdIndex)) {
            return fdCache.get(fdIndex);
        } else {
            //calculate descriptor position in LDisk
            int offset = ios.blocksNum() + FSConfig.FILE_DESCRIPTOR_SIZE * fdIndex;
            int blockIndex = offset / ios.blockLen();
            int blockOffset = offset % ios.blockLen();

            DiskReader reader = new DiskReader(ios, blockIndex, blockOffset);
            return FileDescriptor.fromByteArray(reader.read(FSConfig.FILE_DESCRIPTOR_SIZE));
        }
    }

    private void writeBitMap() {
        DiskWriter writer = new DiskWriter(ios, 0, 0);
        writer.write(Arrays.copyOf(bitMap.toByteArray(), ios.blocksNum()));
    }

    /*
     * Changes file length to newLength, allocating new disk blocks if needed.
     * Does NOT cache/write file descriptor on disk.
     */
    private void extendFile(FileDescriptor fd, int newLength) throws AllocationException {
        assert newLength >= fd.fileLength;

        //check if file can store requested bytes
        if (newLength > ios.blockLen() * FSConfig.BLOCKS_PER_FILE) {
            throw new AllocationException("Increasing file length to " + newLength + "bytes will exceed file size limit");
        }
        int blocksNum = (fd.fileLength + ios.blockLen() - 1) / ios.blockLen();
        int newBlocksNum = (newLength + ios.blockLen() - 1) / ios.blockLen();
        int toAllocateNum = newBlocksNum - blocksNum;
        if (toAllocateNum == 0) {
            //Nothing to allocate
            return;
        }
        List<Integer> freeBlocks = new LinkedList<>();
        for (int i = FIRST_DATA_BLOCK; i < ios.blocksNum(); ++i) {
            if (!bitMap.get(i)) {
                freeBlocks.add(i);
                if (freeBlocks.size() == toAllocateNum) {
                    break;
                }
            }
        }
        if (freeBlocks.size() < toAllocateNum) {
            throw new AllocationException("Could not find enough free disk blocks");
        }
        for (int blockIndex : freeBlocks) {
            fd.blockArray[blocksNum++] = blockIndex;
            bitMap.set(blockIndex);
        }
        writeBitMap();
    }

    /*
     * Changes file length to newLength, freeing disk blocks if possible.
     * Does NOT cache/write file descriptor on disk.
     */
    private void truncateFile(FileDescriptor fd, int newLength) {
        assert newLength <= fd.fileLength;

        int blocksNum = (fd.fileLength + ios.blockLen() - 1) / ios.blockLen();
        int newBlocksNum = (newLength + ios.blockLen() - 1) / ios.blockLen();
        fd.fileLength = newLength;
        if (blocksNum == newBlocksNum) {
            return;
        }
        while (blocksNum > newBlocksNum) {
            assert fd.blockArray[blocksNum - 1] > 0;

            bitMap.clear(fd.blockArray[--blocksNum]);
        }
        writeBitMap();
    }

    private boolean fileExists(String fileName) throws OFTException {
        for (var file : getAllFiles()) {
            if (file.first.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    private void closeAll(){
        for (int i = FSConfig.MAX_OPEN_FILES; i >= 0; --i) {
            try {
                close(i);
            } catch (OFTException ignored) {}
        }
    }

    public byte[] read(int oftIndex, int bytes) throws OFTException, ReadWriteException {
        if (bytes <= 0) {
            throw new ReadWriteException("Cannot read " + bytes + " bytes");
        }
        FileDescriptor fd = getDescriptor(oft.getFDIndex(oftIndex));
        OftEntry entry = oft.getFile(oftIndex);

        // Check if file has enough bytes to read
        if (fd.fileLength - entry.fPos < bytes) {
            bytes = fd.fileLength - entry.fPos;
        }
        byte[] res = new byte[bytes];
        int bytesRead = 0;
        int blockIndex = entry.fPos / ios.blockLen();
        int blockOffset = entry.fPos % ios.blockLen();

        // Read all other bytes
        while (bytesRead < bytes) {
            int bytesToRead = Integer.min(ios.blockLen() - blockOffset, bytes - bytesRead);
            if (entry.readBlockIndex != blockIndex) {
                // Need to read new block into buffer
                // Write current buffer to the disk if needed
                if (entry.blockModified) {
                    ios.writeBlock(fd.blockArray[entry.readBlockIndex], entry.readWriteBuffer);
                    entry.blockModified = false;
                }
                // Need to read new block into buffer
                entry.readWriteBuffer = ios.readBlock(fd.blockArray[blockIndex]);
                entry.readBlockIndex = blockIndex;
            }
            System.arraycopy(entry.readWriteBuffer, 0, res, bytesRead, bytesRead);
            entry.fPos += ios.blockLen();
            bytesRead += bytesToRead;
            blockIndex++;
        }

        return res;
    }

    public int write(int oftIndex, byte[] data) throws OFTException, ReadWriteException, AllocationException {
        FileDescriptor fd = getDescriptor(oft.getFDIndex(oftIndex));
        OftEntry entry = oft.getFile(oftIndex);

        // Before reading/writing blocks, we must ensure the file can store requested bytes
        if (data.length + entry.fPos > ios.blockLen() * FSConfig.BLOCKS_PER_FILE) {
            throw new ReadWriteException("Increasing file by " + data.length + "bytes will exceed file size limit");
        }
        if (entry.fPos + data.length > fd.fileLength) {
            // Allocate the necessary bytes
            extendFile(fd, entry.fPos + data.length);
            //Write updated descriptor on disk
            int fdShift = ios.blocksNum() + FSConfig.FILE_DESCRIPTOR_SIZE * entry.fDescIndex;
            int fdBlockIndex = fdShift / ios.blockLen();
            int fdOffset = fdShift % ios.blockLen();
            DiskWriter fOut = new DiskWriter(ios, fdBlockIndex, fdOffset);
            fOut.write(FileDescriptor.asByteArray(fd));
        }

        int bytesWritten = 0;
        int blockIndex = entry.fPos / ios.blockLen();
        int blockOffset = entry.fPos % ios.blockLen();

        while (bytesWritten < data.length) {
            int bytesToWrite = Integer.min(ios.blockLen() - blockOffset, data.length - bytesWritten);
            if (entry.readBlockIndex != blockIndex) {
                // Need to read new block into buffer
                // Write current buffer to the disk if needed
                if (entry.blockModified) {
                    ios.writeBlock(fd.blockArray[entry.readBlockIndex], entry.readWriteBuffer);
                    entry.blockModified = false;
                }
                // Read new block
                entry.readWriteBuffer = ios.readBlock(fd.blockArray[blockIndex]);
                entry.readBlockIndex = blockIndex;
            }
            // Write data into the buffer
            System.arraycopy(data, bytesWritten, entry.readWriteBuffer, blockOffset,  bytesToWrite);
            entry.blockModified = true;
            bytesWritten += bytesToWrite;
            blockOffset = 0;
            blockIndex++;
        }
        entry.fPos += bytesWritten;
        return bytesWritten;
    }

    public void create(String fileName) throws FSException, OFTException {
        FileDescriptor dirDescriptor = getDescriptor(0);
        if (fileName.length() > FSConfig.MAX_FILENAME_LEN) {
            throw new FSException("File name + " + fileName + " is too long. Maximum length: " + FSConfig.MAX_FILENAME_LEN);
        } else if (dirDescriptor.fileLength / FSConfig.DIRECTORY_ENTRY_SIZE >= MAX_FILES_IN_DIR) {
            throw new FSException("Directory is full");
        } else if (fileExists(fileName)) {
            throw new FSException("File named " + fileName + " already exists");
        }

        //Calculate offset to read file descriptors
        int offset = ios.blocksNum() + FSConfig.FILE_DESCRIPTOR_SIZE;
        int blockIndex = offset / ios.blockLen();
        int blockOffset = offset % ios.blockLen();
        DiskReader reader = new DiskReader(ios, blockIndex, blockOffset);

        //Find free descriptor
        FileDescriptor fd = null;
        int fdIndex = -1;
        for (int i = 0; i < MAX_FILES_IN_DIR; ++i) {
            fd = FileDescriptor.fromByteArray(reader.read(FSConfig.FILE_DESCRIPTOR_SIZE));
            if(fd.fileLength < 0) {
                fdIndex = i + 1;
                fd.fileLength = 0;
                break;
            }
        }
        if (fdIndex == -1) {
            throw new FSException("Could not find free descriptor");
        }

        //Create directory entry
        DirectoryEntry de = new DirectoryEntry(fdIndex, fileName);
        try {
            write(0, DirectoryEntry.asByteArray(de));
        } catch (ReadWriteException | AllocationException e) {
            throw new IllegalStateException("Could not write directory entry, even though directory is not full");
        }

        //Write file descriptor on disk
        offset = ios.blocksNum() + FSConfig.FILE_DESCRIPTOR_SIZE * fdIndex;
        blockIndex = offset / ios.blockLen();
        blockOffset = offset % ios.blockLen();
        DiskWriter writer = new DiskWriter(ios, blockIndex, blockOffset);
        writer.write(FileDescriptor.asByteArray(fd));
    }

    public void destroy(String fileName) throws OFTException, FSException {
        Pair<DirectoryEntry, Integer> fileInfo = findFileInDirectory(fileName);
        int deIndex = fileInfo.second;

        if (deIndex == -1) {
            throw new FSException("Could not find file named " + fileName);
        }
        DirectoryEntry de = fileInfo.first;
        if (oft.getOftIndex(de.fdIndex) != -1) {
            throw new FSException("Could not destroy file " + fileName + "as it is currently open");
        }

        //Delete file from disk
        //Free disk blocks
        truncateFile(getDescriptor(de.fdIndex), 0);
        //Free file descriptor
        int offset = ios.blocksNum() + FSConfig.FILE_DESCRIPTOR_SIZE * de.fdIndex;
        int blockIndex = offset / ios.blockLen();
        int blockOffset = offset % ios.blockLen();
        DiskWriter writer = new DiskWriter(ios, blockIndex, blockOffset);
        writer.write(FileDescriptor.asByteArray(new FileDescriptor()));
        writer.flush();

        //NOTE: Check offsets later;
        //Remove directory entry
        FileDescriptor dirDescriptor = getDescriptor(0);
        //Find last directory entry
        seek(0, dirDescriptor.fileLength - FSConfig.DIRECTORY_ENTRY_SIZE);
        try {
            de = DirectoryEntry.fromByteArray(read(0, FSConfig.DIRECTORY_ENTRY_SIZE));
        } catch (ReadWriteException e) {
            throw new IllegalStateException(e.getMessage());
        }
        //Replace removed DE with last DE
        seek(0, FSConfig.DIRECTORY_ENTRY_SIZE * deIndex);
        try {
            write(0, DirectoryEntry.asByteArray(de));
        } catch (ReadWriteException | AllocationException e) {
            throw new IllegalStateException("Looks like DirectoryEntry is outside directory file...");
        }
        truncateFile(dirDescriptor, dirDescriptor.fileLength - FSConfig.DIRECTORY_ENTRY_SIZE);

        //Write directory descriptor on disk.
        writer = new DiskWriter(ios, ios.blocksNum() / ios.blockLen(), ios.blocksNum() % ios.blockLen());
        writer.write(FileDescriptor.asByteArray(dirDescriptor));
    }

    public int seek(int oftIndex, int pos) throws OFTException {
        FileDescriptor fd = getDescriptor(oft.getFDIndex(oftIndex));
        OftEntry file = oft.getFile(oftIndex);
        if (pos < 0 || pos > fd.fileLength) {
            return -1;
        }
        file.fPos = pos;
        return 1;
    }

    public Pair<DirectoryEntry, Integer> findFileInDirectory(String fileName) throws OFTException {
        // function return file directory entry with idx or
        // file directory entry with idx = -1 if not found
        FileDescriptor dirFd = getDescriptor(0);
        seek(0, 0);
        int numOFFilesInDir = dirFd.fileLength / FSConfig.DIRECTORY_ENTRY_SIZE;
        int dirEntryIdx;
        for (int i = 0; i < numOFFilesInDir; i++) {
            DirectoryEntry curDirEntry = null;
            try {
                curDirEntry = DirectoryEntry.fromByteArray(read(0, FSConfig.DIRECTORY_ENTRY_SIZE));
            } catch (ReadWriteException e) {
                //cannot happen
                throw new IllegalStateException(e.getMessage());
            }
            if (curDirEntry.filename.equals(fileName)){
                dirEntryIdx = i;
                return new Pair<>(curDirEntry, dirEntryIdx);
            }
        }
        return new Pair<>(new DirectoryEntry(), -1);
    }

    public int open(String fileName) throws OFTException, FSException {
        // returns -1 if file with such fileName not found
        // or oftIndex if file found
        Pair<DirectoryEntry, Integer> file = findFileInDirectory(fileName);
        if (file.second == -1) {
            throw new FSException("Could not find file named " + fileName);
        }
        int fdIndex = file.first.fdIndex;
        fdCache.put(fdIndex, getDescriptor(fdIndex));
        return oft.addFile(fdIndex);
    }

    public void close(int oftIndex) throws OFTException {
        int fdIndex = oft.getFDIndex(oftIndex);
        FileDescriptor fd = fdCache.remove(fdIndex);
        OftEntry oftEntry = oft.getFile(oftIndex);
        if (oftEntry.blockModified) {
            ios.writeBlock(fd.blockArray[oftEntry.readBlockIndex], oftEntry.readWriteBuffer);
        }
        oft.removeOftEntry(oftIndex);
    }

    /*
     * Returns names and lengths of all files in root directory.
     */
    public List<Pair<String, Integer>> getAllFiles() throws OFTException {
        FileDescriptor dirDescriptor = getDescriptor(0);
        seek(0, 0);
        int filesNum = dirDescriptor.fileLength / FSConfig.DIRECTORY_ENTRY_SIZE;
        List<Pair<String, Integer>> res = new ArrayList<>(filesNum);
        for (int i = 0; i < filesNum; ++i) {
            try {
                DirectoryEntry de = DirectoryEntry.fromByteArray(read(0, FSConfig.DIRECTORY_ENTRY_SIZE));
                FileDescriptor fd = getDescriptor(de.fdIndex);
                res.add(new Pair<>(de.filename, fd.fileLength));
            } catch (ReadWriteException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return res;
    }
}
