package components;

import file_system.FSConfig;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class FileDescriptor {
    public int fileLength = -1;
    public int[] blockArray;

    public FileDescriptor() {
        blockArray = new int[FSConfig.BLOCKS_PER_FILE];
    }

    public FileDescriptor(int fileLength, int[] blockArray) {
        assert blockArray.length == FSConfig.BLOCKS_PER_FILE;

        this.fileLength = fileLength;
        this.blockArray = blockArray;
    }

    public static FileDescriptor formByteArray(byte[] data) {
        assert data.length == FSConfig.FILE_DESCRIPTOR_SIZE;

        FileDescriptor res = new FileDescriptor();
        res.fileLength = ByteBuffer.wrap(Arrays.copyOf(data, Integer.BYTES)).getInt();
        res.blockArray = ByteBuffer.wrap(Arrays.copyOfRange(data, Integer.BYTES, data.length)).asIntBuffer().array();
        return res;
    }

    public static byte[] asByteArray(FileDescriptor fd) {
        ByteBuffer bb = ByteBuffer.allocate(FSConfig.FILE_DESCRIPTOR_SIZE);
        bb.asIntBuffer().put(fd.fileLength).put(fd.blockArray);
        return bb.array();
    }
}
