package auxiliary;

import components.DirectoryEntry;

public class Pair<F, S> {
    public F first;
    public S second;

    public Pair() { }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
