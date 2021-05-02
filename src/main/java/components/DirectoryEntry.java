package components;

import file_system.FSConfig;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DirectoryEntry {
    public String filename;
    public int fdIndex = -1;

    public DirectoryEntry() {}
    public DirectoryEntry(int fdIndex, String filename) {
        assert filename.length() <= FSConfig.MAX_FILENAME_LEN;

        this.fdIndex = fdIndex;
        this.filename = filename;
    }

    public static DirectoryEntry formByteArray(byte[] data) {
        assert data.length == FSConfig.DIRECTORY_ENTRY_SIZE;

        DirectoryEntry res = new DirectoryEntry();
        res.fdIndex = ByteBuffer.wrap(Arrays.copyOf(data, Integer.BYTES)).getInt();
        res.filename = new String(Arrays.copyOfRange(data, Integer.BYTES, data.length), StandardCharsets.UTF_8);
        return res;
    }

    public static byte[] asByteArray(DirectoryEntry de) {
        byte[] res = Arrays.copyOf(ByteBuffer.allocate(Integer.BYTES).putInt(de.fdIndex).array()
                , FSConfig.MAX_FILENAME_LEN + Integer.BYTES);
        if (de.filename != null) {
            System.arraycopy(de.filename.getBytes(StandardCharsets.UTF_8), 0, res, Integer.BYTES, FSConfig.MAX_FILENAME_LEN);
        }
        return res;
    }
}
