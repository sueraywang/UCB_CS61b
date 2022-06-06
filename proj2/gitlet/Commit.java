package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Sueray
 */
class Commit implements Serializable, Cloneable {

    /** The UID that identifies different commits. */
    private String UID;
    /** The timestamp of commit, initialized to 01/01/1970 00:00:00 (UTC). */
    private String timestamp;
    /** The log of commit */
    private String log;
    /** The parent commit of current commit. */
    private Commit parent;
    /** The branch that current commit belongs to. */
    private String branch;
    /** The tree that maps objects' names to blobs (identified by id/sha1). */
    private TreeMap<String, Blob> treeOfBlobs = new TreeMap<>();

    /** The constructor with no argument creates an initial commit. */
    public Commit() {
        timestamp = "Wed Dec 31 19:00:00 EST 1969";
        log = "initial commit";
        UID = Utils.sha1(log, timestamp);
        parent = this;
        branch = "master";
    }

    public Commit(String log, String timestamp, Commit parent, String branch) {
        this.timestamp = timestamp;
        this.log = log;
        this.parent = parent;
        this.branch = branch;
        UID = Utils.sha1(log, timestamp, Utils.serialize(treeOfBlobs), Utils.serialize(parent));
    }

    /** Add an object to the object tree.
     * @param name the key of object
     * @param b the actual content mapped by name
     */
    public void addBlob(String name, Blob b) {
        treeOfBlobs.put(name, b);
    }

    /** Add an object to the object tree.
     * @param name the key of object
     */
    public void removeBlob(String name) {
        treeOfBlobs.remove(name);
    }

    /** Search for the target in the object tree.
     * @param name the name of target object
     * @return the target object or null if target doesn't exist
     */
    public Blob searchFor(String name) {
        return treeOfBlobs.get(name);
    }

    //getter and setters
    public String getUID() {
        return UID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLog() {
        return log;
    }

    public Commit getParent() {
        return parent;
    }

    public TreeMap<String, Blob> getTreeOfBlobs() {
        return treeOfBlobs;
    }

    public void setTreeOfBlobs(TreeMap<String, Blob> treeOfBlobs) {
        for (Map.Entry<String, Blob> entries : treeOfBlobs.entrySet()) {
            this.treeOfBlobs.put(entries.getKey(), entries.getValue());
        }
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
