package gitlet;

public class Branch {
    private String name;
    private Commit commitOfBranch = null;

    public Branch(String name, Commit commitOfBranch) {
        this.name = name;
        this.commitOfBranch = commitOfBranch;
    }

    /** Returns the furthest point of a branch */
    public Commit leaf() {
        return null;
    }
}
