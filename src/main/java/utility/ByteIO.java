package utility;

public class ByteIO {

    //Read int from byte array, starting at pos
    public static int readInt(byte[] data, int pos) {
        return  ((data[pos    ] & 0xFF) << 24) |
                ((data[pos + 1] & 0xFF) << 16) |
                ((data[pos + 2] & 0xFF) << 8 ) |
                ((data[pos + 3] & 0xFF));
    }

    //Write int val to byte array, starting at pos
    public static void writeInt(byte[] data, int val, int pos) {
        for (int i = 0; i < 4; i++) {
            data[pos + i] = (byte) (val >> (24 - i * 8));
        }
    }
}
