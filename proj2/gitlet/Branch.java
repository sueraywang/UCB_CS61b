package gitlet;

public class Branch {
    private String name;
    private Commit pointer = null;

    public Branch(String name, Commit pointer) {
        this.name = name;
        this.pointer = pointer;
    }

    /** Returns the furthest point of a branch */
    public Commit leaf() {
        return null;
    }
}
