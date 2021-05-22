package file_system;

public class OftEntry {
    public byte[] readWriteBuffer;

    public int fPos;
    public int fDescIndex;
    public int readBlockIndex;

    public boolean blockModified;

    public OftEntry() {
        this.readWriteBuffer = new byte[64];
        this.fPos = 0;
        this.fDescIndex = -1;
        this.readBlockIndex = -1;
        this.blockModified = false;
    }
}
