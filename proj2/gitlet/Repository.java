package gitlet;

import java.io.File;
import java.util.*;

/** Represents a gitlet repository.
 * In the repository, we could add/edit/remove files,
 * committing them, hence keep track of all work done in it.
 *  @author Sueray
 */
@SuppressWarnings("unchecked")
class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** The file of commits */
    public static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** The file with staged Blobs to be added. */
    public static final File fileOfAddStage = Utils.join(GITLET_DIR, "addition");
    /** The file with staged Blobs to be removed. */
    public static final File fileOfRemoveStage = Utils.join(GITLET_DIR, "removal");
    /** The file of HEAD */
    public static final File head = Utils.join(GITLET_DIR, "head");
    /** The file of branches */
    public static final File branches = Utils.join(GITLET_DIR, "branches");
    /** The file of split points */
    public static final File splitPoints = Utils.join(GITLET_DIR, "splitPoints");
    /** The initialCommit for all directories. */
    public static final Commit INITIAL_COMMIT = new Commit();

    /** The HEAD pointer of this Repository. */
    private Commit HEAD = INITIAL_COMMIT;
    /** The branches mapped to their heads. */
    private HashMap<String, Commit> BRANCHES = new HashMap<>();
    /** The split points */
    private ArrayList<Commit> SPLITPOINTS = new ArrayList<>();
    /** The map of Blobs to be staged */
    private HashMap<String, Blob> stagedForAddition = new HashMap<>();
    private HashMap<String, Blob> stagedForRemoval = new HashMap<>();

    public Repository() {
    }

    /** Create a new Gitlet version-control system in CWD. */
    public void initialCommit() {
        //check if .gitlet already exists. If so, print error msg and abort initialization.
        if (gitletExist()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        //make the .gitlet and .stage dir.
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        //treeOfCommits.put(INITIAL_COMMIT.getUID(), INITIAL_COMMIT);
        //set HEAD pointer to the INITIAL_COMMIT.
        HEAD = INITIAL_COMMIT;
        BRANCHES.put("master", INITIAL_COMMIT);
        Utils.writeObject(head, HEAD);
        Utils.writeObject(branches, BRANCHES);
        //Utils.writeObject(fileOfCommits, treeOfCommits);
        Utils.writeObject(Utils.join(COMMITS_DIR, HEAD.getUID()), HEAD);
    }

    /** Add a file to the staging area of current commit.
     * @param fileName the name of target file
     * */
    public void addAFile(String fileName) {
        //construct the object of target file in local repo
        File file = Utils.join(CWD, fileName);
        if (!file.exists()) {
            Utils.exitWithError("File does not exist.");
        }
        //Take a snapShot of blob to be added
        Blob target = Blob.snapShot(file);
        //get current staging area
        if (fileOfAddStage.exists()) {
            stagedForAddition = Utils.readObject(fileOfAddStage, stagedForAddition.getClass());
        }//check the existence of target file in commit
        HEAD = Utils.readObject(head, HEAD.getClass());
        Blob fileInCommit = HEAD.searchFor(fileName);
        if (fileInCommit != null && fileInCommit.equals(target)) {
            //if the file in commit is identical to input
            //remove it from staging area (if there is one) and return.
            if (stagedForAddition.get(fileName) != null) {
                stagedForAddition.remove(fileName);
            }
            return;
        } else {
            stagedForAddition.put(fileName, target);
            Utils.writeObject(fileOfAddStage, stagedForAddition);
        }
    }

    public void removeAFile(String fileName) {
        //construct the copy of target file in local repo
        File file = Utils.join(CWD, fileName);
        Blob target = Blob.snapShot(file);
        //check the existence of target file in commit
        HEAD = Utils.readObject(head, HEAD.getClass());
        Blob fileInCommit = HEAD.searchFor(fileName);
        //remove file from commit
        if (fileInCommit != null) {
            if (fileOfRemoveStage.exists()) {
                stagedForRemoval = Utils.readObject(fileOfRemoveStage, stagedForAddition.getClass());
            }
            stagedForRemoval.put(fileName, target);
            Utils.writeObject(fileOfRemoveStage, stagedForRemoval);
            Utils.restrictedDelete(file);
        } else if (fileOfAddStage.exists()) {
            stagedForAddition = Utils.readObject(fileOfAddStage, stagedForAddition.getClass());
            if (stagedForAddition.get(fileName) != null) {
                stagedForAddition.remove(fileName);
                Utils.writeObject(fileOfAddStage, stagedForAddition);
            }
        } else {
            Utils.exitWithError("No reason to remove the file.");
        }
    }

    /** Saves current commit and create a new commit.
     * @param message the log message of this commit
     * */
    public void commit(String message) {
        if (!(fileOfAddStage.exists() || fileOfRemoveStage.exists())) {
            Utils.exitWithError("No changes added to the commit.");
        }
        if (message.equals("")) {
            Utils.exitWithError("Please enter a commit message.");
        }
        //pull out all changes to be committed and the current commit tree
        if (fileOfAddStage.exists()) {
            stagedForAddition = Utils.readObject(fileOfAddStage, stagedForAddition.getClass());
        }
        if (fileOfRemoveStage.exists()) {
            stagedForRemoval = Utils.readObject(fileOfRemoveStage, stagedForRemoval.getClass());
        }
        HEAD = Utils.readObject(head, HEAD.getClass());

        //create a "current commit" whose parent points to previous "current commit".
        Commit current = new Commit(message, new Date().toString(), HEAD, HEAD.getBranch());
        current.setTreeOfBlobs(HEAD.getTreeOfBlobs());
        HEAD = current;
        //add everything staged for addition to current commit
        for (Map.Entry<String, Blob> entries : stagedForAddition.entrySet()) {
            HEAD.addBlob(entries.getKey(), entries.getValue());
        }
        //remove everything staged for removal in current commit
        for (String keys : stagedForRemoval.keySet()) {
            HEAD.removeBlob(keys);
        }

        BRANCHES = Utils.readObject(branches, BRANCHES.getClass());
        for (String s : BRANCHES.keySet()) {
            splitPoint(s);
        }
        Utils.writeObject(head, HEAD);
        Utils.writeObject(Utils.join(COMMITS_DIR, HEAD.getUID()), HEAD);
        clearStagingArea();
    }

    /** Takes file with fileName in the head commit
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     * @param fileName the name of the file to be updated
     * */
    public void checkoutAFile(String fileName) {
        HEAD = Utils.readObject(head, HEAD.getClass());
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
        HEAD = Utils.readObject(head, HEAD.getClass());
        Commit pointer = HEAD;
        while (pointer != pointer.getParent()) {
            if (pointer.getUID().equals(commitID)) {
                checkoutCommit(pointer, fileName);
                return;
            }
            pointer = pointer.getParent();
        }
        Utils.exitWithError("No commit with that id exists.");
    }

    /** Display information about each commit.
     * Starting at the current head commit,
     * backwards along the commit tree until the initial commit. */
    public void log() {
        HEAD = Utils.readObject(head, HEAD.getClass());
        Commit pointer = HEAD;
        while (pointer != pointer.getParent()) {
            printLogInfo(pointer);
            pointer = pointer.getParent();
        }
        printLogInfo(pointer);
    }

    /** Display information about each commit.
     * Order doesn't matter. */
    public void global_log() {
        List<String> commits = Utils.plainFilenamesIn(COMMITS_DIR);
        for (String s : commits) {
            System.out.println(s);
        }
    }

    /** Prints out the ids of all commits that have the given commit message, one per line. */
    public void find(String arg) {
        int count = 0;
        HEAD = Utils.readObject(head, HEAD.getClass());
        Commit pointer = HEAD;
        while (pointer != pointer.getParent()) {
            if (pointer.getLog().contains(arg)) {
                System.out.println(pointer.getUID());
                count++;
            }
            pointer = pointer.getParent();
        }
        if (count == 0) {
            Utils.exitWithError("Found no commit with that message.");
        }
    }

    public void status() {
        System.out.println("=== Branches ===");
        BRANCHES = Utils.readObject(branches, BRANCHES.getClass());
        HEAD = Utils.readObject(head, HEAD.getClass());
        String currentBranch = HEAD.getBranch();
        for (String s : BRANCHES.keySet()) {
            if (s.equals(currentBranch)) {s = "*" + s;}
            System.out.println(s);
        }
        if (fileOfAddStage.exists()) {
            System.out.println("=== Staged Files ===");
            stagedForAddition = Utils.readObject(fileOfAddStage, stagedForAddition.getClass());
            for (String s : stagedForAddition.keySet()) {
                System.out.println(s);
            }
        }
        if (fileOfRemoveStage.exists()) {
            System.out.println("=== Removed Files ===");
            stagedForRemoval = Utils.readObject(fileOfRemoveStage, stagedForRemoval.getClass());
            for (String s : stagedForRemoval.keySet()) {
                System.out.println(s);
            }
        }
        ArrayList<File> untrackedFiles = untrackedFiles(HEAD);
        if (!untrackedFiles.isEmpty()) {
            System.out.println("=== Untracked Files ===");
            for (File f : untrackedFiles) {
                System.out.println(f.getName());
            }
        }
    }

    /** Creates a new branch with the given name, and points it at the current head commit.
     * @param arg The branch name
     * */
    public void branch(String arg) {
        BRANCHES = Utils.readObject(branches, BRANCHES.getClass());
        if (BRANCHES.keySet().contains(arg)) {
            Utils.exitWithError("A branch with that name already exists.");
        }
        BRANCHES.put(arg,HEAD);
        Utils.writeObject(branches, BRANCHES);
    }

    /** Moves the pointer to the branch designated by arg.
     * @param arg The branch name
     * */
    public void checkoutABranch(String arg) {
        BRANCHES = Utils.readObject(branches, BRANCHES.getClass());
        HEAD = Utils.readObject(head, HEAD.getClass());
        String currentBranch = HEAD.getBranch();
        if (currentBranch.equals(arg)) {
            Utils.exitWithError("No need to checkout the current branch.");
        } else if (BRANCHES.get(arg) == null) {
            Utils.exitWithError("No such branch exists.");
        }
        Commit branchHead = BRANCHES.get(arg);
        if (!untrackedFiles(HEAD).isEmpty()) {
            Utils.exitWithError("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        for (String fileName : branchHead.getTreeOfBlobs().keySet()) {
            checkoutCommit(branchHead, fileName);
        }
        branchHead.setBranch(arg);
        HEAD = branchHead;
        Utils.writeObject(head, HEAD);
    }

    /** Removes the branch designated by arg.
     * @param arg The branch name
     * */
    public void rm_branch(String arg) {
        BRANCHES = Utils.readObject(branches, BRANCHES.getClass());
        HEAD = Utils.readObject(head, HEAD.getClass());
        String currentBranch = HEAD.getBranch();
        if (currentBranch.equals(arg)) {
            Utils.exitWithError("Cannot remove the current branch.");
        } else if (BRANCHES.get(arg) == null) {
            Utils.exitWithError("A branch with that name does not exist.");
        }
        BRANCHES.remove(arg);
        Utils.writeObject(branches, BRANCHES);
    }

    /** Checks out all the files tracked by the given commit.
     * Removes tracked files that are not present in that commit.
     * Also moves the current branch’s head to that commit node.
     * @param arg The commit ID.
     * */
    public void reset(String arg) {
        HEAD = Utils.readObject(head, HEAD.getClass());
        BRANCHES = Utils.readObject(branches, BRANCHES.getClass());
        Commit pointer = HEAD;
        while (pointer != pointer.getParent()) {
            if (pointer.getUID().equals(arg)) {
                for (String fileName :
                        pointer.getTreeOfBlobs().keySet()) {
                    checkoutCommit(pointer, fileName);
                }
                break;
            }
            pointer = pointer.getParent();
        }
        if (pointer == pointer.getParent()) {
            Utils.exitWithError("No commit with that id exists.");
        }
        BRANCHES.put(HEAD.getBranch(), HEAD);
        Utils.writeObject(branches, BRANCHES);
    }

    //not finished
    /** Merges files from the given branch into the current branch.
     * @param arg The branch name of which to be merged.
     * */
    public void merge(String arg) {
        SPLITPOINTS = Utils.readObject(splitPoints, SPLITPOINTS.getClass());
        SPLITPOINTS.add(HEAD);
        Utils.writeObject(splitPoints, SPLITPOINTS);
    }



    //Helper functions
    /** Check whether the .gitlet Repo exists in the CWD
     * @return boolean val indicating the existence of .gitlet
     */
    public boolean gitletExist() {
        return GITLET_DIR.exists();
    }

    /** Clear the staging area. */
    private void clearStagingArea() {
        fileOfAddStage.delete();
        fileOfRemoveStage.delete();
    }

    /** Helper method that takes file with fileName in the commit
     * and puts it in the working directory, overwriting if necessary.
     * The new version of the file is not staged.
     * @param fileName the name of the file to be updated
     * @param commit the commit where file exists
     * */
    private void checkoutCommit(Commit commit, String fileName) {
        Blob target = commit.searchFor(fileName);
        if (target == null) {
            Utils.exitWithError("File does not exist in that commit.");
        }
        File dest = Utils.join(CWD, fileName);
        Utils.writeContents(dest, target.getRef());
    }

    /** Display log info of certain commit. */
    private void printLogInfo(Commit pointer) {
        System.out.println("===");
        System.out.println("commit " + pointer.getUID());
        System.out.println("Date: " + pointer.getTimestamp());
        System.out.println(pointer.getLog());
        System.out.print("\n");
    }

    /** Check for untracked files. */
    private ArrayList<File> untrackedFiles(Commit branchHead) {
        ArrayList<File> untrackedFiles = new ArrayList<>();
        if (fileOfAddStage.exists()) {
            stagedForAddition = Utils.readObject(fileOfAddStage, stagedForAddition.getClass());
        }
        for (File f : CWD.listFiles()) {
            if (f.isDirectory() ||
            stagedForAddition.containsKey(f.getName()) ||
            branchHead.getTreeOfBlobs().containsKey(f.getName())) {
                continue;
            }
            untrackedFiles.add(f);
        }
        return untrackedFiles;
    }

    /** Find split point */
    private void splitPoint(String branchName) {
        BRANCHES = Utils.readObject(branches, BRANCHES.getClass());
        Commit branchHead = BRANCHES.get(branchName);
        if (branchHead.getParent() == HEAD) {
            SPLITPOINTS = Utils.readObject(splitPoints, SPLITPOINTS.getClass());
            SPLITPOINTS.add(HEAD);
            Utils.writeObject(splitPoints, SPLITPOINTS);
        }
    }
}
