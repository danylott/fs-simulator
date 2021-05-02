package exceptions;

public class NotEnoughFreeBlocksException extends Exception {
    public NotEnoughFreeBlocksException (int blocksNeeded, int blocksFound) {
        super("Tried to allocate " + blocksNeeded + " blocks, but found only " + blocksFound);
    }
}
