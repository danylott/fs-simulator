package elements;

public abstract class SerializableElement {

    protected int size;

    public int size() {
        return size;
    }

    //Save this object to byte array starting at pos
    public abstract void serialize(byte[] data, int pos);

    //Read this object from byte array starting at pos
    public abstract void deserialize(byte[] data, int pos);
}
