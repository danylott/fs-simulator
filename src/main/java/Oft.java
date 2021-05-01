public class Oft extends OftInterface{
    @Override
    public int getOftIndex(int _fdIndex) {
        if (_fdIndex < 0 || _fdIndex > FDOPENEDLIMIT)
            return -1;

        for (int i = 0; i < entriesBuffer.size(); i++) {
            if (entriesBuffer.get(i).fDescIndex == _fdIndex)
                return i;
        }
        return -1;
    }

    @Override
    public int addFile(int _fdIndex) {
        // if file exists or no free space in oft
        if (_fdIndex == FDOPENEDLIMIT || getOftIndex(_fdIndex) >= 0)
            return -1;

        OftEntry newOftEntry = new OftEntry();
        newOftEntry.fDescIndex = _fdIndex;

        for (int i = 0; i < FDOPENEDLIMIT; i++) {
            if (entriesBuffer.get(i).fDescIndex == -1) {
                entriesBuffer.set(i, newOftEntry);
                oftSize++;
                return i;
            }
        }
        return -1;
    }

    @Override
    public OftEntry getFile(int _oftIndex) {
        return entriesBuffer.get(_oftIndex);
    }

    @Override
    public void removeOftEntry(int _oftIndex) {
        entriesBuffer.get(_oftIndex).fDescIndex = -1;
        entriesBuffer.get(_oftIndex).blockModified = false;
        entriesBuffer.get(_oftIndex).blockRead = false;
        entriesBuffer.get(_oftIndex).fPos = 0;
        entriesBuffer.get(_oftIndex).readBlockIndex = -1;
        oftSize--;
    }

    @Override
    public int getNumOfOpenFiles() {
        return oftSize;
    }

    @Override
    public int getFDIndexByOftIndex(int _oftIndex) {
        if (_oftIndex < 0 || _oftIndex >= FDOPENEDLIMIT) {
            return -1;
        }
        return entriesBuffer.get(_oftIndex).fDescIndex;
    }
}
