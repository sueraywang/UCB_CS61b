package gitlet;

import java.util.TreeMap;

/** Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Sueray
 */
class Commit {

    /** The UID that identifies different commits. */
    private String UID;
    /** The timestamp of commit, initialized to 01/01/1970 00:00:00 (UTC). */
    private String timestamp;
    /** The log of commit */
    private String log;
    /** The parent commit of current commit. */
    private Commit parent;
    /** The tree that maps objects' names to object references. */
    private TreeMap<String, Object> treeOfObjects = null;

    /** The constructor with no argument creates an initial commit. */
    public Commit() {
        timestamp = "Wed Dec 31 19:00:00 EST 1969";
        log = "initial commit";
        UID = Utils.sha1(log, timestamp);
        parent = this;
    }

    public Commit(String log, String timestamp, Commit parent, TreeMap<String, Object> parentObjects) {
        this.timestamp = timestamp;
        this.log = log;
        this.parent = parent;
        this.treeOfObjects = parentObjects;
        UID = Utils.sha1(log, timestamp, treeOfObjects, parent);
    }

    /** Add an object to the object tree.
     * @param name the key of object
     * @param obj the actual content mapped by name
     */
    public void addObject(String name, Object obj) {
        treeOfObjects.put(name, obj);
    }

    /** Add an object to the object tree.
     * @param name the key of object
     */
    public void removeObject(String name) {
        treeOfObjects.remove(name);
    }

    /** Search for the target in the object tree.
     * @param target the name of target object
     * @return the target object or null if target doesn't exist
     */
    public Object searchForObject(String target) {
        return treeOfObjects.get(target);
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

    public TreeMap<String, Object> getTreeOfObjects() {
        return treeOfObjects;
    }
}
