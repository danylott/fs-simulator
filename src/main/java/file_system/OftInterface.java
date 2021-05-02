package file_system;

import java.util.ArrayList;

public abstract class OftInterface {
    protected static final int FDOPENEDLIMIT = 0;

    protected int oftSize = 0;
    protected ArrayList<OftEntry> entriesBuffer = new ArrayList<>(FDOPENEDLIMIT);


    public abstract int getOftIndex(int _fdIndex);

    public abstract int addFile(int _fdIndex);

    public abstract OftEntry getFile(int _oftIndex);

    public abstract void removeOftEntry(int _oftIndex);

    public abstract int getNumOfOpenFiles();

    public abstract int getFDIndexByOftIndex(int _oftIndex);
}
