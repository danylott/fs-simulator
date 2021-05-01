package components;

public class FileDescriptor {
    public int fileLength = -1;
    public int[] blockArray;

    public FileDescriptor() {}
    public FileDescriptor(int fileLength, int[] blockArray) {
        this.fileLength = fileLength;
        this.blockArray = blockArray;
    }
}
