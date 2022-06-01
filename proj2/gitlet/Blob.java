package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

class Blob implements Serializable {
    private static final long serialVersionUID = 1L;
    String ID;
    Object ref;

    public Blob(Object ref) {
        if (ref instanceof File) {
            this.ID = Utils.sha1(Utils.serialize((File) ref));
        }
        this.ref = ref;
    }

    public Blob(String ID, Object ref) {
        this.ID = ID;
        this.ref = ref;
    }

    public Object getID() {
        return ID;
    }

    public Object getRef() {
        return ref;
    }

    @Override
    public boolean equals(Object obj) {
        Blob b = (Blob) obj;
        return ID.equals(b.getID());
    }

    public static Blob snapShot(File file){
        byte[] contents = Utils.readContents(file);
        Blob copy = new Blob(Utils.sha1(contents), contents);
        return copy;
    }
}
