package gitlet;

import java.io.File;
import java.util.*;

/** Represents a gitlet repository.
 * In the repository, we could add/edit/remove files,
 * committing them, hence keep track of all work done in it.
 *  @author Sueray
 */
class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** The initialCommit for all directories. */
    public static final Commit INITIAL_COMMIT = new Commit();
    /** The HEAD pointer of this Repository. */
    private Commit HEAD = null;
    /** The map of commits, keys are timestap, vals are commits */
    private TreeMap<String, Commit> commitTree;
    /** The staging area */
    private HashMap<String, Object> stagedForAddition = new HashMap<>();
    private HashMap<String, Object> stagedForRemoval = new HashMap<>();

    public Repository() {
    }

    /** Create a new Gitlet version-control system in CWD. */
    public void initialCommit() {
        //check if .gitlet already exists. If so, print error msg and abort initialization.
        if (gitletExist()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        //make the .gitlet dir.
        GITLET_DIR.mkdir();
        //put initial commit into the directory's commit tree.
        commitTree.put(INITIAL_COMMIT.getTimestamp(), INITIAL_COMMIT);
        //create the master branch here and put the initialCommit in it.
        Branch master = new Branch("master", INITIAL_COMMIT);
        //set HEAD pointer to the leaf of master branch.
        HEAD = INITIAL_COMMIT;
    }

    /** Add a file to the staging area of current commit.
     * @param fileName the name of target file
     * */
    public void addAFile(String fileName) {
        //construct the input file
        File inFile = Utils.join(GITLET_DIR, fileName);
        //check the existence of the file in repo
        if (!inFile.exists()) {
            Utils.exitWithError("File does not exist.");
        }
        //check the existence of the file in commit
        File fileInCommit = (File) HEAD.searchForObject(fileName);
        if (fileInCommit != null && filesIdentical(inFile, fileInCommit)) {
            //if the file in commit is identical to input
            //remove it from staging area (if there is one) and return.
            if (stagedForAddition.get(fileName) != null) {
                stagedForAddition.remove(fileName);
            }
            return;
        }
        //and construct a copy to staging area
        stagedForAddition.put(fileName, inFile);
    }

    /** Saves current commit and create a new commit.
     * @param message the log message of this commit
     * */
    public void commit(String message) {
        if (stagedForAddition.isEmpty()) {
            Utils.exitWithError("No changes added to the commit.");
        }
        if (message.equals("")) {
            Utils.exitWithError("Please enter a commit message.");
        }
        //create a "current commit" whose parent points to previous "current commit".
        HEAD = new Commit(message, new Date().toString(), HEAD, HEAD.getTreeOfObjects());
        //add everything staged for addition to current commit
        for (Map.Entry<String, Object> entries : stagedForAddition.entrySet()) {
            HEAD.addObject(entries.getKey(), entries.getValue());
        }
        //remove everything staged for removal in current commit
        for (String keys : stagedForRemoval.keySet()) {
            HEAD.removeObject(keys);
        }
        commitTree.put(HEAD.getTimestamp(), HEAD);
        clearStagingArea();

    }

    /** Takes file with fileName in the head commit
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     * @param fileName the name of the file to be updated
     * */
    public void checkoutAFile(String fileName) {
        checkoutCommit(HEAD, fileName);
    }

    /** Takes file with fileName in the commit with commitID
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     * @param fileName the name of the file to be updated
     * @param commitID the ID of the commit where file exists
     * */
    public void checkoutAFile(String commitID, String fileName) {
        Commit targetCommit = commitTree.get(commitID);
        if (targetCommit == null) {
            Utils.exitWithError("No commit with that id exists.");
        }
        checkoutCommit(targetCommit, fileName);
    }

    /** Display information about each commit.
     * Starting at the current head commit,
     * backwards along the commit tree until the initial commit. */
    public void log() {
        Commit pointer = HEAD;
        while (pointer != INITIAL_COMMIT) {
            System.out.println("===");
            System.out.println("commit " + pointer.getUID());
            System.out.println("Date: " + pointer.getTimestamp());
            System.out.println(pointer.getLog());
            System.out.print("\n");
            pointer = pointer.getParent();
        }
    }


    //not finished
    /** Not finished yet. */
    public void checkoutABranch(String arg) {
    }


    //Helper functions
    /** Check whether two given files are identical.
     * @param a the first file to be compared with
     * @param b the second file to be compared with
     * @return boolean val indicating the equality of two files a and b
     */
    private boolean filesIdentical(File a, File b) {
        return Utils.sha1(a).equals(Utils.sha1(b));
    }

    /** Check whether the .gitlet Repo exists in the CWD
     * @return boolean val indicating the existence of .gitlet
     */
    public boolean gitletExist() {
        return GITLET_DIR.exists();
    }

    /** Clear the staging area. */
    private void clearStagingArea() {
        stagedForAddition.clear();
        stagedForRemoval.clear();
    }

    /** Helper method that takes file with fileName in the commit
     * and puts it in the working directory, overwriting if necessary.
     * The new version of the file is not staged.
     * @param fileName the name of the file to be updated
     * @param commit the commit where file exists
     * */
    private void checkoutCommit(Commit commit, String fileName) {
        File target = (File) commit.searchForObject(fileName);
        if (target == null) {
            Utils.exitWithError("File does not exist in that commit.");
        }
        File dest = Utils.join(CWD, fileName);
        Utils.writeContents(dest, target);
        clearStagingArea();
    }
}
