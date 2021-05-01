package file_system;

import components.FileDescriptor;
import io_system.IOSystemInterface;
import exceptions.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class FileSystem {
    private IOSystemInterface ios;

    private FileDescriptor getDescriptor(int index) {
        //calculate descriptor position in LDisk
        int offset = FSConfig.BLOCKS_NUM + FSConfig.FILE_DESCRIPTOR_SIZE * index;
        int blockNum = offset / FSConfig.BLOCK_SIZE;
        int blockOffset = offset % FSConfig.BLOCK_SIZE;

        //read descriptor data
        //TODO DiscReader when ready
        //Should work tho
        byte[] bytes = Arrays.copyOfRange(ios.read_block(blockNum), blockOffset, blockOffset + FSConfig.FILE_DESCRIPTOR_SIZE);
        int[] ints = new int[1 + FSConfig.BLOCKS_PER_FILE];
        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asIntBuffer().get(ints);

        return new FileDescriptor(ints[0], Arrays.copyOfRange(ints, 1, ints.length));
    }

    public void create(String fileName) throws TooManyFilesException, TooLongFileNameException, FileAlreadyExistsException {
        FileDescriptor directory = getDescriptor(0);
        if (fileName.length() > FSConfig.MAX_FILENAME_LEN) {
            throw new TooLongFileNameException(fileName);
        } else if (directory.fileLength / FSConfig.DIRECTORY_ENTRY_SIZE >= FSConfig.MAX_FILES_IN_DIR) {
            throw new TooManyFilesException();
        } //TODO Check if file already exists.

        //do stuff

    }

    public void destroy(String fileName) {

    }
}
