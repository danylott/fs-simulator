package components;

public class DirectoryEntry {
    public String filename;
    public int fdIndex = -1;

    public DirectoryEntry() {}
    public DirectoryEntry(int fdIndex, String filename) {
        this.fdIndex = fdIndex;
        this.filename = filename;
    }
}
